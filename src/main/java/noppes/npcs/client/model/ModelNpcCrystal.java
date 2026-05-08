/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionf
 */
package noppes.npcs.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.math.RotationAxis;
import noppes.npcs.shared.client.model.NopModelPart;
import org.joml.Quaternionf;

public class ModelNpcCrystal
extends EntityModel {
    private static final float SIN_45 = (float)Math.sin(0.7853981633974483);
    private NopModelPart innerCube;
    private NopModelPart coreCube = new NopModelPart(64, 32, 0, 0);
    private NopModelPart frameCube;
    float ticks;
    float tickCount;

    public ModelNpcCrystal() {
        this.coreCube.addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        this.innerCube = new NopModelPart(64, 32, 32, 0);
        this.innerCube.addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
        this.frameCube = new NopModelPart(64, 32, 0, 16);
        this.frameCube.addBox(-6.0f, 16.0f, -6.0f, 12.0f, 4.0f, 12.0f);
    }

    public void setAngles(Entity p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
    }

    public void animateModel(Entity par1EntityLiving, float f6, float f5, float par9) {
        this.ticks = par9;
        this.tickCount = par1EntityLiving.age;
    }

    public void render(MatrixStack mStack, VertexConsumer ivertex, int lightmapUV, int packedOverlayIn, float red, float green, float blue, float alpha) {
        mStack.push();
        mStack.scale(2.0f, 2.0f, 2.0f);
        mStack.translate(0.0f, -0.5f, 0.0f);
        this.frameCube.render(mStack, ivertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        float f = this.tickCount + this.ticks;
        float f1 = MathHelper.sin((float)(f * 0.2f)) / 2.0f + 0.5f;
        f1 = f1 * f1 + f1;
        float par3 = f * 3.0f;
        float par4 = f1 * 0.2f;
        mStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(par3));
        mStack.translate(0.0f, 0.1f + par4, 0.0f);
        mStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SIN_45, 0.0f, SIN_45));
        this.coreCube.render(mStack, ivertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        float sca = 0.875f;
        mStack.scale(sca, sca, sca);
        mStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SIN_45, 0.0f, SIN_45));
        mStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(par3));
        this.coreCube.render(mStack, ivertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        mStack.scale(sca, sca, sca);
        mStack.multiply(new Quaternionf().setAngleAxis(1.0471976f, SIN_45, 0.0f, SIN_45));
        mStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(par3));
        this.innerCube.render(mStack, ivertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
        mStack.pop();
    }
}

