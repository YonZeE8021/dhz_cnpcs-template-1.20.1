# DHZ Custom NPCs：权限管理说明

**只想快速了解怎么配？** 请看面向非技术读者的 **[PERMISSIONS-BRIEF.md](PERMISSIONS-BRIEF.md)**。

本文说明本模组如何校验玩家权限、相关配置文件项，以及与 LuckPerms 等权限插件的关系（偏技术细节）。

---

## 1. Minecraft 权限等级（permission level）

本模组当前以 **Minecraft 内置权限等级**（0～4）为主判定依据，与原版 `/op` 等级一致：

| 等级 | 典型含义（原版） |
|------|------------------|
| 0 | 普通玩家 |
| 1 | 无（原版未使用） |
| 2 | 多数作弊类命令（如 `/gamemode`、`/give`） |
| 3 | 更高管理命令（如 `/op`、`/ban`） |
| 4 | 单机「允许作弊」或 OP 等级 4 |

具体某玩家当前等级由服务端与存档策略决定（`ops.json`、整合包/插件对 OP 等级的设定等）。**本模组不替代**服务端的 OP 列表，只在各项功能上要求「至少达到配置里写的等级」。

---

## 2. 配置文件位置与加载时机

- **路径**：游戏根目录下 `config/CustomNpcs.cfg`（与 `CustomNpcs` 主类上的 `@ConfigProp` 字段对应）。
- **加载**：模组初始化时由 `ConfigLoader` 读取；**中途改 cfg 不会自动热重载**，需重启服务端（或重启客户端，取决于环境）后生效。
- **缺项**：若 cfg 中缺少下文某项，启动时会用 **Java 字段默认值** 补上，并可能触发 `updateConfig()` 将整文件重写，从而写入新键（便于旧版本 cfg 平滑升级）。

---

## 3. 三档可配置权限等级

下列三项均在 `CustomNpcs.cfg` 中配置，键名与字段名一致。合法范围为 **0～4**；若写出范围，启动时会被钳制到 0～4（见 `CustomNpcs.clampMcPermissionLevel`）。

### 3.1 `NpcManagePermissionLevel`（默认：`3`）

用于 **游戏内 NPC 管理**（不经过 Brigadier 命令树的那一类）：

- 实现位置：`CustomNpcsPermissions.hasPermission(ServerPlayerEntity, …)`，内部为 `player.hasPermissionLevel(NpcManagePermissionLevel)`。
- 典型场景：NPC 编辑 GUI、魔杖/路径等与 `CustomNpcsPermissions.*` 常量绑定的操作；以及 **`/noppes clone add`、`/noppes clone remove`**（与 GUI 能力对齐，避免「能改 NPC 却不能写克隆库」）。

**说明**：`CustomNpcsPermissions` 中虽为每个能力定义了 `PermissionNodeCompat`（如逻辑上的 `customnpcs.npc.gui`），但 **当前实现不逐节点解析**，仅比较权限等级；详见第 6 节。

### 3.2 `NoppesCommandPermissionLevel`（默认：`2`）

用于 **`/noppes` 下偏「日常运营」的子命令**（原逻辑里多为等级 2 的一类）：

- 根命令：`/noppes` 本身的 `requires`。
- `faction`、`mark`、`scene` 整条分支的入口。
- `quest` 中与玩家任务状态相关的子命令（如 start / finish / stop / remove / objective）。
- `dialog` 的 `read`、`unread`、`show`。
- `clone` 的 `list`、`spawn`、`grid`。
- `config` 的 `font` 子命令。

### 3.3 `NoppesAdminPermissionLevel`（默认：`4`）

用于 **`/noppes` 下偏「服务器/数据/危险操作」的子命令**（原逻辑里多为等级 4 的一类）：

- `config`：`leavesdecay`、`vineinflateth`、`icemelts`、`freezenpcs`、`debug`、`scripting`、`chunkloaders` 等。
- `dialog reload`、`quest reload`。
- `schema`（建筑 schematic）根入口。
- `slay`。

---

## 4. 与其它配置项的组合关系

### 4.1 `NoppesCommandOpOnly`（布尔，默认：`false`）

