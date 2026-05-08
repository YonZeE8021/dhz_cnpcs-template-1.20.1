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

public class SPacketRemoteNpcReset
extends PacketServerBasic {
    private int entityId;

    public SPacketRemoteNpcReset(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_RESET;
    }

    public static void encode(SPacketRemoteNpcReset msg, PacketByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static SPacketRemoteNpcReset decode(PacketByteBuf buf) {
        return new SPacketRemoteNpcReset(buf.readInt());
    }

    @Override
    protected void handle() {
        Entity entity = this.player.getWorld().getEntityById(this.entityId);
        if (entity == null || !(entity instanceof EntityNPCInterface)) {
            return;
        }
        this.npc = (EntityNPCInterface)entity;
        this.npc.reset();
    }
}

