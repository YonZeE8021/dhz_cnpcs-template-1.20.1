# DHZ Custom NPCs（Fabric 1.20.1）AI / 维护者技术手册

> **文档用途**：供后续 AI 代理与人类开发者在本仓库内安全修改代码、排查崩溃、发布模组时使用。  
> **维护约定**：升级 Minecraft / Yarn / Loader 后，请更新本文「版本锚点」小节中的表格与日期。

**版本锚点（请随提交更新）**

| 项目 | 值 |
|------|-----|
| 文档同步日期 | （填写：`git rev-parse HEAD` 短哈希 + 日期） |
| Minecraft | `1.20.1` |
| Yarn | `1.20.1+build.10`（见 `gradle.properties`） |
| Fabric Loader | `0.19.2` |
| Fabric API（构建依赖） | `0.92.8+1.20.1` |
| Fabric Loom | `1.16-SNAPSHOT`（插件 id：`net.fabricmc.fabric-loom-remap`） |
| 模组版本 | `mod_version` → `1.0.0`（见 `gradle.properties`） |
| Maven 坐标 | `group`: `com.dahuzhou.dhz_cnpcs`，`artifact`: 与 `settings.gradle` 中 `rootProject.name` 一致 → `dhz_cnpcs` |

---

## 1. 项目定位与来源

### 1.1 是什么

本仓库是 **CustomNPCs（Fabric 版）在 Minecraft 1.20.1 上的移植与构建线**，商业/授权信息见根目录 `LICENSE` 与 `fabric.mod.json` 中的 `license`（当前为 **CC BY-NC**）。

- **Java 包根**：`noppes.npcs`（与上游 CustomNPCs 一致，便于脚本与 API 反射路径不变）。
- **代码风格**：大量类来自 **CFR 反编译**，可见顶部的 `Decompiled with CFR` 注释；新增代码应尽量匹配现有风格（命名、少废话注释、不大面积格式化）。

### 1.2 目录边界（务必分清）

| 路径 | 用途 |
|------|------|
| `src/main/java/` | **唯一权威源码**：日常修改、Mixin、注册表逻辑均在此。 |
| `src/main/resources/` | 资源、`fabric.mod.json`、Mixin JSON、Access Widener、语言包、模型等。 |
| `CustomNPCs-fabric1.20.1-decompiled-remapped/` | **参考树 / 历史对照**（反编译+映射副本）。不要把它当成运行时代码；合并或抄代码时以 `src/main` 为准。 |
| `tools/` | 本地脚本：冒烟测试、Accessor 审计、字段名 Yarn 化辅助等。 |
| `build/` | Gradle 构建输出；**不要提交**，发布物从 `build/libs` 取。 |

### 1.3 入口类（Fabric）

| 入口 | 类 | 文件 |
|------|-----|------|
| `main` | `noppes.npcs.CustomNpcs` | `src/main/java/noppes/npcs/CustomNpcs.java` |
| `client` | `noppes.npcs.CustomNpcsClient` | `src/main/java/noppes/npcs/CustomNpcsClient.java` |
| `fabric-datagen` | `com.dahuzhou.dhz_cnpcs.DHZ_CNPCsDataGenerator` | `src/main/java/com/dahuzhou/dhz_cnpcs/DHZ_CNPCsDataGenerator.java` |

模组清单：`src/main/resources/fabric.mod.json`。

---

## 2. 三重「ID」体系（最易出错）

运营、依赖声明、存档 NBT、资源路径混用不同概念会导致「脚本找不到模组」「存档 ID 变了」等假象问题。必须区分：

### 2.1 Fabric 模组 id（Loader 语义）

- **值**：`dhz_cnpcs`
- **定义位置**：`fabric.mod.json` → `"id": "dhz_cnpcs"`
- **影响**：
  - `FabricLoader.getInstance().getModContainer("dhz_cnpcs")`
  - 其它模组在 `fabric.mod.json` 里写 `"depends": { "customnpcs": ... }` 时，**不会**自动解析到本模组（见第 11 章）。

### 2.2 逻辑 MODID 常量（游戏内字符串）

