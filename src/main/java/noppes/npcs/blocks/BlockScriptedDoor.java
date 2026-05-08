/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ActionResult
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.BlockView
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.world.World
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.block.DoorBlock
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.enums.DoubleBlockHalf
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.block.AbstractBlock
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.entity.BlockEntityTicker
 *  org.jetbrains.annotations.Nullable
 */
package noppes.npcs.blocks;

import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BlockView;
import net.minecraft.item.ItemConvertible;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.state.property.Property;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.entity.BlockEntityTicker;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.EventHooks;
import noppes.npcs.blocks.BlockNpcDoorInterface;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.server.SPacketGuiOpen;
import org.jetbrains.annotations.Nullable;

public class BlockScriptedDoor
extends BlockNpcDoorInterface {
    public BlockScriptedDoor() {
        super(AbstractBlock.Settings.copy((AbstractBlock)Blocks.IRON_DOOR).strength(5.0f, 10.0f));
    }

    public ItemStack getPickStack(BlockView worldIn, BlockPos pos, BlockState state) {
        return new ItemStack((ItemConvertible)CustomBlocks.scripted_door_item);
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileScriptedDoor(pos, state);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    public ActionResult onUse(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult ray) {
        BlockState iblockstate1;
        if (level.isClient) {
            return ActionResult.SUCCESS;
        }
        BlockPos blockpos1 = state.get((Property)HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();
        BlockState class_26802 = iblockstate1 = pos.equals(blockpos1) ? state : level.getBlockState(blockpos1);
        if (iblockstate1.getBlock() != this) {
            return ActionResult.FAIL;
        }
        ItemStack currentItem = player.getInventory().getMainHandStack();
        if (currentItem != null && (currentItem.getItem() == CustomItems.wand || currentItem.getItem() == CustomItems.scripter || currentItem.getItem() == CustomBlocks.scripted_door_item)) {
            PlayerData data = PlayerData.get(player);
            data.scriptBlockPos = blockpos1;
            SPacketGuiOpen.sendOpenGui(player, EnumGuiType.ScriptDoor, null, blockpos1);
            return ActionResult.SUCCESS;
        }
        TileScriptedDoor tile = (TileScriptedDoor)level.getBlockEntity(blockpos1);
        Vec3d vec = ray.getPos();
        float x = (float)(vec.x - (double)pos.getX());
        float y = (float)(vec.y - (double)pos.getY());
        float z = (float)(vec.z - (double)pos.getZ());
        if (EventHooks.onScriptBlockInteract(tile, player, ray.getSide().getId(), x, y, z)) {
            return ActionResult.FAIL;
        }
        this.setOpen((Entity)player, level, iblockstate1, blockpos1, ((Boolean)iblockstate1.get((Property)DoorBlock.OPEN)).equals(false));
        return ActionResult.SUCCESS;
    }

    public void neighborUpdate(BlockState state, World worldIn, BlockPos pos, Block neighborBlock, BlockPos pos2, boolean isMoving) {
        if (state.get((Property)HALF) == DoubleBlockHalf.UPPER) {
            BlockPos blockpos1 = pos.down();
            BlockState iblockstate1 = worldIn.getBlockState(blockpos1);
            if (iblockstate1.getBlock() != this) {
                worldIn.removeBlock(pos, false);
            } else if (neighborBlock != this) {
                this.neighborUpdate(iblockstate1, worldIn, blockpos1, neighborBlock, blockpos1, isMoving);
            }
        } else {
            BlockPos blockpos2 = pos.up();
            BlockState iblockstate2 = worldIn.getBlockState(blockpos2);
            if (iblockstate2.getBlock() != this) {
                worldIn.removeBlock(pos, false);
            } else {
                boolean flag;
                TileScriptedDoor tile = (TileScriptedDoor)worldIn.getBlockEntity(pos);
                if (!worldIn.isClient) {
                    EventHooks.onScriptBlockNeighborChanged(tile, pos2);
                }
                boolean bl = flag = worldIn.isReceivingRedstonePower(pos) || worldIn.isReceivingRedstonePower(blockpos2);
                if ((flag || neighborBlock.getDefaultState().emitsRedstonePower()) && neighborBlock != this && flag != (Boolean)iblockstate2.get((Property)POWERED)) {
                    worldIn.setBlockState(blockpos2, (BlockState)iblockstate2.with((Property)POWERED, (Comparable)Boolean.valueOf(flag)), 2);
                    if (flag != (Boolean)state.get((Property)OPEN)) {
                        this.setOpen(null, worldIn, state, pos, flag);
                    }
                }
                int power = 0;
                for (Direction enumfacing : Direction.values()) {
                    int p = worldIn.getEmittedRedstonePower(pos.offset(enumfacing), enumfacing);
                    if (p <= power) continue;
                    power = p;
                }
                tile.newPower = power;
            }
        }
    }

    public void setOpen(Entity entity, World worldIn, BlockState state, BlockPos pos, boolean open) {
        TileScriptedDoor tile = (TileScriptedDoor)worldIn.getBlockEntity(pos);
        if (EventHooks.onScriptBlockDoorToggle(tile)) {
            return;
        }
        super.setOpen(entity, worldIn, state, pos, open);
    }

    public void onBlockBreakStart(BlockState state, World level, BlockPos pos, PlayerEntity playerIn) {
        BlockState iblockstate1;
        if (level.isClient) {
            return;
        }
        BlockPos blockpos1 = state.get((Property)HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();
        BlockState class_26802 = iblockstate1 = pos.equals(blockpos1) ? state : level.getBlockState(blockpos1);
        if (iblockstate1.getBlock() != this) {
            return;
        }
        TileScriptedDoor tile = (TileScriptedDoor)level.getBlockEntity(blockpos1);
        EventHooks.onScriptBlockClicked(tile, playerIn);
    }

    @Override
    public void onStateReplaced(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
        BlockState iblockstate1;
        if (state.getBlock() == newState.getBlock()) {
            return;
        }
        BlockPos blockpos1 = state.get((Property)HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();
        BlockState class_26802 = iblockstate1 = pos.equals(blockpos1) ? state : level.getBlockState(blockpos1);
        if (!level.isClient && iblockstate1.getBlock() == this) {
            TileScriptedDoor tile = (TileScriptedDoor)level.getBlockEntity(pos);
            EventHooks.onScriptBlockBreak(tile);
        }
        super.onStateReplaced(state, level, pos, newState, isMoving);
    }

    @Override
    public void afterBreak(World level, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        TileScriptedDoor tile;
        if (!level.isClient && !EventHooks.onScriptBlockHarvest(tile = (TileScriptedDoor)level.getBlockEntity(pos), player)) {
            super.afterBreak(level, player, pos, state, blockEntity, tool);
        }
    }

    public void onEntityCollision(BlockState state, World level, BlockPos pos, Entity entityIn) {
        if (level.isClient) {
            return;
        }
        TileScriptedDoor tile = (TileScriptedDoor)level.getBlockEntity(pos);
        EventHooks.onScriptBlockCollide(tile, entityIn);
    }

    public void onBreak(World level, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockState iblockstate1;
        BlockPos blockpos1 = state.get((Property)HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();
        BlockState class_26802 = iblockstate1 = pos.equals(blockpos1) ? state : level.getBlockState(blockpos1);
        if (player.getAbilities().creativeMode && iblockstate1.get((Property)HALF) == DoubleBlockHalf.LOWER && iblockstate1.getBlock() == this) {
            level.removeBlock(blockpos1, false);
        }
    }

    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView level, BlockPos pos) {
        float f = ((TileScriptedDoor)level.getBlockEntity((BlockPos)pos)).blockHardness;
        if (f == -1.0f) {
            return 0.0f;
        }
        int i = player.canHarvest(state) ? 30 : 100;
        return player.getBlockBreakingSpeed(state) / f / (float)i;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return BlockScriptedDoor.createTickerHelper(type, CustomBlocks.tile_scripteddoor, TileScriptedDoor::tick);
    }

    @SuppressWarnings("unchecked")
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> p_152133_, BlockEntityType<E> p_152134_, BlockEntityTicker<? super E> p_152135_) {
        return p_152134_ == p_152133_ ? (BlockEntityTicker<A>)p_152135_ : null;
    }
}

