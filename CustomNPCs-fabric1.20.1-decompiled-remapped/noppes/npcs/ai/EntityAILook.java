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
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAILook
extends Goal {
    private final EntityNPCInterface npc;
    private int idle = 0;
    private double lookX;
    private double lookZ;
    private boolean forced = false;
    private Entity forcedEntity = null;

    public EntityAILook(EntityNPCInterface npc) {
        this.npc = npc;
        this.setControls(EnumSet.of(Goal.Control.field_18406));
    }

    public boolean canStart() {
        if (this.forced) {
            return true;
        }
        if (this.npc.isAttacking() || !this.npc.getNavigation().isIdle() || this.npc.isSleeping() || !this.npc.isAlive()) {
            return false;
        }
        if (this.npc.isInteracting() || this.npc.ais.getStandingType() > 0 || this.idle > 0) {
            return true;
        }
        return this.npc.getRandom().nextFloat() < 0.004f;
    }

    public void start() {
        if (this.npc.ais.getStandingType() == 0 || this.npc.ais.getStandingType() == 3) {
            double var1 = Math.PI * 2 * this.npc.getRandom().nextDouble();
            if (this.npc.ais.getStandingType() == 3) {
                var1 = Math.PI / 180 * (double)this.npc.ais.orientation + 0.6283185307179586 + 1.8849555921538759 * this.npc.getRandom().nextDouble();
            }
            this.lookX = Math.cos(var1);
            this.lookZ = Math.sin(var1);
            this.idle = 20 + this.npc.getRandom().nextInt(20);
        }
    }

    public void rotate(Entity entity) {
        this.forced = true;
        this.forcedEntity = entity;
    }

    public void rotate(int degrees) {
        this.forced = true;
        this.npc.headYaw = this.npc.bodyYaw = (float)degrees;
        this.npc.setYaw(degrees);
    }

    public void stop() {
        this.forced = false;
        this.forcedEntity = null;
    }

    public void tick() {
        Entity lookat = null;
        if (this.forced && this.forcedEntity != null) {
            lookat = this.forcedEntity;
        } else if (this.npc.isInteracting()) {
            Iterator<LivingEntity> ita = this.npc.interactingEntities.iterator();
            double closestDistance = 12.0;
            while (ita.hasNext()) {
                LivingEntity entity = ita.next();
                double distance = entity.squaredDistanceTo((Entity)this.npc);
                if (distance < closestDistance) {
                    closestDistance = entity.squaredDistanceTo((Entity)this.npc);
                    lookat = entity;
                    continue;
                }
                if (!(distance > 12.0)) continue;
                ita.remove();
            }
        } else if (this.npc.ais.getStandingType() == 2) {
            lookat = this.npc.getWorld().getClosestPlayer((Entity)this.npc, 16.0);
        }
        if (lookat != null) {
            this.npc.getLookControl().lookAt(lookat, 10.0f, (float)this.npc.getMaxLookPitchChange());
            return;
        }
        if (this.idle > 0) {
            --this.idle;
            this.npc.getLookControl().lookAt(this.npc.getX() + this.lookX, this.npc.getY() + (double)this.npc.getStandingEyeHeight(), this.npc.getZ() + this.lookZ, 10.0f, (float)this.npc.getMaxLookPitchChange());
        }
        if (this.npc.ais.getStandingType() == 1 && !this.forced) {
            this.npc.headYaw = this.npc.bodyYaw = (float)this.npc.ais.orientation;
            this.npc.setYaw(this.npc.ais.orientation);
        }
    }
}

