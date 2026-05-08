#!/usr/bin/env python3
"""
将源码中 Minecraft 类的 intermediary 字段名 (field_xxxxx) 替换为 Yarn 名。
依赖 Fabric Yarn mappings.tiny（intermediary -> named）。
"""
from __future__ import annotations

import re
import sys
import urllib.request
from pathlib import Path

YARN_URL = "https://maven.fabricmc.net/net/fabricmc/yarn/1.20.1+build.10/yarn-1.20.1+build.10-v2.jar"


def load_or_fetch_tiny(dest: Path) -> Path:
    tiny = dest / "mappings.tiny"
    if tiny.is_file():
        return tiny
    dest.mkdir(parents=True, exist_ok=True)
    jar = dest / "yarn.jar"
    print(f"Downloading {YARN_URL}", file=sys.stderr)
    urllib.request.urlretrieve(YARN_URL, jar)
    import zipfile

    with zipfile.ZipFile(jar, "r") as z:
        z.extract("mappings/mappings.tiny", dest)
    return dest / "mappings" / "mappings.tiny"


def parse_tiny(tiny_path: Path):
    classes_n2i: dict[str, str] = {}
    fields: dict[tuple[str, str], str] = {}
    current_i: str | None = None

    with tiny_path.open(encoding="utf-8") as f:
        for line in f:
            parts = [p for p in line.rstrip("\n").split("\t") if p != ""]
            if not parts:
                continue
            kind = parts[0]
            if kind == "c" and len(parts) >= 3:
                current_i = parts[1]
                named = parts[2]
                classes_n2i[named] = current_i
            elif kind == "f" and len(parts) >= 4 and current_i:
                finter, fnamed = parts[2], parts[3]
                fields[(current_i, finter)] = fnamed
    return classes_n2i, fields


def pkg_dots_to_slash(pkg: str) -> str:
    return pkg.replace(".", "/")


def resolve_owner_slash(expr: str, imports: dict[str, str], pkg_dots: str | None, classes_n2i: dict[str, str]) -> str | None:
    """expr 如 EntityAttributes 或 VertexFormat.DrawMode"""
    parts = expr.split(".")
    if len(parts) == 1:
        simp = parts[0]
        if simp in imports:
            return imports[simp]
        if pkg_dots:
            cand = pkg_dots_to_slash(pkg_dots) + "/" + simp
            if cand in classes_n2i:
                return cand
        return None
    outer = parts[0]
    if outer not in imports:
        return None
    base = imports[outer]
    for inner in parts[1:]:
        base = base + "$" + inner
    if base in classes_n2i:
        return base
    return None


def process_java(path: Path, classes_n2i: dict[str, str], fields: dict[tuple[str, str], str]) -> bool:
    text = path.read_text(encoding="utf-8")
    pkg_m = re.search(r"^package\s+([\w.]+);", text, re.MULTILINE)
    pkg_dots = pkg_m.group(1) if pkg_m else None

    imports: dict[str, str] = {}
    for m in re.finditer(r"^import\s+([\w.$]+);", text, re.MULTILINE):
        full = m.group(1)
        if full.endswith(".*"):
            continue
        simp = full.split(".")[-1]
        imports[simp] = full.replace(".", "/")

    changed = False

    def repl(m: re.Match) -> str:
        nonlocal changed
        chain = m.group(1)
        finter = m.group(2)
        owner_named = resolve_owner_slash(chain, imports, pkg_dots, classes_n2i)
        if not owner_named:
            return m.group(0)
        owner_inter = classes_n2i.get(owner_named)
        if not owner_inter:
            return m.group(0)
        fname = fields.get((owner_inter, finter))
        if not fname:
            return m.group(0)
        changed = True
        return f"{chain}.{fname}"

    # 区分大小写的标识符链 + field_
    # 类名以大写字母开头，避免匹配 npc.field_ 等变量
    new_text = re.sub(
        r"\b((?:[A-Z][\w]*)(?:\.[A-Za-z_][\w]*)*)\.(field_\d+)\b",
        repl,
        text,
    )
    if changed:
        path.write_text(new_text, encoding="utf-8")
    return changed


def main():
    root = Path(__file__).resolve().parents[1]
    src = root / "src" / "main" / "java"
    cache = root / "build" / "yarnify-cache"
    tiny = load_or_fetch_tiny(cache)
    classes_n2i, fields = parse_tiny(tiny)
    n = 0
    for path in sorted(src.rglob("*.java")):
        if process_java(path, classes_n2i, fields):
            print(path.relative_to(root))
            n += 1
    print(f"Updated {n} files.", file=sys.stderr)


if __name__ == "__main__":
    main()
