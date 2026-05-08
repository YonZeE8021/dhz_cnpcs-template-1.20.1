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

public class SPacketTransportCategorySave
extends PacketServerBasic {
    private int id;
    private String name;

    public SPacketTransportCategorySave(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_TRANSPORT;
    }

    public static void encode(SPacketTransportCategorySave msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
        buf.writeString(msg.name);
    }

    public static SPacketTransportCategorySave decode(PacketByteBuf buf) {
        return new SPacketTransportCategorySave(buf.readInt(), buf.readString(Short.MAX_VALUE));
    }

    @Override
    protected void handle() {
        TransportController.getInstance().saveCategory(this.name, this.id);
    }
}

