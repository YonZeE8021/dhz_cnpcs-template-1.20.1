/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.texture.SpriteAtlasTexture
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.model.json.ModelTransformationMode
 *  net.minecraft.client.render.entity.EntityRenderer
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 */
package noppes.npcs.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemConvertible;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.entity.EntityRenderer;
import noppes.npcs.entity.EntityProjectile;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class RenderProjectile<T extends EntityProjectile>
extends EntityRenderer<T> {
    public boolean renderWithColor = true;
    private static final Identifier field_110780_a = new Identifier("textures/entity/projectiles/arrow.png");
    private static final Identifier field_110798_h = new Identifier("textures/misc/enchanted_item_glint.png");
    private boolean crash = false;
    private boolean crash2 = false;

    public RenderProjectile(EntityRendererFactory.Context manager) {
        super(manager);
    }

    public void render(T projectile, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider buffer, int packedLight) {
        MinecraftClient mc = MinecraftClient.getInstance();
        matrixStack.push();
        float scale = (float)((EntityProjectile)((Object)projectile)).getSize() / 10.0f;
        ItemStack item = ((EntityProjectile)((Object)projectile)).getItemDisplay();
        matrixStack.scale(scale, scale, scale);
        if (((EntityProjectile)((Object)projectile)).isArrow()) {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp((float)partialTicks, (float)((EntityProjectile)((Object)projectile)).prevYaw, (float)projectile.getYaw()) - 90.0f));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp((float)partialTicks, (float)((EntityProjectile)((Object)projectile)).prevPitch, (float)projectile.getPitch())));
            float f9 = (float)((EntityProjectile)((Object)projectile)).arrowShake - partialTicks;
            if (f9 > 0.0f) {
                float f10 = -MathHelper.sin((float)(f9 * 3.0f)) * f9;
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f10));
            }
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0f));
            matrixStack.scale(0.05625f, 0.05625f, 0.05625f);
            matrixStack.translate(-4.0, 0.0, 0.0);
            VertexConsumer ivertexbuilder = buffer.getBuffer(RenderLayer.getEntityCutout((Identifier)this.getTextureLocation(projectile)));
            MatrixStack.Entry matrixstack$entry = matrixStack.peek();
            Matrix4f matrix4f = matrixstack$entry.getPositionMatrix();
            Matrix3f matrix3f = matrixstack$entry.getNormalMatrix();
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, -2, 0.0f, 0.15625f, -1, 0, 0, packedLight);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, 2, 0.15625f, 0.15625f, -1, 0, 0, packedLight);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, 2, 0.15625f, 0.3125f, -1, 0, 0, packedLight);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, -2, 0.0f, 0.3125f, -1, 0, 0, packedLight);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, -2, 0.0f, 0.15625f, 1, 0, 0, packedLight);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, 2, 2, 0.15625f, 0.15625f, 1, 0, 0, packedLight);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, 2, 0.15625f, 0.3125f, 1, 0, 0, packedLight);
            this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -7, -2, -2, 0.0f, 0.3125f, 1, 0, 0, packedLight);
            for (int j = 0; j < 4; ++j) {
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
                this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -8, -2, 0, 0.0f, 0.0f, 0, 1, 0, packedLight);
                this.drawVertex(matrix4f, matrix3f, ivertexbuilder, 8, -2, 0, 0.5f, 0.0f, 0, 1, 0, packedLight);
                this.drawVertex(matrix4f, matrix3f, ivertexbuilder, 8, 2, 0, 0.5f, 0.15625f, 0, 1, 0, packedLight);
                this.drawVertex(matrix4f, matrix3f, ivertexbuilder, -8, 2, 0, 0.0f, 0.15625f, 0, 1, 0, packedLight);
            }
        } else if (((EntityProjectile)((Object)projectile)).is3D()) {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp((float)partialTicks, (float)((EntityProjectile)((Object)projectile)).prevYaw, (float)projectile.getYaw()) - 180.0f));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp((float)partialTicks, (float)((EntityProjectile)((Object)projectile)).prevPitch, (float)projectile.getPitch())));
            matrixStack.translate(0.0f, -0.125f, 0.25f);
            if (item.getItem() instanceof BlockItem && Block.getBlockFromItem((Item)item.getItem()).getDefaultState().getRenderType() == BlockRenderType.field_11456) {
                matrixStack.translate(0.0f, 0.1875f, -0.3125f);
                matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(20.0f));
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0f));
                float f8 = 0.375f;
                matrixStack.scale(-f8, -f8, f8);
            }
            if (!this.crash) {
                try {
                    mc.getItemRenderer().renderItem(item, ModelTransformationMode.field_4320, packedLight, OverlayTexture.DEFAULT_UV, matrixStack, buffer, null, 0);
                }
                catch (Throwable e) {
                    this.crash = true;
                }
            } else if (!this.crash2) {
                try {
                    mc.getItemRenderer().renderItem(item, ModelTransformationMode.field_4315, packedLight, OverlayTexture.DEFAULT_UV, matrixStack, buffer, null, 0);
                }
                catch (Throwable ee) {
                    this.crash2 = true;
                }
            } else {
                mc.getItemRenderer().renderItem(new ItemStack((ItemConvertible)Blocks.field_10566), ModelTransformationMode.field_4318, packedLight, OverlayTexture.DEFAULT_UV, matrixStack, buffer, null, 0);
            }
        } else {
            matrixStack.scale(0.5f, 0.5f, 0.5f);
            matrixStack.multiply(this.dispatcher.camera.getRotation());
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
            mc.getItemRenderer().renderItem(item, ModelTransformationMode.field_4318, packedLight, OverlayTexture.DEFAULT_UV, matrixStack, buffer, null, 0);
        }
        if (!((EntityProjectile)((Object)projectile)).is3D() || ((EntityProjectile)((Object)projectile)).glows()) {
            // empty if block
        }
        matrixStack.pop();
    }

    protected Identifier func_110779_a(EntityProjectile projectile) {
        return projectile.isArrow() ? field_110780_a : SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    public Identifier getTextureLocation(T par1Entity) {
        return ((EntityProjectile)((Object)par1Entity)).isArrow() ? field_110780_a : SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    public void drawVertex(Matrix4f matrix, Matrix3f normals, VertexConsumer vertexBuilder, int offsetX, int offsetY, int offsetZ, float textureX, float textureY, int p_229039_9_, int p_229039_10_, int p_229039_11_, int packedLightIn) {
        vertexBuilder.vertex(matrix, (float)offsetX, (float)offsetY, (float)offsetZ).color(255, 255, 255, 255).texture(textureX, textureY).overlay(OverlayTexture.DEFAULT_UV).light(packedLightIn).normal(normals, (float)p_229039_9_, (float)p_229039_11_, (float)p_229039_10_).next();
    }
}

