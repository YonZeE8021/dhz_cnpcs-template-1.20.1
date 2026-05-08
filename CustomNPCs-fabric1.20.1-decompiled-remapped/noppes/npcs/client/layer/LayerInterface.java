/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 */
package noppes.npcs.client.layer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import noppes.npcs.ModelData;
import noppes.npcs.ModelPartData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.shared.client.model.NopModelPart;

public abstract class LayerInterface
extends FeatureRenderer {
    protected LivingEntityRenderer render;
    protected EntityCustomNpc npc;
    protected ModelData playerdata;
    public BipedEntityModel base;
    private int color;

    public LayerInterface(LivingEntityRenderer render) {
        super((FeatureRendererContext)render);
        this.render = render;
        this.base = (BipedEntityModel)render.getModel();
    }

    public void setColor(ModelPartData data, LivingEntity entity) {
    }

    protected float red() {
        if (this.npc.hurtTime > 0 || this.npc.deathTime > 0) {
            return 1.0f;
        }
        return (float)(this.color >> 16 & 0xFF) / 255.0f;
    }

    protected float green() {
        if (this.npc.hurtTime > 0 || this.npc.deathTime > 0) {
            return 0.0f;
        }
        return (float)(this.color >> 8 & 0xFF) / 255.0f;
    }

    protected float blue() {
        if (this.npc.hurtTime > 0 || this.npc.deathTime > 0) {
            return 0.0f;
        }
        return (float)(this.color & 0xFF) / 255.0f;
    }

    protected float alpha() {
        boolean flag = !this.npc.isInvisible();
        boolean flag1 = !flag && !this.npc.isInvisibleTo((PlayerEntity)MinecraftClient.getInstance().player);
        return flag1 ? 0.15f : 0.99f;
    }

    public void preRender(ModelPartData data) {
        if (this.npc.hurtTime > 0 || this.npc.deathTime > 0) {
            return;
        }
        this.color = data.color;
        if (this.npc.display.getTint() != 0xFFFFFF) {
            this.color = data.color != 0xFFFFFF ? this.blend(data.color, this.npc.display.getTint(), 0.5f) : this.npc.display.getTint();
        }
    }

    public int blend(int color1, int color2, float ratio) {
        if (ratio >= 1.0f) {
            return color2;
        }
        if (ratio <= 0.0f) {
            return color1;
        }
        int aR = (color1 & 0xFF0000) >> 16;
        int aG = (color1 & 0xFF00) >> 8;
        int aB = color1 & 0xFF;
        int bR = (color2 & 0xFF0000) >> 16;
        int bG = (color2 & 0xFF00) >> 8;
        int bB = color2 & 0xFF;
        int R = (int)((float)aR + (float)(bR - aR) * ratio);
        int G = (int)((float)aG + (float)(bG - aG) * ratio);
        int B = (int)((float)aB + (float)(bB - aB) * ratio);
        return R << 16 | G << 8 | B;
    }

    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, Entity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        this.npc = (EntityCustomNpc)entity;
        if (this.npc.isInvisibleTo((PlayerEntity)MinecraftClient.getInstance().player)) {
            return;
        }
        this.playerdata = this.npc.modelData;
        this.base = (BipedEntityModel)this.render.getModel();
        this.rotate(matrixStackIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        matrixStackIn.push();
        if (entity.isInvisible()) {
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)0.15f);
            RenderSystem.depthMask((boolean)false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc((int)770, (int)771);
        }
        if (this.npc.hurtTime > 0 || this.npc.deathTime > 0) {
            // empty if block
        }
        if (this.npc.isInSneakingPose()) {
            // empty if block
        }
        this.render(matrixStackIn, bufferIn, packedLightIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        if (entity.isInvisible()) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask((boolean)true);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
        matrixStackIn.pop();
    }

    public RenderLayer getRenderType(ModelPartData data) {
        Identifier resource = this.npc.textureLocation;
        if (!data.playerTexture) {
            resource = data.getResource();
        }
        return RenderLayer.getEntityTranslucent((Identifier)resource);
    }

    public void setRotation(NopModelPart model, float x, float y, float z) {
        model.xRot = x;
        model.yRot = y;
        model.zRot = z;
    }

    public abstract void render(MatrixStack var1, VertexConsumerProvider var2, int var3, float var4, float var5, float var6, float var7, float var8, float var9);

    public abstract void rotate(MatrixStack var1, float var2, float var3, float var4, float var5, float var6, float var7);
}

