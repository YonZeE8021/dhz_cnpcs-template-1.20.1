/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 */
package noppes.npcs.client.layer;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.layer.LayerInterface;
import noppes.npcs.client.layer.LayerPreRender;
import noppes.npcs.client.model.ModelHeadwear;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.shared.client.model.Model2DRenderer;

public class LayerHeadwear
extends LayerInterface
implements LayerPreRender {
    private final ModelHeadwear headwear = new ModelHeadwear();

    public LayerHeadwear(LivingEntityRenderer render) {
        super(render);
    }

    @Override
    public void render(MatrixStack mStack, VertexConsumerProvider typeBuffer, int lightmapUV, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
        if (CustomNpcs.HeadWearType != 1 || this.npc.textureLocation == null) {
            return;
        }
        float red = 1.0f;
        float blue = 1.0f;
        float green = 1.0f;
        if (this.npc.hurtTime <= 0 && this.npc.deathTime <= 0) {
            int color = this.npc.display.getTint();
            red = (float)(color >> 16 & 0xFF) / 255.0f;
            green = (float)(color >> 8 & 0xFF) / 255.0f;
            blue = (float)(color & 0xFF) / 255.0f;
        }
        this.base.head.rotate(mStack);
        Model2DRenderer.textureOverride = this.npc.textureLocation;
        VertexConsumer ivertex = typeBuffer.getBuffer(RenderLayer.getEntityTranslucent((Identifier)this.npc.textureLocation));
        int m = OverlayTexture.packUv((int)OverlayTexture.getU((float)0.0f), (int)OverlayTexture.getV((this.npc.hurtTime > 0 || this.npc.deathTime > 0 ? 1 : 0) != 0));
        this.headwear.render(mStack, ivertex, lightmapUV, m, red, green, blue, this.alpha());
        Model2DRenderer.textureOverride = null;
    }

    @Override
    public void rotate(MatrixStack matrixStack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void preRender(EntityCustomNpc npc) {
        boolean bl = this.base.hat.visible = this.base.head.visible && CustomNpcs.HeadWearType != 1;
        if (!this.base.hat.visible) {
            this.headwear.config = null;
        }
    }
}

