/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.goal.Goal
 *  net.minecraft.entity.ai.goal.Goal$Control
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.MathHelper
 */
package noppes.npcs.ai;

import java.util.EnumSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIPounceTarget
extends Goal {
    private EntityNPCInterface npc;
    private LivingEntity leapTarget;
    private float leapSpeed = 1.3f;

    public EntityAIPounceTarget(EntityNPCInterface leapingEntity) {
        this.npc = leapingEntity;
        this.setControls(EnumSet.of(Goal.Control.field_18407));
    }

    public boolean canStart() {
        if (!this.npc.isOnGround()) {
            return false;
        }
        this.leapTarget = this.npc.getTarget();
        if (this.leapTarget == null || !this.npc.getVisibilityCache().canSee((Entity)this.leapTarget)) {
            return false;
        }
        return !this.npc.isInRange((Entity)this.leapTarget, 4.0) && this.npc.isInRange((Entity)this.leapTarget, 8.0) ? this.npc.getRandom().nextInt(5) == 0 : false;
    }

    public boolean shouldContinue() {
        return !this.npc.isOnGround();
    }

    public void start() {
        double varX = this.leapTarget.getX() - this.npc.getX();
        double varY = this.leapTarget.getBoundingBox().minY - this.npc.getBoundingBox().minY;
        double varZ = this.leapTarget.getZ() - this.npc.getZ();
        float varF = (float)Math.sqrt(varX * varX + varZ * varZ);
        float angle = this.getAngleForXYZ(varX, varY, varZ, varF);
        float yaw = (float)(Math.atan2(varX, varZ) * 180.0 / Math.PI);
        Vec3d mo = new Vec3d((double)(MathHelper.sin((float)(yaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(angle / 180.0f * (float)Math.PI))), (double)MathHelper.sin((float)((angle + 1.0f) / 180.0f * (float)Math.PI)), (double)(MathHelper.cos((float)(yaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(angle / 180.0f * (float)Math.PI))));
        mo.multiply((double)this.leapSpeed);
        this.npc.setVelocity(mo);
    }

    public float getAngleForXYZ(double varX, double varY, double varZ, double horiDist) {
        float g = 0.1f;
        float var1 = this.leapSpeed * this.leapSpeed;
        double var2 = (double)g * horiDist;
        double var3 = (double)g * horiDist * horiDist + 2.0 * varY * (double)var1;
        double var4 = (double)(var1 * var1) - (double)g * var3;
        if (var4 < 0.0) {
            return 90.0f;
        }
        float var6 = var1 - (float)Math.sqrt(var4);
        return (float)(Math.atan2(var6, var2) * 180.0 / Math.PI);
    }
}

