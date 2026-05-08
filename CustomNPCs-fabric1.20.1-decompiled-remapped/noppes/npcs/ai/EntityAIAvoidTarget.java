/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.pathing.Path
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.PathAwareEntity
 *  net.minecraft.entity.ai.goal.Goal
 *  net.minecraft.entity.ai.goal.Goal$Control
 *  net.minecraft.entity.ai.pathing.EntityNavigation
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.entity.ai.NoPenaltyTargeting
 */
package noppes.npcs.ai;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIAvoidTarget
extends Goal {
    private EntityNPCInterface npc;
    private Entity closestLivingEntity;
    private float distanceFromEntity;
    private float health;
    private Path entityPathEntity;
    private EntityNavigation entityPathNavigate;
    private Class targetEntityClass;

    public EntityAIAvoidTarget(EntityNPCInterface par1EntityNPC) {
        this.npc = par1EntityNPC;
        this.distanceFromEntity = this.npc.stats.aggroRange;
        this.health = this.npc.getHealth();
        this.entityPathNavigate = par1EntityNPC.getNavigation();
        this.setControls(EnumSet.of(Goal.Control.field_18405));
    }

    public boolean canStart() {
        LivingEntity target = this.npc.getTarget();
        if (target == null) {
            return false;
        }
        this.targetEntityClass = target.getClass();
        if (this.targetEntityClass == PlayerEntity.class) {
            this.closestLivingEntity = this.npc.getWorld().getClosestPlayer((Entity)this.npc, (double)this.distanceFromEntity);
            if (this.closestLivingEntity == null) {
                return false;
            }
        } else {
            List var1 = this.npc.getWorld().getNonSpectatingEntities(this.targetEntityClass, this.npc.getBoundingBox().expand((double)this.distanceFromEntity, 3.0, (double)this.distanceFromEntity));
            if (var1.isEmpty()) {
                return false;
            }
            this.closestLivingEntity = (Entity)var1.get(0);
        }
        if (!this.npc.getVisibilityCache().canSee(this.closestLivingEntity) && this.npc.ais.directLOS) {
            return false;
        }
        Vec3d var2 = NoPenaltyTargeting.findFrom((PathAwareEntity)this.npc, (int)16, (int)7, (Vec3d)new Vec3d(this.closestLivingEntity.getX(), this.closestLivingEntity.getY(), this.closestLivingEntity.getZ()));
        if (var2 == null || var2 == Vec3d.ZERO) {
            return false;
        }
        if (this.closestLivingEntity.squaredDistanceTo(var2.x, var2.y, var2.z) < this.closestLivingEntity.squaredDistanceTo((Entity)this.npc)) {
            return false;
        }
        this.entityPathEntity = this.entityPathNavigate.findPathTo(var2.x, var2.y, var2.z, 0);
        return this.entityPathEntity != null;
    }

    public boolean shouldContinue() {
        return !this.entityPathNavigate.isIdle();
    }

    public void start() {
        this.entityPathNavigate.startMovingAlong(this.entityPathEntity, 1.0);
    }

    public void stop() {
        this.closestLivingEntity = null;
        this.npc.setTarget(null);
    }

    public void tick() {
        if (this.npc.isInRange(this.closestLivingEntity, 7.0)) {
            this.npc.getNavigation().setSpeed(1.2);
        } else {
            this.npc.getNavigation().setSpeed(1.0);
        }
    }
}

