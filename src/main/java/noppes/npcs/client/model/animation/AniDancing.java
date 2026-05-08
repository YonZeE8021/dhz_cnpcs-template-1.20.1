/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 */
package noppes.npcs.client.model.animation;

import net.minecraft.entity.Entity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import noppes.npcs.client.model.animation.AnimationBase;

public class AniDancing
implements AnimationBase {
    @Override
    public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
        float dancing = (float)entity.age / 4.0f;
        float dancing2 = (float)(entity.age + 1) / 4.0f;
        dancing += (dancing2 - dancing) * MinecraftClient.getInstance().getLastFrameDuration();
        float x = (float)Math.sin(dancing);
        float y = (float)Math.abs(Math.cos(dancing));
        model.hat.pivotX = model.head.pivotX = x * 0.75f;
        model.hat.pivotY = model.head.pivotY = y * 1.25f - 0.02f + (float)(entity.isInSneakingPose() ? 4 : 0);
        model.hat.pivotZ = model.head.pivotZ = -y * 0.75f;
        model.leftArm.pivotX += x * 0.25f;
        model.leftArm.pivotY += y * 1.25f;
        model.rightArm.pivotX += x * 0.25f;
        model.rightArm.pivotY += y * 1.25f;
        model.body.pivotX = x * 0.25f;
    }

    @Override
    public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
    }
}

