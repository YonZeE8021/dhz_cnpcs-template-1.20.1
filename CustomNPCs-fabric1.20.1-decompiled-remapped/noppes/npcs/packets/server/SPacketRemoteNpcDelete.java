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
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketNpcDelete;
import noppes.npcs.packets.server.SPacketRemoteNpcsGet;

public class SPacketRemoteNpcDelete
extends PacketServerBasic {
    private int entityId;

    public SPacketRemoteNpcDelete(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_DELETE;
    }

    public static void encode(SPacketRemoteNpcDelete msg, PacketByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static SPacketRemoteNpcDelete decode(PacketByteBuf buf) {
        return new SPacketRemoteNpcDelete(buf.readInt());
    }

    @Override
    protected void handle() {
        Entity entity = this.player.getWorld().getEntityById(this.entityId);
        if (entity == null || !(entity instanceof EntityNPCInterface)) {
            return;
        }
        this.npc = (EntityNPCInterface)entity;
        this.npc.delete();
        Packets.sendNearby((Entity)this.npc, new PacketNpcDelete(this.npc.getId()));
        SPacketRemoteNpcsGet.sendNearbyNpcs(this.player);
    }
}

