/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.entity.Entity
 *  net.minecraft.world.World
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.world.ClientWorld
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import noppes.npcs.controllers.PhysicsHelper;
import noppes.npcs.shared.common.PacketBasic;

public class PacketUpdatePhysics
extends PacketBasic {
    private final EntitySpawnS2CPacket pkt;
    private final int id;

    public PacketUpdatePhysics(Entity entity) {
        this.id = entity.getId();
        this.pkt = new EntitySpawnS2CPacket(entity);
    }

    public PacketUpdatePhysics(int id, EntitySpawnS2CPacket pkt) {
        this.id = id;
        this.pkt = pkt;
    }

    public static void encode(PacketUpdatePhysics msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
        msg.pkt.write(buf);
    }

    public static PacketUpdatePhysics decode(PacketByteBuf buf) {
        return new PacketUpdatePhysics(buf.readInt(), new EntitySpawnS2CPacket(buf));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        ClientWorld w = MinecraftClient.getInstance().world;
        Entity entity = w.getEntityById(this.id);
        if (entity == null) {
            MinecraftClient.getInstance().getNetworkHandler().onEntitySpawn(this.pkt);
        }
        if (PhysicsHelper.Enabled) {
            PhysicsHelper.resetEntityPhysics((World)w, this.id);
        }
    }
}

