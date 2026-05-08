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
import noppes.npcs.api.wrapper.gui.CustomGuiAssetsSelectorWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketCustomGuiButton
extends PacketServerBasic {
    private final UUID buttonId;

    public SPacketCustomGuiButton(UUID id) {
        this.buttonId = id;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketCustomGuiButton msg, PacketByteBuf buf) {
        buf.writeUuid(msg.buttonId);
    }

    public static SPacketCustomGuiButton decode(PacketByteBuf buf) {
        return new SPacketCustomGuiButton(buf.readUuid());
    }

    @Override
    protected void handle() {
        ScreenHandler class_17032 = this.player.currentScreenHandler;
        if (class_17032 instanceof ContainerCustomGui) {
            PlayerWrapper p;
            ContainerCustomGui container = (ContainerCustomGui)class_17032;
            ICustomGuiComponent comp = container.activeGui.getComponentUuid(this.buttonId);
            if (comp instanceof CustomGuiButtonWrapper) {
                CustomGuiButtonWrapper button = (CustomGuiButtonWrapper)comp;
                p = (PlayerWrapper)NpcAPI.Instance().getIEntity((Entity)this.player);
                button.onPress(container.activeGui);
                EventHooks.onCustomGuiButton(p, container.activeGui, button);
            }
            if (comp instanceof CustomGuiAssetsSelectorWrapper) {
                CustomGuiAssetsSelectorWrapper assets = (CustomGuiAssetsSelectorWrapper)comp;
                p = (PlayerWrapper)NpcAPI.Instance().getIEntity((Entity)this.player);
                assets.onPress(container.activeGui);
            }
        }
    }
}

