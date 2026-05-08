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
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketCustomGuiFocusUpdate
extends PacketServerBasic {
    private final UUID id;
    private final boolean focus;

    public SPacketCustomGuiFocusUpdate(UUID id, boolean focus) {
        this.id = id;
        this.focus = focus;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketCustomGuiFocusUpdate msg, PacketByteBuf buf) {
        buf.writeUuid(msg.id);
        buf.writeBoolean(msg.focus);
    }

    public static SPacketCustomGuiFocusUpdate decode(PacketByteBuf buf) {
        return new SPacketCustomGuiFocusUpdate(buf.readUuid(), buf.readBoolean());
    }

    @Override
    protected void handle() {
        ScreenHandler class_17032 = this.player.currentScreenHandler;
        if (class_17032 instanceof ContainerCustomGui) {
            ContainerCustomGui container = (ContainerCustomGui)class_17032;
            ICustomGuiComponent comp = container.activeGui.getComponentUuid(this.id);
            if (comp instanceof CustomGuiTextFieldWrapper) {
                CustomGuiTextFieldWrapper tf = (CustomGuiTextFieldWrapper)comp;
                PlayerWrapper p = (PlayerWrapper)NpcAPI.Instance().getIEntity((Entity)this.player);
                tf.setFocused(this.focus);
                if (!this.focus) {
                    tf.onFocusLost(container.activeGui);
                }
            }
        }
    }
}

