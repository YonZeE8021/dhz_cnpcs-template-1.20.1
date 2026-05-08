/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.shared.client.gui.listeners.IGuiClose;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiClose
extends PacketBasic {
    private final NbtCompound data;

    public PacketGuiClose(NbtCompound data) {
        this.data = data;
    }

    public PacketGuiClose() {
        this(new NbtCompound());
    }

    public static void encode(PacketGuiClose msg, PacketByteBuf buf) {
        buf.writeNbt(msg.data);
    }

    public static PacketGuiClose decode(PacketByteBuf buf) {
        return new PacketGuiClose(buf.readNbt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Screen gui = MinecraftClient.getInstance().currentScreen;
        if (gui == null) {
            return;
        }
        if (gui instanceof IGuiClose) {
            ((IGuiClose)gui).setClose(this.data);
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.setScreen(null);
        mc.mouse.lockCursor();
    }
}

