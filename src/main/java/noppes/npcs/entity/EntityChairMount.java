/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.MovementType
 *  net.minecraft.world.World
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.listener.ClientPlayPacketListener
 *  net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
 */
package noppes.npcs.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;

public class EntityChairMount
extends Entity {
    public EntityChairMount(EntityType type, World world) {
        super(type, world);
    }

    public double getMountedHeightOffset() {
        return 0.5;
    }

    protected void initDataTracker() {
    }

    public void baseTick() {
        super.baseTick();
        if (this.getWorld() != null && !this.getWorld().isClient && this.getPassengerList().isEmpty()) {
            this.discard();
        }
    }

    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket((Entity)this);
    }

    public boolean isInvisible() {
        return true;
    }

    public void move(MovementType type, Vec3d vec) {
    }

    public void readNbt(NbtCompound tagCompound) {
    }

    protected void readCustomDataFromNbt(NbtCompound compound) {
    }

    protected void writeCustomDataToNbt(NbtCompound compound) {
    }

    public NbtCompound writeNbt(NbtCompound tagCompound) {
        return tagCompound;
    }

    public boolean isCollidable() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    public boolean handleFallDamage(float distance, float damageMultiplier, DamageSource source) {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_, float p_70056_8_, int p_70056_9_, boolean bo) {
        this.setPosition(p_70056_1_, p_70056_3_, p_70056_5_);
        this.setRotation(p_70056_7_, p_70056_8_);
    }
}

