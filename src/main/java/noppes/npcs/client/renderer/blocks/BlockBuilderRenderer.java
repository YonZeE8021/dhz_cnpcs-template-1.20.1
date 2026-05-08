/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.WorldRenderer
 *  net.minecraft.util.math.RotationAxis
 */
package noppes.npcs.client.renderer.blocks;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.RotationAxis;
import noppes.npcs.CustomBlocks;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.client.renderer.blocks.BlockRendererInterface;
import noppes.npcs.schematics.Schematic;

public class BlockBuilderRenderer
extends BlockRendererInterface<TileBuilder> {
    private static final ItemStack item = new ItemStack((ItemConvertible)CustomBlocks.builder);
    public static Schematic schematic = null;
    public static BlockPos pos = null;

    public BlockBuilderRenderer(BlockEntityRendererFactory.Context dispatcher) {
        super(dispatcher);
    }

    public void render(TileBuilder tile, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider buffer, int light, int overlay) {
        matrixStack.push();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.disableBlend();
        if (tile.getPos().equals(TileBuilder.DrawPos)) {
            ClientEventHandler.onRenderTick(matrixStack, tile.getPos(), tile);
        }
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        matrixStack.pop();
    }

    public void drawSelectionBox(MatrixStack matrixStack, VertexConsumerProvider buffer, BlockPos pos) {
        Box bb = new Box(BlockPos.ORIGIN, pos);
        matrixStack.translate(0.001f, 0.001f, 0.001f);
        WorldRenderer.drawBox((MatrixStack)matrixStack, (VertexConsumer)buffer.getBuffer(RenderLayer.getLines()), (Box)bb, (float)1.0f, (float)0.0f, (float)0.0f, (float)1.0f);
    }
}

