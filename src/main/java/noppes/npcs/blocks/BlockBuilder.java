/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ActionResult
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.sound.BlockSoundGroup
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.BlockState
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.block.AbstractBlock
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.entity.BlockEntityTicker
 */
package noppes.npcs.blocks;

import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockRenderType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.entity.BlockEntityTicker;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.BlockInterface;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;

public class BlockBuilder
extends BlockInterface {
    public static final IntProperty ROTATION = IntProperty.of((String)"rotation", (int)0, (int)3);

    public BlockBuilder() {
        super(AbstractBlock.Settings.copy((AbstractBlock)Blocks.BARRIER).sounds(BlockSoundGroup.STONE));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{ROTATION});
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public ActionResult onUse(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult ray) {
        if (level.isClient) {
            return ActionResult.SUCCESS;
        }
        ItemStack currentItem = player.getInventory().getMainHandStack();
        if (currentItem.getItem() == CustomItems.wand || currentItem.getItem() == CustomBlocks.builder_item) {
            SPacketGuiOpen.sendOpenGui(player, EnumGuiType.BuilderBlock, null, pos);
        }
        return ActionResult.SUCCESS;
    }

    public BlockState getPlacementState(ItemPlacementContext context) {
        int var6 = MathHelper.floor((double)((double)(context.getPlayer().getYaw() / 90.0f) + 0.5)) & 3;
        if (!context.getWorld().isClient) {
            SPacketGuiOpen.sendOpenGui(context.getPlayer(), EnumGuiType.BuilderBlock, null, context.getBlockPos());
        }
        return (BlockState)this.getDefaultState().with((Property)ROTATION, (Comparable)Integer.valueOf(var6));
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileBuilder(pos, state);
    }

    public void onStateReplaced(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (TileBuilder.DrawPos != null && TileBuilder.DrawPos.equals(pos)) {
            TileBuilder.SetDrawPos(null);
        }
        super.onStateReplaced(state, level, pos, newState, isMoving);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return BlockBuilder.checkType(type, CustomBlocks.tile_builder, TileBuilder::tick);
    }
}

