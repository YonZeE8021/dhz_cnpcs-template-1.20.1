/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.entity.ai.pathing.PathNode
 */
package noppes.npcs.api.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ai.pathing.PathNode;
import noppes.npcs.api.IPos;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLiving;
import noppes.npcs.api.entity.IMob;
import noppes.npcs.api.wrapper.BlockPosWrapper;
import noppes.npcs.api.wrapper.EntityLivingBaseWrapper;

public class EntityLivingWrapper<T extends MobEntity>
extends EntityLivingBaseWrapper<T>
implements IMob {
    public EntityLivingWrapper(T entity) {
        super(entity);
    }

    @Override
    public void navigateTo(double x, double y, double z, double speed) {
        ((MobEntity)this.entity).getNavigation().stop();
        ((MobEntity)this.entity).getNavigation().startMovingTo(x, y, z, speed * 0.7);
    }

    @Override
    public void clearNavigation() {
        ((MobEntity)this.entity).getNavigation().stop();
    }

    @Override
    public IPos getNavigationPath() {
        if (!this.isNavigating()) {
            return null;
        }
        PathNode point = ((MobEntity)this.entity).getNavigation().getCurrentPath().getEnd();
        if (point == null) {
            return null;
        }
        return new BlockPosWrapper(new BlockPos(point.x, point.y, point.z));
    }

    @Override
    public boolean isNavigating() {
        return !((MobEntity)this.entity).getNavigation().isIdle();
    }

    @Override
    public boolean isAttacking() {
        return super.isAttacking() || ((MobEntity)this.entity).getTarget() != null;
    }

    @Override
    public void setAttackTarget(IEntityLiving living) {
        if (living == null) {
            ((MobEntity)this.entity).setTarget(null);
        } else {
            ((MobEntity)this.entity).setTarget(living.getMCEntity());
        }
        super.setAttackTarget(living);
    }

    @Override
    public IEntityLiving getAttackTarget() {
        IEntityLiving base = (IEntityLiving)NpcAPI.Instance().getIEntity((Entity)((MobEntity)this.entity).getTarget());
        return base != null ? base : super.getAttackTarget();
    }

    @Override
    public boolean canSeeEntity(IEntity entity) {
        return ((MobEntity)this.entity).getVisibilityCache().canSee(entity.getMCEntity());
    }

    @Override
    public void jump() {
        ((MobEntity)this.entity).getJumpControl().setActive();
    }
}

