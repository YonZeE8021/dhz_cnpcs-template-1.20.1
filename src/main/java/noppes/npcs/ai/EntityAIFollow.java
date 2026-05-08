/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.goal.Goal
 *  net.minecraft.entity.ai.goal.Goal$Control
 */
package noppes.npcs.ai;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIFollow
extends Goal {
    private EntityNPCInterface npc;
    private LivingEntity owner;
    public int updateTick = 0;

    public EntityAIFollow(EntityNPCInterface npc) {
        this.npc = npc;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    public boolean canStart() {
        if (!this.canExcute()) {
            return false;
        }
        return !this.npc.isInRange((Entity)this.owner, this.npc.followRange());
    }

    public boolean canExcute() {
        return this.npc.isAlive() && this.npc.isFollower() && !this.npc.isAttacking() && (this.owner = this.npc.getOwner()) != null && this.npc.ais.animationType != 1;
    }

    public void start() {
        this.updateTick = 10;
    }

    public boolean shouldContinue() {
        return !this.npc.getNavigation().isIdle() && !this.npc.isInRange((Entity)this.owner, 2.0) && this.canExcute();
    }

    public void stop() {
        this.owner = null;
        this.npc.getNavigation().stop();
    }

    public void tick() {
        ++this.updateTick;
        if (this.updateTick < 10) {
            return;
        }
        this.updateTick = 0;
        this.npc.getLookControl().lookAt((Entity)this.owner, 10.0f, (float)this.npc.getMaxLookPitchChange());
        double distance = this.npc.squaredDistanceTo((Entity)this.owner);
        double speed = 1.0 + distance / 150.0;
        if (speed > 3.0) {
            speed = 3.0;
        }
        if (this.owner.isSprinting()) {
            speed += 0.5;
        }
        if (this.npc.getNavigation().startMovingTo((Entity)this.owner, speed) || this.npc.isInRange((Entity)this.owner, 16.0)) {
            return;
        }
        this.npc.tpTo(this.owner);
    }
}

