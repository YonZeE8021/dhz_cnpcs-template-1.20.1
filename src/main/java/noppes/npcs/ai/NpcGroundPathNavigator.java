/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ai.pathing.PathNodeNavigator
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.entity.ai.pathing.MobNavigation
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.entity.ai.pathing.PathNodeType
 */
package noppes.npcs.ai;

import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.ai.pathing.PathNodeType;
import noppes.npcs.ai.NpcWalkNodeEvaluator;

public class NpcGroundPathNavigator
extends MobNavigation {
    public NpcGroundPathNavigator(MobEntity p_26448_, World p_26449_) {
        super(p_26448_, p_26449_);
    }

    protected PathNodeNavigator createPathNodeNavigator(int p_26453_) {
        this.nodeMaker = new NpcWalkNodeEvaluator();
        this.nodeMaker.setCanEnterOpenDoors(true);
        return new PathNodeNavigator(this.nodeMaker, p_26453_);
    }

    protected void continueFollowingPath() {
        Vec3d vec3d = this.getPos();
        int i = this.currentPath.getLength();
        for (int j = this.currentPath.getCurrentNodeIndex(); j < this.currentPath.getLength(); ++j) {
            if ((double)this.currentPath.getNode((int)j).y == Math.floor(vec3d.y)) continue;
            i = j;
            break;
        }
        this.nodeReachProximity = this.entity.getWidth() > 0.75f ? this.entity.getWidth() / 2.0f : 0.75f - this.entity.getWidth() / 2.0f;
        BlockPos vec3i = this.currentPath.getCurrentNodePos();
        if (MathHelper.abs((float)((float)(this.entity.getX() - ((double)vec3i.getX() + 0.5)))) < this.nodeReachProximity && MathHelper.abs((float)((float)(this.entity.getZ() - ((double)vec3i.getZ() + 0.5)))) < this.nodeReachProximity && Math.abs(this.entity.getY() - (double)vec3i.getY()) < 1.0) {
            this.currentPath.next();
        }
        int k = MathHelper.ceil((float)this.entity.getWidth());
        int l = MathHelper.ceil((float)this.entity.getHeight());
        int i1 = k;
        for (int j1 = i - 1; j1 >= this.currentPath.getCurrentNodeIndex(); --j1) {
            if (!this.isDirectPathBetweenPoints(vec3d, this.currentPath.getNodePosition((Entity)this.entity, j1), k, l, i1)) continue;
            this.currentPath.setCurrentNodeIndex(j1);
            break;
        }
        this.checkTimeouts(vec3d);
    }

    protected boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
        int i = MathHelper.floor((double)posVec31.x);
        int j = MathHelper.floor((double)posVec31.z);
        double d0 = posVec32.x - posVec31.x;
        double d1 = posVec32.z - posVec31.z;
        double d2 = d0 * d0 + d1 * d1;
        if (d2 < 1.0E-8) {
            return false;
        }
        double d3 = 1.0 / Math.sqrt(d2);
        if (!this.isSafeToStandAt(i, (int)posVec31.y, j, sizeX += 2, sizeY, sizeZ += 2, posVec31, d0 *= d3, d1 *= d3)) {
            return false;
        }
        sizeX -= 2;
        sizeZ -= 2;
        double d4 = 1.0 / Math.abs(d0);
        double d5 = 1.0 / Math.abs(d1);
        double d6 = (double)i - posVec31.x;
        double d7 = (double)j - posVec31.z;
        if (d0 >= 0.0) {
            d6 += 1.0;
        }
        if (d1 >= 0.0) {
            d7 += 1.0;
        }
        d6 /= d0;
        d7 /= d1;
        int k = d0 < 0.0 ? -1 : 1;
        int l = d1 < 0.0 ? -1 : 1;
        int i1 = MathHelper.floor((double)posVec32.x);
        int j1 = MathHelper.floor((double)posVec32.z);
        int k1 = i1 - i;
        int l1 = j1 - j;
        while (k1 * k > 0 || l1 * l > 0) {
            if (d6 < d7) {
                d6 += d4;
                k1 = i1 - (i += k);
            } else {
                d7 += d5;
                l1 = j1 - (j += l);
            }
            if (this.isSafeToStandAt(i, (int)posVec31.y, j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) continue;
            return false;
        }
        return true;
    }

    private boolean isPositionClear(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d p_179692_7_, double p_179692_8_, double p_179692_10_) {
        for (BlockPos blockpos : BlockPos.iterate((BlockPos)new BlockPos(x, y, z), (BlockPos)new BlockPos(x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1))) {
            double d1;
            double d0 = (double)blockpos.getX() + 0.5 - p_179692_7_.x;
            if (!(d0 * p_179692_8_ + (d1 = (double)blockpos.getZ() + 0.5 - p_179692_7_.z) * p_179692_10_ >= 0.0) || this.world.getBlockState(blockpos).canPathfindThrough((BlockView)this.world, blockpos, NavigationType.LAND)) continue;
            return false;
        }
        return true;
    }

    private boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d vec31, double p_179683_8_, double p_179683_10_) {
        int i = x - sizeX / 2;
        int j = z - sizeZ / 2;
        if (!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, vec31, p_179683_8_, p_179683_10_)) {
            return false;
        }
        for (int k = i; k < i + sizeX; ++k) {
            for (int l = j; l < j + sizeZ; ++l) {
                double d0 = (double)k + 0.5 - vec31.x;
                double d1 = (double)l + 0.5 - vec31.z;
                if (!(d0 * p_179683_8_ + d1 * p_179683_10_ >= 0.0)) continue;
                PathNodeType pathnodetype = ((NpcWalkNodeEvaluator)this.nodeMaker).getNodeType(this.entity, k, y - 1, l);
                if (pathnodetype == PathNodeType.WATER) {
                    return false;
                }
                if (pathnodetype == PathNodeType.LAVA) {
                    return false;
                }
                if (pathnodetype == PathNodeType.OPEN) {
                    return false;
                }
                pathnodetype = ((NpcWalkNodeEvaluator)this.nodeMaker).getNodeType(this.entity, k, y, l);
                float f = this.entity.getPathfindingPenalty(pathnodetype);
                if (f < 0.0f || f >= 8.0f) {
                    return false;
                }
                if (pathnodetype != PathNodeType.DAMAGE_FIRE && pathnodetype != PathNodeType.DANGER_FIRE && pathnodetype != PathNodeType.DAMAGE_OTHER) continue;
                return false;
            }
        }
        return true;
    }
}

