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

public class AniCrawling
implements AnimationBase {
    @Override
    public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
    }

    @Override
    public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
        model.head.roll = -netHeadYaw / 57.295776f;
        model.head.yaw = 0.0f;
        model.hat.pitch = model.head.pitch = -0.95993114f;
        model.hat.yaw = model.head.yaw;
        model.hat.roll = model.head.roll;
        if ((double)limbSwingAmount > 0.25) {
            limbSwingAmount = 0.25f;
        }
        float movement = MathHelper.cos((float)(limbSwing * 0.8f + (float)Math.PI)) * limbSwingAmount;
        model.leftArm.pitch = (float)Math.PI - movement * 0.25f;
        model.leftArm.yaw = movement * -0.46f;
        model.leftArm.roll = movement * -0.2f;
        model.leftArm.pivotY = 2.0f - movement * 9.0f;
        model.rightArm.pitch = (float)Math.PI + movement * 0.25f;
        model.rightArm.yaw = movement * -0.4f;
        model.rightArm.roll = movement * -0.2f;
        model.rightArm.pivotY = 2.0f + movement * 9.0f;
        model.body.yaw = movement * 0.1f;
        model.body.pitch = 0.0f;
        model.body.roll = movement * 0.1f;
        model.leftLeg.pitch = movement * 0.1f;
        model.leftLeg.yaw = movement * 0.1f;
        model.leftLeg.roll = -0.122173056f - movement * 0.25f;
        model.leftLeg.pivotY = 10.4f + movement * 9.0f;
        model.leftLeg.pivotZ = movement * 0.6f;
        model.rightLeg.pitch = movement * -0.1f;
        model.rightLeg.yaw = movement * 0.1f;
        model.rightLeg.roll = 0.122173056f - movement * 0.25f;
        model.rightLeg.pivotY = 10.4f - movement * 9.0f;
        model.rightLeg.pivotZ = movement * -0.6f;
    }
}

