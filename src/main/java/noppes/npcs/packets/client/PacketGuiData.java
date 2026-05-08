/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtTagSizeTracker
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtTagSizeTracker;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiData
extends PacketBasic {
    private final NbtCompound data;

    public PacketGuiData(NbtCompound data) {
        this.data = data;
    }

    public static void encode(PacketGuiData msg, PacketByteBuf buf) {
        buf.writeNbt(msg.data);
    }

    public static PacketGuiData decode(PacketByteBuf buf) {
        return new PacketGuiData(buf.readNbt(new NbtTagSizeTracker(Long.MAX_VALUE)));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Screen gui = MinecraftClient.getInstance().currentScreen;
        if (gui == null) {
            return;
        }
        if (gui instanceof IGuiInterface && ((IGuiInterface)gui).hasSubGui()) {
            gui = ((IGuiInterface)gui).getSubGui();
        }
        if (gui instanceof IGuiData) {
            ((IGuiData)gui).setGuiData(this.data);
        }
    }
}

