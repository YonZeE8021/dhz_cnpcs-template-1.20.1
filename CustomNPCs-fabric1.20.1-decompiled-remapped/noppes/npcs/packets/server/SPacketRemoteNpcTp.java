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
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketRemoteNpcTp
extends PacketServerBasic {
    private int entityId;

    public SPacketRemoteNpcTp(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(SPacketRemoteNpcTp msg, PacketByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static SPacketRemoteNpcTp decode(PacketByteBuf buf) {
        return new SPacketRemoteNpcTp(buf.readInt());
    }

    @Override
    protected void handle() {
        Entity entity = this.player.getWorld().getEntityById(this.entityId);
        if (entity == null || !(entity instanceof EntityNPCInterface)) {
            return;
        }
        this.npc = (EntityNPCInterface)entity;
        this.player.networkHandler.requestTeleport(this.npc.getX(), this.npc.getY(), this.npc.getZ(), 0.0f, 0.0f);
    }
}

