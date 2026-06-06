#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
校验 Mixin 中的 @Accessor / @Invoker / @Inject / @Redirect 目标名是否与当前 Yarn 映射一致。

依据：Fabric Maven 上的 yarn-<mc+build>-v2.jar 内 mappings/mappings.tiny

用法（在项目根目录）:
  py -3 tools/mixin_accessor_audit.py
  py -3 tools/mixin_accessor_audit.py --mixin-dir src/main/java/noppes/npcs/mixin
  py -3 tools/mixin_accessor_audit.py --yarn 1.20.1+build.10

说明:
  - 检查「声明为 @Mixin 的目标类」上是否**直接**存在该字段/方法名；
    继承自父类的方法在 Tiny 里不在子类行下，可能产生误报，需人工看 Yarn 文档。
  - @Inject/@Redirect 的 method 支持简单名与带描述符形式；通配符 `*` 会截断到 `(` 前。
  - 自动下载的映射会缓存在 tools/.yarn-cache/
"""

from __future__ import annotations

import argparse
import re
import sys
import zipfile
from pathlib import Path
from typing import Dict, List, Optional, Set, Tuple
from urllib.parse import quote
from urllib.request import urlopen

# --- Tiny v2 (Fabric Yarn v2 jar) 极简解析 ---

def load_yarn_tiny_from_jar(jar_path: Path) -> str:
    with zipfile.ZipFile(jar_path, "r") as z:
        return z.read("mappings/mappings.tiny").decode("utf-8")


def parse_tiny_v2(tiny_text: str) -> Tuple[Dict[str, Set[str]], Dict[str, Set[str]]]:
    """
    返回:
      class_fields: named_internal_class -> set of named field names
      class_methods: named_internal_class -> set of named method names (不含 <init>/<clinit> 可过滤)
    """
    class_fields: Dict[str, Set[str]] = {}
    class_methods: Dict[str, Set[str]] = {}
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
            current = parts[2]  # named class, slash form
            class_fields.setdefault(current, set())
            class_methods.setdefault(current, set())
            continue

        if not current:
            continue

        # 子行: 以 tab 开头，split 后 parts[0] == ''
        if head != "" or len(parts) < 2:
            continue
        kind = parts[1]

        if kind == "f" and len(parts) >= 5:
            # \tf\t<desc>\t<inter_field>\t<named_field>
            named_field = parts[4]
            class_fields[current].add(named_field)
        elif kind == "m" and len(parts) >= 5:
            named_method = parts[4]
            if named_method not in ("<init>", "<clinit>"):
                class_methods[current].add(named_method)
        # p / c 等子节点忽略

    return class_fields, class_methods


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
            fq = resolve_simple_name(imports, simple)
            if not fq and package:
                fq = f"{package}.{simple}"
            if fq:
                targets.append(slash_name(fq))
    for rx in (MIXIN_SINGLE, MIXIN_SINGLE2):
        for m in rx.finditer(text):
            simple = m.group(1).split(".")[-1]
            fq = resolve_simple_name(imports, simple)
            if not fq and package:
                fq = f"{package}.{simple}"
            if fq:
                targets.append(slash_name(fq))
    return targets


def resolve_simple_name(imports: Dict[str, str], simple: str) -> Optional[str]:
    if simple in imports:
        return imports[simple]
    # 已在同一包或 java.lang — 这里无法解析同包，交给调用方传入 package
    return None


def infer_accessor_target(method_sig_line: str) -> Optional[str]:
    """从接口方法名推断 @Accessor 无参时的目标字段名（启发式）。"""
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


def slash_name(binary: str) -> str:
    return binary.replace(".", "/")


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
}


def method_available_on_class(
    cls: str,
    method_name: str,
    class_methods: Dict[str, Set[str]],
) -> bool:
    methods = class_methods.get(cls)
    if methods and method_name in methods:
        return True
    for parent in INHERITED_METHOD_SOURCES.get(cls, []):
        parent_methods = class_methods.get(parent)
        if parent_methods and method_name in parent_methods:
            return True
    return False


def check_method_exists(
    path: Path,
    line_no: int,
    kind: str,
    method_name: str,
    mixin_targets: List[str],
    class_methods: Dict[str, Set[str]],
) -> List[str]:
    issues: List[str] = []
    for cls in mixin_targets:
        methods = class_methods.get(cls)
        if methods is None and cls not in INHERITED_METHOD_SOURCES:
            issues.append(f"{path}:{line_no}: 映射中无类 {cls} (检查 import / @Mixin)")
            continue
        if method_available_on_class(cls, method_name, class_methods):
            continue
        near = sorted(
            x for x in methods if method_name.lower() in x.lower() or x.lower() in method_name.lower()
        )[:8]
        hint = f"  候选(模糊): {near}" if near else f"  该类部分方法: {sorted(list(methods))[:12]}..."
        issues.append(
            f"{path}:{line_no}: 类 {cls.replace('/', '.')} 无方法 `{method_name}` ({kind})\n{hint}"
        )
    return issues


def scan_file(
    path: Path,
    class_fields: Dict[str, Set[str]],
    class_methods: Dict[str, Set[str]],
) -> List[str]:
    issues: List[str] = []
    text = path.read_text(encoding="utf-8", errors="replace")
    pkg_m = re.search(r"^\s*package\s+([\w.]+)\s*;", text, re.MULTILINE)
    package = pkg_m.group(1) if pkg_m else ""

    imports: Dict[str, str] = {}
    for im in IMPORT_RE.findall(text):
        simple = im.rsplit(".", 1)[-1]
        imports[simple] = im

    mixin_targets = list(dict.fromkeys(extract_mixin_targets(text, package, imports)))

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
                    issues.append(f"{path}:{i+1}: @Accessor 无显式名且无法从方法推断")
                    continue

            for cls in mixin_targets:
                fields = class_fields.get(cls)
                if fields is None:
                    issues.append(f"{path}:{i+1}: 映射中无类 {cls} (检查 import / @Mixin)")
                    continue
                if target_name not in fields:
                    near = sorted(
                        x for x in fields if target_name.lower() in x.lower() or x.lower() in target_name.lower()
                    )[:8]
                    hint = f"  候选(模糊): {near}" if near else f"  该类部分字段: {sorted(list(fields))[:12]}..."
                    issues.append(
                        f"{path}:{i+1}: 类 {cls.replace('/', '.')} 无字段 `{target_name}` (Accessor)\n{hint}"
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
            if not method_name:
                issues.append(f"{path}:{i+1}: @Invoker 无法解析方法名")
                continue

            for cls in mixin_targets:
                methods = class_methods.get(cls)
                if methods is None:
                    continue
                if method_name not in methods:
                    near = sorted(x for x in methods if method_name.lower() in x.lower() or x.lower() in method_name.lower())[
                        :8
                    ]
                    hint = f"  候选(模糊): {near}" if near else f"  该类部分方法: {sorted(list(methods))[:12]}..."
                    issues.append(
                        f"{path}:{i+1}: 类 {cls.replace('/', '.')} 无方法 `{method_name}` (Invoker)\n{hint}"
                    )

        if "@Inject" in line:
            im = INJECT_METHOD_RE.search(line)
            if im:
                for method_name in extract_method_names_from_annotation(im.group(1)):
                    issues.extend(
                        check_method_exists(path, i + 1, "Inject", method_name, mixin_targets, class_methods)
                    )

        if "@Redirect" in line:
            rm = REDIRECT_METHOD_RE.search(line)
            if rm:
                for method_name in extract_method_names_from_annotation(rm.group(1)):
                    issues.extend(
                        check_method_exists(path, i + 1, "Redirect", method_name, mixin_targets, class_methods)
                    )

    return issues


def main() -> int:
    ap = argparse.ArgumentParser(description="审计 Mixin Accessor/Invoker/Inject/Redirect 与 Yarn 命名是否一致")
    ap.add_argument(
        "--project-root",
        type=Path,
        default=Path(__file__).resolve().parent.parent,
        help="Gradle 项目根目录（默认为本脚本上级）",
    )
    ap.add_argument(
        "--mixin-dir",
        type=Path,
        default=None,
        help="只扫描此目录下 .java（默认: src/main/java/noppes/npcs/mixin）",
    )
    ap.add_argument("--yarn", type=str, default=None, help="覆盖 gradle 的 yarn_mappings，如 1.20.1+build.10")

    args = ap.parse_args()
    project_root = args.project_root
    gradle_props = project_root / "gradle.properties"
    mc_ver, yarn_coord = read_gradle_yarn(gradle_props)
    if args.yarn:
        yarn_coord = args.yarn.strip()
    if "+" not in yarn_coord:
        raise SystemExit("--yarn 格式应为 <mc版本>+<build>，例如 1.20.1+build.10")

    mixin_dir = args.mixin_dir or (project_root / "src/main/java/noppes/npcs/mixin")
    if not mixin_dir.is_dir():
        raise SystemExit(f"目录不存在: {mixin_dir}")

    cache_dir = project_root / "tools" / ".yarn-cache"
    jar_name = f"yarn-{yarn_coord}-v2.jar"
    jar_path = cache_dir / jar_name
    if not jar_path.is_file():
        download_yarn_jar(mc_ver, yarn_coord.split("+", 1)[1], jar_path)

    tiny = load_yarn_tiny_from_jar(jar_path)
    class_fields, class_methods = parse_tiny_v2(tiny)

    all_issues: List[str] = []
    for java in sorted(mixin_dir.glob("*.java")):
        all_issues.extend(scan_file(java, class_fields, class_methods))

    if not all_issues:
        print("未发现问题（在「仅检查 Mixin 目标类自身字段/方法」前提下）。")
        return 0

    print(f"共 {len(all_issues)} 条提示/问题:\n")
    for item in all_issues:
        print(item)
        print()
    return 1


if __name__ == "__main__":
    sys.exit(main())
