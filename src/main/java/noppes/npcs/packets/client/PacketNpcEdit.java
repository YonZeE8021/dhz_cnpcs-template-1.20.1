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
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcEdit
extends PacketBasic {
    private final int id;

    public PacketNpcEdit(int id) {
        this.id = id;
    }

    public static void encode(PacketNpcEdit msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
    }

    public static PacketNpcEdit decode(PacketByteBuf buf) {
        return new PacketNpcEdit(buf.readInt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Entity entity = MinecraftClient.getInstance().world.getEntityById(this.id);
        if (entity == null || !(entity instanceof EntityNPCInterface)) {
            NoppesUtil.setLastNpc(null);
        } else {
            NoppesUtil.setLastNpc((EntityNPCInterface)entity);
        }
    }
}

