/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.client.world.ClientWorld
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.random.Random;
import net.minecraft.client.world.ClientWorld;
import noppes.npcs.shared.common.PacketBasic;

public class PacketParticle
extends PacketBasic {
    private final double posX;
    private final double posY;
    private final double posZ;
    private final float height;
    private final float width;
    private final String name;

    public PacketParticle(double posX, double posY, double posZ, float height, float width, String name) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.height = height;
        this.width = width;
        this.name = name;
    }

    public static void encode(PacketParticle msg, PacketByteBuf buf) {
        buf.writeDouble(msg.posX);
        buf.writeDouble(msg.posY);
        buf.writeDouble(msg.posZ);
        buf.writeFloat(msg.height);
        buf.writeFloat(msg.width);
        buf.writeString(msg.name);
    }

    public static PacketParticle decode(PacketByteBuf buf) {
        return new PacketParticle(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readFloat(), buf.readString(Short.MAX_VALUE));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        ClientWorld world = MinecraftClient.getInstance().world;
        Random rand = world.random;
        if (this.name.equals("heal")) {
            for (int k = 0; k < 6; ++k) {
                world.addParticle((ParticleEffect)ParticleTypes.field_11213, this.posX + (rand.nextDouble() - 0.5) * (double)this.width, this.posY + rand.nextDouble() * (double)this.height, this.posZ + (rand.nextDouble() - 0.5) * (double)this.width, 0.0, 0.0, 0.0);
                world.addParticle((ParticleEffect)ParticleTypes.field_11245, this.posX + (rand.nextDouble() - 0.5) * (double)this.width, this.posY + rand.nextDouble() * (double)this.height, this.posZ + (rand.nextDouble() - 0.5) * (double)this.width, 0.0, 0.0, 0.0);
            }
        }
    }
}

