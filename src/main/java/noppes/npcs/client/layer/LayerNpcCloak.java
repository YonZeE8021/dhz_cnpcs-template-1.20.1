/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 */
package noppes.npcs.client.layer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import noppes.npcs.client.layer.LayerInterface;

public class LayerNpcCloak
extends LayerInterface {
    public LayerNpcCloak(LivingEntityRenderer render) {
        super(render);
    }

    @Override
    public void render(MatrixStack mStack, VertexConsumerProvider typeBuffer, int lightmapUV, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
        if (this.npc.textureCloakLocation == null) {
            if (this.npc.display.getCapeTexture() == null || this.npc.display.getCapeTexture().isEmpty() || !(this.base instanceof PlayerEntityModel)) {
                return;
            }
            this.npc.textureCloakLocation = new Identifier(this.npc.display.getCapeTexture());
        }
        mStack.push();
        mStack.translate(0.0, 0.0, 0.125);
        double d0 = MathHelper.lerp((double)partialTicks, (double)this.npc.prevChasingPosX, (double)this.npc.chasingPosX) - MathHelper.lerp((double)partialTicks, (double)this.npc.prevX, (double)this.npc.getX());
        double d1 = MathHelper.lerp((double)partialTicks, (double)this.npc.prevChasingPosY, (double)this.npc.chasingPosY) - MathHelper.lerp((double)partialTicks, (double)this.npc.prevY, (double)this.npc.getY());
        double d2 = MathHelper.lerp((double)partialTicks, (double)this.npc.prevChasingPosZ, (double)this.npc.chasingPosZ) - MathHelper.lerp((double)partialTicks, (double)this.npc.prevZ, (double)this.npc.getZ());
        float f = this.npc.prevBodyYaw + (this.npc.bodyYaw - this.npc.prevBodyYaw);
        double d3 = MathHelper.sin((float)(f * ((float)Math.PI / 180)));
        double d4 = -MathHelper.cos((float)(f * ((float)Math.PI / 180)));
        float f1 = (float)d1 * 10.0f;
        f1 = MathHelper.clamp((float)f1, (float)-6.0f, (float)32.0f);
        float f2 = (float)(d0 * d3 + d2 * d4) * 100.0f;
        f2 = MathHelper.clamp((float)f2, (float)0.0f, (float)150.0f);
        float f3 = (float)(d0 * d4 - d2 * d3) * 100.0f;
        f3 = MathHelper.clamp((float)f3, (float)-20.0f, (float)20.0f);
        if (f2 < 0.0f) {
            f2 = 0.0f;
        }
        f1 += MathHelper.sin((float)(MathHelper.lerp((float)partialTicks, (float)this.npc.prevHorizontalSpeed, (float)this.npc.horizontalSpeed) * 6.0f)) * 32.0f * partialTicks;
        if (this.npc.isInSneakingPose()) {
            f1 += 25.0f;
        }
        mStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0f + f2 / 2.0f + f1));
        mStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f3 / 2.0f));
        mStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - f3 / 2.0f));
        VertexConsumer ivertexbuilder = typeBuffer.getBuffer(RenderLayer.getEntityTranslucent((Identifier)this.npc.textureCloakLocation));
        ((PlayerEntityModel)this.base).renderCape(mStack, ivertexbuilder, lightmapUV, OverlayTexture.DEFAULT_UV);
        mStack.pop();
    }

    @Override
    public void rotate(MatrixStack matrixStack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    }
}

