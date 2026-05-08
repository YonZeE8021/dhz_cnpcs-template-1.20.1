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
 *  net.minecraft.command.CommandException
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.server.command.CommandManager
 *  net.minecraft.text.Text
 */
package noppes.npcs.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Map;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import noppes.npcs.entity.data.DataScenes;

public class CmdScene {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        LiteralArgumentBuilder command = (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal((String)"scene").requires(source -> source.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)CommandManager.literal((String)"time").executes(context -> {
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)"Active scenes:"), false);
            for (Map.Entry<String, DataScenes.SceneState> entry : DataScenes.StartedScenes.entrySet()) {
                ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.translatable((String)"Scene %s time is %s", (Object[])new Object[]{entry.getKey(), ((DataScenes.SceneState)entry.getValue()).ticks}), false);
            }
            return 1;
        })).then(((RequiredArgumentBuilder)CommandManager.argument((String)"time", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(context -> {
            int ticks = IntegerArgumentType.getInteger((CommandContext)context, (String)"time");
            for (DataScenes.SceneState state : DataScenes.StartedScenes.values()) {
                state.ticks = ticks;
            }
            return 1;
        })).then(CommandManager.argument((String)"name", (ArgumentType)StringArgumentType.string()).executes(context -> {
            String name = StringArgumentType.getString((CommandContext)context, (String)"name");
            DataScenes.SceneState state = DataScenes.StartedScenes.get(name.toLowerCase());
            if (state == null) {
                throw new CommandException((Text)Text.translatable((String)"Unknown scene name %s", (Object[])new Object[]{name}));
            }
            state.ticks = IntegerArgumentType.getInteger((CommandContext)context, (String)"time");
            ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.translatable((String)"Scene %s set to %s", (Object[])new Object[]{name, state.ticks}), false);
            return 1;
        }))))).then(((LiteralArgumentBuilder)CommandManager.literal((String)"reset").executes(context -> {
            DataScenes.Reset((ServerCommandSource)context.getSource(), null);
            return 1;
        })).then(CommandManager.argument((String)"name", (ArgumentType)StringArgumentType.string()).executes(context -> {
            DataScenes.Reset((ServerCommandSource)context.getSource(), StringArgumentType.getString((CommandContext)context, (String)"name"));
            return 1;
        })))).then(CommandManager.literal((String)"start").then(CommandManager.argument((String)"name", (ArgumentType)StringArgumentType.string()).executes(context -> {
            DataScenes.Start(((ServerCommandSource)context.getSource()).getServer(), StringArgumentType.getString((CommandContext)context, (String)"name"));
            return 1;
        })))).then(((LiteralArgumentBuilder)CommandManager.literal((String)"pause").executes(context -> {
            DataScenes.Pause((ServerCommandSource)context.getSource(), null);
            return 1;
        })).then(CommandManager.argument((String)"name", (ArgumentType)StringArgumentType.string()).executes(context -> {
            DataScenes.Pause((ServerCommandSource)context.getSource(), StringArgumentType.getString((CommandContext)context, (String)"name"));
            return 1;
        })));
        return command;
    }
}

