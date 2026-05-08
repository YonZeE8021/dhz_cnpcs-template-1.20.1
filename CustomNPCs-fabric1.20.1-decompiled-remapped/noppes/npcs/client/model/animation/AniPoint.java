/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 */
package noppes.npcs.client.model.animation;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import noppes.npcs.client.model.animation.AnimationBase;

public class AniPoint
implements AnimationBase {
    @Override
    public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
        model.rightArm.pitch = -1.570796f;
        model.rightArm.yaw = netHeadYaw / 57.295776f;
        model.rightArm.roll = 0.0f;
    }

    @Override
    public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
    }
}

