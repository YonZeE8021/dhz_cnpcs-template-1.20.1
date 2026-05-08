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
import noppes.npcs.client.gui.select.GuiSoundSelection;
import noppes.npcs.shared.common.PacketBasic;

public class PacketSoundGUIOpen
extends PacketBasic {
    public static void encode(PacketSoundGUIOpen msg, PacketByteBuf buf) {
    }

    public static PacketSoundGUIOpen decode(PacketByteBuf buf) {
        return new PacketSoundGUIOpen();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        try {
            MinecraftClient minecraft = MinecraftClient.getInstance();
            minecraft.setScreen((Screen)new GuiSoundSelection(""));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

