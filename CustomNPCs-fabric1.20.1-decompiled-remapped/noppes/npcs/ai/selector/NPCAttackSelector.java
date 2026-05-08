/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.ai.selector;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobGuard;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.companion.CompanionGuard;

public class NPCAttackSelector
implements Predicate<LivingEntity> {
    private EntityNPCInterface npc;

    public NPCAttackSelector(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public boolean isEntityApplicable(LivingEntity entity) {
        if (!entity.isAlive() || entity == this.npc || !this.npc.isInRange((Entity)entity, this.npc.stats.aggroRange) || entity.getHealth() < 1.0f) {
            return false;
        }
        if (this.npc.ais.directLOS && !this.npc.getVisibilityCache().canSee((Entity)entity)) {
            return false;
        }
        if (!this.npc.isFollower() && this.npc.ais.shouldReturnHome()) {
            int allowedDistance = this.npc.stats.aggroRange * 2;
            if (this.npc.ais.getMovingType() == 1) {
                allowedDistance += this.npc.ais.walkingRange;
            }
            double distance = entity.squaredDistanceTo((double)this.npc.getStartXPos(), this.npc.getStartYPos(), (double)this.npc.getStartZPos());
            if (this.npc.ais.getMovingType() == 2) {
                int[] arr = this.npc.ais.getCurrentMovingPath();
                distance = entity.squaredDistanceTo((double)arr[0], (double)arr[1], (double)arr[2]);
            }
            if (distance > (double)(allowedDistance * allowedDistance)) {
                return false;
            }
        }
        if (this.npc.job.getType() == 3 && ((JobGuard)this.npc.job).isEntityApplicable((Entity)entity)) {
            return true;
        }
        if (this.npc.role.getType() == 6) {
            RoleCompanion role = (RoleCompanion)this.npc.role;
            if (role.companionJobInterface.getType() == EnumCompanionJobs.GUARD && ((CompanionGuard)role.companionJobInterface).isEntityApplicable((Entity)entity)) {
                return true;
            }
        }
        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)entity;
            return this.npc.faction.isAggressiveToPlayer((PlayerEntity)player) && !player.getAbilities().invulnerable;
        }
        if (entity instanceof EntityNPCInterface) {
            if (((EntityNPCInterface)entity).isKilled()) {
                return false;
            }
            if (this.npc.advanced.attackOtherFactions) {
                return this.npc.faction.isAggressiveToNpc((EntityNPCInterface)entity);
            }
        }
        return false;
    }

    public boolean apply(LivingEntity ob) {
        return this.isEntityApplicable(ob);
    }
}

