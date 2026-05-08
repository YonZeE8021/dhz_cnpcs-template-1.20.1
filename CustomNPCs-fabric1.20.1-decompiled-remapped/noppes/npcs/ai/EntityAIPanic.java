/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.mob.PathAwareEntity
 *  net.minecraft.entity.ai.goal.Goal
 *  net.minecraft.entity.ai.goal.Goal$Control
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.entity.ai.NoPenaltyTargeting
 */
package noppes.npcs.ai;

import java.util.EnumSet;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.NoPenaltyTargeting;

public class EntityAIPanic
extends Goal {
    private PathAwareEntity entityCreature;
    private float speed;
    private double randPosX;
    private double randPosY;
    private double randPosZ;

    public EntityAIPanic(PathAwareEntity par1Mob, float limbSwingAmount) {
        this.entityCreature = par1Mob;
        this.speed = limbSwingAmount;
        this.setControls(EnumSet.of(Goal.Control.field_18405));
    }

    public boolean canStart() {
        if (this.entityCreature.getTarget() == null && !this.entityCreature.isOnFire()) {
            return false;
        }
        Vec3d var1 = NoPenaltyTargeting.find((PathAwareEntity)this.entityCreature, (int)5, (int)4);
        if (var1 == null) {
            return false;
        }
        this.randPosX = var1.x;
        this.randPosY = var1.y;
        this.randPosZ = var1.z;
        return true;
    }

    public void start() {
        this.entityCreature.getNavigation().startMovingTo(this.randPosX, this.randPosY, this.randPosZ, (double)this.speed);
    }

    public boolean shouldContinue() {
        if (this.entityCreature.getTarget() == null) {
            return false;
        }
        return !this.entityCreature.getNavigation().isIdle();
    }
}

