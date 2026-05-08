/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ActionResult
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.BlockState
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.BooleanProperty
 *  net.minecraft.state.property.Property
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.block.AbstractBlock
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.entity.BlockEntityTicker
 */
package noppes.npcs.blocks;

import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.entity.BlockEntityTicker;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.blocks.BlockInterface;
import noppes.npcs.blocks.tiles.TileRedstoneBlock;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;

public class BlockNpcRedstone
extends BlockInterface {
    public static final BooleanProperty ACTIVE = BooleanProperty.of((String)"active");

    public BlockNpcRedstone() {
        super(AbstractBlock.Settings.copy((AbstractBlock)Blocks.field_10340).luminance(state -> 12).strength(50.0f, 2000.0f));
    }

    public ActionResult onUse(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult ray) {
        if (level.isClient) {
            return ActionResult.SUCCESS;
        }
        ItemStack currentItem = player.getInventory().getMainHandStack();
        if (currentItem != null && currentItem.getItem() == CustomItems.wand && CustomNpcsPermissions.hasPermission((ServerPlayerEntity)player, CustomNpcsPermissions.EDIT_BLOCKS)) {
            SPacketGuiOpen.sendOpenGui(player, EnumGuiType.RedstoneBlock, null, pos);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public void onBlockAdded(BlockState state, World par1Level, BlockPos pos, BlockState stateNew, boolean bo) {
        par1Level.updateNeighborsAlways(pos, (Block)this);
        par1Level.updateNeighborsAlways(pos.down(), (Block)this);
        par1Level.updateNeighborsAlways(pos.up(), (Block)this);
        par1Level.updateNeighborsAlways(pos.west(), (Block)this);
        par1Level.updateNeighborsAlways(pos.east(), (Block)this);
        par1Level.updateNeighborsAlways(pos.south(), (Block)this);
        par1Level.updateNeighborsAlways(pos.north(), (Block)this);
    }

    public void onPlaced(World level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack item) {
        if (!level.isClient && entity instanceof PlayerEntity) {
            SPacketGuiOpen.sendOpenGui((PlayerEntity)entity, EnumGuiType.RedstoneBlock, null, pos);
        }
    }

    public void onStateReplaced(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
        this.onBlockAdded(state, level, pos, state, isMoving);
    }

    public int getWeakRedstonePower(BlockState state, BlockView worldIn, BlockPos pos, Direction side) {
        return this.isActivated(state);
    }

    public int getStrongRedstonePower(BlockState state, BlockView level, BlockPos pos, Direction side) {
        return this.isActivated(state);
    }

    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{ACTIVE});
    }

    public int isActivated(BlockState state) {
        return (Boolean)state.get((Property)ACTIVE) != false ? 15 : 0;
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileRedstoneBlock(pos, state);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.field_11458;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return BlockNpcRedstone.checkType(type, CustomBlocks.tile_redstoneblock, TileRedstoneBlock::tick);
    }
}

