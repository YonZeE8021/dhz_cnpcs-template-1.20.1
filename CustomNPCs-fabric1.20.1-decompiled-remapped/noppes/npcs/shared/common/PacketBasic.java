/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.client.MinecraftClient
 */
package noppes.npcs.shared.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;

public abstract class PacketBasic {
    public PlayerEntity player;

    public static void handle(PacketBasic msg) {
        msg.handleClient();
    }

    @Environment(value=EnvType.CLIENT)
    private void handleClient() {
        this.player = MinecraftClient.getInstance().player;
        MinecraftClient.getInstance().execute(this::handle);
    }

    protected abstract void handle();
}

