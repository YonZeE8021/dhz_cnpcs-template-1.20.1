/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.registry.tag.DamageTypeTags
 */
package noppes.npcs;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;

public class Resistances {
    public float knockback = 1.0f;
    public float arrow = 1.0f;
    public float melee = 1.0f;
    public float explosion = 1.0f;

    public NbtCompound save() {
        NbtCompound compound = new NbtCompound();
        compound.putFloat("Knockback", this.knockback);
        compound.putFloat("Arrow", this.arrow);
        compound.putFloat("Melee", this.melee);
        compound.putFloat("Explosion", this.explosion);
        return compound;
    }

    public void readToNBT(NbtCompound compound) {
        this.knockback = compound.getFloat("Knockback");
        this.arrow = compound.getFloat("Arrow");
        this.melee = compound.getFloat("Melee");
        this.explosion = compound.getFloat("Explosion");
    }

    public float applyResistance(DamageSource source, float damage) {
        if (source.getName().equals("arrow") || source.getName().equals("thrown") || source.isIn(DamageTypeTags.field_42247)) {
            damage *= 2.0f - this.arrow;
        } else if (source.getName().equals("player") || source.getName().equals("mob") || source.getName().equals("npc")) {
            damage *= 2.0f - this.melee;
        } else if (source.getName().equals("explosion") || source.getName().equals("explosion.player")) {
            damage *= 2.0f - this.explosion;
        }
        return damage;
    }
}

