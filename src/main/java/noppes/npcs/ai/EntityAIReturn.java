/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.mob.PathAwareEntity
 *  net.minecraft.entity.ai.goal.Goal
 *  net.minecraft.entity.ai.goal.Goal$Control
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.entity.ai.NoPenaltyTargeting
 */
package noppes.npcs.ai;

import java.util.EnumSet;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIReturn
extends Goal {
    public static final int MaxTotalTicks = 600;
    private final EntityNPCInterface npc;
    private int stuckTicks = 0;
    private int totalTicks = 0;
    private double endPosX;
    private double endPosY;
    private double endPosZ;
    private boolean wasAttacked = false;
    private double[] preAttackPos;
    private int stuckCount = 0;

    public EntityAIReturn(EntityNPCInterface npc) {
        this.npc = npc;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    public boolean canStart() {
        if (this.npc.hasOwner() || this.npc.hasVehicle() || !this.npc.ais.shouldReturnHome() || this.npc.isKilled() || !this.npc.getNavigation().isIdle() || this.npc.isInteracting()) {
            return false;
        }
        if (!(this.npc.ais.findShelter != 0 || this.npc.getWorld().isDay() && !this.npc.getWorld().isRaining() || this.npc.getWorld().getDimension().hasSkyLight())) {
            BlockPos pos = new BlockPos((int)this.npc.getStartXPos(), (int)this.npc.getStartYPos(), (int)this.npc.getStartZPos());
            if (this.npc.getWorld().isSkyVisible(pos) || this.npc.getWorld().getLuminance(pos) <= 8) {
                return false;
            }
        } else if (this.npc.ais.findShelter == 1 && this.npc.getWorld().isDay()) {
            BlockPos pos = new BlockPos((int)this.npc.getStartXPos(), (int)this.npc.getStartYPos(), (int)this.npc.getStartZPos());
            if (this.npc.getWorld().isSkyVisible(pos)) {
                return false;
            }
        }
        if (this.npc.isAttacking()) {
            if (!this.wasAttacked) {
                this.wasAttacked = true;
                this.preAttackPos = new double[]{this.npc.getX(), this.npc.getY(), this.npc.getZ()};
            }
            return false;
        }
        if (!this.npc.isAttacking() && this.wasAttacked) {
            return true;
        }
        if (this.npc.ais.getMovingType() == 2 && this.npc.ais.distanceToSqrToPathPoint() < (double)(CustomNpcs.NpcNavRange * CustomNpcs.NpcNavRange)) {
            return false;
        }
        if (this.npc.ais.getMovingType() == 1) {
            double x = this.npc.getX() - (double)this.npc.getStartXPos();
            double z = this.npc.getZ() - (double)this.npc.getStartZPos();
            return !this.npc.isInRange(this.npc.getStartXPos(), -6666.0, this.npc.getStartZPos(), this.npc.ais.walkingRange);
        }
        if (this.npc.ais.getMovingType() == 0) {
            return !this.npc.isVeryNearAssignedPlace();
        }
        return false;
    }

    public boolean shouldContinue() {
        if (this.npc.isFollower() || this.npc.isKilled() || this.npc.isAttacking() || this.npc.isVeryNearAssignedPlace() || this.npc.isInteracting() || this.npc.hasVehicle()) {
            return false;
        }
        if (this.npc.getNavigation().isIdle() && this.wasAttacked && !this.isTooFar()) {
            return false;
        }
        return this.totalTicks <= 600;
    }

    public void tick() {
        ++this.totalTicks;
        if (this.totalTicks > 600) {
            this.npc.setPosition(this.endPosX, this.endPosY, this.endPosZ);
            this.npc.getNavigation().stop();
            return;
        }
        if (this.stuckTicks > 0) {
            --this.stuckTicks;
        } else if (this.npc.getNavigation().isIdle()) {
            ++this.stuckCount;
            this.stuckTicks = 10;
            if (this.totalTicks > 30 && this.wasAttacked && this.isTooFar() || this.stuckCount > 5) {
                this.npc.setPosition(this.endPosX, this.endPosY, this.endPosZ);
                this.npc.getNavigation().stop();
            } else {
                this.navigate(this.stuckCount % 2 == 1);
            }
        } else {
            this.stuckCount = 0;
        }
    }

    private boolean isTooFar() {
        double z;
        double x;
        int allowedDistance = this.npc.stats.aggroRange * 2;
        if (this.npc.ais.getMovingType() == 1) {
            allowedDistance += this.npc.ais.walkingRange;
        }
        return (x = this.npc.getX() - this.endPosX) * x + (z = this.npc.getZ() - this.endPosZ) * z > (double)(allowedDistance * allowedDistance);
    }

    public void start() {
        this.stuckTicks = 0;
        this.totalTicks = 0;
        this.stuckCount = 0;
        this.navigate(false);
    }

    private void navigate(boolean towards) {
        if (!this.wasAttacked) {
            this.endPosX = this.npc.getStartXPos();
            this.endPosY = this.npc.getStartYPos();
            this.endPosZ = this.npc.getStartZPos();
        } else {
            this.endPosX = this.preAttackPos[0];
            this.endPosY = this.preAttackPos[1];
            this.endPosZ = this.preAttackPos[2];
        }
        double posX = this.endPosX;
        double posY = this.endPosY;
        double posZ = this.endPosZ;
        double range = Math.sqrt(this.npc.squaredDistanceTo(posX, posY, posZ));
        if (range > (double)CustomNpcs.NpcNavRange || towards) {
            Vec3d start;
            Vec3d pos;
            int distance = (int)range;
            distance = distance > CustomNpcs.NpcNavRange ? CustomNpcs.NpcNavRange / 2 : (distance /= 2);
            if (distance > 2 && (pos = NoPenaltyTargeting.findTo((PathAwareEntity)this.npc, (int)(distance / 2), (int)(distance / 2 > 7 ? 7 : distance / 2), (Vec3d)(start = new Vec3d(posX, posY, posZ)), (double)1.5707963267948966)) != null) {
                posX = pos.x;
                posY = pos.y;
                posZ = pos.z;
            }
        }
        this.npc.getNavigation().stop();
        this.npc.getNavigation().startMovingTo(posX, posY, posZ, 1.0);
    }

    public void stop() {
        this.wasAttacked = false;
        this.npc.getNavigation().stop();
    }
}

