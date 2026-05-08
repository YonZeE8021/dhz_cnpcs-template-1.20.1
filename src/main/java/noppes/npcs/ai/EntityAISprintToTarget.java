/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.goal.Goal
 *  net.minecraft.entity.ai.goal.Goal$Control
 *  net.minecraft.util.math.Vec3d
 */
package noppes.npcs.ai;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAISprintToTarget
extends Goal {
    private EntityNPCInterface npc;

    public EntityAISprintToTarget(EntityNPCInterface par1EntityLiving) {
        this.npc = par1EntityLiving;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    public boolean canStart() {
        LivingEntity runTarget = this.npc.getTarget();
        if (runTarget == null || this.npc.getNavigation().isIdle()) {
            return false;
        }
        switch (this.npc.ais.onAttack) {
            case 0: {
                return !this.npc.isInRange((Entity)runTarget, 8.0) ? this.npc.isOnGround() : false;
            }
            case 2: {
                return this.npc.isInRange((Entity)runTarget, 7.0) ? this.npc.isOnGround() : false;
            }
        }
        return false;
    }

    public boolean shouldContinue() {
        Vec3d mo = this.npc.getVelocity();
        return this.npc.isAlive() && this.npc.isOnGround() && this.npc.hurtTime <= 0 && mo.x != 0.0 && mo.z != 0.0;
    }

    public void start() {
        this.npc.setSprinting(true);
    }

    public void stop() {
        this.npc.setSprinting(false);
    }
}