- **`false`（默认）**：`/noppes npc` 与 `/noppes script` 命令树的入口权限使用 **`NoppesCommandPermissionLevel`**（与多数子命令一档）。
- **`true`**：上述两条命令树的入口改为 **`NoppesAdminPermissionLevel`**，用于希望「只有高权限管理员才能动 NPC 命令行 / 脚本重载」的服务器。

### 4.2 `OpsOnly`（布尔，默认：`true`，见静态初始化块）

与 **权限等级** 不同：开启时要求玩家必须在服务端的 **OP 列表**（`ops.json`）中，否则部分交互会被拒绝（例如魔杖等，见 `ItemNpcWand`、`ServerEventsHandler` 等）。

- **权限等级**：数值门槛（可 cfg）。
- **OpsOnly**：是否在 OP 列表中的额外开关。

两者可能同时生效，服主需一并考虑。

### 4.3 `DisablePermissions`

源码中存在该字段，**当前未见在权限路径上被读取**；可视为预留或未接线项，**不要依赖它关闭校验**。

---

## 5. 功能与配置对照简表

| 能力范围 | 使用的配置项 |
|----------|----------------|
| NPC GUI、工具、数据包内 `CustomNpcsPermissions.hasPermission` | `NpcManagePermissionLevel` |
| `/noppes clone add`、`remove` | `NpcManagePermissionLevel` |
| `/noppes` 根及第 3.2 节列出的子命令 | `NoppesCommandPermissionLevel` |
| `/noppes` 第 3.3 节列出的子命令 | `NoppesAdminPermissionLevel` |
| `NoppesCommandOpOnly == true` 时的 `/noppes npc`、`/noppes script` 入口 | `NoppesAdminPermissionLevel` |
| `NoppesCommandOpOnly == false` 时的同上 | `NoppesCommandPermissionLevel` |

---

## 6. LuckPerms 与「权限节点」

### 6.1 当前行为

- 本模组 **未集成** Fabric 生态常用的 **Fabric Permissions API**，因此 **LuckPerms 无法通过 `customnpcs.xxx` 这类节点** 像部分现代模组那样直接接管本模组的允许/拒绝逻辑。
- `CustomNpcsPermissions` 里的 `PermissionNodeCompat` 常量（命名空间 `customnpcs`，路径如 `npc.gui`、`global.dialog` 等）主要用于 **数据包/内部 API 形态兼容**；**实际判定仍以权限等级为准**（见第 3.1 节）。
- 脚本 API：`WrapperNpcAPI.hasPermissionNode` 当前固定返回 `false`；`registerPermissionNode` 会抛出「不再支持」。

### 6.2 若需 LuckPerms 精细控制

需要在开发上增加一层，例如：

- 引入 **fabric-permissions-api**，在 `hasPermission` 与命令 `requires` 中查询节点；或  
- 由权限插件通过 **修改玩家权限等级**（若插件支持）与本 cfg 三档配合。

以上不属于当前仓库默认行为，部署文档中应向服主说明 **以 OP 等级 + `CustomNpcs.cfg` 三档为主**。

---

## 7. 相关源码索引（便于维护）

| 主题 | 主要文件 |
|------|-----------|
| cfg 字段定义、钳制、初始化 | `src/main/java/noppes/npcs/CustomNpcs.java` |
| 游戏内 NPC 管理统一入口 | `src/main/java/noppes/npcs/CustomNpcsPermissions.java` |
| cfg 读写、缺键合并 | `src/main/java/noppes/npcs/config/ConfigLoader.java` |
| `/noppes` 各子命令 `requires` | `src/main/java/noppes/npcs/command/*.java` |

---

## 8. 服主配置建议（简要）

- 希望 **多数建造/剧情服成员** 能用魔杖改 NPC、但不想给 4 级 OP：将 **`NpcManagePermissionLevel`** 设为 **`3`**（默认已是 3），并确保目标玩家在对应等级（或通过插件映射到该等级）。
- 希望 **仅服主** 能改树叶/冰面/脚本开关等：保持 **`NoppesAdminPermissionLevel` = `4`**，且勿随意降低。
- 需要 **完全禁止非 OP** 使用魔杖等：除权限等级外，启用 **`OpsOnly`** 并维护 `ops.json`。

文档版本与代码不一致时，以 `CustomNpcs.java` 中 `@ConfigProp` 注释及命令类中的 `hasPermissionLevel(CustomNpcs.*)` 为准。
