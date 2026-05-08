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
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNpcDialogRemove
extends PacketServerBasic {
    private int slot;

    public SPacketNpcDialogRemove(int slot) {
        this.slot = slot;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketNpcDialogRemove msg, PacketByteBuf buf) {
        buf.writeInt(msg.slot);
    }

    public static SPacketNpcDialogRemove decode(PacketByteBuf buf) {
        return new SPacketNpcDialogRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        this.npc.dialogs.remove(this.slot);
    }
}

