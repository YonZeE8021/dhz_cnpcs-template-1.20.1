/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.sound.SoundCategory
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketPlaySound
extends PacketBasic {
    private final String name;
    private final BlockPos pos;
    private final float volume;
    private final float pitch;

    public PacketPlaySound(String name, BlockPos pos, float volume, float pitch) {
        this.name = name;
        this.pos = pos;
        this.volume = volume;
        this.pitch = pitch;
    }

    public static void encode(PacketPlaySound msg, PacketByteBuf buf) {
        buf.writeString(msg.name);
        buf.writeBlockPos(msg.pos);
        buf.writeFloat(msg.volume);
        buf.writeFloat(msg.pitch);
    }

    public static PacketPlaySound decode(PacketByteBuf buf) {
        return new PacketPlaySound(buf.readString(Short.MAX_VALUE), buf.readBlockPos(), buf.readFloat(), buf.readFloat());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        MusicController.Instance.playSound(SoundCategory.field_15246, this.name, this.pos, this.volume, this.pitch);
    }
}

