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

public class AniYes
implements AnimationBase {
    @Override
    public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
    }

    @Override
    public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
        float ticks = (float)(entity.age - animationStart) / 8.0f;
        float ticks2 = (float)(entity.age + 1 - animationStart) / 8.0f;
        ticks += (ticks2 - ticks) * MinecraftClient.getInstance().getLastFrameDuration();
        float ani = (ticks %= 2.0f) - 0.5f;
        if (ticks > 1.0f) {
            ani = 1.5f - ticks;
        }
        model.head.pitch = ani;
    }
}

