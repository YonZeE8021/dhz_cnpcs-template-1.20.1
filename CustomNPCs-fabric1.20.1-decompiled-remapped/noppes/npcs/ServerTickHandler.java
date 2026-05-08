/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$StartTick
 *  net.fabricmc.fabric.api.networking.v1.PacketSender
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents$Join
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.ScreenHandlerListener
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.scoreboard.ScoreboardObjective
 *  net.minecraft.scoreboard.ScoreboardPlayerScore
 *  net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket
 *  net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket
 *  net.minecraft.scoreboard.ServerScoreboard
 *  net.minecraft.scoreboard.ServerScoreboard$UpdateMode
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.server.network.ServerPlayNetworkHandler
 *  net.minecraft.server.MinecraftServer
 */
package noppes.npcs;

import java.util.ArrayList;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.CustomItems;
import noppes.npcs.NPCSpawning;
import noppes.npcs.controllers.MassBlockController;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.controllers.VisibilityController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSync;

public class ServerTickHandler
implements ServerTickEvents.StartTick,
ServerPlayConnectionEvents.Join {
    public int ticks = 0;

    public void onStartTick(MinecraftServer server) {
        if (this.ticks++ >= 20) {
            SchematicController.Instance.updateBuilding();
            MassBlockController.Update();
            this.ticks = 0;
            for (DataScenes.SceneState state : DataScenes.StartedScenes.values()) {
                if (state.paused) continue;
                ++state.ticks;
            }
            for (DataScenes.SceneContainer entry : DataScenes.ScenesToRun) {
                entry.update();
            }
            DataScenes.ScenesToRun = new ArrayList<DataScenes.SceneContainer>();
        }
        for (ServerWorld level : server.getWorlds()) {
            NPCSpawning.findChunksForSpawning(level);
        }
        for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
            PlayerData data = PlayerData.get(player);
            if (player.getEntityWorld().getTimeOfDay() % 24000L == 1L || player.getEntityWorld().getTimeOfDay() % 240000L == 12001L) {
                VisibilityController.instance.onUpdate((ServerPlayerEntity)player);
            }
            if (data.updateClient) {
                Packets.send((ServerPlayerEntity)player, new PacketSync(8, data.getSyncNBT(), true));
                VisibilityController.instance.onUpdate((ServerPlayerEntity)player);
                data.updateClient = false;
            }
            if (data.prevHeldItem != player.getMainHandStack() && (data.prevHeldItem.getItem() == CustomItems.wand || player.getMainHandStack().getItem() == CustomItems.wand)) {
                VisibilityController.instance.onUpdate((ServerPlayerEntity)player);
            }
            data.prevHeldItem = player.getMainHandStack();
        }
    }

    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        final ServerPlayerEntity player = handler.player;
        for (ServerWorld level : server.getWorlds()) {
            ServerScoreboard board = level.getScoreboard();
            for (String objective : Availability.scores) {
                ScoreboardObjective so = board.getNullableObjective(objective);
                if (so == null) continue;
                if (board.getSlot(so) == 0) {
                    player.networkHandler.sendPacket((Packet)new ScoreboardObjectiveUpdateS2CPacket(so, 0));
                }
                ScoreboardPlayerScore sco = board.getPlayerScore(player.getEntityName(), so);
                player.networkHandler.sendPacket((Packet)new ScoreboardPlayerUpdateS2CPacket(ServerScoreboard.UpdateMode.field_13431, so.getName(), sco.getPlayerName(), sco.getScore()));
            }
        }
        player.playerScreenHandler.addListener(new ScreenHandlerListener(){

            public void onSlotUpdate(ScreenHandler container, int slotInd, ItemStack stack) {
                if (player.getWorld().isClient) {
                    return;
                }
                PlayerQuestData playerdata = PlayerData.get((PlayerEntity)player).questData;
                playerdata.checkQuestCompletion((PlayerEntity)player, 0);
            }

            public void onPropertyUpdate(ScreenHandler container, int varToUpdate, int newValue) {
            }
        });
        PlayerData data = PlayerData.get((PlayerEntity)handler.player);
        String serverName = "local";
        if (server.isDedicated()) {
            serverName = "server";
        } else if (server.isRemote()) {
            serverName = "lan";
        }
        SyncController.syncPlayer(handler.player);
    }
}

