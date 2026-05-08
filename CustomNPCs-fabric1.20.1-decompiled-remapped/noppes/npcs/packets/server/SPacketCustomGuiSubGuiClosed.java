/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketCustomGuiSubGuiClosed
extends PacketServerBasic {
    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketCustomGuiSubGuiClosed msg, PacketByteBuf buf) {
    }

    public static SPacketCustomGuiSubGuiClosed decode(PacketByteBuf buf) {
        return new SPacketCustomGuiSubGuiClosed();
    }

    @Override
    protected void handle() {
        ScreenHandler class_17032 = this.player.currentScreenHandler;
        if (class_17032 instanceof ContainerCustomGui) {
            ContainerCustomGui container = (ContainerCustomGui)class_17032;
            if (container.customGui.hasSubGui()) {
                container.activeGui.close();
            }
        }
    }
}

