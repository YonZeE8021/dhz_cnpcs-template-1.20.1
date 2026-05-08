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

public class SPacketLinkedAdd
extends PacketServerBasic {
    private String name;

    public SPacketLinkedAdd(String name) {
        this.name = name;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_LINKED;
    }

    public static void encode(SPacketLinkedAdd msg, PacketByteBuf buf) {
        buf.writeString(msg.name, Short.MAX_VALUE);
    }

    public static SPacketLinkedAdd decode(PacketByteBuf buf) {
        return new SPacketLinkedAdd(buf.readString(Short.MAX_VALUE));
    }

    @Override
    protected void handle() {
        LinkedNpcController.Instance.addData(this.name);
        Vector<String> list = new Vector<String>();
        for (LinkedNpcController.LinkedData data : LinkedNpcController.Instance.list) {
            list.add(data.name);
        }
        Packets.send(this.player, new PacketGuiScrollList(list));
    }
}

