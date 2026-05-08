/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 */
package noppes.npcs.client.model.animation;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import noppes.npcs.client.model.animation.AnimationBase;

public class AniHug
implements AnimationBase {
    @Override
    public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
        float f6 = MathHelper.sin((float)(model.handSwingProgress * 3.141593f));
        float f7 = MathHelper.sin((float)((1.0f - (1.0f - model.handSwingProgress) * (1.0f - model.handSwingProgress)) * 3.141593f));
        model.rightArm.roll = 0.0f;
        model.leftArm.roll = 0.0f;
        model.rightArm.yaw = -(0.1f - f6 * 0.6f);
        model.leftArm.yaw = 0.1f;
        model.rightArm.pitch = -1.570796f;
        model.leftArm.pitch = -1.570796f;
        model.rightArm.pitch -= f6 * 1.2f - f7 * 0.4f;
        model.rightArm.roll += MathHelper.cos((float)(ageInTicks * 0.09f)) * 0.05f + 0.05f;
        model.leftArm.roll -= MathHelper.cos((float)(ageInTicks * 0.09f)) * 0.05f + 0.05f;
        model.rightArm.pitch += MathHelper.sin((float)(ageInTicks * 0.067f)) * 0.05f;
        model.leftArm.pitch -= MathHelper.sin((float)(ageInTicks * 0.067f)) * 0.05f;
    }

    @Override
    public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
    }
}

