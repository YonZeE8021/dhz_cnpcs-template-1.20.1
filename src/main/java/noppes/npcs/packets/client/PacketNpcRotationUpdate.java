/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.client.MinecraftClient
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.client.MinecraftClient;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcRotationUpdate
extends PacketBasic {
    private final int id;
    private final int orientation;

    public PacketNpcRotationUpdate(int id, int orientation) {
        this.id = id;
        this.orientation = orientation;
    }

    public static void encode(PacketNpcRotationUpdate msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
        buf.writeInt(msg.orientation);
    }

    public static PacketNpcRotationUpdate decode(PacketByteBuf buf) {
        return new PacketNpcRotationUpdate(buf.readInt(), buf.readInt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Entity entity = MinecraftClient.getInstance().world.getEntityById(this.id);
        if (entity == null || !(entity instanceof EntityNPCInterface)) {
            return;
        }
        ((EntityNPCInterface)entity).ais.orientation = this.orientation;
    }
}

