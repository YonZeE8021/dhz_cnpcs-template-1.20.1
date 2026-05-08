/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.entity.model.EntityModel
 */
package noppes.npcs.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import noppes.npcs.entity.EntityNpcSlime;
import noppes.npcs.shared.client.model.NopModelPart;

@Environment(value=EnvType.CLIENT)
public class ModelNpcSlime<T extends EntityNpcSlime>
extends EntityModel<T> {
    NopModelPart outerBody = new NopModelPart(64, 64, 0, 0);
    NopModelPart innerBody;
    NopModelPart slimeRightEye;
    NopModelPart slimeLeftEye;
    NopModelPart slimeMouth;

    public ModelNpcSlime(int par1) {
        this.outerBody.addBox(-8.0f, 32.0f, -8.0f, 16.0f, 16.0f, 16.0f);
        if (par1 > 0) {
            this.innerBody = new NopModelPart(64, 64, 0, 32);
            this.innerBody.addBox(-3.0f, 17.0f, -3.0f, 6.0f, 6.0f, 6.0f);
            this.slimeRightEye = new NopModelPart(64, 64, 0, 0);
            this.slimeRightEye.addBox(-3.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f);
            this.slimeLeftEye = new NopModelPart(64, 64, 0, 4);
            this.slimeLeftEye.addBox(1.25f, 18.0f, -3.5f, 2.0f, 2.0f, 2.0f);
            this.slimeMouth = new NopModelPart(64, 64, 0, 8);
            this.slimeMouth.addBox(0.0f, 21.0f, -3.5f, 1.0f, 1.0f, 1.0f);
        }
    }

    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    public void render(MatrixStack mStack, VertexConsumer iVertex, int lightmapUV, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if (this.innerBody != null) {
            this.innerBody.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        } else {
            mStack.push();
            mStack.scale(0.5f, 0.5f, 0.5f);
            this.outerBody.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
            mStack.pop();
        }
        if (this.slimeRightEye != null) {
            this.slimeRightEye.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
            this.slimeLeftEye.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
            this.slimeMouth.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        }
    }
}

