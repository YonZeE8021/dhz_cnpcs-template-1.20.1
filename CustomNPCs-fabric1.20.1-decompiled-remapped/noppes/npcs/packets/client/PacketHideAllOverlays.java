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

public class PacketHideAllOverlays
extends PacketBasic {
    private final boolean id;

    public PacketHideAllOverlays(boolean id) {
        this.id = id;
    }

    public static void encode(PacketHideAllOverlays msg, PacketByteBuf buf) {
        buf.writeBoolean(msg.id);
    }

    public static PacketHideAllOverlays decode(PacketByteBuf buf) {
        return new PacketHideAllOverlays(buf.readBoolean());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        OverlayController.getInstance().clear();
    }
}

