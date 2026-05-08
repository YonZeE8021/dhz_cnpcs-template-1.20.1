/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketPlayMusic
extends PacketBasic {
    private final String name;
    private final boolean streaming;
    private final boolean looping;

    public PacketPlayMusic(String name, boolean streaming, boolean looping) {
        this.name = name;
        this.streaming = streaming;
        this.looping = looping;
    }

    public static void encode(PacketPlayMusic msg, PacketByteBuf buf) {
        buf.writeString(msg.name);
        buf.writeBoolean(msg.streaming);
        buf.writeBoolean(msg.looping);
    }

    public static PacketPlayMusic decode(PacketByteBuf buf) {
        return new PacketPlayMusic(buf.readString(Short.MAX_VALUE), buf.readBoolean(), buf.readBoolean());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        if (this.streaming) {
            MusicController.Instance.playStreaming(this.name, (Entity)this.player, this.looping);
        } else {
            MusicController.Instance.playMusic(this.name, (Entity)this.player, this.looping);
        }
    }
}

