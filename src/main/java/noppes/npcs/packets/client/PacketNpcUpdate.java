/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.entity.Entity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.client.MinecraftClient
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.client.MinecraftClient;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcUpdate
extends PacketBasic {
    private final int id;
    private final NbtCompound data;

    public PacketNpcUpdate(int id, NbtCompound data) {
        this.id = id;
        this.data = data;
    }

    public static void encode(PacketNpcUpdate msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
        buf.writeNbt(msg.data);
    }

    public static PacketNpcUpdate decode(PacketByteBuf buf) {
        return new PacketNpcUpdate(buf.readInt(), buf.readNbt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Entity entity = MinecraftClient.getInstance().world.getEntityById(this.id);
        if (entity == null || !(entity instanceof EntityNPCInterface)) {
            return;
        }
        ((EntityNPCInterface)entity).readSpawnData(this.data);
    }
}

