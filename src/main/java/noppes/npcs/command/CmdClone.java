/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.world.World
 *  net.minecraft.command.CommandException
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.server.command.CommandManager
 *  net.minecraft.command.argument.BlockPosArgumentType
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.text.Text
 *  net.minecraft.server.world.ServerWorld
 */
package noppes.npcs.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.server.world.ServerWorld;
import noppes.npcs.CustomNpcs;
import noppes.npcs.command.CmdNoppes;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.entity.EntityNPCInterface;

public class CmdClone {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        LiteralArgumentBuilder command = CommandManager.literal((String)"clone");
        command.then(((LiteralArgumentBuilder)CommandManager.literal((String)"list").requires(source -> source.hasPermissionLevel(CustomNpcs.NoppesCommandPermissionLevel))).then(CommandManager.argument((String)"tab", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(context -> {
            int tab = IntegerArgumentType.getInteger((CommandContext)context, (String)"tab");
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)"--- Stored NPCs --- (server side)"), false);
            for (String name : ServerCloneController.Instance.getClones(tab)) {
                ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)name), false);
            }
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)"------------------------------------"), false);
            return 1;
        })));
        command.then(((LiteralArgumentBuilder)CommandManager.literal((String)"add").requires(source -> source.hasPermissionLevel(CustomNpcs.NpcManagePermissionLevel))).then(CommandManager.argument((String)"npc", (ArgumentType)StringArgumentType.string()).then(((RequiredArgumentBuilder)CommandManager.argument((String)"tab", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(context -> {
            CmdClone.addClone((CommandContext<ServerCommandSource>)context, "");
            return 1;
        })).then(CommandManager.argument((String)"name", (ArgumentType)StringArgumentType.string()).executes(context -> {
            CmdClone.addClone((CommandContext<ServerCommandSource>)context, StringArgumentType.getString((CommandContext)context, (String)"name"));
            return 1;
        })))));
        command.then(((LiteralArgumentBuilder)CommandManager.literal((String)"remove").requires(source -> source.hasPermissionLevel(CustomNpcs.NpcManagePermissionLevel))).then(CommandManager.argument((String)"npc", (ArgumentType)StringArgumentType.string()).then(CommandManager.argument((String)"tab", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(context -> {
            String nametodel = StringArgumentType.getString((CommandContext)context, (String)"npc");
            int tab = IntegerArgumentType.getInteger((CommandContext)context, (String)"tab");
            boolean deleted = false;
            for (String name : ServerCloneController.Instance.getClones(tab)) {
                if (!nametodel.equalsIgnoreCase(name)) continue;
                ServerCloneController.Instance.removeClone(name, tab);
                deleted = true;
                break;
            }
            if (!deleted) {
                throw new CommandException((Text)Text.translatable((String)"Npc '%s' wasn't found", (Object[])new Object[]{nametodel}));
            }
            return 1;
        }))));
        command.then(((LiteralArgumentBuilder)CommandManager.literal((String)"spawn").requires(source -> source.hasPermissionLevel(CustomNpcs.NoppesCommandPermissionLevel))).then(CommandManager.argument((String)"npc", (ArgumentType)StringArgumentType.string()).then(((RequiredArgumentBuilder)CommandManager.argument((String)"tab", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(context -> {
            CmdClone.spawnClone((CommandContext<ServerCommandSource>)context, new BlockPos((int)((ServerCommandSource)context.getSource()).getPosition().x, (int)((ServerCommandSource)context.getSource()).getPosition().y, (int)((ServerCommandSource)context.getSource()).getPosition().z), "");
            return 1;
        })).then(((RequiredArgumentBuilder)CommandManager.argument((String)"pos", (ArgumentType)BlockPosArgumentType.blockPos()).executes(context -> {
            CmdClone.spawnClone((CommandContext<ServerCommandSource>)context, BlockPosArgumentType.getLoadedBlockPos((CommandContext)context, (String)"pos"), "");
            return 1;
        })).then(CommandManager.argument((String)"display_name", (ArgumentType)StringArgumentType.string()).executes(context -> {
            CmdClone.spawnClone((CommandContext<ServerCommandSource>)context, BlockPosArgumentType.getLoadedBlockPos((CommandContext)context, (String)"pos"), StringArgumentType.getString((CommandContext)context, (String)"display_name"));
            return 1;
        }))))));
        command.then(((LiteralArgumentBuilder)CommandManager.literal((String)"grid").requires(source -> source.hasPermissionLevel(CustomNpcs.NoppesCommandPermissionLevel))).then(CommandManager.argument((String)"npc", (ArgumentType)StringArgumentType.string()).then(CommandManager.argument((String)"tab", (ArgumentType)IntegerArgumentType.integer((int)0)).then(CommandManager.argument((String)"length", (ArgumentType)IntegerArgumentType.integer()).then(((RequiredArgumentBuilder)CommandManager.argument((String)"width", (ArgumentType)IntegerArgumentType.integer()).executes(context -> {
            int length = IntegerArgumentType.getInteger((CommandContext)context, (String)"length");
            int width = IntegerArgumentType.getInteger((CommandContext)context, (String)"width");
            for (int x = 0; x < length; ++x) {
                for (int z = 0; z < width; ++z) {
                    CmdClone.spawnClone((CommandContext<ServerCommandSource>)context, new BlockPos((int)((ServerCommandSource)context.getSource()).getPosition().x, (int)((ServerCommandSource)context.getSource()).getPosition().y, (int)((ServerCommandSource)context.getSource()).getPosition().z).add(length, 0, width), "");
                }
            }
            return 1;
        })).then(((RequiredArgumentBuilder)CommandManager.argument((String)"pos", (ArgumentType)BlockPosArgumentType.blockPos()).executes(context -> {
            int length = IntegerArgumentType.getInteger((CommandContext)context, (String)"length");
            int width = IntegerArgumentType.getInteger((CommandContext)context, (String)"width");
            for (int x = 0; x < length; ++x) {
                for (int z = 0; z < width; ++z) {
                    CmdClone.spawnClone((CommandContext<ServerCommandSource>)context, BlockPosArgumentType.getLoadedBlockPos((CommandContext)context, (String)"pos").add(length, 0, width), "");
                }
            }
            return 1;
        })).then(CommandManager.argument((String)"display_name", (ArgumentType)StringArgumentType.string()).executes(context -> {
            int length = IntegerArgumentType.getInteger((CommandContext)context, (String)"length");
            int width = IntegerArgumentType.getInteger((CommandContext)context, (String)"width");
            for (int x = 0; x < length; ++x) {
                for (int z = 0; z < width; ++z) {
                    CmdClone.spawnClone((CommandContext<ServerCommandSource>)context, BlockPosArgumentType.getLoadedBlockPos((CommandContext)context, (String)"pos").add(length, 0, width), StringArgumentType.getString((CommandContext)context, (String)"display_name"));
                }
            }
            return 1;
        }))))))));
        return command;
    }

    private static void addClone(CommandContext<ServerCommandSource> context, String newName) {
        NbtCompound compound;
        String name = StringArgumentType.getString(context, (String)"npc");
        if (newName.isEmpty()) {
            newName = name;
        }
        int tab = IntegerArgumentType.getInteger(context, (String)"tab");
        List<EntityNPCInterface> list = CmdNoppes.getNpcsByName(((ServerCommandSource)context.getSource()).getWorld(), name);
        if (list.isEmpty()) {
            return;
        }
        EntityNPCInterface npc = list.get(0);
        if (!npc.saveSelfNbt(compound = new NbtCompound())) {
            return;
        }
        ServerCloneController.Instance.addClone(compound, newName, tab);
    }

    private static void spawnClone(CommandContext<ServerCommandSource> context, BlockPos pos, String newName) {
        String name = StringArgumentType.getString(context, (String)"npc").replaceAll("%", " ");
        int tab = IntegerArgumentType.getInteger(context, (String)"tab");
        NbtCompound compound = ServerCloneController.Instance.getCloneData((ServerCommandSource)context.getSource(), name, tab);
        if (compound == null) {
            throw new CommandException((Text)Text.literal((String)"Unknown npc"));
        }
        if (pos == BlockPos.ORIGIN) {
            throw new CommandException((Text)Text.literal((String)"Location needed"));
        }
        ServerWorld world = ((ServerCommandSource)context.getSource()).getWorld();
        Entity entity = (Entity)EntityType.getEntityFromNbt((NbtCompound)compound, (World)world).get();
        entity.setPosition((double)pos.getX() + 0.5, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5);
        if (entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface)entity;
            npc.ais.setStartPos(pos);
            if (!newName.isEmpty()) {
                npc.display.setName(newName.replaceAll("%", " "));
            }
        }
        world.spawnEntity(entity);
    }
}

