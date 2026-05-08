/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.mob.PathAwareEntity
 *  net.minecraft.entity.ai.goal.Goal
 *  net.minecraft.entity.ai.goal.Goal$Control
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.random.Random
 */
package noppes.npcs.ai;

import java.util.EnumSet;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class EntityAIFindShade
extends Goal {
    private PathAwareEntity theCreature;
    private double shelterX;
    private double shelterY;
    private double shelterZ;
    private World level;

    public EntityAIFindShade(PathAwareEntity par1Mob) {
        this.theCreature = par1Mob;
        this.level = par1Mob.getWorld();
        this.setControls(EnumSet.of(Goal.Control.field_18405));
    }

    public boolean canStart() {
        if (!this.level.isDay()) {
            return false;
        }
        if (!this.level.isSkyVisible(new BlockPos((int)this.theCreature.getX(), (int)this.theCreature.getBoundingBox().minY, (int)this.theCreature.getZ()))) {
            return false;
        }
        Vec3d var1 = this.findPossibleShelter();
        if (var1 == null) {
            return false;
        }
        this.shelterX = var1.x;
        this.shelterY = var1.y;
        this.shelterZ = var1.z;
        return true;
    }

    public boolean shouldContinue() {
        return !this.theCreature.getNavigation().isIdle();
    }

    public void start() {
        this.theCreature.getNavigation().startMovingTo(this.shelterX, this.shelterY, this.shelterZ, 1.0);
    }

    private Vec3d findPossibleShelter() {
        Random random = this.theCreature.getRandom();
        BlockPos blockpos = new BlockPos((int)this.theCreature.getX(), (int)this.theCreature.getBoundingBox().minY, (int)this.theCreature.getZ());
        for (int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
            if (this.level.isSkyVisible(blockpos1) || !(this.theCreature.getPathfindingFavor(blockpos1) < 0.0f)) continue;
            return new Vec3d((double)blockpos1.getX(), (double)blockpos1.getY(), (double)blockpos1.getZ());
        }
        return null;
    }
}

