/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.client.model.ModelPart
 */
package noppes.npcs.client.model;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.model.ModelPart;

public class ModelClassicPlayer<T extends LivingEntity>
extends PlayerEntityModel<T> {
    public ModelClassicPlayer(ModelPart p_170821_, float scale) {
        super(p_170821_, false);
    }

    public void setAngles(T entity, float par1, float limbSwingAmount, float par3, float par4, float par5) {
        super.setAngles(entity, par1, limbSwingAmount, par3, par4, par5);
        float j = 2.0f;
        if (entity.isSprinting()) {
            j = 1.0f;
        }
        this.rightArm.pitch += MathHelper.cos((float)(par1 * 0.6662f + (float)Math.PI)) * j * limbSwingAmount;
        this.leftArm.pitch += MathHelper.cos((float)(par1 * 0.6662f)) * j * limbSwingAmount;
        this.leftArm.roll += (MathHelper.cos((float)(par1 * 0.2812f)) - 1.0f) * limbSwingAmount;
        this.rightArm.roll += (MathHelper.cos((float)(par1 * 0.2312f)) + 1.0f) * limbSwingAmount;
        this.leftSleeve.pitch = this.leftArm.pitch;
        this.leftSleeve.yaw = this.leftArm.yaw;
        this.leftSleeve.roll = this.leftArm.roll;
        this.rightSleeve.pitch = this.rightArm.pitch;
        this.rightSleeve.yaw = this.rightArm.yaw;
        this.rightSleeve.roll = this.rightArm.roll;
    }
}