- **值**：`customnpcs`
- **定义位置**：`CustomNpcs.MODID`（`src/main/java/noppes/npcs/CustomNpcs.java`）
- **用途示例**：
  - 运行时目录：`Dir` → 通常为 `{游戏目录}/customnpcs/`（具体拼接见 `CustomNpcs` 初始化）
  - 资源命名空间：`customnpcs:textures/...`、`customnpcs:lang/...`
  - 配置：`ConfigLoader` 使用的前缀 **`CustomNpcs`**（见配置小节）

### 2.3 注册表 / 世界存档中的命名空间（Registry）

方块、物品、实体类型、方块实体类型等仍注册为 **`customnpcs:<路径>`**，与上游 CustomNPCs **保持一致**，以便：

- 已有世界的区块实体 `id`、实体类型字符串、`customnpcs:` 物品 id 不因 fork 而失效。

**实现参考**：`CustomBlocks.registerBlocks()`、`CustomEntities.registerEntities()` 等中的 `Registry.register(..., "customnpcs:...", ...)`。

### 2.4 NpcAPI 可用性（脚本侧）

`NpcAPI.IsAvailable()`（`src/main/java/noppes/npcs/api/NpcAPI.java`）在 Loader 层同时接受：

- `dhz_cnpcs`（本 fork 的 fabric id）
- `customnpcs`（若未来合并 jar 或别名模组）

避免脚本只判断 `customnpcs` 时装载本模组却拿到 `Instance()` 为 null 的旧问题。

---

## 3. 构建与技术栈

### 3.1 Gradle 文件

| 文件 | 作用 |
|------|------|
| `settings.gradle` | 插件仓库（Fabric Maven）、`rootProject.name = 'dhz_cnpcs'`（影响默认 jar 前缀）。 |
| `build.gradle` | Loom、依赖、`compileJava` release 17、`jar`、`processResources` 展开 `fabric.mod.json` 版本。 |
| `gradle.properties` | `minecraft_version`、`yarn_mappings`、`loader_version`、`loom_version`、`fabric_api_version`、`mod_version`、`maven_group`。 |

### 3.2 Java 版本

- **编译**：`tasks.withType(JavaCompile)` → **`release = 17`**（字节码 17）。
- **运行**：可使用 **JDK 17 或 21**；整合包与 HMCL 需与团队约定一致。客户端日志若出现 LWJGL/JNI 警告通常与 JDK 发行版有关，优先保证 Loader + MC 版本匹配。

### 3.3 映射与 Loom

- **开发映射**：**Yarn**（`net.fabricmc:yarn:<yarn_mappings>:v2`）。
- **插件**：`net.fabricmc.fabric-loom-remap`（见 `build.gradle`），负责编译期命名 + **remapJar** 产出发布用字节码。
- **访问拓宽**：`loom { accessWidenerPath = file("src/main/resources/customnpcs.accesswidener") }`

### 3.4 依赖与打 fat jar

| 依赖 | 说明 |
|------|------|
| `fabric-loader` | `modImplementation` |
| `fabric-api` | `modImplementation`，版本见 `fabric_api_version` |
| `nashorn-core` | `implementation(include(...))` → **打进模组 jar**，解决 JDK 17+ 无内置 Nashorn 问题 |

### 3.5 产物与发布流程（必读）

1. **日常验证**：`./gradlew compileJava`（或 Windows `gradlew.bat compileJava`）。
2. **本地跑客户端**：`./gradlew runClient`（开发环境自带 Yarn 映射与 dev classpath）。
3. **发布到玩家 / 启动器**：执行 **`./gradlew build`**（或至少 **`remapJar`**），使用：
   - **`build/libs/dhz_cnpcs-<mod_version>.jar`**（名称随 `settings.gradle` 与 `mod_version` 可能略有差异，以实际为准）。

**禁止**：把 `build/classes/java/main` 或未经过 **remap** 的中间产物当作模组塞进 `.minecraft/mods`。

**原因**：启动器运行时使用 **intermediary**；开发环境 `runClient` 与 HMCL 的类加载/映射组合不同；只有 **Loom remap 后的 jar** 才能保证 `@Mixin` 注解与 `@Accessor` 重映射与正式环境一致。

---

## 4. 映射环境：开发（Yarn）vs 正式（Intermediary）

### 4.1 三套名字怎样共存

