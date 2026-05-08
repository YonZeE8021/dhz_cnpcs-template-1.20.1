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
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiUpdate
extends PacketBasic {
    public static void encode(PacketGuiUpdate msg, PacketByteBuf buf) {
    }

    public static PacketGuiUpdate decode(PacketByteBuf buf) {
        return new PacketGuiUpdate();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Screen gui = MinecraftClient.getInstance().currentScreen;
        if (gui == null) {
            return;
        }
        if (gui instanceof IGuiInterface) {
            IGuiInterface igui = (IGuiInterface)gui;
            igui.initGui();
        }
    }
}

