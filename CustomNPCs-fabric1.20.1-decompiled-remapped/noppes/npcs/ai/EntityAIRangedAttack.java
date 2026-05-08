/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.goal.Goal
 *  net.minecraft.entity.ai.goal.Goal$Control
 *  net.minecraft.entity.ai.RangedAttackMob
 */
package noppes.npcs.ai;

import java.util.EnumSet;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.RangedAttackMob;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIRangedAttack
extends Goal {
    private final EntityNPCInterface npc;
    private LivingEntity attackTarget;
    private int rangedAttackTime = 0;
    private int moveTries = 0;
    private int burstCount = 0;
    private int attackTick = 0;
    private boolean hasFired = false;

    public EntityAIRangedAttack(RangedAttackMob par1RangedAttackMob) {
        if (!(par1RangedAttackMob instanceof LivingEntity)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        this.npc = (EntityNPCInterface)par1RangedAttackMob;
        this.rangedAttackTime = this.npc.stats.ranged.getDelayMin() / 2;
        this.setControls(EnumSet.of(Goal.Control.field_18405));
    }

    public boolean canStart() {
        this.attackTarget = this.npc.getTarget();
        if (this.attackTarget == null || !this.attackTarget.isAlive() || !this.npc.isInRange((Entity)this.attackTarget, this.npc.stats.aggroRange) || this.npc.inventory.getProjectile() == null) {
            return false;
        }
        return this.npc.stats.ranged.getMeleeRange() < 1 || !this.npc.isInRange((Entity)this.attackTarget, this.npc.stats.ranged.getMeleeRange());
    }

    public void stop() {
        this.attackTarget = null;
        this.npc.setTarget(null);
        this.npc.getNavigation().stop();
        this.moveTries = 0;
        this.hasFired = false;
        this.rangedAttackTime = this.npc.stats.ranged.getDelayMin() / 2;
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        this.npc.getLookControl().lookAt((Entity)this.attackTarget, 30.0f, 30.0f);
        double var1 = this.npc.squaredDistanceTo(this.attackTarget.getX(), this.attackTarget.getBoundingBox().minY, this.attackTarget.getZ());
        float range = this.npc.stats.ranged.getRange() * this.npc.stats.ranged.getRange();
        if (this.npc.ais.directLOS) {
            this.moveTries = this.npc.getVisibilityCache().canSee((Entity)this.attackTarget) ? ++this.moveTries : 0;
            int v = 15;
            if (var1 <= (double)range && this.moveTries >= v) {
                this.npc.getNavigation().stop();
            } else {
                this.npc.getNavigation().startMovingTo((Entity)this.attackTarget, 1.0);
            }
        }
        if (this.rangedAttackTime-- <= 0 && var1 <= (double)range && (this.npc.getVisibilityCache().canSee((Entity)this.attackTarget) || this.npc.stats.ranged.getFireType() == 2)) {
            if (this.burstCount++ <= this.npc.stats.ranged.getBurst()) {
                this.rangedAttackTime = this.npc.stats.ranged.getBurstDelay();
            } else {
                this.burstCount = 0;
                this.hasFired = true;
                this.rangedAttackTime = this.npc.stats.ranged.getDelayRNG();
            }
            if (this.burstCount > 1) {
                boolean indirect = false;
                switch (this.npc.stats.ranged.getFireType()) {
                    case 1: {
                        indirect = var1 > (double)range / 2.0;
                        break;
                    }
                    case 2: {
                        indirect = !this.npc.getVisibilityCache().canSee((Entity)this.attackTarget);
                    }
                }
                this.npc.attack(this.attackTarget, indirect ? 1.0f : 0.0f);
                if (this.npc.currentAnimation != 6) {
                    this.npc.swingHand(Hand.field_5808);
                }
            }
        }
    }

    public boolean hasFired() {
        return this.hasFired;
    }
}

