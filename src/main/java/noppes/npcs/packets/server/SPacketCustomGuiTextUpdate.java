/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import java.util.UUID;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiAssetsSelectorWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketCustomGuiTextUpdate
extends PacketServerBasic {
    private final UUID id;
    private final String text;

    public SPacketCustomGuiTextUpdate(UUID id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketCustomGuiTextUpdate msg, PacketByteBuf buf) {
        buf.writeUuid(msg.id);
        buf.writeString(msg.text, 131068);
    }

    public static SPacketCustomGuiTextUpdate decode(PacketByteBuf buf) {
        return new SPacketCustomGuiTextUpdate(buf.readUuid(), buf.readString(131068));
    }

    @Override
    protected void handle() {
        ScreenHandler class_17032 = this.player.currentScreenHandler;
        if (class_17032 instanceof ContainerCustomGui) {
            ContainerCustomGui container = (ContainerCustomGui)class_17032;
            ICustomGuiComponent comp = container.activeGui.getComponentUuid(this.id);
            if (comp instanceof CustomGuiTextFieldWrapper) {
                CustomGuiTextFieldWrapper tf = (CustomGuiTextFieldWrapper)comp;
                tf.setText(this.text);
                tf.onChange(container.activeGui);
            }
            if (comp instanceof CustomGuiAssetsSelectorWrapper) {
                CustomGuiAssetsSelectorWrapper as = (CustomGuiAssetsSelectorWrapper)comp;
                as.setSelected(this.text);
                as.onChange(container.activeGui);
            }
        }
    }
}