| 名称体系 | 典型形态 | 何时出现 |
|----------|-----------|----------|
| **Yarn（named）** | `ItemEntity`、`getItemAge()`、`itemAge` | IDE 里阅读的源码、`runClient` |
| **Intermediary** | `class_1542`、`field_7204`、`method_6985` | 崩溃栈、`javap` remap 后 class、日志里的混淆名 |
| **Mojang 官方映射（限定上下文）** | 部分第三方文档 | 本项目主线以 **Yarn + intermediary** 为准 |

Fabric Loom 在 **`remapJar`** 时会把源码里的 Yarn 名写回中间名，并把 Mixin 注解里的引用 remap 到运行时需要的形式。

### 4.2 Mixin Accessor / Invoker 命名陷阱（真实案例）

**现象**：`./gradlew runClient` 正常，**HMCL / 启动器**崩溃：

```text
InvalidAccessorException: No candidates were found matching field_6012:I in net/minecraft/class_1542
...
@Accessor ... ::age(I)V
```

**原因**：

- `Entity` 在 intermediary 中有字段 **`age` → `field_6012`**。
- `ItemEntity`（`class_1542`）上有 **`itemAge` → `field_7204`**（掉落物存在时间，区别于实体通用 tick 年龄）。
- 若 **`ItemEntityMixin` 中访问器方法命名为 `age(int)`**，Mixin 在应用 intermediary 映射时可能把 setter **错误绑定到父类 `Entity.age`**，而 **`ItemEntity` 字节码里并不存在 `field_6012`** → 启动失败。

**修复（已实现）**：

- 访问器方法改用 **无歧义名称**，例如 `setItemAge(int)`；
- 仍使用 `@Accessor("itemAge")` 绑定 Yarn 字段名（remap 后会落到 `field_7204`）。

**参考文件**：`src/main/java/noppes/npcs/mixin/ItemEntityMixin.java`  
**调用处**：`EntityItemWrapper.setAge` → `((ItemEntityMixin) entity).setItemAge(...)`

**规则归纳（给 AI）**：

1. `@Accessor` / `@Invoker` 的 **接口方法名** 避免与 **任意父类** 在 intermediary 下同名的字段/方法冲突；优先使用 **`setFoo` / `getFoo` / `callBar`** 等显式命名。
2. 继承链上的字段优先查 **Yarn 文档** 或 `mappings.tiny`，确认字段声明在哪个类。
3. `tools/mixin_accessor_audit.py` **无法覆盖「子类访问器误绑父类名」** 这类逻辑错误，需要运行时思维。

### 4.3 `@Inject` / `@Redirect` 目标

- `method = "...)` 描述符必须与本仓库 **当前 Yarn 版本** 一致。
- 升级 Yarn：`gradle.properties` 中 `yarn_mappings` 变更后全量编译 + `runClient` + 启动器各跑一次。
- **`remapJar` 日志**若出现 `Cannot remap XXX because it does not exist in any of the targets [...]`：说明 **Mixin 字符串仍指向旧 Yarn 方法名**，需打开对应类对照 Yarn javadoc 或 `mappings.tiny` 修正。

---

## 5. Mixin 架构与完整清单

### 5.1 配置文件

- **路径**：`src/main/resources/customnpcs.mixins.json`
- **`package`**：`noppes.npcs.mixin`（所有 mixin 类默认包名）
- **`compatibilityLevel`**：`JAVA_17`
- **`minVersion`**：`0.8`（Mixin 最低版本）

**注意**：本文件 **未** 手写 `refmap` 文件名；Loom 在构建时生成并嵌入 remap 流程所需信息，无需手动复制旧版 `customnpcs.refmap.json`（参考树里的 refmap 可能与当前 Yarn 不一致）。

### 5.2 环境与 Side

| JSON 键 | 加载环境 | 说明 |
|---------|-----------|------|
| `mixins` | 通用（服务端 + 客户端共用逻辑） | 包含世界、实体、命令、数据存储等。 |
| `client` | 仅客户端 | GUI、渲染、键盘、`MinecraftClient` 等。 |

新增 Mixin 时：**必须**把类名加到对应数组，否则不会生效。

### 5.3 可选 / 脆弱 Mixin

- **`TLSkinCapeMixin`**：依赖第三方类 `org.tlauncher.TLSkinCape`。未安装相关环境时日志可能出现 `ClassNotFoundException`，一般为 **WARN**，不应视为本模组核心依赖。

