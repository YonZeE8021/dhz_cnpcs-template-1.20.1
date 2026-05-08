/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.entity.player.PlayerEntity
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
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketAchievement;
import noppes.npcs.packets.client.PacketChat;

public class CmdQuest {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        LiteralArgumentBuilder<ServerCommandSource> command = CommandManager.literal((String)"quest");
        command.then(CommandManager.literal((String)"start").requires((ServerCommandSource source) -> source.hasPermissionLevel(2)).then(CommandManager.argument((String)"players", (ArgumentType)EntityArgumentType.players()).then(CommandManager.argument((String)"quest", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers((CommandContext)context, (String)"players");
            if (players.isEmpty()) {
                return 1;
            }
            Quest quest = QuestController.instance.quests.get(IntegerArgumentType.getInteger((CommandContext)context, (String)"quest"));
            if (quest == null) {
                throw new CommandException((Text)Text.literal((String)"Unknown QuestID"));
            }
            for (ServerPlayerEntity player : players) {
                PlayerData data = PlayerData.get((PlayerEntity)player);
                QuestData questdata = new QuestData(quest);
                data.questData.activeQuests.put(quest.id, questdata);
                data.save(true);
                Packets.send(player, new PacketAchievement((Text)Text.translatable((String)"quest.newquest"), (Text)Text.translatable((String)quest.title), 2));
                Packets.send(player, new PacketChat((Text)Text.translatable((String)"quest.newquest").append(":").append((Text)Text.translatable((String)quest.title))));
            }
            return 1;
        }))));
        command.then(CommandManager.literal((String)"finish").requires((ServerCommandSource source) -> source.hasPermissionLevel(2)).then(CommandManager.argument((String)"players", (ArgumentType)EntityArgumentType.players()).then(CommandManager.argument((String)"quest", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers((CommandContext)context, (String)"players");
            if (players.isEmpty()) {
                return 1;
            }
            Quest quest = QuestController.instance.quests.get(IntegerArgumentType.getInteger((CommandContext)context, (String)"quest"));
            if (quest == null) {
                throw new CommandException((Text)Text.literal((String)"Unknown QuestID"));
            }
            for (ServerPlayerEntity player : players) {
                PlayerData data = PlayerData.get((PlayerEntity)player);
                data.questData.finishedQuests.put(quest.id, System.currentTimeMillis());
                data.save(true);
            }
            return 1;
        }))));
        command.then(CommandManager.literal((String)"stop").requires((ServerCommandSource source) -> source.hasPermissionLevel(2)).then(CommandManager.argument((String)"players", (ArgumentType)EntityArgumentType.players()).then(CommandManager.argument((String)"quest", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers((CommandContext)context, (String)"players");
            if (players.isEmpty()) {
                return 1;
            }
            Quest quest = QuestController.instance.quests.get(IntegerArgumentType.getInteger((CommandContext)context, (String)"quest"));
            if (quest == null) {
                throw new CommandException((Text)Text.literal((String)"Unknown QuestID"));
            }
            for (ServerPlayerEntity player : players) {
                PlayerData data = PlayerData.get((PlayerEntity)player);
                data.questData.activeQuests.remove(quest.id);
                data.save(true);
            }
            return 1;
        }))));
        command.then(CommandManager.literal((String)"remove").requires((ServerCommandSource source) -> source.hasPermissionLevel(2)).then(CommandManager.argument((String)"players", (ArgumentType)EntityArgumentType.players()).then(CommandManager.argument((String)"quest", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers((CommandContext)context, (String)"players");
            if (players.isEmpty()) {
                return 1;
            }
            Quest quest = QuestController.instance.quests.get(IntegerArgumentType.getInteger((CommandContext)context, (String)"quest"));
            if (quest == null) {
                throw new CommandException((Text)Text.literal((String)"Unknown QuestID"));
            }
            for (ServerPlayerEntity player : players) {
                PlayerData data = PlayerData.get((PlayerEntity)player);
                data.questData.activeQuests.remove(quest.id);
                data.questData.finishedQuests.remove(quest.id);
                data.save(true);
            }
            return 1;
        }))));
        command.then(CommandManager.literal((String)"objective").requires((ServerCommandSource source) -> source.hasPermissionLevel(2)).then(CommandManager.argument((String)"players", (ArgumentType)EntityArgumentType.players()).then(((RequiredArgumentBuilder)CommandManager.argument((String)"quest", (ArgumentType)IntegerArgumentType.integer((int)0)).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers((CommandContext)context, (String)"players");
            if (players.isEmpty()) {
                return 1;
            }
            Quest quest = QuestController.instance.quests.get(IntegerArgumentType.getInteger((CommandContext)context, (String)"quest"));
            if (quest == null) {
                throw new CommandException((Text)Text.literal((String)"Unknown QuestID"));
            }
            for (ServerPlayerEntity player : players) {
                IQuestObjective[] objectives;
                PlayerData data = PlayerData.get((PlayerEntity)player);
                if (!data.questData.activeQuests.containsKey(quest.id)) continue;
                for (IQuestObjective ob : objectives = quest.questInterface.getObjectives((PlayerEntity)player)) {
                    player.sendMessage(ob.getMCText());
                }
            }
            return 1;
        })).then(((RequiredArgumentBuilder)CommandManager.argument((String)"objective", (ArgumentType)IntegerArgumentType.integer((int)0, (int)3)).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers((CommandContext)context, (String)"players");
            if (players.isEmpty()) {
                return 1;
            }
            Quest quest = QuestController.instance.quests.get(IntegerArgumentType.getInteger((CommandContext)context, (String)"quest"));
            if (quest == null) {
                throw new CommandException((Text)Text.literal((String)"Unknown QuestID"));
            }
            int objective = IntegerArgumentType.getInteger((CommandContext)context, (String)"objective");
            for (ServerPlayerEntity player : players) {
                IQuestObjective[] objectives;
                PlayerData data = PlayerData.get((PlayerEntity)player);
                if (!data.questData.activeQuests.containsKey(quest.id) || objective >= (objectives = quest.questInterface.getObjectives((PlayerEntity)player)).length) continue;
                player.sendMessage(objectives[objective].getMCText());
            }
            return 1;
        })).then(CommandManager.argument((String)"value", (ArgumentType)IntegerArgumentType.integer()).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers((CommandContext)context, (String)"players");
            if (players.isEmpty()) {
                return 1;
            }
            Quest quest = QuestController.instance.quests.get(IntegerArgumentType.getInteger((CommandContext)context, (String)"quest"));
            if (quest == null) {
                throw new CommandException((Text)Text.literal((String)"Unknown QuestID"));
            }
            int objective = IntegerArgumentType.getInteger((CommandContext)context, (String)"objective");
            int value = IntegerArgumentType.getInteger((CommandContext)context, (String)"value");
            for (ServerPlayerEntity player : players) {
                IQuestObjective[] objectives;
                PlayerData data = PlayerData.get((PlayerEntity)player);
                if (!data.questData.activeQuests.containsKey(quest.id) || objective >= (objectives = quest.questInterface.getObjectives((PlayerEntity)player)).length) continue;
                objectives[objective].setProgress(value);
            }
            return 1;
        }))))));
        command.then(CommandManager.literal((String)"reload").requires((ServerCommandSource source) -> source.hasPermissionLevel(4)).executes(context -> {
            new QuestController().load();
            SyncController.syncAllQuests();
            return 1;
        }));
        return command;
    }
}

