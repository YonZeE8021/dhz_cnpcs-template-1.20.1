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
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerFactionData;

public class CmdFaction {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        LiteralArgumentBuilder command = (LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal((String)"faction").requires(source -> source.hasPermissionLevel(2))).then(CommandManager.argument((String)"players", (ArgumentType)EntityArgumentType.players()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)CommandManager.argument((String)"faction", (ArgumentType)IntegerArgumentType.integer((int)0)).then(CommandManager.literal((String)"add").then(CommandManager.argument((String)"points", (ArgumentType)IntegerArgumentType.integer()).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers((CommandContext)context, (String)"players");
            if (players.isEmpty()) {
                return 1;
            }
            Faction faction = FactionController.instance.factions.get(IntegerArgumentType.getInteger((CommandContext)context, (String)"faction"));
            if (faction == null) {
                throw new CommandException((Text)Text.literal((String)"Unknown FactionID"));
            }
            int points = IntegerArgumentType.getInteger((CommandContext)context, (String)"points");
            for (ServerPlayerEntity player : players) {
                PlayerData data = PlayerData.get((PlayerEntity)player);
                PlayerFactionData playerfactiondata = data.factionData;
                playerfactiondata.increasePoints((PlayerEntity)player, faction.id, points);
                data.save(true);
            }
            return 1;
        })))).then(CommandManager.literal((String)"set").then(CommandManager.argument((String)"points", (ArgumentType)IntegerArgumentType.integer()).executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers((CommandContext)context, (String)"players");
            if (players.isEmpty()) {
                return 1;
            }
            Faction faction = FactionController.instance.factions.get(IntegerArgumentType.getInteger((CommandContext)context, (String)"faction"));
            if (faction == null) {
                throw new CommandException((Text)Text.literal((String)"Unknown FactionID"));
            }
            int points = IntegerArgumentType.getInteger((CommandContext)context, (String)"points");
            for (ServerPlayerEntity player : players) {
                PlayerData data = PlayerData.get((PlayerEntity)player);
                PlayerFactionData playerfactiondata = data.factionData;
                playerfactiondata.factionData.put(faction.id, points);
                data.save(true);
            }
            return 1;
        })))).then(CommandManager.literal((String)"reset").executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers((CommandContext)context, (String)"players");
            if (players.isEmpty()) {
                return 1;
            }
            Faction faction = FactionController.instance.factions.get(IntegerArgumentType.getInteger((CommandContext)context, (String)"faction"));
            if (faction == null) {
                throw new CommandException((Text)Text.literal((String)"Unknown FactionID"));
            }
            for (ServerPlayerEntity player : players) {
                PlayerData data = PlayerData.get((PlayerEntity)player);
                data.factionData.factionData.put(faction.id, faction.defaultPoints);
                data.save(true);
            }
            return 1;
        }))).then(CommandManager.literal((String)"drop").executes(context -> {
            Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers((CommandContext)context, (String)"players");
            if (players.isEmpty()) {
                return 1;
            }
            Faction faction = FactionController.instance.factions.get(IntegerArgumentType.getInteger((CommandContext)context, (String)"faction"));
            if (faction == null) {
                throw new CommandException((Text)Text.literal((String)"Unknown FactionID"));
            }
            for (ServerPlayerEntity player : players) {
                PlayerData data = PlayerData.get((PlayerEntity)player);
                data.factionData.factionData.remove(faction.id);
                data.save(true);
            }
            return 1;
        }))));
        return command;
    }
}

