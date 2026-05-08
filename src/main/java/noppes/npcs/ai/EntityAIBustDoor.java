/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.entity.ai.goal.DoorInteractGoal
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.block.BlockState
 */
package noppes.npcs.ai;

import net.minecraft.util.Hand;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.goal.DoorInteractGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Block;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.BlockState;

public class EntityAIBustDoor
extends DoorInteractGoal {
    private int breakingTime;
    private int lastBreakStage = -1;

    public EntityAIBustDoor(MobEntity par1EntityLiving) {
        super(par1EntityLiving);
    }

    public boolean canStart() {
        return !super.canStart() ? false : !this.isDoorOpen();
    }

    public void start() {
        super.start();
        this.breakingTime = 0;
    }

    public boolean shouldContinue() {
        return this.breakingTime <= 240 && !this.isDoorOpen() && this.mob.getBlockPos().getSquaredDistance((Vec3i)this.doorPos) < 4.0;
    }

    public void stop() {
        super.stop();
        this.mob.getWorld().setBlockBreakingInfo(this.mob.getId(), this.doorPos, -1);
    }

    public void tick() {
        super.tick();
        if (this.mob.getRandom().nextInt(20) == 0) {
            this.mob.getWorld().syncWorldEvent((PlayerEntity)null, 1010, this.doorPos, 0);
            this.mob.swingHand(Hand.MAIN_HAND);
        }
        ++this.breakingTime;
        int var1 = (int)((float)this.breakingTime / 240.0f * 10.0f);
        if (var1 != this.lastBreakStage) {
            this.mob.getWorld().setBlockBreakingInfo(this.mob.getId(), this.doorPos, var1);
            this.lastBreakStage = var1;
        }
        if (this.breakingTime == 240) {
            this.mob.getWorld().removeBlock(this.doorPos, false);
            this.mob.getWorld().syncWorldEvent((PlayerEntity)null, 1012, this.doorPos, 0);
            this.mob.getWorld().syncWorldEvent((PlayerEntity)null, 2001, this.doorPos, Block.getRawIdFromState((BlockState)this.mob.getWorld().getBlockState(this.doorPos)));
        }
    }
}

