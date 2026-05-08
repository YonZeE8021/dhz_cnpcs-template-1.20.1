/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockEntityProvider
 *  net.minecraft.text.Text
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.font.TextRenderer$TextLayerType
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.model.json.ModelTransformationMode
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 */
package noppes.npcs.client.renderer.blocks;

import java.util.Objects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.text.Text;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.text.StringVisitable;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.renderer.blocks.BlockRendererInterface;

public class BlockScriptedRenderer
extends BlockRendererInterface<TileScripted> {
    private static Random random = Random.create();

    public BlockScriptedRenderer(BlockEntityRendererFactory.Context dispatcher) {
        super(dispatcher);
    }

    public void render(TileScripted tile, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider buffer, int light, int overlay) {
        matrixStack.push();
        if (this.overrideModel()) {
            matrixStack.translate(0.5f, 0.5f, 0.5f);
            matrixStack.scale(2.0f, 2.0f, 2.0f);
            this.renderItem(new ItemStack((ItemConvertible)CustomBlocks.scripted), matrixStack, buffer, light, overlay);
        } else {
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)tile.rotationY));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float)tile.rotationX));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)tile.rotationZ));
            matrixStack.scale(tile.scaleX, tile.scaleY, tile.scaleZ);
            Block b = tile.blockModel;
            if (b == null || b == Blocks.AIR || b == CustomBlocks.scripted) {
                matrixStack.translate(0.5f, 0.5f, 0.5f);
                matrixStack.scale(2.0f, 2.0f, 2.0f);
                this.renderItem(tile.itemModel, matrixStack, buffer, light, overlay);
            } else {
                BlockState state = b.getDefaultState();
                this.renderBlock(tile, b, state, matrixStack, buffer, light, overlay);
                if (state.hasBlockEntity() && !tile.renderTileErrored) {
                    try {
                        BlockEntityRenderer renderer;
                        if (tile.renderTile == null) {
                            BlockEntity entity = ((BlockEntityProvider)b).createBlockEntity(tile.getPos(), state);
                            entity.setWorld(tile.getWorld());
                            tile.renderTile = entity;
                            tile.renderState = state;
                            tile.renderTileUpdate = ((BlockEntityProvider)b).getTicker(tile.getWorld(), state, entity.getType());
                        }
                        if ((renderer = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(tile.renderTile)) != null) {
                            renderer.render(tile.renderTile, partialTicks, matrixStack, buffer, light, overlay);
                        } else {
                            tile.renderTileErrored = true;
                        }
                    }
                    catch (Exception e) {
                        tile.renderTileErrored = true;
                    }
                }
            }
        }
        matrixStack.pop();
        if (!tile.text1.text.isEmpty()) {
            this.drawText(matrixStack, tile.text1, buffer, light, overlay);
        }
        if (!tile.text2.text.isEmpty()) {
            this.drawText(matrixStack, tile.text2, buffer, light, overlay);
        }
        if (!tile.text3.text.isEmpty()) {
            this.drawText(matrixStack, tile.text3, buffer, light, overlay);
        }
        if (!tile.text4.text.isEmpty()) {
            this.drawText(matrixStack, tile.text4, buffer, light, overlay);
        }
        if (!tile.text5.text.isEmpty()) {
            this.drawText(matrixStack, tile.text5, buffer, light, overlay);
        }
        if (!tile.text6.text.isEmpty()) {
            this.drawText(matrixStack, tile.text6, buffer, light, overlay);
        }
    }

    private void drawText(MatrixStack matrixStack, TileScripted.TextPlane text1, VertexConsumerProvider buffer, int light, int overlay) {
        if (text1.textBlock == null || text1.textHasChanged) {
            text1.textBlock = new TextBlockClient(text1.text, 336, true, MinecraftClient.getInstance().player);
            text1.textHasChanged = false;
        }
        matrixStack.push();
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)text1.rotationY));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float)text1.rotationX));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)text1.rotationZ));
        matrixStack.scale(text1.scale, text1.scale, 1.0f);
        matrixStack.translate(text1.offsetX, text1.offsetY, text1.offsetZ);
        float f1 = 0.6666667f;
        float f3 = 0.0133f * f1;
        matrixStack.translate(0.0f, 0.5f, 0.01f);
        matrixStack.scale(f3, -f3, f3);
        TextRenderer fontrenderer = MinecraftClient.getInstance().textRenderer;
        float lineOffset = 0.0f;
        if (text1.textBlock.lines.size() < 14) {
            lineOffset = (14.0f - (float)text1.textBlock.lines.size()) / 2.0f;
        }
        for (int i = 0; i < text1.textBlock.lines.size(); ++i) {
            Text text = text1.textBlock.lines.get(i);
            float f = -fontrenderer.getWidth((StringVisitable)text) / 2;
            double d = lineOffset + (float)i;
            Objects.requireNonNull(fontrenderer);
            fontrenderer.draw(text, f, (float)((int)(d * (9.0 - 0.3))), 0, false, matrixStack.peek().getPositionMatrix(), buffer, TextRenderer.TextLayerType.NORMAL, light, overlay);
        }
        matrixStack.pop();
    }

    private void renderItem(ItemStack item, MatrixStack matrixStack, VertexConsumerProvider buffer, int light, int overlay) {
        MinecraftClient.getInstance().getItemRenderer().renderItem(item, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrixStack, buffer, null, 0);
    }

    private void renderBlock(TileScripted tile, Block b, BlockState state, MatrixStack matrixStack, VertexConsumerProvider buffer, int light, int overlay) {
        matrixStack.push();
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(state, matrixStack, buffer, light, OverlayTexture.DEFAULT_UV);
        if (random.nextInt(12) == 1) {
            state.getBlock().randomDisplayTick(state, tile.getWorld(), tile.getPos(), random);
        }
        matrixStack.pop();
    }

    private boolean overrideModel() {
        ItemStack held = MinecraftClient.getInstance().player.getMainHandStack();
        if (held == null) {
            return false;
        }
        return held.getItem() == CustomItems.wand || held.getItem() == CustomItems.scripter;
    }
}