### 5.4 Mixin 类完整列表

#### `mixins`（29 个）

| 类名 |
|------|
| AbstractContainerMenuMixin |
| ArmorMaterialsMixin |
| BaseSpawnerMixin |
| ChunkMapMixin |
| EntityIMixin |
| EntityLivingIMixin |
| EntityLivingMixin |
| EntityPersistentData |
| GoalSelectorMixin |
| IceBlockMixin |
| ItemEntityMixin |
| LeavesBlockMixin |
| ListNBTMixin |
| MixinClientboundAddEntityPacket |
| MixinCommands |
| MixinPersistentEntitySectionManager |
| MixinPlayerDataStorage |
| MixinServerEntity |
| MixinServerLevel |
| MixinServerPlayer |
| NoiseChunkGeneratorMixin |
| PackRepositoryMixin |
| PersistentEntitySectionManagerMixin |
| ScoreBoardMixin |
| ServerLevelMixin |
| SynchedEntityDataMixin |
| VineBlockMixin |
| WalkAnimationStateMixin |
| MixinExecutorUtil |

#### `client`（27 个）

| 类名 |
|------|
| AbstractClientPlayerEntityMixin |
| AgeableModelMixin |
| ArmorLayerMixin |
| BipedBodyMixin |
| ClientPlayNetHandlerMixin |
| ClientTextTooltipMixin |
| ItemModelMesherMixin |
| LivingRenderer2Mixin |
| LivingRenderer3Mixin |
| LivingRendererMixin |
| MatrixStackMixin |
| MinecraftAccessor |
| MixinAbstractContainerScreen |
| MixinClientPacketListener |
| MixinEditBox |
| MixinKeyboardHandler |
| MixinLivingRenderer |
| MixinMinecraft |
| ModelPartMixin |
| ModelRendererMixin |
| MouseHelperMixin |
| MusicManagerMixin |
| NetworkPlayerInfoMixin |
| ParticleManagerMixin |
| ScreenMixin |
| SkinManagerMixin |
| TLSkinCapeMixin |

---

## 6. 注册与世界数据

### 6.1 方块 / 物品 / 方块实体 / 实体

- 注册名维持 **`customnpcs:`** 前缀（见 `CustomBlocks`、`CustomEntities`）。
- **可选第二套 id**：`CustomNpcs.FixUpdateFromPre_1_12` 为 true 时，实体注册可能额外注册 `customnpcs.<name>` 形式（仅当明确需要旧版兼容时开启；默认 `false`）。

### 6.2 BlockEntityType 与 DFU

- 使用 **`BlockEntityType.Builder.create(...).build(null)`**（见 `CustomBlocks.createTile`）。
- **`null`** 表示不向 DataFixer 注册 choice 类型，从而避免启动阶段刷屏：
  - `No data fixer registered for tile...`
- **代价**：若需支持「极老版本世界一次性升级到当前 DFU 链」的专用 schema，需要另行实现 **DataFixer / Schema**（超出常规 fork 范围）。

### 6.3 EntityType 与 FabricEntityTypeBuilder

- 使用 **`FabricEntityTypeBuilder`**（`net.fabricmc.fabric.api.object.builder.v1.entity`）构建自定义实体类型，避免走 `EntityType.Builder.build(String)` 路径触发的 DFU 注册与同类日志刷屏（实体 id 列表的 `No data fixer registered for customnpcs:...`）。
- **列表**：所有需要在世界中生成的 NPC / 弹射物 / 椅子坐骑等均在 `CustomEntities` 注册。

---

## 7. 客户端生命周期与渲染

### 7.1 实体渲染器注册时机

- **正确做法**：在 **`CustomNpcsClient.onInitializeClient()`** 开头（或尽早）调用 **`CustomRenderers.registerEntityRenderer()`**。
- **原因**：Fabric 约定在客户端初始化阶段注册 `EntityRendererRegistry`；若仅在 `MixinMinecraft` 构造函数注入里注册，可能出现 **`EntityRenderDispatcher` 已查询渲染器却尚未注册** → `entityRenderer == null` → 渲染世界崩溃。

**相关文件**：

- `src/main/java/noppes/npcs/CustomNpcsClient.java`
- `src/main/java/noppes/npcs/client/CustomRenderers.java`

