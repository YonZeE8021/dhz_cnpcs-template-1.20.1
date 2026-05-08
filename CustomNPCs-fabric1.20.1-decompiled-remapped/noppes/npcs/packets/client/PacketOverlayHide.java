/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.client.controllers.OverlayController;
import noppes.npcs.shared.common.PacketBasic;

public class PacketOverlayHide
extends PacketBasic {
    private final int id;

    public PacketOverlayHide(int id) {
        this.id = id;
    }

    public static void encode(PacketOverlayHide msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
    }

    public static PacketOverlayHide decode(PacketByteBuf buf) {
        return new PacketOverlayHide(buf.readInt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        OverlayController.getInstance().removeOverlay(this.id);
    }
}

