/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.util.math.RotationAxis
 */
package noppes.npcs.client.renderer;

import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.util.math.RotationAxis;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.shared.client.model.Model2DRenderer;

public class MarkRenderer {
    public static final Identifier markExclamation = new Identifier("customnpcs", "textures/marks/exclamation.png");
    public static final Identifier markQuestion = new Identifier("customnpcs", "textures/marks/question.png");
    public static final Identifier markPointer = new Identifier("customnpcs", "textures/marks/pointer.png");
    public static final Identifier markCross = new Identifier("customnpcs", "textures/marks/cross.png");
    public static final Identifier markSkull = new Identifier("customnpcs", "textures/marks/skull.png");
    public static final Identifier markStar = new Identifier("customnpcs", "textures/marks/star.png");
    public static int displayList = -1;
    public static Model2DRenderer renderer = new Model2DRenderer(32, 32, 0, 0, 32, 32, markExclamation);

    public static void render(LivingEntity entity, MatrixStack poseStack, VertexConsumerProvider buffer, int packedLight, MarkData.Mark mark) {
        MatrixStack matrixStack = poseStack;
        matrixStack.push();
        int color = mark.color;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        Identifier location = markExclamation;
        if (mark.type == 1) {
            location = markQuestion;
        } else if (mark.type == 3) {
            location = markPointer;
        } else if (mark.type == 5) {
            location = markCross;
        } else if (mark.type == 4) {
            location = markSkull;
        } else if (mark.type == 6) {
            location = markStar;
        }
        matrixStack.translate(0.0, (double)entity.getHeight() + 0.6, 0.0);
        matrixStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(180.0f));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.headYaw));
        matrixStack.translate(-0.5f, 0.0f, 0.0f);
        renderer.render(location, matrixStack, buffer.getBuffer(RenderLayer.getEntityCutout((Identifier)location)), packedLight, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0f);
        matrixStack.pop();
    }
}

