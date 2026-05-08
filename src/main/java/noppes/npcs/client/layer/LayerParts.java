/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 */
package noppes.npcs.client.layer;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import noppes.npcs.ModelData;
import noppes.npcs.ModelPartConfig;
import noppes.npcs.client.parts.ModelPartWrapper;
import noppes.npcs.client.parts.MpmPart;
import noppes.npcs.client.parts.MpmPartAbstractClient;
import noppes.npcs.client.parts.MpmPartData;
import noppes.npcs.client.parts.PartBehaviorType;
import noppes.npcs.client.parts.PartRenderType;
import noppes.npcs.constants.BodyPart;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.shared.common.util.NopVector3f;

public class LayerParts<T extends EntityCustomNpc, M extends BipedEntityModel<T>>
extends FeatureRenderer<T, M> {
    public LayerParts(LivingEntityRenderer<T, M> render) {
        super(render);
    }

    public void render(MatrixStack mStack, VertexConsumerProvider typeBuffer, int lightmapUV, EntityCustomNpc player, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
        ModelData data = ModelData.get(player);
        for (MpmPartData part : data.mpmParts) {
            MpmPart mp = part.getPart();
            if (mp == null || mp.renderType == PartRenderType.NONE || !mp.isEnabled) continue;
            MpmPartAbstractClient partc = (MpmPartAbstractClient)mp;
            this.rotate(data, partc, player, (BipedEntityModel)this.getContextModel(), limbSwing, limbSwingAmount, partialTicks, age, netHeadYaw, headPitch);
            LayerParts.renderPart(part, partc, mStack, typeBuffer, lightmapUV, player, (BipedEntityModel)this.getContextModel(), data);
        }
        data.startMoveAnimation = false;
        data.startAnimation = false;
    }

    public static void renderPart(MpmPartData data, MpmPartAbstractClient partc, MatrixStack mStack, VertexConsumerProvider typeBuffer, int lightmapUV, EntityCustomNpc player, BipedEntityModel model, ModelData pdata) {
        ModelPartConfig config;
        ModelPartWrapper lmodelPart;
        ModelPartWrapper rmodelPart;
        mStack.push();
        boolean shouldRender = true;
        if (partc.bodyPart == BodyPart.HEAD) {
            model.head.rotate(mStack);
        }
        if (partc.bodyPart == BodyPart.BODY) {
            model.body.rotate(mStack);
        }
        if (partc.bodyPart == BodyPart.LEGS) {
            rmodelPart = partc.getPart("right_leg");
            lmodelPart = partc.getPart("left_leg");
            if (rmodelPart != null) {
                shouldRender = false;
                mStack.push();
                config = pdata.getPartConfig(EnumParts.LEG_RIGHT);
                mStack.translate(0.0f, config.transY * 2.0f, 0.0f);
                mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
                if (lmodelPart != null) {
                    lmodelPart.setVisible(false);
                }
                rmodelPart.setVisible(true);
                partc.render(data, mStack, typeBuffer, lightmapUV, (LivingEntity)player);
                mStack.pop();
            }
            if (lmodelPart != null) {
                shouldRender = false;
                mStack.push();
                config = pdata.getPartConfig(EnumParts.LEG_LEFT);
                mStack.translate(0.0f, config.transY * 2.0f, 0.0f);
                mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
                if (rmodelPart != null) {
                    rmodelPart.setVisible(false);
                }
                lmodelPart.setVisible(true);
                partc.render(data, mStack, typeBuffer, lightmapUV, (LivingEntity)player);
                mStack.pop();
            }
            if (shouldRender) {
                config = pdata.getPartConfig(EnumParts.LEG_LEFT);
                mStack.translate(0.0f, config.transY * 2.0f, 0.0f);
                mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
            }
        }
        if (partc.bodyPart == BodyPart.ARMS) {
            rmodelPart = partc.getPart("right_arm");
            lmodelPart = partc.getPart("left_arm");
            if (rmodelPart != null) {
                shouldRender = false;
                mStack.push();
                config = pdata.getPartConfig(EnumParts.ARM_RIGHT);
                mStack.translate(0.25f * (config.scaleX - 1.0f), config.transY + (1.0f - config.scaleY) * 0.125f, 0.0f);
                mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
                if (lmodelPart != null) {
                    lmodelPart.setVisible(false);
                }
                rmodelPart.setVisible(true);
                partc.render(data, mStack, typeBuffer, lightmapUV, (LivingEntity)player);
                mStack.pop();
            }
            if (lmodelPart != null) {
                shouldRender = false;
                mStack.push();
                config = pdata.getPartConfig(EnumParts.ARM_LEFT);
                mStack.translate(-0.25f * (config.scaleX - 1.0f), config.transY + (1.0f - config.scaleY) * 0.125f, 0.0f);
                mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
                if (rmodelPart != null) {
                    rmodelPart.setVisible(false);
                }
                lmodelPart.setVisible(true);
                partc.render(data, mStack, typeBuffer, lightmapUV, (LivingEntity)player);
                mStack.pop();
            }
            if (shouldRender) {
                config = pdata.getPartConfig(EnumParts.ARM_LEFT);
                mStack.translate(0.0f, config.transY + (1.0f - config.scaleY) * 0.125f, 0.0f);
                mStack.scale(config.scaleX, config.scaleY, config.scaleZ);
            }
        }
        if (shouldRender) {
            partc.render(data, mStack, typeBuffer, lightmapUV, (LivingEntity)player);
        }
        mStack.pop();
    }

    private void rotate(ModelData playerdata, MpmPartAbstractClient part, EntityCustomNpc player, BipedEntityModel base, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
        ModelPartWrapper modelPartL;
        ModelPartWrapper modelPart;
        ModelPartWrapper modelPart2;
        BipedEntityModel model;
        part.animationData.animation(19, (int)age, partialTicks);
        if ((double)limbSwingAmount > 0.01) {
            if (player.isOnGround()) {
                if (player.ais.getAnimation() == 7) {
                    playerdata.setAnimation(7);
                } else {
                    playerdata.setAnimation(15);
                }
            } else {
                playerdata.setAnimation(17);
            }
        } else if (player.ais.getAnimation() == 0) {
            playerdata.setAnimation(16);
        } else {
            playerdata.setAnimation(player.ais.getAnimation());
        }
        int moveAnimation = playerdata.getMoveAnimtion((LivingEntity)player);
        if (playerdata.startMoveAnimation) {
            part.animationData.start(moveAnimation);
        }
        boolean didAnimation = false;
        if (playerdata.animation != 0) {
            if (playerdata.startAnimation) {
                part.animationData.start(playerdata.animation);
            }
            didAnimation = part.animationData.animation(playerdata.animation, (int)age, partialTicks);
        }
        if (!(didAnimation || moveAnimation != 16 && moveAnimation != 18)) {
            part.animationData.animation(moveAnimation, (int)age, partialTicks);
        } else {
            part.animationData.animation(moveAnimation, MathHelper.cos((float)(limbSwing * 0.6662f)) * limbSwingAmount / 2.0f + 0.5f);
        }
        if (part.animationType == PartBehaviorType.LEGS) {
            model = (BipedEntityModel)this.getContextModel();
            modelPart2 = part.getPart("right_leg");
            if (modelPart2 != null) {
                modelPart2.setRot(new NopVector3f(model.rightLeg.pitch, model.rightLeg.yaw, model.rightLeg.roll));
                modelPart2.setPos(new NopVector3f(model.rightLeg.pivotX, model.rightLeg.pivotY, model.rightLeg.pivotZ));
            }
            if ((modelPart2 = part.getPart("left_leg")) != null) {
                modelPart2.setRot(new NopVector3f(model.leftLeg.pitch, model.leftLeg.yaw, model.leftLeg.roll));
                modelPart2.setPos(new NopVector3f(model.leftLeg.pivotX, model.leftLeg.pivotY, model.leftLeg.pivotZ));
            }
        }
        if (part.animationType == PartBehaviorType.ARMS) {
            model = (BipedEntityModel)this.getContextModel();
            modelPart2 = part.getPart("right_arm");
            if (modelPart2 != null) {
                modelPart2.setRot(new NopVector3f(model.rightArm.pitch, model.rightArm.yaw, model.rightArm.roll));
                modelPart2.setPos(new NopVector3f(model.rightArm.pivotX, model.rightArm.pivotY, model.rightArm.pivotZ));
            }
            if ((modelPart2 = part.getPart("left_arm")) != null) {
                modelPart2.setRot(new NopVector3f(model.leftArm.pitch, model.leftArm.yaw, model.leftArm.roll));
                modelPart2.setPos(new NopVector3f(model.leftArm.pivotX, model.leftArm.pivotY, model.leftArm.pivotZ));
            }
        }
        if (part.animationType == PartBehaviorType.BEARD) {
            part.rot = part.rot.set(base.head.pitch < 0.0f ? 0.0f : -base.head.pitch, part.rot.y, part.rot.z);
        }
        if (part.animationType == PartBehaviorType.HAIR) {
            ModelPart head = base.head;
            if (head.pitch < 0.0f) {
                part.rot = part.rot.set(-head.pitch * 1.2f, part.rot.y, part.rot.z);
                if (head.pitch > -1.0f) {
                    part.pos = part.pos.set(part.pos.x, -head.pitch * 1.5f, -head.pitch * 1.5f);
                }
            } else {
                part.pos = NopVector3f.ZERO;
            }
        }
        if (part.animationType == PartBehaviorType.WINGS) {
            float xRot;
            float zRot;
            modelPart = part.getPart("right_wing");
            modelPartL = part.getPart("left_wing");
            if (player.getWorld().isAir(player.getBlockPos().down())) {
                float motion = Math.abs(MathHelper.sin((float)(limbSwing * 0.033f + (float)Math.PI)) * 0.4f) * limbSwingAmount;
                float speed = 0.55f + 0.5f * motion;
                float y = MathHelper.sin((float)(age * 0.35f));
                xRot = zRot = y * 0.5f * speed;
            } else {
                zRot = MathHelper.cos((float)(age * 0.09f)) * 0.05f + 0.05f;
                xRot = MathHelper.sin((float)(age * 0.067f)) * 0.05f;
            }
            modelPart.setRot(modelPart.oriRot.add(xRot, xRot, zRot));
            modelPartL.setRot(modelPartL.oriRot.add(xRot, -xRot, -zRot));
        }
        if (part.animationType == PartBehaviorType.WINGS2) {
            float yRot;
            modelPart = part.getPart("right_wing");
            modelPartL = part.getPart("left_wing");
            if (player.getWorld().isAir(player.getBlockPos().down())) {
                float motion = Math.abs(MathHelper.sin((float)(limbSwing * 0.033f + (float)Math.PI)) * 0.4f) * limbSwingAmount;
                float speed = 0.55f + 0.5f * motion;
                float y = MathHelper.sin((float)(age * 0.35f));
                yRot = y * 0.5f * speed;
            } else {
                yRot = MathHelper.sin((float)(age * 0.07f)) * 0.44f;
            }
            modelPart.setRot(modelPart.oriRot.add(0.0f, yRot, 0.0f));
            modelPartL.setRot(modelPartL.oriRot.add(0.0f, -yRot, 0.0f));
        }
    }
}