### 7.2 Builtin 方块实体物品渲染

- **`CustomTileEntityItemStackRenderer`**（实现 Fabric `BuiltinItemRendererRegistry.DynamicItemRenderer`）：
  - **不得在** `instance()` / `onInitializeClient` 早期缓存 **`BlockEntityRenderDispatcher`**；
  - 在 **`render(...)`** 内通过 `MinecraftClient.getInstance().getBlockEntityRenderDispatcher()` 获取；
  - 若为 `null` 直接返回，避免创造栏绘制物品时 NPE。

### 7.3 MixinMinecraft 注入点

- `<init>` 里注入 **`createSearchTrees`** 的 target 必须是 **`Lnet/minecraft/client/MinecraftClient;createSearchTrees()V`**（Yarn 下类名为 `MinecraftClient`，不是旧映射里的 `Minecraft`）。

---

## 8. 网络与协议

- 频道 **`Identifier` 命名空间**仍为 **`customnpcs`**（见 `src/main/java/noppes/npcs/packets/Packets.java`）。
- **Fabric 模组 id 改为 `dhz_cnpcs` 不会改变** 已有网络 id；服务端与客户端必须使用 **同一构建** 的模组 jar，避免 packet 索引不一致。

---

## 9. 配置与文件布局

### 9.1 ConfigLoader

- 构造：`new ConfigLoader(this.getClass(), new File(dir, "config"), "CustomNpcs")`（见 `CustomNpcs` 初始化逻辑）。
- 生成的配置文件位于 **`config/`** 目录下，前缀与 **`CustomNpcs`** 字符串相关（具体文件名以运行时为准）。

### 9.2 全局数据目录

- `CustomNpcs.Dir`：基于 `MODID`（`customnpcs`）目录，用于脚本、导出、缓存等（详见 `CustomNpcs` 静态初始化）。

---

## 10. 工具与冒烟测试

### 10.1 `tools/smoke_client.bat`

- 作用：项目根目录执行 `compileJava` → `runClient`。
- 用途：快速验证编译与客户端能否启动（Windows）。

### 10.2 `tools/mixin_accessor_audit.py`

- **作用**：下载/缓存 Yarn `mappings.tiny`，校验 **`@Mixin` 目标类上是否直接声明了 `@Accessor`/`@Invoker` 所用的 Yarn 名**。
- **局限**（脚本头部已说明）：
  - 继承自父类的字段在 Tiny 里未必出现在子类行 → **可能误报**；
  - **不检查** `@Inject` 方法签名、`@At` target、客户端初始化顺序等非 Accessor 问题。

### 10.3 `tools/run_mixin_audit.bat`

- Windows 下调用上述 Python 脚本的便捷封装。

### 10.4 `tools/yarnify_intermediary_fields.py`

- **作用**：把源码里误写的 **intermediary 字段名**（`field_xxxxx`）批量替换为 **Yarn 名**（依赖固定版本的 Yarn jar URL，脚本内为 `1.20.1+build.10`）。
- **使用场景**：从旧反编译代码迁移时批量修正字段引用（需在 git 下Review diff）。

---

## 11. 与其它模组及服务器替换的兼容性

### 11.1 `fabric.mod.json` 依赖名 `customnpcs`

若第三方模组声明：

```json
"depends": {
  "customnpcs": "*"
}
```

Fabric Loader 只认 **`id` 字段**。本 jar 的 **`id` 为 `dhz_cnpcs`**，则：

- Loader 可能判定 **`customnpcs` 未安装** → 前置模组拒绝加载或报错。

**可选对策**（择一，需团队决策）：

1. **Fork 前置模组**，把依赖改为 `dhz_cnpcs`；
2. **发布变体**：额外提供一个 `fabric.mod.json` 中 `id` 为 `customnpcs` 的 jar（小心与官方 CustomNPCs **冲突**，不可同时安装两个实现）；
3. **文档告知玩家**：整合包作者用依赖覆盖或自定义元数据（取决于 Loader 版本能力）。

### 11.2 存档与命名空间

- 方块/实体/物品的 **注册 id** 仍为 `customnpcs:` → **有利于**从上游 CustomNPCs 世界迁移。
- **无法保证**「零风险」：务必 **备份**，并在副本世界上验证 NPC、脚本、方块实体。

---

