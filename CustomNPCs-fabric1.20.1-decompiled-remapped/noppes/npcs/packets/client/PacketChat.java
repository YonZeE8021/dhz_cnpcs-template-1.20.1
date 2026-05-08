/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.text.Text
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import noppes.npcs.shared.common.PacketBasic;

public class PacketChat
extends PacketBasic {
    private final Text message;

    public PacketChat(Text message) {
        this.message = message;
    }

    public static void encode(PacketChat msg, PacketByteBuf buf) {
        buf.writeText(msg.message);
    }

    public static PacketChat decode(PacketByteBuf buf) {
        return new PacketChat(buf.readText());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        this.player.sendMessage(this.message);
    }
}

