/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.goal.Goal
 *  net.minecraft.entity.ai.goal.Goal$Control
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.ai.TargetPredicate
 */
package noppes.npcs.ai;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.TargetPredicate;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIWatchClosest
extends Goal {
    private EntityNPCInterface npc;
    protected Entity closestEntity;
    private float maxDistance;
    private int lookTime;
    private float change;
    private Class<? extends LivingEntity> watchedClass;
    protected final TargetPredicate predicate;

    public EntityAIWatchClosest(EntityNPCInterface par1EntityLiving, Class<? extends LivingEntity> limbSwingAmountClass, float par3) {
        this.npc = par1EntityLiving;
        this.watchedClass = limbSwingAmountClass;
        this.maxDistance = par3;
        this.change = 0.002f;
        this.setControls(EnumSet.of(Goal.Control.LOOK));
        this.predicate = TargetPredicate.createNonAttackable().setBaseMaxDistance((double)par3);
    }

    public boolean canStart() {
        if (this.npc.getRandom().nextFloat() >= this.change || this.npc.isInteracting()) {
            return false;
        }
        if (this.npc.getTarget() != null) {
            this.closestEntity = this.npc.getTarget();
        }
        if (this.watchedClass == PlayerEntity.class) {
            this.closestEntity = this.npc.getWorld().getClosestPlayer((Entity)this.npc, (double)this.maxDistance);
        } else {
            this.closestEntity = this.npc.getWorld().getClosestEntity(this.watchedClass, this.predicate, (LivingEntity)this.npc, this.npc.getX(), this.npc.getEyeY(), this.npc.getZ(), this.npc.getBoundingBox().expand((double)this.maxDistance, 3.0, (double)this.maxDistance));
            if (this.closestEntity != null) {
                return this.npc.canNpcSee(this.closestEntity);
            }
        }
        return this.closestEntity != null;
    }

    public boolean shouldContinue() {
        if (this.npc.isInteracting() || this.npc.isAttacking() || !this.closestEntity.isAlive() || !this.npc.isAlive()) {
            return false;
        }
        return !this.npc.isInRange(this.closestEntity, this.maxDistance) ? false : this.lookTime > 0;
    }

    public void start() {
        this.lookTime = 60 + this.npc.getRandom().nextInt(60);
    }

    public void stop() {
        this.closestEntity = null;
    }

    public void tick() {
        this.npc.getLookControl().lookAt(this.closestEntity.getX(), this.closestEntity.getY() + (double)this.closestEntity.getStandingEyeHeight(), this.closestEntity.getZ(), 10.0f, (float)this.npc.getMaxLookPitchChange());
        --this.lookTime;
    }
}

