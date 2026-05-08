/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 */
package noppes.npcs.client.model.animation;

import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import noppes.npcs.ModelData;
import noppes.npcs.client.model.animation.AniAim;
import noppes.npcs.client.model.animation.AniBlank;
import noppes.npcs.client.model.animation.AniBow;
import noppes.npcs.client.model.animation.AniCrawling;
import noppes.npcs.client.model.animation.AniDancing;
import noppes.npcs.client.model.animation.AniHug;
import noppes.npcs.client.model.animation.AniNo;
import noppes.npcs.client.model.animation.AniPoint;
import noppes.npcs.client.model.animation.AniWaving;
import noppes.npcs.client.model.animation.AniYes;
import noppes.npcs.client.model.animation.AnimationBase;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityNPCInterface;

public class AnimationHandler {
    private static final HashMap<Integer, AnimationBase> ANIMATIONS = new HashMap();

    public static void animateBipedPre(ModelData data, BipedEntityModel bipedModel, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        EntityNPCInterface npc = (EntityNPCInterface)livingEntity;
        bipedModel.body.pivotZ = 0.0f;
        bipedModel.body.pivotY = 0.0f;
        bipedModel.body.pivotX = 0.0f;
        bipedModel.body.roll = 0.0f;
        bipedModel.body.yaw = 0.0f;
        bipedModel.body.pitch = 0.0f;
        bipedModel.head.pitch = 0.0f;
        bipedModel.hat.pitch = 0.0f;
        bipedModel.head.roll = 0.0f;
        bipedModel.hat.roll = 0.0f;
        bipedModel.head.pivotX = 0.0f;
        bipedModel.hat.pivotX = 0.0f;
        bipedModel.head.pivotY = 0.0f;
        bipedModel.hat.pivotY = 0.0f;
        bipedModel.head.pivotZ = 0.0f;
        bipedModel.hat.pivotZ = 0.0f;
        bipedModel.leftLeg.pitch = 0.0f;
        bipedModel.leftLeg.yaw = 0.0f;
        bipedModel.leftLeg.roll = 0.0f;
        bipedModel.rightLeg.pitch = 0.0f;
        bipedModel.rightLeg.yaw = 0.0f;
        bipedModel.rightLeg.roll = 0.0f;
        bipedModel.leftArm.pivotX = 0.0f;
        bipedModel.leftArm.pivotY = 2.0f;
        bipedModel.leftArm.pivotZ = 0.0f;
        bipedModel.rightArm.pivotX = 0.0f;
        bipedModel.rightArm.pivotY = 2.0f;
        bipedModel.rightArm.pivotZ = 0.0f;
        AnimationBase animation = AnimationHandler.getAnimationFor(npc.currentAnimation);
        if (animation != null) {
            animation.animatePre(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, (Entity)livingEntity, bipedModel, npc.animationStart);
        }
    }

    public static void animateBipedPost(ModelData data, BipedEntityModel bipedModel, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        AnimationBase animation;
        EntityNPCInterface npc = (EntityNPCInterface)livingEntity;
        if (livingEntity.isSleeping() && bipedModel.head.pitch < 0.0f) {
            bipedModel.head.pitch = 90.0f;
            bipedModel.hat.pitch = 90.0f;
        }
        if ((animation = AnimationHandler.getAnimationFor(npc.currentAnimation)) != null) {
            animation.animatePost(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, (Entity)livingEntity, bipedModel, npc.animationStart);
        }
        if (bipedModel.sneaking) {
            bipedModel.body.pitch = 0.5f / data.getPartConfig((EnumParts)EnumParts.BODY).scaleY;
        }
        if (bipedModel instanceof PlayerEntityModel) {
            PlayerEntityModel playerModel = (PlayerEntityModel)bipedModel;
            playerModel.leftPants.copyTransform(playerModel.leftLeg);
            playerModel.rightPants.copyTransform(playerModel.rightLeg);
            playerModel.leftSleeve.copyTransform(playerModel.leftArm);
            playerModel.rightSleeve.copyTransform(playerModel.rightArm);
            playerModel.jacket.copyTransform(playerModel.body);
        }
        bipedModel.hat.copyTransform(bipedModel.head);
    }

    public static void addAnimation(int enumAnimation, AnimationBase animationBase) {
        ANIMATIONS.put(enumAnimation, animationBase);
    }

    public static HashMap<Integer, AnimationBase> getAllAnimations() {
        return ANIMATIONS;
    }

    public static AnimationBase getAnimationFor(int animation) {
        return ANIMATIONS.get(animation);
    }

    static {
        AnimationHandler.addAnimation(0, new AniBlank());
        AnimationHandler.addAnimation(2, new AniBlank());
        AnimationHandler.addAnimation(7, new AniCrawling());
        AnimationHandler.addAnimation(3, new AniHug());
        AnimationHandler.addAnimation(5, new AniDancing());
        AnimationHandler.addAnimation(10, new AniWaving());
        AnimationHandler.addAnimation(11, new AniBow());
        AnimationHandler.addAnimation(13, new AniYes());
        AnimationHandler.addAnimation(12, new AniNo());
        AnimationHandler.addAnimation(8, new AniPoint());
        AnimationHandler.addAnimation(14, new AniBlank());
        AnimationHandler.addAnimation(6, new AniAim());
        AnimationHandler.addAnimation(9, new AnimationBase(){

            @Override
            public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
            }

            @Override
            public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
                model.head.pitch = 0.7f;
                model.hat.pitch = 0.7f;
            }
        });
        AnimationHandler.addAnimation(1, new AnimationBase(){

            @Override
            public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
                model.riding = true;
            }

            @Override
            public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
                model.riding = false;
            }
        });
        AnimationHandler.addAnimation(4, new AnimationBase(){

            @Override
            public void animatePre(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
                model.sneaking = true;
            }

            @Override
            public void animatePost(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, Entity entity, BipedEntityModel model, int animationStart) {
                model.sneaking = false;
            }
        });
    }
}

