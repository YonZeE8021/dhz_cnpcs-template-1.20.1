/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.MovementType
 *  net.minecraft.entity.mob.PathAwareEntity
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.block.BlockState
 */
package noppes.npcs.entity;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import noppes.npcs.entity.EntityNPCInterface;

public abstract class EntityNPCFlying
extends EntityNPCInterface {
    public EntityNPCFlying(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    @Override
    public boolean canFly() {
        return this.ais.movementType == 1;
    }

    @Override
    public boolean handleFallDamage(float distance, float damageMultiplier, DamageSource source) {
        if (!this.canFly()) {
            return super.handleFallDamage(distance, damageMultiplier, source);
        }
        return false;
    }

    protected void fall(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        if (!this.canFly()) {
            super.fall(y, onGroundIn, state, pos);
        }
    }

    @Override
    public void travel(Vec3d v) {
        if (!this.canFly()) {
            super.travel(v);
            return;
        }
        Vec3d m = this.getVelocity();
        if (!this.isTouchingWater() && this.ais.movementType == 2) {
            m = new Vec3d(0.0, -0.15, 0.0);
            this.move(MovementType.field_6308, m);
        } else if (this.isTouchingWater() && this.ais.movementType == 1) {
            this.updateVelocity(0.02f, v);
            this.move(MovementType.field_6308, m);
            m = this.getVelocity().multiply(0.8);
        } else if (this.isInLava()) {
            this.updateVelocity(0.02f, v);
            this.move(MovementType.field_6308, m);
            m = this.getVelocity().multiply(0.5);
        } else {
            BlockPos ground = new BlockPos((int)this.getX(), (int)(this.getY() - 1.0), (int)this.getZ());
            float f = 0.91f;
            if (this.isOnGround()) {
                f = 0.54600006f;
            }
            float f1 = 0.16277137f / (f * f * f);
            f = 0.91f;
            if (this.isOnGround()) {
                f = 0.54600006f;
            }
            this.updateVelocity(this.isOnGround() ? 0.1f * f1 : 0.02f, v);
            this.move(MovementType.field_6308, this.getVelocity());
            m = this.getVelocity().multiply((double)f);
        }
        this.setVelocity(m);
        this.updateLimbs(false);
    }

    public boolean isClimbing() {
        return false;
    }
}

