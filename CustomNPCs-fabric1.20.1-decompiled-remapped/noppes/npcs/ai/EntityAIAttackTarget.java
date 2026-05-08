/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.pathing.Path
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.goal.Goal
 *  net.minecraft.entity.ai.goal.Goal$Control
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package noppes.npcs.ai;

import java.util.EnumSet;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIAttackTarget
extends Goal {
    private World level;
    private EntityNPCInterface npc;
    private LivingEntity entityTarget;
    private int attackTick = 0;
    private Path entityPathEntity;
    private int field_75445_i;
    private BlockPos startPos = BlockPos.ORIGIN;

    public EntityAIAttackTarget(EntityNPCInterface par1EntityLiving) {
        this.npc = par1EntityLiving;
        this.level = par1EntityLiving.getWorld();
        this.setControls(EnumSet.of(Goal.Control.field_18405));
    }

    public boolean canStart() {
        LivingEntity entitylivingbase = this.npc.getTarget();
        if (entitylivingbase == null || !entitylivingbase.isAlive()) {
            return false;
        }
        int melee = this.npc.stats.ranged.getMeleeRange();
        if (!(this.npc.inventory.getProjectile() == null || melee > 0 && this.npc.isInRange((Entity)entitylivingbase, melee))) {
            return false;
        }
        this.entityTarget = entitylivingbase;
        this.entityPathEntity = this.npc.getNavigation().findPathTo((Entity)entitylivingbase, 0);
        return this.entityPathEntity != null;
    }

    public boolean shouldContinue() {
        this.entityTarget = this.npc.getTarget();
        if (this.entityTarget == null) {
            this.entityTarget = this.npc.getAttacker();
        }
        if (this.entityTarget == null || !this.entityTarget.isAlive()) {
            return false;
        }
        if (!this.npc.isInRange((Entity)this.entityTarget, this.npc.stats.aggroRange)) {
            return false;
        }
        int melee = this.npc.stats.ranged.getMeleeRange();
        if (melee > 0 && !this.npc.isInRange((Entity)this.entityTarget, melee)) {
            return false;
        }
        return this.isWithinRestriction(this.entityTarget.getBlockPos());
    }

    public boolean isWithinRestriction(BlockPos pos) {
        int range = Math.max(this.npc.stats.aggroRange * 2, 64);
        return this.startPos.getSquaredDistance((Vec3i)pos) < (double)(range * range);
    }

    public void start() {
        this.startPos = this.npc.getBlockPos();
        this.npc.getNavigation().startMovingAlong(this.entityPathEntity, 1.3);
        this.field_75445_i = 0;
    }

    public void stop() {
        this.entityPathEntity = null;
        this.entityTarget = null;
        this.npc.getNavigation().stop();
    }

    public boolean shouldRunEveryTick() {
        return true;
    }

    public void tick() {
        this.npc.getLookControl().lookAt((Entity)this.entityTarget, 30.0f, 30.0f);
        if (--this.field_75445_i <= 0) {
            this.field_75445_i = 4 + this.npc.getRandom().nextInt(7);
            this.npc.getNavigation().startMovingTo((Entity)this.entityTarget, (double)1.3f);
        }
        this.attackTick = Math.max(this.attackTick - 1, 0);
        double y = this.entityTarget.getY();
        if (this.entityTarget.getBoundingBox() != null) {
            y = this.entityTarget.getBoundingBox().minY;
        }
        double distance = this.npc.squaredDistanceTo(this.entityTarget.getX(), y, this.entityTarget.getZ());
        double range = (float)(this.npc.stats.melee.getRange() * this.npc.stats.melee.getRange()) + this.entityTarget.getWidth();
        double minRange = this.npc.getWidth() * 2.0f * this.npc.getWidth() * 2.0f + this.entityTarget.getWidth();
        if (minRange > range) {
            range = minRange;
        }
        if (distance <= range && (this.npc.canNpcSee((Entity)this.entityTarget) || distance < minRange) && this.attackTick <= 0) {
            this.attackTick = this.npc.stats.melee.getDelay();
            this.npc.swingHand(Hand.field_5808);
            this.npc.tryAttack((Entity)this.entityTarget);
        }
    }
}

