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
import noppes.npcs.controllers.DialogController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketDialogCategoryRemove
extends PacketServerBasic {
    private int id;

    public SPacketDialogCategoryRemove(int id) {
        this.id = id;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_DIALOG;
    }

    public static void encode(SPacketDialogCategoryRemove msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketDialogCategoryRemove decode(PacketByteBuf buf) {
        return new SPacketDialogCategoryRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        DialogController.instance.removeCategory(this.id);
        Packets.send(this.player, new PacketGuiUpdate());
    }
}