## 12. 常见崩溃与日志指纹（排障表）

| 日志指纹 | 可能原因 | 优先查看 |
|----------|-----------|-----------|
| `InvalidAccessorException` + `class_1542` + `field_6012` + `@Accessor` `age` | ItemEntity 访问器误绑 `Entity.age` | `ItemEntityMixin`、`EntityItemWrapper` |
| `entityRenderer` is null / `shouldRender` NPE | 实体类型未注册渲染器或注册过晚 | `CustomNpcsClient`、`CustomRenderers` |
| `blockEntityRenderDispatcher` null（绘制物品） | TE ISR 过早缓存 dispatcher | `CustomTileEntityItemStackRenderer.render` |
| `Mixin apply failed` + 某 `@Inject` | 目标方法签名或 Yarn 名变更 | 对应 Mixin、`remapJar` Cannot remap 日志 |
| `No data fixer registered for ...`（刷屏） | 历史上使用 `Util.getChoiceType` / `EntityType.Builder.build(id)` | 已改为 `build(null)` / `FabricEntityTypeBuilder`；若回归则检查注册路径 |
| `NpcAPI.Instance()` null | `IsAvailable()` 否 | `NpcAPI.java`、是否正确加载 `dhz_cnpcs` |
| `TLSkinCape` ClassNotFound | 可选皮肤模组缺失 | 可忽略或用安装 Cape 模组 |
| JVM 首行 `Unknown module: cpw.mods.bootstraplauncher` | 启动器拼接了 Forge/旧 JVM 参数 | 单独通常非致命；若崩溃需结合后续 Caused by |

---

## 13. AI 修改代码时的约束（仓库级）

1. **最小可行改动**：不把无关文件卷入 PR；不大面积「美化」CFR 代码。
2. **禁止随意修改** `Registry.register(..., "customnpcs:...", ...)` 中的 id，除非同步迁移存档与文档并明确破坏性版本号。
3. **新增 Mixin**：同步更新 `customnpcs.mixins.json`，并选对 `mixins` vs `client`。
4. **Accessor 命名**：遵循第 4.2 节，避免与父类 intermediary 字段混淆。
5. **验证顺序**：`compileJava` → 功能相关 `runClient` → **`build` 取 `build/libs` jar** 在启动器烟测。

---

## 14. 附录

### 14.1 关键文件索引

| 类别 | 路径 |
|------|------|
| 模组清单 | `src/main/resources/fabric.mod.json` |
| Mixin 列表 | `src/main/resources/customnpcs.mixins.json` |
| Access Widener | `src/main/resources/customnpcs.accesswidener` |
| 服务端入口 | `src/main/java/noppes/npcs/CustomNpcs.java` |
| 客户端入口 | `src/main/java/noppes/npcs/CustomNpcsClient.java` |
| 方块 / 方块实体 | `src/main/java/noppes/npcs/CustomBlocks.java` |
| 实体类型 | `src/main/java/noppes/npcs/CustomEntities.java` |
| 物品 / 创造栏 | `src/main/java/noppes/npcs/CustomItems.java`、`CustomTabs.java` |
| 网络 | `src/main/java/noppes/npcs/packets/Packets.java` |
| API 入口 | `src/main/java/noppes/npcs/api/NpcAPI.java` |
| Mixin 包 | `src/main/java/noppes/npcs/mixin/` |
| 客户端渲染注册 | `src/main/java/noppes/npcs/client/CustomRenderers.java` |

### 14.2 Gradle 任务对照

| 任务 | 用途 |
|------|------|
| `compileJava` | 最快编译反馈 |
| `jar` | 未 remap 的中间 jar |
| `remapJar` | 生成可分发的 remap 产物（通常由 `build` 触发） |
| `runClient` | 开发客户端 |
| `runServer` | 开发服务端（若需冒烟可加 `tools/smoke_server.bat`，当前仓库若不存在可自行添加） |
| `build` | 常规 CI / 发布：`build/libs` |

### 14.3 延伸阅读（官方）

- Fabric Wiki：Loom、Mixin、事件与网络（英文）
- Yarn Javadoc：`https://maven.fabricmc.net/docs/`（路径中带 yarn 版本）

---

**文档结束**。修改仓库结构或迁移 MC 版本后，请同步更新第 0 节表格与本手册相关章节。
