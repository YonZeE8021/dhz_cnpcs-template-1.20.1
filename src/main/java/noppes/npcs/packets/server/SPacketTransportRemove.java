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
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.server.SPacketTransportGet;

public class SPacketTransportRemove
extends PacketServerBasic {
    private int id;

    public SPacketTransportRemove(int id) {
        this.id = id;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_TRANSPORT;
    }

    public static void encode(SPacketTransportRemove msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketTransportRemove decode(PacketByteBuf buf) {
        return new SPacketTransportRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        TransportLocation loc = TransportController.getInstance().removeLocation(this.id);
        if (loc != null) {
            SPacketTransportGet.sendTransportData(this.player, loc.category.id);
        }
    }
}

