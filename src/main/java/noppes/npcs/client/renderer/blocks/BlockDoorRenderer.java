/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.model.BakedModel
 *  net.minecraft.item.ItemStack
 *  net.minecraft.block.Block
 *  net.minecraft.block.DoorBlock
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.enums.DoorHinge
 *  net.minecraft.block.enums.DoubleBlockHalf
 *  net.minecraft.state.property.Property
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.BlockRenderManager
 */
package noppes.npcs.client.renderer.blocks;

import java.util.Random;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.state.property.Property;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.BlockRenderManager;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileDoor;
import noppes.npcs.client.renderer.blocks.BlockRendererInterface;

public class BlockDoorRenderer
extends BlockRendererInterface<TileDoor> {
    private static Random random = new Random();

    public BlockDoorRenderer(BlockEntityRendererFactory.Context dispatcher) {
        super(dispatcher);
    }

    public void render(TileDoor tile, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider buffer, int light, int overlay) {
        BlockState original = tile.getWorld().getBlockState(tile.getPos());
        if (original.isAir()) {
            return;
        }
        BlockPos lowerPos = tile.getPos();
        if (original.get((Property)DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            lowerPos = tile.getPos().down();
        }
        BlockPos upperPos = lowerPos.up();
        TileDoor lowerTile = (TileDoor)tile.getWorld().getBlockEntity(lowerPos);
        TileDoor upperTile = (TileDoor)tile.getWorld().getBlockEntity(upperPos);
        if (lowerTile == null || upperTile == null) {
            return;
        }
        BlockState lowerState = lowerTile.getCachedState();
        BlockState upperState = upperTile.getCachedState();
        Block b = lowerTile.blockModel;
        if (this.overrideModel()) {
            b = CustomBlocks.scripted_door;
        }
        BlockState state = b.getDefaultState();
        state = (BlockState)state.with((Property)DoorBlock.HALF, (Comparable)((DoubleBlockHalf)original.get((Property)DoorBlock.HALF)));
        state = (BlockState)state.with((Property)DoorBlock.FACING, (Comparable)((Direction)lowerState.get((Property)DoorBlock.FACING)));
        state = (BlockState)state.with((Property)DoorBlock.OPEN, (Comparable)((Boolean)lowerState.get((Property)DoorBlock.OPEN)));
        state = (BlockState)state.with((Property)DoorBlock.HINGE, (Comparable)((DoorHinge)upperState.get((Property)DoorBlock.HINGE)));
        state = (BlockState)state.with((Property)DoorBlock.POWERED, (Comparable)((Boolean)upperState.get((Property)DoorBlock.POWERED)));
        matrixStack.push();
        this.renderBlock(matrixStack, buffer, tile, lowerState.getBlock(), state, light, overlay);
        matrixStack.pop();
    }

    private void renderBlock(MatrixStack matrixStack, VertexConsumerProvider buffer, TileDoor tile, Block b, BlockState state, int light, int overlay) {
        BlockRenderManager dispatcher = MinecraftClient.getInstance().getBlockRenderManager();
        BakedModel ibakedmodel = dispatcher.getModel(state);
        if (ibakedmodel == null) {
            dispatcher.renderBlockAsEntity(state, matrixStack, buffer, light, overlay);
        } else {
            dispatcher.getModelRenderer().render(matrixStack.peek(), buffer.getBuffer(RenderLayers.getEntityBlockLayer((BlockState)state, (boolean)false)), state, ibakedmodel, 1.0f, 1.0f, 1.0f, light, overlay);
        }
    }

    private boolean overrideModel() {
        ItemStack held = MinecraftClient.getInstance().player.getMainHandStack();
        if (held == null) {
            return false;
        }
        return held.getItem() == CustomItems.wand || held.getItem() == CustomItems.scripter || held.getItem() == CustomBlocks.scripted_door_item;
    }
}

