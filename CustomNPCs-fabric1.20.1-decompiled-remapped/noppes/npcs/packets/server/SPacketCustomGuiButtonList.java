/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonListWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketCustomGuiButtonList
extends PacketServerBasic {
    private final UUID buttonId;
    private final boolean isRightClick;

    public SPacketCustomGuiButtonList(UUID id, boolean isRightClick) {
        this.buttonId = id;
        this.isRightClick = isRightClick;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketCustomGuiButtonList msg, PacketByteBuf buf) {
        buf.writeUuid(msg.buttonId);
        buf.writeBoolean(msg.isRightClick);
    }

    public static SPacketCustomGuiButtonList decode(PacketByteBuf buf) {
        return new SPacketCustomGuiButtonList(buf.readUuid(), buf.readBoolean());
    }

    @Override
    protected void handle() {
        ScreenHandler class_17032 = this.player.currentScreenHandler;
        if (class_17032 instanceof ContainerCustomGui) {
            ContainerCustomGui container = (ContainerCustomGui)class_17032;
            ICustomGuiComponent comp = container.activeGui.getComponentUuid(this.buttonId);
            if (comp instanceof CustomGuiButtonListWrapper) {
                CustomGuiButtonListWrapper button = (CustomGuiButtonListWrapper)comp;
                PlayerWrapper p = (PlayerWrapper)NpcAPI.Instance().getIEntity((Entity)this.player);
                button.setSelected(button.getSelected() + (this.isRightClick ? 1 : -1));
                button.onPress(container.activeGui);
                EventHooks.onCustomGuiButton(p, container.activeGui, button);
            }
        }
    }
}

