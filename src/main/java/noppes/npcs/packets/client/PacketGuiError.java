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
import noppes.npcs.shared.client.gui.listeners.IGuiError;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiError
extends PacketBasic {
    private final int error;
    private final NbtCompound data;

    public PacketGuiError(int error, NbtCompound data) {
        this.error = error;
        this.data = data;
    }

    public static void encode(PacketGuiError msg, PacketByteBuf buf) {
        buf.writeInt(msg.error);
        buf.writeNbt(msg.data);
    }

    public static PacketGuiError decode(PacketByteBuf buf) {
        return new PacketGuiError(buf.readInt(), buf.readNbt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Screen gui = MinecraftClient.getInstance().currentScreen;
        if (gui == null || !(gui instanceof IGuiError)) {
            return;
        }
        ((IGuiError)gui).setError(this.error, this.data);
    }
}

