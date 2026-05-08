/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiScrollSelected
extends PacketBasic {
    private final String selected;

    public PacketGuiScrollSelected(String selected) {
        this.selected = selected;
    }

    public static void encode(PacketGuiScrollSelected msg, PacketByteBuf buf) {
        buf.writeString(msg.selected);
    }

    public static PacketGuiScrollSelected decode(PacketByteBuf buf) {
        return new PacketGuiScrollSelected(buf.readString(Short.MAX_VALUE));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Screen gui = MinecraftClient.getInstance().currentScreen;
        if (gui == null || !(gui instanceof IScrollData)) {
            return;
        }
        ((IScrollData)gui).setSelected(this.selected);
    }
}

