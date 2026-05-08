/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.particle.DustParticleEffect
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.api.constants;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;

public class ParticleType {
    public static final int NONE = 0;
    public static final int SMOKE = 1;
    public static final int PORTAL = 2;
    public static final int REDSTONE = 3;
    public static final int LIGHTNING = 4;
    public static final int LARGE_SMOKE = 5;
    public static final int MAGIC = 6;
    public static final int ENCHANT = 7;
    public static final int CRIT = 8;

    public static ParticleEffect getMCType(int type) {
        if (type == 1) {
            return ParticleTypes.field_11251;
        }
        if (type == 2) {
            return ParticleTypes.field_11214;
        }
        if (type == 3) {
            return new RedstoneParticleType();
        }
        if (type == 4) {
            return ParticleTypes.field_11208;
        }
        if (type == 5) {
            return ParticleTypes.field_11237;
        }
        if (type == 6) {
            return ParticleTypes.field_11249;
        }
        if (type == 7) {
            return ParticleTypes.field_11215;
        }
        if (type == 8) {
            return ParticleTypes.field_11205;
        }
        return null;
    }

    static class RedstoneParticleType
    extends DustParticleEffect {
        protected RedstoneParticleType() {
            super(DustParticleEffect.RED, 1.0f);
        }

        public void write(PacketByteBuf p_197553_1_) {
        }

        public String asString() {
            return Registries.PARTICLE_TYPE.getId((Object)ParticleTypes.field_11212).toString();
        }
    }
}

