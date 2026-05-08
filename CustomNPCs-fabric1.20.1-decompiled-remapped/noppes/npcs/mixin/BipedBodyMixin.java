/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package noppes.npcs.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.model.animation.AnimationHandler;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.roles.JobPuppet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BipedEntityModel.class})
public class BipedBodyMixin<T extends LivingEntity> {
    @Inject(at={@At(value="HEAD")}, method={"setupAnim"})
    private void setupAnimPre(T livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callbackInfo) {
        BipedEntityModel bipedModel = (BipedEntityModel)this;
        if (livingEntity instanceof EntityCustomNpc && bipedModel instanceof PlayerEntityModel) {
            EntityCustomNpc playerEntity = (EntityCustomNpc)((Object)livingEntity);
            ClientProxy.data = playerEntity.modelData;
            ClientProxy.playerModel = (PlayerEntityModel)bipedModel;
            RenderCustomNpc renderer = (RenderCustomNpc)MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(livingEntity);
            ClientProxy.armorLayer = renderer.armorLayer;
            AnimationHandler.animateBipedPre(ClientProxy.data, bipedModel, livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    @Inject(at={@At(value="TAIL")}, method={"setupAnim"})
    private void setupAnimPost(T livingEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callbackInfo) {
        BipedEntityModel bipedModel = (BipedEntityModel)this;
        if (livingEntity instanceof EntityCustomNpc) {
            JobPuppet job;
            EntityCustomNpc npc = (EntityCustomNpc)((Object)livingEntity);
            AnimationHandler.animateBipedPost(ClientProxy.data, bipedModel, livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            if (npc.job.getType() == 9 && (job = (JobPuppet)npc.job).isActive()) {
                float pi = (float)Math.PI;
                float partialTicks = MinecraftClient.getInstance().getLastFrameDuration();
                if (!job.head.disabled) {
                    bipedModel.hat.pitch = bipedModel.head.pitch = job.getRotationX(job.head, job.head2, partialTicks) * pi;
                    bipedModel.hat.yaw = bipedModel.head.yaw = job.getRotationY(job.head, job.head2, partialTicks) * pi;
                    bipedModel.hat.roll = bipedModel.head.roll = job.getRotationZ(job.head, job.head2, partialTicks) * pi;
                }
                if (!job.body.disabled) {
                    bipedModel.body.pitch = job.getRotationX(job.body, job.body2, partialTicks) * pi;
                    bipedModel.body.yaw = job.getRotationY(job.body, job.body2, partialTicks) * pi;
                    bipedModel.body.roll = job.getRotationZ(job.body, job.body2, partialTicks) * pi;
                }
                if (!job.larm.disabled) {
                    bipedModel.leftArm.pitch = job.getRotationX(job.larm, job.larm2, partialTicks) * pi;
                    bipedModel.leftArm.yaw = job.getRotationY(job.larm, job.larm2, partialTicks) * pi;
                    bipedModel.leftArm.roll = job.getRotationZ(job.larm, job.larm2, partialTicks) * pi;
                    if (npc.display.getHasLivingAnimation()) {
                        bipedModel.leftArm.roll -= MathHelper.cos((float)(ageInTicks * 0.09f)) * 0.05f + 0.05f;
                        bipedModel.leftArm.pitch -= MathHelper.sin((float)(ageInTicks * 0.067f)) * 0.05f;
                    }
                }
                if (!job.rarm.disabled) {
                    bipedModel.rightArm.pitch = job.getRotationX(job.rarm, job.rarm2, partialTicks) * pi;
                    bipedModel.rightArm.yaw = job.getRotationY(job.rarm, job.rarm2, partialTicks) * pi;
                    bipedModel.rightArm.roll = job.getRotationZ(job.rarm, job.rarm2, partialTicks) * pi;
                    if (npc.display.getHasLivingAnimation()) {
                        bipedModel.rightArm.roll += MathHelper.cos((float)(ageInTicks * 0.09f)) * 0.05f + 0.05f;
                        bipedModel.rightArm.pitch += MathHelper.sin((float)(ageInTicks * 0.067f)) * 0.05f;
                    }
                }
                if (!job.rleg.disabled) {
                    bipedModel.rightLeg.pitch = job.getRotationX(job.rleg, job.rleg2, partialTicks) * pi;
                    bipedModel.rightLeg.yaw = job.getRotationY(job.rleg, job.rleg2, partialTicks) * pi;
                    bipedModel.rightLeg.roll = job.getRotationZ(job.rleg, job.rleg2, partialTicks) * pi;
                }
                if (!job.lleg.disabled) {
                    bipedModel.leftLeg.pitch = job.getRotationX(job.lleg, job.lleg2, partialTicks) * pi;
                    bipedModel.leftLeg.yaw = job.getRotationY(job.lleg, job.lleg2, partialTicks) * pi;
                    bipedModel.leftLeg.roll = job.getRotationZ(job.lleg, job.lleg2, partialTicks) * pi;
                }
            }
        }
    }
}

