/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

@Environment(value=EnvType.CLIENT)
public class LayerGlow<T extends EntityNPCInterface, M extends EntityModel<T>>
extends FeatureRenderer<T, M> {
    public LayerGlow(RenderCustomNpc npcRenderer) {
        super((FeatureRendererContext)npcRenderer);
    }

    public void render(MatrixStack matrixStackIn, VertexConsumerProvider typeBuffer, int packedLightIn, T npc, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (((EntityNPCInterface)((Object)npc)).display.getOverlayTexture().isEmpty()) {
            return;
        }
        if (((EntityNPCInterface)((Object)npc)).textureGlowLocation == null) {
            ((EntityNPCInterface)((Object)npc)).textureGlowLocation = new Identifier(((EntityNPCInterface)((Object)npc)).display.getOverlayTexture());
        }
        VertexConsumer ivertexbuilder = null;
        ivertexbuilder = ((EntityNPCInterface)((Object)npc)).display.isOverlayGlowing() ? typeBuffer.getBuffer(RenderLayer.getEntityTranslucentEmissive((Identifier)((EntityNPCInterface)((Object)npc)).textureGlowLocation)) : typeBuffer.getBuffer(RenderLayer.getEntityTranslucent((Identifier)((EntityNPCInterface)((Object)npc)).textureGlowLocation));
        this.getContextModel().render(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlay(npc, (float)0.0f), 1.0f, 1.0f, 1.0f, 1.0f);
    }
}

