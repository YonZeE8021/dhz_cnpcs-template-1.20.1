/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 */
package noppes.npcs.client.layer;

import net.minecraft.entity.Entity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import noppes.npcs.client.model.ModelNpcSlime;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityNpcSlime;

public class LayerSlimeNpc<T extends EntityNpcSlime>
extends FeatureRenderer<T, ModelNpcSlime<T>> {
    private final LivingEntityRenderer renderer;
    private final EntityModel slimeModel = new ModelNpcSlime(0);

    public LayerSlimeNpc(LivingEntityRenderer renderer) {
        super((FeatureRendererContext)renderer);
        this.renderer = renderer;
    }

    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, T living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!((EntityNPCInterface)(living)).isInvisible()) {
            ((ModelNpcSlime)this.getContextModel()).copyStateTo(this.slimeModel);
            this.slimeModel.animateModel(living, limbSwing, limbSwingAmount, partialTicks);
            this.slimeModel.setAngles(living, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(living)));
            this.slimeModel.render(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlay(living, (float)0.0f), 1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}

