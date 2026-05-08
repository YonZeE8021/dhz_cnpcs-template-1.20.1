/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents$EndTick
 *  net.fabricmc.fabric.api.networking.v1.PacketSender
 *  net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents$Join
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.server.network.ServerPlayNetworkHandler
 *  net.minecraft.server.MinecraftServer
 */
package noppes.npcs;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerSkinData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncSkin;

public class SkinEventHandler
implements ServerTickEvents.EndTick,
ServerPlayConnectionEvents.Join {
    public void onEndTick(MinecraftServer server) {
        if (PlayerSkinData.needsAnyResync()) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                PlayerData playerData = PlayerData.get((PlayerEntity)player);
                if (!playerData.skinData.isActive() || !playerData.skinData.hasChanged()) continue;
                Packets.sendAll(new PacketSyncSkin(playerData.playername, playerData.skinData));
                playerData.skinData.markSynced();
            }
            PlayerSkinData.resyncPerformed();
        }
    }

    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.player;
        PlayerData playerData = PlayerData.get((PlayerEntity)player);
        if (playerData.skinData.isActive()) {
            Packets.sendAll(new PacketSyncSkin(playerData.playername, playerData.skinData));
        }
        for (ServerPlayerEntity otherPlayer : server.getPlayerManager().getPlayerList()) {
            PlayerData otherPlayerData = PlayerData.get((PlayerEntity)otherPlayer);
            if (!otherPlayerData.skinData.isActive()) continue;
            Packets.send(player, new PacketSyncSkin(otherPlayerData.playername, otherPlayerData.skinData));
        }
    }
}

