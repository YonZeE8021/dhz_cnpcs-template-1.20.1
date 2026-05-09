/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.World
 *  net.minecraft.command.CommandException
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.server.command.CommandManager
 *  net.minecraft.command.argument.EntityArgumentType
 *  net.minecraft.text.Text
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityDialogNpc;

public class CmdDialog {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        LiteralArgumentBuilder command = CommandManager.literal((String)"dialog");
        command.then(((LiteralArgumentBuilder)CommandManager.literal((String)"reload").requires(source -> source.hasPermissionLevel(CustomNpcs.NoppesAdminPermissionLevel))).executes(context -> {
            new DialogController().load();
            SyncController.syncAllDialogs();
            return 1;
        }));
        command.then(((LiteralArgumentBuilder)CommandManager.literal((String)"read").requires(source -> source.hasPermissionLevel(CustomNpcs.NoppesCommandPermissionLevel))).then(CommandManager.argument((String)"players", (ArgumentType)EntityArgumentType.players()).then(CommandManager.argument((String)"dialog", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers((CommandContext)context, (String)"players");
            if (players.isEmpty()) {
                return 1;
            }
            Dialog dialog = DialogController.instance.dialogs.get(IntegerArgumentType.getInteger((CommandContext)context, (String)"dialog"));
            if (dialog == null) {
                throw new CommandException((Text)Text.literal((String)"Unknown DialogID"));
            }
            for (ServerPlayerEntity player : players) {
                PlayerData data = PlayerData.get((PlayerEntity)player);
                if (data.dialogData.dialogsRead.contains(dialog.id)) continue;
                data.dialogData.dialogsRead.add(dialog.id);
                data.save(true);
            }
            return 1;
        }))));
        command.then(((LiteralArgumentBuilder)CommandManager.literal((String)"unread").requires(source -> source.hasPermissionLevel(CustomNpcs.NoppesCommandPermissionLevel))).then(CommandManager.argument((String)"players", (ArgumentType)EntityArgumentType.players()).then(CommandManager.argument((String)"dialog", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers((CommandContext)context, (String)"players");
            if (players.isEmpty()) {
                return 1;
            }
            Dialog dialog = DialogController.instance.dialogs.get(IntegerArgumentType.getInteger((CommandContext)context, (String)"dialog"));
            if (dialog == null) {
                throw new CommandException((Text)Text.literal((String)"Unknown DialogID"));
            }
            for (ServerPlayerEntity player : players) {
                PlayerData data = PlayerData.get((PlayerEntity)player);
                if (!data.dialogData.dialogsRead.contains(dialog.id)) continue;
                data.dialogData.dialogsRead.remove(dialog.id);
                data.save(true);
            }
            return 1;
        }))));
        command.then(((LiteralArgumentBuilder)CommandManager.literal((String)"show").requires(source -> source.hasPermissionLevel(CustomNpcs.NoppesCommandPermissionLevel))).then(CommandManager.argument((String)"players", (ArgumentType)EntityArgumentType.players()).then(CommandManager.argument((String)"dialog", (ArgumentType)IntegerArgumentType.integer((int)0)).then(CommandManager.argument((String)"name", (ArgumentType)StringArgumentType.string()).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers((CommandContext)context, (String)"players");
            if (players.isEmpty()) {
                return 1;
            }
            Dialog dialog = DialogController.instance.dialogs.get(IntegerArgumentType.getInteger((CommandContext)context, (String)"dialog"));
            if (dialog == null) {
                throw new CommandException((Text)Text.literal((String)"Unknown DialogID"));
            }
            EntityDialogNpc npc = new EntityDialogNpc((World)((ServerCommandSource)context.getSource()).getWorld());
            DialogOption option = new DialogOption();
            option.dialogId = dialog.id;
            option.title = dialog.title;
            npc.dialogs.put(0, option);
            npc.display.setName(StringArgumentType.getString((CommandContext)context, (String)"name"));
            for (ServerPlayerEntity player : players) {
                EntityUtil.Copy((LivingEntity)player, (LivingEntity)npc);
                NoppesUtilServer.openDialog((PlayerEntity)player, npc, dialog);
            }
            return 1;
        })))));
        return command;
    }
}

