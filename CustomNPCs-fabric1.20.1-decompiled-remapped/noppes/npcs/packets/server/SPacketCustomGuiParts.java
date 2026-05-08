/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketCustomGuiParts
extends PacketServerBasic {
    private final NbtCompound data;

    public SPacketCustomGuiParts(NbtCompound data) {
        this.data = data;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketCustomGuiParts msg, PacketByteBuf buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketCustomGuiParts decode(PacketByteBuf buf) {
        return new SPacketCustomGuiParts(buf.readNbt());
    }

    @Override
    protected void handle() {
        ScreenHandler class_17032 = this.player.currentScreenHandler;
        if (class_17032 instanceof ContainerCustomGui) {
            ContainerCustomGui container = (ContainerCustomGui)class_17032;
            container.customGui.npc.modelData.load(this.data);
            container.customGui.npc.updateClient = true;
        }
    }
}

