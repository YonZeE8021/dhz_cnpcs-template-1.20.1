/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.world.ClientWorld
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcVisibleFalse
extends PacketBasic {
    private final int id;

    public PacketNpcVisibleFalse(int id) {
        this.id = id;
    }

    public static void encode(PacketNpcVisibleFalse msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
    }

    public static PacketNpcVisibleFalse decode(PacketByteBuf buf) {
        return new PacketNpcVisibleFalse(buf.readInt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        ClientWorld w = MinecraftClient.getInstance().world;
        Entity entity = w.getEntityById(this.id);
        if (entity == null || !(entity instanceof EntityNPCInterface)) {
            return;
        }
        w.removeEntity(this.id, Entity.RemovalReason.DISCARDED);
    }
}

