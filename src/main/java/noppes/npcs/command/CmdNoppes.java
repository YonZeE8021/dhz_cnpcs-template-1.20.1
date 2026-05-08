/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.server.command.CommandManager
 *  net.minecraft.server.world.ServerWorld
 */
package noppes.npcs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import noppes.npcs.CustomEntities;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.command.CmdClone;
import noppes.npcs.command.CmdConfig;
import noppes.npcs.command.CmdDialog;
import noppes.npcs.command.CmdFaction;
import noppes.npcs.command.CmdMark;
import noppes.npcs.command.CmdNPC;
import noppes.npcs.command.CmdQuest;
import noppes.npcs.command.CmdScene;
import noppes.npcs.command.CmdSchematics;
import noppes.npcs.command.CmdScript;
import noppes.npcs.command.CmdSlay;
import noppes.npcs.entity.EntityNPCInterface;

public class CmdNoppes {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal((String)"noppes").requires(p_198816_0_ -> p_198816_0_.hasPermissionLevel(2))).then(CmdClone.register())).then(CmdConfig.register())).then(CmdDialog.register())).then(CmdFaction.register())).then(CmdMark.register())).then(CmdNPC.register())).then(CmdQuest.register())).then(CmdScene.register())).then(CmdSchematics.register())).then(CmdScript.register())).then(CmdSlay.register()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static List<EntityNPCInterface> getNpcsByName(ServerWorld level, String name) {
        TypeFilter<Entity, EntityCustomNpc> filter = (TypeFilter)(Object)CustomEntities.entityCustomNpc;
        return (List<EntityNPCInterface>)(List<?>)level.getEntitiesByType(filter, (EntityCustomNpc npc) -> npc.display.getName().equalsIgnoreCase(name));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T extends Entity> List<T> getEntities(EntityType<T> type, ServerWorld level) {
        TypeFilter<Entity, T> filter = (TypeFilter)(Object)type;
        return (List<T>)(List<?>)level.getEntitiesByType(filter, entity -> true);
    }
}

