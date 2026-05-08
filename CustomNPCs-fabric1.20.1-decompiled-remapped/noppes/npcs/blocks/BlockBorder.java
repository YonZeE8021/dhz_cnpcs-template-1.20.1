/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ActionResult
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.nbt.NbtCompound
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockRenderType;
import net.minecraft.nbt.NbtCompound;
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
import noppes.npcs.blocks.tiles.TileBorder;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;

public class BlockBorder
extends BlockInterface {
    public static final IntProperty ROTATION = IntProperty.of((String)"rotation", (int)0, (int)3);

    public BlockBorder() {
        super(AbstractBlock.Settings.copy((AbstractBlock)Blocks.field_10499).sounds(BlockSoundGroup.STONE));
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{ROTATION});
    }

    public ActionResult onUse(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult ray) {
        ItemStack currentItem = player.getInventory().getMainHandStack();
        if (!level.isClient && currentItem.getItem() == CustomItems.wand) {
            SPacketGuiOpen.sendOpenGui(player, EnumGuiType.Border, null, pos);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public BlockState getPlacementState(ItemPlacementContext context) {
        if (context.getPlayer() != null) {
            int l = MathHelper.floor((double)((double)(context.getPlayer().getYaw() * 4.0f / 360.0f) + 0.5)) & 3;
            return (BlockState)this.getDefaultState().with((Property)ROTATION, (Comparable)Integer.valueOf(l %= 4));
        }
        return super.getPlacementState(context);
    }

    public void onPlaced(World level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack item) {
        TileBorder tile = (TileBorder)level.getBlockEntity(pos);
        TileBorder adjacent = this.getTile(level, pos.west());
        if (adjacent == null) {
            adjacent = this.getTile(level, pos.south());
        }
        if (adjacent == null) {
            adjacent = this.getTile(level, pos.north());
        }
        if (adjacent == null) {
            adjacent = this.getTile(level, pos.east());
        }
        if (adjacent != null) {
            NbtCompound compound = new NbtCompound();
            adjacent.writeExtraNBT(compound);
            tile.readExtraNBT(compound);
        }
        tile.rotation = (Integer)state.get((Property)ROTATION);
        if (!level.isClient && entity instanceof PlayerEntity) {
            SPacketGuiOpen.sendOpenGui((PlayerEntity)entity, EnumGuiType.Border, null, pos);
        }
    }

    private TileBorder getTile(World level, BlockPos pos) {
        BlockEntity tile = level.getBlockEntity(pos);
        if (tile != null && tile instanceof TileBorder) {
            return (TileBorder)tile;
        }
        return null;
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.field_11458;
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileBorder(pos, state);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return BlockBorder.checkType(type, CustomBlocks.tile_border, TileBorder::tick);
    }
}

