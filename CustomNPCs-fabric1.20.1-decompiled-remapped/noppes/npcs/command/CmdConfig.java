/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.server.command.CommandManager
 *  net.minecraft.text.Text
 */
package noppes.npcs.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.ChunkController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketConfigFont;

public class CmdConfig {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        LiteralArgumentBuilder command = CommandManager.literal((String)"config");
        command.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal((String)"leavesdecay").requires(source -> source.hasPermissionLevel(4))).executes(context -> {
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)("LeavesDecay: " + CustomNpcs.LeavesDecayEnabled)), false);
            return 1;
        })).then(CommandManager.argument((String)"boolean", (ArgumentType)BoolArgumentType.bool()).executes(context -> {
            CustomNpcs.LeavesDecayEnabled = BoolArgumentType.getBool((CommandContext)context, (String)"boolean");
            CustomNpcs.Config.updateConfig();
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)("LeavesDecay: " + CustomNpcs.LeavesDecayEnabled)), false);
            return 1;
        })));
        command.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal((String)"vineinflateth").requires(source -> source.hasPermissionLevel(4))).executes(context -> {
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)("VineGrowth: " + CustomNpcs.VineGrowthEnabled)), false);
            return 1;
        })).then(CommandManager.argument((String)"boolean", (ArgumentType)BoolArgumentType.bool()).executes(context -> {
            CustomNpcs.VineGrowthEnabled = BoolArgumentType.getBool((CommandContext)context, (String)"boolean");
            CustomNpcs.Config.updateConfig();
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)("VineGrowth: " + CustomNpcs.VineGrowthEnabled)), false);
            return 1;
        })));
        command.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal((String)"icemelts").requires(source -> source.hasPermissionLevel(4))).executes(context -> {
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)("IceMelts: " + CustomNpcs.IceMeltsEnabled)), false);
            return 1;
        })).then(CommandManager.argument((String)"boolean", (ArgumentType)BoolArgumentType.bool()).executes(context -> {
            CustomNpcs.IceMeltsEnabled = BoolArgumentType.getBool((CommandContext)context, (String)"boolean");
            CustomNpcs.Config.updateConfig();
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)("IceMelts: " + CustomNpcs.IceMeltsEnabled)), false);
            return 1;
        })));
        command.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal((String)"freezenpcs").requires(source -> source.hasPermissionLevel(4))).executes(context -> {
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)("Frozen NPCs: " + CustomNpcs.FreezeNPCs)), false);
            return 1;
        })).then(CommandManager.argument((String)"boolean", (ArgumentType)BoolArgumentType.bool()).executes(context -> {
            CustomNpcs.FreezeNPCs = BoolArgumentType.getBool((CommandContext)context, (String)"boolean");
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)("Frozen NPCs: " + CustomNpcs.FreezeNPCs)), false);
            return 1;
        })));
        command.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal((String)"debug").requires(source -> source.hasPermissionLevel(4))).executes(context -> {
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)("Verbose debug is " + CustomNpcs.VerboseDebug)), false);
            return 1;
        })).then(CommandManager.argument((String)"boolean", (ArgumentType)BoolArgumentType.bool()).executes(context -> {
            CustomNpcs.VerboseDebug = BoolArgumentType.getBool((CommandContext)context, (String)"boolean");
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)("Verbose debug is now" + CustomNpcs.VerboseDebug)), false);
            return 1;
        })));
        command.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal((String)"scripting").requires(source -> source.hasPermissionLevel(4))).executes(context -> {
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)("Scripting is " + CustomNpcs.EnableScripting)), false);
            return 1;
        })).then(CommandManager.argument((String)"boolean", (ArgumentType)BoolArgumentType.bool()).executes(context -> {
            CustomNpcs.EnableScripting = BoolArgumentType.getBool((CommandContext)context, (String)"boolean");
            CustomNpcs.Config.updateConfig();
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)("Scripting is now" + CustomNpcs.EnableScripting)), false);
            return 1;
        })));
        command.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal((String)"chunkloaders").requires(source -> source.hasPermissionLevel(4))).executes(context -> {
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)("ChunkLoaders: " + ChunkController.instance.size() + "/" + CustomNpcs.ChuckLoaders)), false);
            return 1;
        })).then(CommandManager.argument((String)"number", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(context -> {
            CustomNpcs.ChuckLoaders = IntegerArgumentType.getInteger((CommandContext)context, (String)"number");
            CustomNpcs.Config.updateConfig();
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)("Max ChunkLoaders: " + CustomNpcs.ChuckLoaders)), false);
            return 1;
        })));
        command.then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal((String)"font").requires(source -> source.hasPermissionLevel(2))).executes(context -> {
            Packets.send(((ServerCommandSource)context.getSource()).getPlayerOrThrow(), new PacketConfigFont("", 0));
            return 1;
        })).then(((RequiredArgumentBuilder)CommandManager.argument((String)"font", (ArgumentType)StringArgumentType.string()).executes(context -> {
            Packets.send(((ServerCommandSource)context.getSource()).getPlayerOrThrow(), new PacketConfigFont(StringArgumentType.getString((CommandContext)context, (String)"font"), 18));
            return 1;
        })).then(CommandManager.argument((String)"size", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(context -> {
            Packets.send(((ServerCommandSource)context.getSource()).getPlayerOrThrow(), new PacketConfigFont(StringArgumentType.getString((CommandContext)context, (String)"font"), IntegerArgumentType.getInteger((CommandContext)context, (String)"size")));
            return 1;
        }))));
        return command;
    }
}

