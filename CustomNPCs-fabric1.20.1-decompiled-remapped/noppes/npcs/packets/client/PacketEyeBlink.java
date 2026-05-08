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
import noppes.npcs.ModelData;
import noppes.npcs.ModelEyeData;
import noppes.npcs.client.parts.MpmPartData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketEyeBlink
extends PacketBasic {
    private final int id;

    public PacketEyeBlink(int id) {
        this.id = id;
    }

    public static void encode(PacketEyeBlink msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
    }

    public static PacketEyeBlink decode(PacketByteBuf buf) {
        return new PacketEyeBlink(buf.readInt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Entity entity = MinecraftClient.getInstance().world.getEntityById(this.id);
        if (entity == null || !(entity instanceof EntityNPCInterface)) {
            return;
        }
        ModelData data = ((EntityCustomNpc)entity).modelData;
        for (MpmPartData pd : data.mpmParts) {
            if (!(pd instanceof ModelEyeData)) continue;
            ((ModelEyeData)pd).blinkStart = System.currentTimeMillis();
        }
    }
}

