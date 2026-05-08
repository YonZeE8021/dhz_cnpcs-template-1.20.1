/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 */
package noppes.npcs.client.model.animation;

import net.minecraft.entity.Entity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import noppes.npcs.client.model.animation.AnimationBase;

public class AniWaving
implements AnimationBase {
    @Override
    public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
    }

    @Override
    public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
        float f = MathHelper.sin((float)((float)entity.age * 0.27f));
        float f2 = MathHelper.sin((float)((float)(entity.age + 1) * 0.27f));
        f += (f2 - f) * MinecraftClient.getInstance().getLastFrameDuration();
        model.rightArm.pitch = -0.1f;
        model.rightArm.yaw = 0.0f;
        model.rightArm.roll = (float)(2.141592653589793 - (double)(f * 0.5f));
    }
}

