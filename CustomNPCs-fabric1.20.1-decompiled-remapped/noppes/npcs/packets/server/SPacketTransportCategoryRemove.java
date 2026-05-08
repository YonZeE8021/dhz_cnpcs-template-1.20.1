/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.server.SPacketTransportCategoriesGet;

public class SPacketTransportCategoryRemove
extends PacketServerBasic {
    private int id;

    public SPacketTransportCategoryRemove(int id) {
        this.id = id;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_TRANSPORT;
    }

    public static void encode(SPacketTransportCategoryRemove msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketTransportCategoryRemove decode(PacketByteBuf buf) {
        return new SPacketTransportCategoryRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        TransportController.getInstance().removeCategory(this.id);
        SPacketTransportCategoriesGet.sendTransportCategoryData(this.player);
    }
}

