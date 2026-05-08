/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketNpcDelete;

public class SPacketNpcDelete
extends PacketServerBasic {
    @Override
    public boolean requiresNpc() {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_DELETE;
    }

    public static void encode(SPacketNpcDelete msg, PacketByteBuf buf) {
    }

    public static SPacketNpcDelete decode(PacketByteBuf buf) {
        return new SPacketNpcDelete();
    }

    @Override
    protected void handle() {
        Packets.sendNearby((Entity)this.npc, new PacketNpcDelete(this.npc.getId()));
        this.npc.delete();
    }
}

