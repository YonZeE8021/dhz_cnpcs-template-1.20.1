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
import noppes.npcs.packets.PacketServerBasic;

public class SPacketPlayerCloseContainer
extends PacketServerBasic {
    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketPlayerCloseContainer msg, PacketByteBuf buf) {
    }

    public static SPacketPlayerCloseContainer decode(PacketByteBuf buf) {
        return new SPacketPlayerCloseContainer();
    }

    @Override
    protected void handle() {
        this.player.closeHandledScreen();
    }
}

