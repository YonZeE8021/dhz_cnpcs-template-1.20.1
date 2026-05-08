/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.client.MinecraftClient
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.client.MinecraftClient;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.shared.common.PacketBasic;

public class PacketMarkData
extends PacketBasic {
    private final int id;
    private final NbtCompound data;

    public PacketMarkData(int id, NbtCompound data) {
        this.id = id;
        this.data = data;
    }

    public static void encode(PacketMarkData msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
        buf.writeNbt(msg.data);
    }

    public static PacketMarkData decode(PacketByteBuf buf) {
        return new PacketMarkData(buf.readInt(), buf.readNbt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Entity entity = MinecraftClient.getInstance().world.getEntityById(this.id);
        if (entity == null || !(entity instanceof LivingEntity)) {
            return;
        }
        MarkData mark = MarkData.get((LivingEntity)entity);
        mark.setNBT(this.data);
    }
}

