# Mixin 代码链验证报告

> 生成依据：`gradlew genSources` + `minecraft-unpicked.jar` 的 `javap -c` 字节码对照 + `tools/mixin_accessor_audit.py` 描述符级审计。

## P0 已修复

| 文件 | 问题 | 修复 |
|------|------|------|
| `ModelRendererMixin` | `translate(MatrixStack)` 在 1.20.1 不存在 | 改为 `rotate(MatrixStack)` HEAD/TAIL，等价原 `translateAndRotate` |
| `EntityLivingMixin` | 仅注入 `swingHand(Hand)`，双参重载可绕过 | 改为 `swingHand(Lnet/minecraft/util/Hand;Z)V` 完整描述符（单参版本会委托至此） |

## P1 调用链核对

### PackRepositoryMixin → `ResourcePackManager.createResourcePacks`

- **字节码**：`MinecraftClient` 在资源重载路径调用 `ResourcePackManager.createResourcePacks()`。
- **结论**：TAIL 注入可正确追加 `cnpcs` 目录资源包（替代旧版 `openAllSelected` 的等价入口）。

### MixinEditBox → `TextFieldWidget.write` + `stripInvalidChars`

- **字节码**：`write(String)` 内部 `invokestatic SharedConstants.stripInvalidChars`。
- **输入路径**：`insertText` / `charTyped` / 粘贴等均 `invokevirtual write(String)`。
- **结论**：`@Redirect` 锚点有效；`GuiTextFieldNop` 免过滤逻辑覆盖全部文本写入路径。

### LeavesBlockMixin → `hasRandomTicks`

- **字节码**：`LeavesBlock` 覆写 `hasRandomTicks(BlockState)`，返回是否参与随机刻。
- **结论**：关闭 `LeavesDecayEnabled` 时返回 `false` 可阻止树叶腐烂随机刻（语义等价旧版 `isRandomlyTicking`）。

## 静态审计

```bash
py -3 tools/mixin_accessor_audit.py
```

- 名称 + **intermediary 描述符** 级匹配：0 error
- `@Redirect` javap 锚点：`MixinEditBox` 命中

## 游戏烟测矩阵（需人工勾选）

| 功能 | 相关 Mixin / 代码 | 步骤 | 通过 |
|------|-------------------|------|------|
| 玩家模型部位缩放 | `ModelRendererMixin` | 调整 NPC/玩家模型配置，进第三人称观察肢体比例 | [ ] |
| 自定义资源包 | `PackRepositoryMixin` | F3+T 重载资源，确认 `customnpcs/` 纹理仍加载 | [ ] |
| NPC 染色 | `AgeableModelMixin` | 放置 tint 非白的 NPC | [ ] |
| 传送器挥动 | `EntityLivingMixin` | 主手持有传送器右键挥动 | [ ] |
| 跨维传送 | `MixinServerPlayer` | 持传送器跨维度 | [ ] |
| 命令拦截 | `MixinCommands` | `/give` 等受脚本控制的命令 | [ ] |
| 实体 Mark NBT | `EntityPersistentData` | 下线重进后 Mark 仍在 | [ ] |
| 方块脚本 NBT | `BlockEntityPersistentData` | 脚本方块数据重启后保留 | [ ] |
| 任务持久化 | `PlayerData` / `MixinPlayerDataStorage` | 接取任务 → 下线 → 重进进度仍在 | [ ] |
| 树叶腐烂开关 | `LeavesBlockMixin` | `LeavesDecayEnabled=false` 时树叶不腐 | [ ] |
| 文本框免过滤 | `MixinEditBox` | 脚本 GUI 文本框可输入特殊字符 | [ ] |

启动时确认日志 **无** `Mixin apply failed` / `Critical injection failure`。

## 自动化烟测（2026-06-07）

| 检查项 | 结果 |
|--------|------|
| `py -3 tools/mixin_accessor_audit.py` | 通过（0 error） |
| `gradlew clean build` | 通过，0 `Cannot remap` |
| `gradlew runClient` | 通过，集成服务器启动、玩家进世界、无 Mixin 注入失败 |
| 上表功能矩阵（渲染/传送等） | 需人工进游戏勾选 |
