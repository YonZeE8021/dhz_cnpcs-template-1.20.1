/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.mixinintf;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;

public interface IMixinClientboundAddEntityPacket {
    public Entity getEntity();

    public PacketByteBuf getBuf();
}

