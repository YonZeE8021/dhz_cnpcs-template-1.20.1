#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
校验 Mixin 中的 @Accessor / @Invoker / @Inject / @Redirect 目标名是否与当前 Yarn 映射一致。

依据：Fabric Maven 上的 yarn-<mc+build>-v2.jar 内 mappings/mappings.tiny

用法（在项目根目录）:
  py -3 tools/mixin_accessor_audit.py
  py -3 tools/mixin_accessor_audit.py --mixin-dir src/main/java/noppes/npcs/mixin
  py -3 tools/mixin_accessor_audit.py --yarn 1.20.1+build.10
  py -3 tools/mixin_accessor_audit.py --no-descriptors   # 仅名称级检查

说明:
  - 名称级：检查 @Mixin 目标类（含继承表）上是否存在该字段/方法名。
  - 描述符级（默认开启）：从 @Inject/@Redirect 回调参数推断 JVM 描述符，在 Tiny 中精确匹配。
  - @Redirect 锚点：对 minecraft-unpicked.jar 运行 javap，确认宿主方法体内存在 target INVOKE。
  - 自动下载的映射缓存在 tools/.yarn-cache/
"""

from __future__ import annotations

import argparse
import os
import re
import subprocess
import sys
import zipfile
from dataclasses import dataclass, field
from pathlib import Path
from typing import Dict, List, Optional, Set, Tuple
from urllib.parse import quote
from urllib.request import urlopen

# --- Tiny v2 (Fabric Yarn v2 jar) 解析 ---

MethodDescIndex = Dict[str, Dict[str, Set[str]]]  # class -> method_name -> set(desc)


def load_yarn_tiny_from_jar(jar_path: Path) -> str:
    with zipfile.ZipFile(jar_path, "r") as z:
        return z.read("mappings/mappings.tiny").decode("utf-8")


def parse_tiny_v2(
    tiny_text: str,
) -> Tuple[Dict[str, Set[str]], Dict[str, Set[str]], MethodDescIndex, Dict[str, str]]:
    """
    返回:
      class_fields: named_internal_class -> set of named field names
      class_methods: named_internal_class -> set of named method names
      class_method_descs: named_internal_class -> method_name -> set of JVM descriptors (intermediary)
      named_to_inter_class: named slash class -> intermediary slash class
    """
    class_fields: Dict[str, Set[str]] = {}
    class_methods: Dict[str, Set[str]] = {}
    class_method_descs: MethodDescIndex = {}
    named_to_inter_class: Dict[str, str] = {}
    current: Optional[str] = None

    for raw in tiny_text.splitlines():
        if not raw:
            continue
        parts = raw.split("\t")
        head = parts[0]

        if head == "tiny":
            continue
        if head == "c":
            if len(parts) < 3:
                continue
            inter_cls, named_cls = parts[1], parts[2]
            named_to_inter_class[named_cls] = inter_cls
            current = named_cls
            class_fields.setdefault(current, set())
            class_methods.setdefault(current, set())
            class_method_descs.setdefault(current, {})
            continue

        if not current:
            continue

        if head != "" or len(parts) < 2:
            continue
        kind = parts[1]

        if kind == "f" and len(parts) >= 5:
            class_fields[current].add(parts[4])
        elif kind == "m" and len(parts) >= 5:
            desc, named_method = parts[2], parts[4]
            if named_method in ("<init>", "<clinit>"):
                continue
            class_methods[current].add(named_method)
            class_method_descs[current].setdefault(named_method, set()).add(desc)

    return class_fields, class_methods, class_method_descs, named_to_inter_class


def remap_descriptor_to_intermediary(desc: str, named_to_inter: Dict[str, str]) -> str:
    """将描述符中的 named 类名替换为 intermediary 类名。"""

    def repl(match: re.Match[str]) -> str:
        named = match.group(1)
        inter = named_to_inter.get(named, named)
        return f"L{inter};"

    return re.sub(r"L([^;]+);", repl, desc)


def download_yarn_jar(mc_version: str, yarn_build: str, dest: Path) -> None:
    coord = f"{mc_version}+{yarn_build}"
    enc = quote(coord, safe="")
    url = f"https://maven.fabricmc.net/net/fabricmc/yarn/{enc}/yarn-{coord}-v2.jar"
    dest.parent.mkdir(parents=True, exist_ok=True)
    print(f"下载 Yarn 映射: {url}", file=sys.stderr)
    with urlopen(url) as resp:
        dest.write_bytes(resp.read())


def read_gradle_yarn(gradle_props: Path) -> Tuple[str, str]:
    text = gradle_props.read_text(encoding="utf-8", errors="replace")
    mc = re.search(r"^minecraft_version=(.+)$", text, re.MULTILINE)
    ym = re.search(r"^yarn_mappings=(.+)$", text, re.MULTILINE)
    if not mc or not ym:
        raise SystemExit(f"无法从 {gradle_props} 解析 minecraft_version / yarn_mappings")
    return mc.group(1).strip(), ym.group(1).strip()


def find_minecraft_unpicked_jar(mc_version: str, yarn_coord: str) -> Optional[Path]:
    """定位 Loom 缓存中的 minecraft-unpicked.jar（Yarn 命名字节码）。"""
    gradle_home = Path(os.environ.get("GRADLE_USER_HOME", Path.home() / ".gradle"))
    yarn_folder = yarn_coord.replace(".", "_").replace("+", ".")
    candidates = [
        gradle_home / "caches" / "fabric-loom" / mc_version / f"net.fabricmc.yarn.1_20_1.{yarn_coord}-v2",
        gradle_home / "caches" / "fabric-loom" / mc_version / f"net.fabricmc.yarn.{mc_version.replace('.', '_')}.{yarn_coord}-v2",
    ]
    for base in candidates:
        jar = base / "minecraft-unpicked.jar"
        if jar.is_file():
            return jar
    loom_dir = gradle_home / "caches" / "fabric-loom" / mc_version
    if loom_dir.is_dir():
        for jar in loom_dir.rglob("minecraft-unpicked.jar"):
            return jar
    return None


# --- Java 源文件扫描 ---

MIXIN_CLASS_BLOCK = re.compile(r"@Mixin\s*\(\s*value\s*=\s*\{([^}]+)\}\s*\)")
MIXIN_CLASS_BLOCK2 = re.compile(r"@Mixin\s*\(\s*\{([^}]+)\}\s*\)")
MIXIN_SINGLE = re.compile(r"@Mixin\s*\(\s*value\s*=\s*([A-Za-z_][\w.]*)\s*\.class\s*\)")
MIXIN_SINGLE2 = re.compile(r"@Mixin\s*\(\s*([A-Za-z_][\w.]*)\s*\.class\s*\)")

IMPORT_RE = re.compile(r"^import\s+([\w.]+)\s*;", re.MULTILINE)

ACCESSOR_RE = re.compile(
    r"@Accessor(?:\(\s*(?:value\s*=\s*)?[\"']([^\"']+)[\"']\s*\))?",
    re.MULTILINE,
)
INVOKER_RE = re.compile(
    r"@Invoker(?:\(\s*(?:value\s*=\s*)?[\"']([^\"']+)[\"']\s*\))?",
    re.MULTILINE,
)
INJECT_METHOD_RE = re.compile(
    r"method\s*=\s*\{([^}]+)\}",
    re.MULTILINE,
)
REDIRECT_METHOD_RE = re.compile(
    r"@Redirect\s*\(\s*method\s*=\s*\{([^}]+)\}",
    re.MULTILINE,
)
REDIRECT_TARGET_RE = re.compile(
    r"target\s*=\s*\"([^\"]+)\"",
    re.MULTILINE,
)
HANDLER_SIG_RE = re.compile(
    r"^(?:public|protected|private)?\s*(?:static\s+)?(?:final\s+)?[\w.<>,\s\[\]?]+\s+(\w+)\s*\(([^)]*)\)",
)

PRIMITIVE_DESC = {
    "void": "V",
    "boolean": "Z",
    "byte": "B",
    "char": "C",
    "short": "S",
    "int": "I",
    "long": "J",
    "float": "F",
    "double": "D",
}

JAVA_LANG_TYPES = {
    "String": "Ljava/lang/String;",
    "Object": "Ljava/lang/Object;",
    "Integer": "Ljava/lang/Integer;",
    "Boolean": "Ljava/lang/Boolean;",
}

SKIP_CALLBACK_TYPES = {
    "CallbackInfo",
    "CallbackInfoReturnable",
    "Cancellable",
}

INHERITED_METHOD_SOURCES: Dict[str, List[str]] = {
    "net/minecraft/entity/LivingEntity": ["net/minecraft/entity/Entity"],
    "net/minecraft/server/network/ServerPlayerEntity": ["net/minecraft/entity/Entity"],
    "net/minecraft/block/LeavesBlock": ["net/minecraft/block/Block"],
    "net/minecraft/block/IceBlock": ["net/minecraft/block/AbstractBlock"],
    "net/minecraft/block/VineBlock": ["net/minecraft/block/AbstractBlock"],
    "net/minecraft/client/render/entity/model/AnimalModel": [
        "net/minecraft/client/render/entity/model/EntityModel",
        "net/minecraft/client/model/Model",
    ],
    "net/minecraft/client/render/entity/model/BipedEntityModel": [
        "net/minecraft/client/render/entity/model/EntityModel",
        "net/minecraft/client/model/Model",
    ],
    "net/minecraft/world/gen/chunk/NoiseChunkGenerator": ["net/minecraft/world/gen/chunk/ChunkGenerator"],
    "net/minecraft/client/gui/screen/ingame/HandledScreen": [
        "net/minecraft/client/gui/screen/Screen",
        "net/minecraft/client/gui/ParentElement",
        "net/minecraft/client/gui/Element",
    ],
    "net/minecraft/client/network/ClientPlayNetworkHandler": [
        "net/minecraft/network/listener/ClientPlayPacketListener",
    ],
    "net/minecraft/block/entity/BlockEntity": [],
}


@dataclass
class AuditIssue:
    level: str  # error | warn | info
    message: str


@dataclass
class AuditReport:
    issues: List[AuditIssue] = field(default_factory=list)

    def add(self, level: str, message: str) -> None:
        self.issues.append(AuditIssue(level, message))

    @property
    def errors(self) -> List[AuditIssue]:
        return [i for i in self.issues if i.level == "error"]

    @property
    def warnings(self) -> List[AuditIssue]:
        return [i for i in self.issues if i.level == "warn"]


def slash_name(binary: str) -> str:
    return binary.replace(".", "/")


def dot_name(binary: str) -> str:
    return binary.replace("/", ".")


def extract_mixin_targets(text: str, package: str, imports: Dict[str, str]) -> List[str]:
    targets: List[str] = []
    blocks: List[str] = []
    for rx in (MIXIN_CLASS_BLOCK, MIXIN_CLASS_BLOCK2):
        for m in rx.finditer(text):
            blocks.append(m.group(1))
    for inner in blocks:
        for part in inner.split(","):
            part = part.strip()
            mm = re.match(r"([A-Za-z_][\w.]*)\s*\.class", part)
            if not mm:
                continue
            simple = mm.group(1).split(".")[-1]
            fq = resolve_simple_name(imports, simple) or (f"{package}.{simple}" if package else None)
            if fq:
                targets.append(slash_name(fq))
    for rx in (MIXIN_SINGLE, MIXIN_SINGLE2):
        for m in rx.finditer(text):
            simple = m.group(1).split(".")[-1]
            fq = resolve_simple_name(imports, simple) or (f"{package}.{simple}" if package else None)
            if fq:
                targets.append(slash_name(fq))
    return list(dict.fromkeys(targets))


def resolve_simple_name(imports: Dict[str, str], simple: str) -> Optional[str]:
    return imports.get(simple)


def infer_accessor_target(method_sig_line: str) -> Optional[str]:
    m = re.search(r"^\s*(?:public\s+)?(?:static\s+)?([\w.]+(?:<[^>]+>)?)\s+(\w+)\s*\(", method_sig_line)
    if not m:
        return None
    name = m.group(2)
    if name.startswith("get") and len(name) > 3:
        return name[3].lower() + name[4:]
    if name.startswith("is") and len(name) > 2 and name[2].isupper():
        return name[2].lower() + name[3:]
    if name.startswith("set") and len(name) > 3:
        return name[3].lower() + name[4:]
    return name


def infer_invoker_target(method_name: str) -> str:
    if method_name.startswith("call") and len(method_name) > 4:
        rest = method_name[4:]
        return rest[0].lower() + rest[1:] if rest else method_name
    return method_name


def extract_method_names_from_annotation(raw: str) -> List[str]:
    names: List[str] = []
    for part in raw.split(","):
        part = part.strip().strip('"').strip("'")
        if not part:
            continue
        if "(" in part:
            part = part[: part.index("(")]
        if part.endswith("*"):
            part = part[:-1]
        if part.startswith("<init>"):
            continue
        if part:
            names.append(part)
    return names


def extract_method_descriptor_from_annotation(raw: str) -> Optional[str]:
    """从 method={"foo(L...;)V"} 提取完整描述符；简单名返回 None。"""
    for part in raw.split(","):
        part = part.strip().strip('"').strip("'")
        if "(" in part and ")" in part:
            start = part.index("(")
            return part[start:]
    return None


def java_type_to_desc(
    type_str: str,
    imports: Dict[str, str],
    package: str,
    named_to_inter: Dict[str, str],
) -> str:
    type_str = type_str.strip()
    if not type_str:
        return ""
    while type_str.startswith("@"):
        type_str = type_str.split(None, 1)[-1].strip()
    array_dim = 0
    while type_str.endswith("[]"):
        array_dim += 1
        type_str = type_str[:-2].strip()
    base = type_str.split("<", 1)[0].strip()
    if base in PRIMITIVE_DESC:
        desc = PRIMITIVE_DESC[base]
    elif base in JAVA_LANG_TYPES:
        desc = JAVA_LANG_TYPES[base]
    elif base in ("T",) or (len(base) == 1 and base.isupper()):
        # Mixin 泛型参数 — 无法静态解析，用 Entity 占位（仅用于 setAngles 等）
        desc = f"L{named_to_inter.get('net/minecraft/entity/Entity', 'net/minecraft/class_1297')};"
    elif "." in base:
        named = slash_name(base)
        inter = named_to_inter.get(named, named)
        desc = f"L{inter};"
    else:
        fq = imports.get(base) or (f"{package}.{base}" if package else base)
        named = slash_name(fq)
        inter = named_to_inter.get(named, named)
        desc = f"L{inter};"
    return ("[" * array_dim) + desc


def split_java_params(params: str) -> List[str]:
    if not params.strip():
        return []
    parts: List[str] = []
    depth = 0
    current: List[str] = []
    for ch in params:
        if ch == "<":
            depth += 1
        elif ch == ">":
            depth -= 1
        elif ch == "," and depth == 0:
            parts.append("".join(current).strip())
            current = []
            continue
        current.append(ch)
    tail = "".join(current).strip()
    if tail:
        parts.append(tail)
    return parts


def find_handler_after(lines: List[str], start: int) -> Optional[Tuple[str, str]]:
    """返回 (handler_name, params_str)。"""
    for j in range(start + 1, min(start + 12, len(lines))):
        line = lines[j].strip()
        if not line or line.startswith("@"):
            continue
        if line.startswith("}"):
            continue
        m = HANDLER_SIG_RE.search(line)
        if m:
            return m.group(1), m.group(2)
    return None


@dataclass
class InferredSignature:
    param_desc: str  # intermediary 形式，如 (Lnet/minecraft/class_1268;Z)
    match_prefix_only: bool = True  # @Inject + CallbackInfo* 只比对参数前缀
    cir_generic: Optional[str] = None


def infer_inject_signature(
    handler_params: str,
    imports: Dict[str, str],
    package: str,
    explicit_desc: Optional[str],
    named_to_inter: Dict[str, str],
) -> Optional[InferredSignature]:
    if explicit_desc:
        p_end = explicit_desc.rfind(")")
        if p_end < 0:
            return None
        param_desc = remap_descriptor_to_intermediary(explicit_desc[: p_end + 1], named_to_inter)
        return InferredSignature(param_desc=param_desc, match_prefix_only=False)

    params = split_java_params(handler_params)
    desc_parts: List[str] = []
    cir_generic: Optional[str] = None
    for p in params:
        tokens = p.strip().rsplit(" ", 1)
        if len(tokens) < 2:
            continue
        type_name = tokens[0].strip()
        simple = type_name.split(".")[-1].split("<")[0]
        if simple == "CallbackInfoReturnable":
            gm = re.search(r"CallbackInfoReturnable<([^>]+)>", type_name)
            if gm:
                cir_generic = gm.group(1).strip()
            continue
        if simple in SKIP_CALLBACK_TYPES:
            continue
        desc_parts.append(java_type_to_desc(type_name, imports, package, named_to_inter))

    param_desc = "(" + "".join(desc_parts) + ")"
    if cir_generic:
        return InferredSignature(param_desc=param_desc, match_prefix_only=True, cir_generic=cir_generic)
    return InferredSignature(param_desc=param_desc, match_prefix_only=True)


def lookup_method_descs(
    cls: str,
    method_name: str,
    class_method_descs: MethodDescIndex,
) -> Set[str]:
    found: Set[str] = set()
    descs = class_method_descs.get(cls, {}).get(method_name)
    if descs:
        found |= descs
    for parent in INHERITED_METHOD_SOURCES.get(cls, []):
        parent_descs = class_method_descs.get(parent, {}).get(method_name)
        if parent_descs:
            found |= parent_descs
    return found


def method_available_on_class(
    cls: str,
    method_name: str,
    class_methods: Dict[str, Set[str]],
) -> bool:
    if class_methods.get(cls, set()) and method_name in class_methods[cls]:
        return True
    for parent in INHERITED_METHOD_SOURCES.get(cls, []):
        if method_name in class_methods.get(parent, set()):
            return True
    return False


def check_descriptor_match(
    cls: str,
    method_name: str,
    sig: InferredSignature,
    class_method_descs: MethodDescIndex,
) -> Tuple[str, Set[str]]:
    """
    返回 (status, matching_descs)
    status: exact | inherited | ambiguous | missing
    """
    def matches(desc: str) -> bool:
        if not desc.startswith(sig.param_desc):
            return False
        if sig.match_prefix_only:
            return len(desc) > len(sig.param_desc)
        return desc == sig.param_desc + "V"

    def find_in(class_name: str) -> Set[str]:
        return {d for d in class_method_descs.get(class_name, {}).get(method_name, set()) if matches(d)}

    direct = find_in(cls)
    if direct:
        return "exact", direct

    inherited_hits: Set[str] = set()
    for parent in INHERITED_METHOD_SOURCES.get(cls, []):
        inherited_hits |= find_in(parent)
    if inherited_hits:
        return "inherited", inherited_hits

    all_descs = lookup_method_descs(cls, method_name, class_method_descs)
    if not all_descs:
        return "missing", set()
    hits = {d for d in all_descs if matches(d)}
    if hits:
        return "exact", hits
    if sig.cir_generic:
        prefix_hits = {d for d in all_descs if d.startswith(sig.param_desc)}
        if len(prefix_hits) == 1:
            return "exact", prefix_hits
        if prefix_hits:
            return "ambiguous", prefix_hits
    return "ambiguous", all_descs


def parse_redirect_target(target: str) -> Tuple[str, str, str]:
    """
    Lnet/minecraft/SharedConstants;stripInvalidChars(Ljava/lang/String;)Ljava/lang/String;
    -> owner slash, method, desc (with parens)
    """
    owner, rest = target.split(";", 1)
    owner = owner.lstrip("L")
    method, desc = rest.split("(", 1)
    return owner, method, "(" + desc


def javap_method_body(mc_jar: Path, class_dot: str, method_name: str) -> Optional[str]:
    try:
        proc = subprocess.run(
            ["javap", "-classpath", str(mc_jar), "-c", "-p", class_dot],
            capture_output=True,
            text=True,
            encoding="utf-8",
            errors="replace",
            timeout=60,
        )
    except (FileNotFoundError, subprocess.TimeoutExpired):
        return None
    if proc.returncode != 0:
        return None
    text = proc.stdout
    pattern = re.compile(
        rf"(?:public|protected|private).*{re.escape(method_name)}\([^;]*\);",
        re.MULTILINE,
    )
    m = pattern.search(text)
    if not m:
        return None
    start = m.start()
    next_method = re.search(r"\n  (?:public|protected|private)", text[m.end() :])
    end = m.end() + (next_method.start() if next_method else len(text) - m.end())
    return text[start:end]


def redirect_anchor_exists(
    mc_jar: Path,
    host_class: str,
    host_method: str,
    target: str,
    class_method_descs: MethodDescIndex,
) -> Tuple[bool, str]:
    owner, callee, callee_desc = parse_redirect_target(target)
    body = javap_method_body(mc_jar, dot_name(host_class), host_method)
    if body is None:
        return False, "无法 javap 宿主方法（需先 gradlew genSources 并由 Loom 生成 minecraft-unpicked.jar）"

    # 匹配 invokevirtual/interface invokestatic 等
    needle_simple = f"// Method {dot_name(owner)}.{callee}:"
    if needle_simple in body:
        return True, "javap 命中 INVOKE"

    # 描述符可能以 () 形式出现在注释
    if callee in body and callee_desc.split(")")[0].replace("(", "") in body.replace(" ", ""):
        return True, "javap 模糊命中"

    return False, f"宿主 {host_method} 字节码中未找到对 {dot_name(owner)}.{callee}{callee_desc} 的调用"


def check_method_exists(
    path: Path,
    line_no: int,
    kind: str,
    method_name: str,
    mixin_targets: List[str],
    class_methods: Dict[str, Set[str]],
) -> List[AuditIssue]:
    issues: List[AuditIssue] = []
    for cls in mixin_targets:
        methods = class_methods.get(cls)
        if methods is None and cls not in INHERITED_METHOD_SOURCES:
            issues.append(AuditIssue("error", f"{path}:{line_no}: 映射中无类 {cls} (检查 import / @Mixin)"))
            continue
        if method_available_on_class(cls, method_name, class_methods):
            continue
        near = sorted(
            x for x in (methods or set()) if method_name.lower() in x.lower() or x.lower() in method_name.lower()
        )[:8]
        hint = f"  候选(模糊): {near}" if near else f"  该类部分方法: {sorted(list(methods or []))[:12]}..."
        issues.append(
            AuditIssue(
                "error",
                f"{path}:{line_no}: 类 {dot_name(cls)} 无方法 `{method_name}` ({kind})\n{hint}",
            )
        )
    return issues


def check_inject_descriptor(
    path: Path,
    line_no: int,
    method_name: str,
    sig: Optional[InferredSignature],
    mixin_targets: List[str],
    class_method_descs: MethodDescIndex,
) -> List[AuditIssue]:
    if not sig:
        return [
            AuditIssue("warn", f"{path}:{line_no}: Inject `{method_name}` 无法推断描述符，跳过描述符级检查")
        ]
    issues: List[AuditIssue] = []
    for cls in mixin_targets:
        status, matched = check_descriptor_match(cls, method_name, sig, class_method_descs)
        if status in ("exact", "inherited"):
            label = "精确匹配" if status == "exact" else "继承匹配"
            desc_show = sorted(matched)[0] if matched else sig.param_desc
            issues.append(
                AuditIssue("info", f"{path}:{line_no}: [{label}] {dot_name(cls)}.{method_name}{desc_show}")
            )
        elif status == "missing":
            issues.append(
                AuditIssue(
                    "error",
                    f"{path}:{line_no}: 描述符不匹配 — {dot_name(cls)} 无方法 `{method_name}{sig.param_desc}...`",
                )
            )
        else:
            issues.append(
                AuditIssue(
                    "error",
                    f"{path}:{line_no}: 描述符歧义 — {dot_name(cls)}.{method_name}{sig.param_desc}\n"
                    f"  多个重载均匹配参数前缀: {sorted(matched)}\n"
                    f"  请在 @Inject method 中写完整描述符，例如 method={{\"{method_name}{sorted(matched)[0]}\"}}",
                )
            )
    return issues


def check_ambiguous_overload(
    path: Path,
    line_no: int,
    method_name: str,
    raw_methods: str,
    sig: Optional[InferredSignature],
    mixin_targets: List[str],
    class_method_descs: MethodDescIndex,
) -> List[AuditIssue]:
    """注解仅写方法名且存在多重载时警告（Mixin 可能绑错）。"""
    if extract_method_descriptor_from_annotation(raw_methods) or not sig:
        return []
    issues: List[AuditIssue] = []
    for cls in mixin_targets:
        all_descs = lookup_method_descs(cls, method_name, class_method_descs)
        prefix_hits = {d for d in all_descs if d.startswith(sig.param_desc) and len(d) > len(sig.param_desc)}
        if len(prefix_hits) > 1:
            issues.append(
                AuditIssue(
                    "warn",
                    f"{path}:{line_no}: 重载歧义 — {dot_name(cls)}.{method_name} 有 {len(prefix_hits)} 个匹配重载: "
                    f"{sorted(prefix_hits)}；建议写完整 method 描述符",
                )
            )
    return issues


def scan_file(
    path: Path,
    class_fields: Dict[str, Set[str]],
    class_methods: Dict[str, Set[str]],
    class_method_descs: MethodDescIndex,
    named_to_inter: Dict[str, str],
    mc_jar: Optional[Path],
    check_descriptors: bool,
    check_redirects: bool,
) -> List[AuditIssue]:
    issues: List[AuditIssue] = []
    text = path.read_text(encoding="utf-8", errors="replace")
    pkg_m = re.search(r"^\s*package\s+([\w.]+)\s*;", text, re.MULTILINE)
    package = pkg_m.group(1) if pkg_m else ""

    imports: Dict[str, str] = {}
    for im in IMPORT_RE.findall(text):
        imports[im.rsplit(".", 1)[-1]] = im

    mixin_targets = extract_mixin_targets(text, package, imports)
    if not mixin_targets:
        return issues

    lines = text.splitlines()

    for i, line in enumerate(lines):
        if "@Accessor" in line:
            am = ACCESSOR_RE.search(line)
            explicit = am.group(1) if am and am.group(1) else None
            target_name = explicit
            if not target_name:
                for j in range(i, min(i + 6, len(lines))):
                    inferred = infer_accessor_target(lines[j])
                    if inferred:
                        target_name = inferred
                        break
                if not target_name:
                    issues.append(AuditIssue("error", f"{path}:{i+1}: @Accessor 无显式名且无法从方法推断"))
                    continue
            for cls in mixin_targets:
                fields = class_fields.get(cls)
                if fields is None:
                    issues.append(AuditIssue("error", f"{path}:{i+1}: 映射中无类 {cls}"))
                    continue
                if target_name not in fields:
                    near = sorted(x for x in fields if target_name.lower() in x.lower())[:8]
                    hint = f"  候选: {near}" if near else ""
                    issues.append(
                        AuditIssue("error", f"{path}:{i+1}: 类 {dot_name(cls)} 无字段 `{target_name}` (Accessor){hint}")
                    )

        if "@Invoker" in line:
            im = INVOKER_RE.search(line)
            explicit = im.group(1) if im and im.group(1) else None
            method_name = explicit
            if not method_name:
                for j in range(i, min(i + 8, len(lines))):
                    mm = re.search(r"^\s*(?:public\s+)?[\w.<>,\s]+\s+(\w+)\s*\(", lines[j])
                    if mm:
                        method_name = infer_invoker_target(mm.group(1))
                        break
            if method_name:
                issues.extend(
                    check_method_exists(path, i + 1, "Invoker", method_name, mixin_targets, class_methods)
                )

        if "@Inject" in line and "method" in line:
            im = INJECT_METHOD_RE.search(line)
            if im:
                raw_methods = im.group(1)
                explicit_desc = extract_method_descriptor_from_annotation(raw_methods)
                handler = find_handler_after(lines, i)
                inferred_sig = None
                if handler and check_descriptors:
                    _, params = handler
                    inferred_sig = infer_inject_signature(
                        params, imports, package, explicit_desc, named_to_inter
                    )
                for method_name in extract_method_names_from_annotation(raw_methods):
                    issues.extend(
                        check_method_exists(path, i + 1, "Inject", method_name, mixin_targets, class_methods)
                    )
                    if check_descriptors:
                        issues.extend(
                            check_ambiguous_overload(
                                path, i + 1, method_name, raw_methods, inferred_sig, mixin_targets, class_method_descs
                            )
                        )
                        issues.extend(
                            check_inject_descriptor(
                                path, i + 1, method_name, inferred_sig, mixin_targets, class_method_descs
                            )
                        )

        if "@Redirect" in line:
            rm = REDIRECT_METHOD_RE.search(line)
            tm = REDIRECT_TARGET_RE.search(line)
            if rm:
                raw_methods = rm.group(1)
                for method_name in extract_method_names_from_annotation(raw_methods):
                    issues.extend(
                        check_method_exists(path, i + 1, "Redirect", method_name, mixin_targets, class_methods)
                    )
                    if check_redirects and tm and mc_jar:
                        target = tm.group(1)
                        for cls in mixin_targets:
                            ok, detail = redirect_anchor_exists(
                                mc_jar, cls, method_name, target, class_method_descs
                            )
                            if ok:
                                issues.append(
                                    AuditIssue(
                                        "info",
                                        f"{path}:{i+1}: [Redirect锚点] {dot_name(cls)}.{method_name} -> {detail}",
                                    )
                                )
                            else:
                                issues.append(
                                    AuditIssue(
                                        "error",
                                        f"{path}:{i+1}: [Redirect锚点缺失] {dot_name(cls)}.{method_name}: {detail}",
                                    )
                                )

    return issues


def main() -> int:
    ap = argparse.ArgumentParser(description="审计 Mixin 与 Yarn 命名及描述符一致性")
    ap.add_argument("--project-root", type=Path, default=Path(__file__).resolve().parent.parent)
    ap.add_argument("--mixin-dir", type=Path, default=None)
    ap.add_argument("--yarn", type=str, default=None)
    ap.add_argument("--no-descriptors", action="store_true", help="跳过描述符级检查")
    ap.add_argument("--no-redirect-check", action="store_true", help="跳过 @Redirect javap 锚点检查")
    ap.add_argument("--mc-jar", type=Path, default=None, help="minecraft-unpicked.jar 路径")

    args = ap.parse_args()
    project_root = args.project_root
    gradle_props = project_root / "gradle.properties"
    mc_ver, yarn_coord = read_gradle_yarn(gradle_props)
    if args.yarn:
        yarn_coord = args.yarn.strip()

    mixin_dir = args.mixin_dir or (project_root / "src/main/java/noppes/npcs/mixin")
    if not mixin_dir.is_dir():
        raise SystemExit(f"目录不存在: {mixin_dir}")

    cache_dir = project_root / "tools" / ".yarn-cache"
    jar_path = cache_dir / f"yarn-{yarn_coord}-v2.jar"
    if not jar_path.is_file():
        download_yarn_jar(mc_ver, yarn_coord.split("+", 1)[1], jar_path)

    tiny = load_yarn_tiny_from_jar(jar_path)
    class_fields, class_methods, class_method_descs, named_to_inter = parse_tiny_v2(tiny)

    mc_jar = args.mc_jar
    if not mc_jar:
        mc_jar = find_minecraft_unpicked_jar(mc_ver, yarn_coord)
    check_descriptors = not args.no_descriptors
    check_redirects = check_descriptors and not args.no_redirect_check

    all_issues: List[AuditIssue] = []
    for java in sorted(mixin_dir.glob("*.java")):
        all_issues.extend(
            scan_file(
                java,
                class_fields,
                class_methods,
                class_method_descs,
                named_to_inter,
                mc_jar,
                check_descriptors,
                check_redirects,
            )
        )

    errors = [i for i in all_issues if i.level == "error"]
    warnings = [i for i in all_issues if i.level == "warn"]
    infos = [i for i in all_issues if i.level == "info"]

    if not errors and not warnings:
        print(f"未发现问题（描述符检查: {'开' if check_descriptors else '关'}，Redirect锚点: {'开' if check_redirects else '关'}）。")
        if infos:
            print(f"\n{len(infos)} 条描述符/锚点确认（info）。")
        return 0

    if errors:
        print(f"错误 {len(errors)} 条:\n")
        for item in errors:
            print(item.message)
            print()
    if warnings:
        print(f"警告 {len(warnings)} 条:\n")
        for item in warnings:
            print(item.message)
            print()
    if infos and not errors:
        print(f"信息 {len(infos)} 条（仅确认，无 error）。")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())
