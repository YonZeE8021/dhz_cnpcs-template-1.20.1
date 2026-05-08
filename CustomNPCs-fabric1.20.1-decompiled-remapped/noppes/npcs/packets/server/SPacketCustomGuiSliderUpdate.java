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
import noppes.npcs.api.wrapper.gui.CustomGuiSliderWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketCustomGuiSliderUpdate
extends PacketServerBasic {
    private final UUID id;
    private final float value;

    public SPacketCustomGuiSliderUpdate(UUID id, float value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketCustomGuiSliderUpdate msg, PacketByteBuf buf) {
        buf.writeUuid(msg.id);
        buf.writeFloat(msg.value);
    }

    public static SPacketCustomGuiSliderUpdate decode(PacketByteBuf buf) {
        return new SPacketCustomGuiSliderUpdate(buf.readUuid(), buf.readFloat());
    }

    @Override
    protected void handle() {
        ScreenHandler class_17032 = this.player.currentScreenHandler;
        if (class_17032 instanceof ContainerCustomGui) {
            ContainerCustomGui container = (ContainerCustomGui)class_17032;
            ICustomGuiComponent comp = container.activeGui.getComponentUuid(this.id);
            if (comp instanceof CustomGuiSliderWrapper) {
                CustomGuiSliderWrapper slider = (CustomGuiSliderWrapper)comp;
                slider.setValue(this.value);
                slider.onChange(container.activeGui);
            }
        }
    }
}

