/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.goal.ActiveTargetGoal
 *  net.minecraft.scoreboard.AbstractTeam
 */
package noppes.npcs.ai.target;

import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.scoreboard.AbstractTeam;
import noppes.npcs.entity.EntityNPCInterface;

public class NpcNearestAttackableTargetGoal<T extends LivingEntity>
extends ActiveTargetGoal<T> {
    private int unseenTicks1;

    public NpcNearestAttackableTargetGoal(EntityNPCInterface npc, Class<T> c, int range, boolean b, boolean b2, Predicate<LivingEntity> selector) {
        super((MobEntity)npc, c, range, b, b2, selector);
        if (npc.ais.attackInvisible) {
            this.targetPredicate.ignoreDistanceScalingFactor();
        }
        if (!npc.ais.directLOS) {
            this.targetPredicate.ignoreVisibility();
        }
    }

    public void start() {
        this.unseenTicks1 = 0;
        this.mob.setTarget(this.targetEntity);
        super.start();
    }

    public void stop() {
        this.mob.setTarget(null);
        this.target = null;
    }

    public boolean shouldContinue() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity == null) {
            livingentity = this.target;
        }
        if (livingentity == null) {
            return false;
        }
        if (!this.mob.canTarget(livingentity)) {
            return false;
        }
        AbstractTeam team = this.mob.getScoreboardTeam();
        AbstractTeam team1 = livingentity.getScoreboardTeam();
        if (team != null && team1 == team) {
            return false;
        }
        double d0 = this.getFollowRange();
        if (this.mob.squaredDistanceTo((Entity)livingentity) > d0 * d0) {
            return false;
        }
        if (this.checkVisibility) {
            if (this.mob.getVisibilityCache().canSee((Entity)livingentity)) {
                this.unseenTicks1 = 0;
            } else if (++this.unseenTicks1 > NpcNearestAttackableTargetGoal.toGoalTicks((int)this.maxTimeWithoutVisibility)) {
                return false;
            }
        }
        this.mob.setTarget(livingentity);
        return true;
    }
}

