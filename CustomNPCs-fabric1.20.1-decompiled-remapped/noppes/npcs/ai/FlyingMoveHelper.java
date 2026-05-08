/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.entity.ai.control.MoveControl
 *  net.minecraft.entity.ai.control.MoveControl$State
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.entity.attribute.EntityAttributes
 */
package noppes.npcs.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.attribute.EntityAttributes;
import noppes.npcs.entity.EntityNPCInterface;

public class FlyingMoveHelper
extends MoveControl {
    private EntityNPCInterface entity;
    private int courseChangeCooldown;

    public FlyingMoveHelper(EntityNPCInterface entity) {
        super((MobEntity)entity);
        this.entity = entity;
    }

    public void tick() {
        if (this.state == MoveControl.State.field_6378 && this.courseChangeCooldown-- <= 0) {
            this.courseChangeCooldown = 4;
            double d0 = this.getTargetX() - this.entity.getX();
            double d1 = this.getTargetY() - this.entity.getY();
            double d2 = this.getTargetZ() - this.entity.getZ();
            Vec3d vector3d = new Vec3d(this.getTargetX() - this.entity.getX(), this.getTargetY() - this.entity.getY(), this.getTargetZ() - this.entity.getZ());
            double length = vector3d.length();
            vector3d = vector3d.normalize();
            if (length > 0.5 && this.isNotColliding(vector3d, MathHelper.ceil((double)length))) {
                double speed = this.entity.getAttributeInstance(EntityAttributes.field_23719).getValue() / 2.5;
                if (length < 3.0 && speed > (double)0.1f) {
                    speed = 0.1f;
                }
                Vec3d m = this.entity.getVelocity().add(vector3d.multiply(speed));
                this.entity.setVelocity(m);
                this.entity.setYaw(-((float)Math.atan2(m.x, m.z)) * 180.0f / (float)Math.PI);
                this.entity.bodyYaw = this.entity.getYaw();
            } else {
                this.state = MoveControl.State.field_6377;
            }
        }
    }

    private boolean isNotColliding(Vec3d vec, int length) {
        Box axisalignedbb = this.entity.getBoundingBox();
        for (int i = 1; i < length; ++i) {
            axisalignedbb = axisalignedbb.offset(vec);
            if (this.entity.getWorld().isSpaceEmpty((Entity)this.entity, axisalignedbb)) continue;
            return false;
        }
        return true;
    }
}

