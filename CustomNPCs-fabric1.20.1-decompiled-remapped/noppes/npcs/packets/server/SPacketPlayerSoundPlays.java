/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.EventHooks;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketPlayerSoundPlays
extends PacketServerBasic {
    private final String sound;
    private final String category;
    private final boolean looping;

    public SPacketPlayerSoundPlays(String sound, String category, boolean looping) {
        this.sound = sound;
        this.category = category;
        this.looping = looping;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketPlayerSoundPlays msg, PacketByteBuf buf) {
        buf.writeString(msg.sound == null ? "" : msg.sound);
        buf.writeString(msg.category == null ? "" : msg.category);
        buf.writeBoolean(msg.looping);
    }

    public static SPacketPlayerSoundPlays decode(PacketByteBuf buf) {
        return new SPacketPlayerSoundPlays(buf.readString(Short.MAX_VALUE), buf.readString(Short.MAX_VALUE), buf.readBoolean());
    }

    @Override
    protected void handle() {
        EventHooks.onPlayerPlaySound(this.player, this.sound, this.category, this.looping);
    }
}

