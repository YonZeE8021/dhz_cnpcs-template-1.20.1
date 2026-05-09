# 本仓库 AI / 代理协作提示

完整技术细节、版本锚点、Mixin 清单、排障表与发布流程见：

**[docs/AI_DEVELOPMENT.md](docs/AI_DEVELOPMENT.md)**

## 三条硬规则

1. **最小改动**：只改完成任务所需的文件与行；禁止无关重构或大面积格式化 CFR 反编译代码。
2. **勿破坏存档 ID**：不要随意修改 `Registry.register(..., "customnpcs:...", ...)` 等注册名；新增 Mixin 必须同步 `src/main/resources/customnpcs.mixins.json`。
3. **验证与发布**：改完后至少 `compileJava`；客户端相关需 `runClient`；发给玩家/启动器的必须是 `./gradlew build` 生成的 **`build/libs/`** 下 **remap 后的** jar，勿使用未 remap 的 classes。
