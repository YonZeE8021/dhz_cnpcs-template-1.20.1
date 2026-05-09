/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

public class CustomNpcsPermissions {
    public static final PermissionNodeCompat<Boolean> NPC_DELETE = new PermissionNodeCompat<Boolean>("customnpcs", "npc.delete", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> NPC_CREATE = new PermissionNodeCompat<Boolean>("customnpcs", "npc.create", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> NPC_GUI = new PermissionNodeCompat<Boolean>("customnpcs", "npc.gui", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> NPC_FREEZE = new PermissionNodeCompat<Boolean>("customnpcs", "npc.freeze", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> NPC_RESET = new PermissionNodeCompat<Boolean>("customnpcs", "npc.reset", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> NPC_AI = new PermissionNodeCompat<Boolean>("customnpcs", "npc.ai", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> NPC_ADVANCED = new PermissionNodeCompat<Boolean>("customnpcs", "npc.advanced", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> NPC_DISPLAY = new PermissionNodeCompat<Boolean>("customnpcs", "npc.display", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> NPC_INVENTORY = new PermissionNodeCompat<Boolean>("customnpcs", "npc.inventory", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> NPC_STATS = new PermissionNodeCompat<Boolean>("customnpcs", "npc.stats", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> NPC_CLONE = new PermissionNodeCompat<Boolean>("customnpcs", "npc.clone", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> GLOBAL_LINKED = new PermissionNodeCompat<Boolean>("customnpcs", "global.linked", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> GLOBAL_PLAYERDATA = new PermissionNodeCompat<Boolean>("customnpcs", "global.playerdata", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> GLOBAL_BANK = new PermissionNodeCompat<Boolean>("customnpcs", "global.bank", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> GLOBAL_DIALOG = new PermissionNodeCompat<Boolean>("customnpcs", "global.dialog", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> GLOBAL_QUEST = new PermissionNodeCompat<Boolean>("customnpcs", "global.quest", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> GLOBAL_FACTION = new PermissionNodeCompat<Boolean>("customnpcs", "global.faction", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> GLOBAL_TRANSPORT = new PermissionNodeCompat<Boolean>("customnpcs", "global.transport", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> GLOBAL_RECIPE = new PermissionNodeCompat<Boolean>("customnpcs", "global.recipe", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> GLOBAL_NATURALSPAWN = new PermissionNodeCompat<Boolean>("customnpcs", "global.naturalspawn", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> SPAWNER_MOB = new PermissionNodeCompat<Boolean>("customnpcs", "spawner.mob", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> SPAWNER_CREATE = new PermissionNodeCompat<Boolean>("customnpcs", "spawner.create", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> TOOL_MOUNTER = new PermissionNodeCompat<Boolean>("customnpcs", "tool.mounter", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> TOOL_PATHER = new PermissionNodeCompat<Boolean>("customnpcs", "tool.pather", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> TOOL_SCRIPTER = new PermissionNodeCompat<Boolean>("customnpcs", "tool.scripter", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> TOOL_NBTBOOK = new PermissionNodeCompat<Boolean>("customnpcs", "tool.nbtbook", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> EDIT_VILLAGER = new PermissionNodeCompat<Boolean>("customnpcs", "edit.villager", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> EDIT_BLOCKS = new PermissionNodeCompat<Boolean>("customnpcs", "edit.blocks", PermissionTypes.BOOLEAN, (player, id, context) -> true);
    public static final PermissionNodeCompat<Boolean> SOULSTONE_ALL = new PermissionNodeCompat<Boolean>("customnpcs", "soulstone.all", PermissionTypes.BOOLEAN, (player, id, context) -> false);
    public static final PermissionNodeCompat<Boolean> SCENES = new PermissionNodeCompat<Boolean>("customnpcs", "scenes", PermissionTypes.BOOLEAN, (player, id, context) -> true);

    public static boolean hasPermission(ServerPlayerEntity player, PermissionNodeCompat<Boolean> permission) {
        return player.hasPermissionLevel(CustomNpcs.NpcManagePermissionLevel);
    }
}

