/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 */
package noppes.npcs.client.renderer.blocks;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import noppes.npcs.blocks.BlockCarpentryBench;
import noppes.npcs.blocks.tiles.TileBlockAnvil;
import noppes.npcs.client.model.blocks.ModelCarpentryBench;

public class BlockCarpentryBenchRenderer
implements BlockEntityRenderer<TileBlockAnvil> {
    private final ModelCarpentryBench model = new ModelCarpentryBench();
    private static final Identifier TEXTURE = new Identifier("customnpcs", "textures/models/carpentrybench.png");
    private static final RenderLayer type = RenderLayer.getEntityCutout((Identifier)TEXTURE);

    public BlockCarpentryBenchRenderer(BlockEntityRendererFactory.Context dispatcher) {
    }

    public void render(TileBlockAnvil te, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider buffer, int light, int overlay) {
        int rotation = 0;
        if (te.getPos() != BlockPos.ORIGIN) {
            rotation = (Integer)te.getCachedState().get((Property)BlockCarpentryBench.ROTATION);
        }
        matrixStack.push();
        RenderSystem.disableBlend();
        matrixStack.translate(0.5f, 1.4f, 0.5f);
        matrixStack.scale(0.95f, 0.95f, 0.95f);
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(90 * rotation)));
        this.model.render(matrixStack, buffer.getBuffer(type), light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
    }
}

