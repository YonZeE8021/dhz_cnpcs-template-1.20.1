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

import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.api.wrapper.gui.CustomGuiComponentWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import noppes.npcs.shared.common.PacketBasic;

public class PacketGuiComponentUpdate
extends PacketBasic {
    private UUID id;
    private NbtCompound data;

    public PacketGuiComponentUpdate(UUID id, NbtCompound data) {
        this.id = id;
        this.data = data;
    }

    public static void encode(PacketGuiComponentUpdate msg, PacketByteBuf buf) {
        buf.writeUuid(msg.id);
        buf.writeNbt(msg.data);
    }

    public static PacketGuiComponentUpdate decode(PacketByteBuf buf) {
        return new PacketGuiComponentUpdate(buf.readUuid(), buf.readNbt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Screen gui = MinecraftClient.getInstance().currentScreen;
        if (gui == null) {
            return;
        }
        if (gui instanceof GuiCustom) {
            GuiCustom cgui = (GuiCustom)gui;
            CustomGuiComponentWrapper component = (CustomGuiComponentWrapper)cgui.guiWrapper.getComponentUuid(this.id);
            component.fromNBT(this.data);
            IGuiComponent guic = cgui.getComponent(this.id);
            guic.init();
        }
    }
}

