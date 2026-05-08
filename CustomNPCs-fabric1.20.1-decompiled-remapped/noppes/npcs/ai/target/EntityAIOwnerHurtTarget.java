/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.goal.Goal$Control
 *  net.minecraft.entity.ai.goal.TrackTargetGoal
 *  net.minecraft.entity.ai.TargetPredicate
 */
package noppes.npcs.ai.target;

import java.util.EnumSet;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.ai.TargetPredicate;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIOwnerHurtTarget
extends TrackTargetGoal {
    EntityNPCInterface npc;
    LivingEntity theTarget;
    private int field_142050_e;

    public EntityAIOwnerHurtTarget(EntityNPCInterface npc) {
        super((MobEntity)npc, false);
        this.npc = npc;
        this.setControls(EnumSet.of(Goal.Control.field_18408));
    }

    public boolean canStart() {
        if (!this.npc.isFollower() || !this.npc.role.defendOwner()) {
            return false;
        }
        LivingEntity entitylivingbase = this.npc.getOwner();
        if (entitylivingbase == null) {
            return false;
        }
        this.theTarget = entitylivingbase.getAttacking();
        int i = entitylivingbase.getLastAttackTime();
        return i != this.field_142050_e && this.canTrack(this.theTarget, TargetPredicate.DEFAULT);
    }

    public void start() {
        this.npc.setTarget(this.theTarget);
        LivingEntity entitylivingbase = this.npc.getOwner();
        if (entitylivingbase != null) {
            this.field_142050_e = entitylivingbase.getLastAttackTime();
        }
        super.start();
    }
}

