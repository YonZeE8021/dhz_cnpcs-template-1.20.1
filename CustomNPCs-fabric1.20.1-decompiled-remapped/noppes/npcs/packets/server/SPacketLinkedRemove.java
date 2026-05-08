/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import java.util.Vector;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiScrollList;

public class SPacketLinkedRemove
extends PacketServerBasic {
    private String name;

    public SPacketLinkedRemove(String name) {
        this.name = name;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_LINKED;
    }

    public static void encode(SPacketLinkedRemove msg, PacketByteBuf buf) {
        buf.writeString(msg.name, Short.MAX_VALUE);
    }

    public static SPacketLinkedRemove decode(PacketByteBuf buf) {
        return new SPacketLinkedRemove(buf.readString(Short.MAX_VALUE));
    }

    @Override
    protected void handle() {
        LinkedNpcController.Instance.removeData(this.name);
        Vector<String> list = new Vector<String>();
        for (LinkedNpcController.LinkedData data : LinkedNpcController.Instance.list) {
            list.add(data.name);
        }
        Packets.send(this.player, new PacketGuiScrollList(list));
    }
}

