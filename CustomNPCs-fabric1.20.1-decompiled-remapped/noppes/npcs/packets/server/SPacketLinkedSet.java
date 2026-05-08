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
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketLinkedSet
extends PacketServerBasic {
    private String name;

    public SPacketLinkedSet(String name) {
        this.name = name;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketLinkedSet msg, PacketByteBuf buf) {
        buf.writeString(msg.name, Short.MAX_VALUE);
    }

    public static SPacketLinkedSet decode(PacketByteBuf buf) {
        return new SPacketLinkedSet(buf.readString(Short.MAX_VALUE));
    }

    @Override
    protected void handle() {
        this.npc.linkedName = this.name;
        LinkedNpcController.Instance.loadNpcData(this.npc);
    }
}

