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
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.server.command.CommandManager
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.text.Text
 */
package noppes.npcs.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.math.Vec3d;
import net.minecraft.text.Text;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.controllers.ScriptController;

public class CmdScript {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        LiteralArgumentBuilder command = (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal((String)"script").requires(source -> source.hasPermissionLevel(CustomNpcs.NoppesCommandOpOnly ? 4 : 2))).then(CommandManager.literal((String)"reload").executes(context -> {
            ScriptController.Instance.loadCategories();
            if (ScriptController.Instance.loadPlayerScripts()) {
                ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)"Reload player scripts succesfully"), false);
            } else {
                ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)"Failed reloading player scripts"), false);
            }
            if (ScriptController.Instance.loadForgeScripts()) {
                ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)"Reload forge scripts succesfully"), false);
            } else {
                ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)"Failed reloading forge scripts"), false);
            }
            if (ScriptController.Instance.loadStoredData()) {
                ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)"Reload stored data succesfully"), false);
            } else {
                ((ServerCommandSource)context.getSource()).sendFeedback(() -> Text.literal((String)"Failed reloading stored data"), false);
            }
            return 1;
        }))).then(CommandManager.literal((String)"trigger").then(((RequiredArgumentBuilder)CommandManager.argument((String)"id", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(context -> {
            IWorld level = NpcAPI.Instance().getIWorld(((ServerCommandSource)context.getSource()).getWorld());
            Vec3d bpos = ((ServerCommandSource)context.getSource()).getPosition();
            IPos pos = NpcAPI.Instance().getIPos(bpos.x, bpos.y, bpos.z);
            int id = IntegerArgumentType.getInteger((CommandContext)context, (String)"id");
            IEntity e = NpcAPI.Instance().getIEntity(((ServerCommandSource)context.getSource()).getEntity());
            EventHooks.onScriptTriggerEvent(id, level, pos, e, new String[0]);
            return 1;
        })).then(CommandManager.argument((String)"args", (ArgumentType)StringArgumentType.greedyString()).executes(context -> {
            IWorld level = NpcAPI.Instance().getIWorld(((ServerCommandSource)context.getSource()).getWorld());
            Vec3d bpos = ((ServerCommandSource)context.getSource()).getPosition();
            IPos pos = NpcAPI.Instance().getIPos(bpos.x, bpos.y, bpos.z);
            IEntity e = NpcAPI.Instance().getIEntity(((ServerCommandSource)context.getSource()).getEntity());
            int id = IntegerArgumentType.getInteger((CommandContext)context, (String)"id");
            EventHooks.onScriptTriggerEvent(id, level, pos, e, StringArgumentType.getString((CommandContext)context, (String)"args").split(" "));
            return 1;
        }))));
        return command;
    }
}

