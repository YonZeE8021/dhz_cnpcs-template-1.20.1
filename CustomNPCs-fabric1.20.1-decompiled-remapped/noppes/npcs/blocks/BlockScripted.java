/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ActionResult
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.explosion.Explosion
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome$Precipitation
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.sound.BlockSoundGroup
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.ShapeContext
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.block.AbstractBlock
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.entity.BlockEntityTicker
 *  net.minecraft.loot.context.LootContextParameterSet$Builder
 *  org.jetbrains.annotations.Nullable
 */
package noppes.npcs.blocks;

import java.util.Collections;
import java.util.List;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.loot.context.LootContextParameterSet;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.EventHooks;
import noppes.npcs.blocks.BlockInterface;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.server.SPacketGuiOpen;
import org.jetbrains.annotations.Nullable;

public class BlockScripted
extends BlockInterface {
    public static final VoxelShape AABB = VoxelShapes.cuboid((Box)new Box((double)0.001f, (double)0.001f, (double)0.001f, (double)0.998f, (double)0.998f, (double)0.998f));

    public BlockScripted() {
        super(AbstractBlock.Settings.copy((AbstractBlock)Blocks.field_10340).sounds(BlockSoundGroup.STONE).strength(5.0f, 10.0f));
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileScripted(pos, state);
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView level, BlockPos pos, ShapeContext context) {
        return AABB;
    }

    public VoxelShape getCollisionShape(BlockState blockState, BlockView level, BlockPos pos, ShapeContext context) {
        TileScripted tile = (TileScripted)level.getBlockEntity(pos);
        if (tile != null && tile.isPassible) {
            return VoxelShapes.empty();
        }
        return AABB;
    }

    public ActionResult onUse(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult ray) {
        if (level.isClient) {
            return ActionResult.SUCCESS;
        }
        ItemStack currentItem = player.getInventory().getMainHandStack();
        if (currentItem != null && (currentItem.getItem() == CustomItems.wand || currentItem.getItem() == CustomItems.scripter)) {
            PlayerData data = PlayerData.get(player);
            data.scriptBlockPos = pos;
            SPacketGuiOpen.sendOpenGui(player, EnumGuiType.ScriptBlock, null, pos);
            return ActionResult.SUCCESS;
        }
        Vec3d vec = ray.getPos();
        float x = (float)(vec.x - (double)pos.getX());
        float y = (float)(vec.y - (double)pos.getY());
        float z = (float)(vec.z - (double)pos.getZ());
        TileScripted tile = (TileScripted)level.getBlockEntity(pos);
        return EventHooks.onScriptBlockInteract(tile, player, ray.getSide().getId(), x, y, z) ? ActionResult.FAIL : ActionResult.SUCCESS;
    }

    public void onPlaced(World level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack item) {
        if (!level.isClient && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            PlayerData data = PlayerData.get(player);
            data.scriptBlockPos = pos;
            SPacketGuiOpen.sendOpenGui(player, EnumGuiType.ScriptBlock, null, pos);
        }
    }

    public void onEntityCollision(BlockState state, World level, BlockPos pos, Entity entityIn) {
        if (level.isClient) {
            return;
        }
        TileScripted tile = (TileScripted)level.getBlockEntity(pos);
        EventHooks.onScriptBlockCollide(tile, entityIn);
    }

    public void precipitationTick(BlockState state, World level, BlockPos pos, Biome.Precipitation type) {
        if (level.isClient || type != Biome.Precipitation.field_9382) {
            return;
        }
        TileScripted tile = (TileScripted)level.getBlockEntity(pos);
        EventHooks.onScriptBlockRainFill(tile);
    }

    public void onLandedUpon(World level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (level.isClient) {
            return;
        }
        TileScripted tile = (TileScripted)level.getBlockEntity(pos);
        fallDistance = EventHooks.onScriptBlockFallenUpon(tile, entity, fallDistance);
        super.onLandedUpon(level, state, pos, entity, fallDistance);
    }

    public void onBlockBreakStart(BlockState state, World level, BlockPos pos, PlayerEntity player) {
        if (level.isClient) {
            return;
        }
        TileScripted tile = (TileScripted)level.getBlockEntity(pos);
        EventHooks.onScriptBlockClicked(tile, player);
    }

    public void onStateReplaced(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!level.isClient) {
            TileScripted tile = (TileScripted)level.getBlockEntity(pos);
            EventHooks.onScriptBlockBreak(tile);
        }
        super.onStateReplaced(state, level, pos, newState, isMoving);
    }

    public void onBreak(World level, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(level, pos, state, player);
    }

    public void afterBreak(World level, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        TileScripted tile;
        if (!level.isClient && !EventHooks.onScriptBlockHarvest(tile = (TileScripted)level.getBlockEntity(pos), player)) {
            super.afterBreak(level, player, pos, state, blockEntity, tool);
        }
    }

    public List<ItemStack> getDroppedStacks(BlockState p_287732_, LootContextParameterSet.Builder p_287596_) {
        return Collections.emptyList();
    }

    public void onDestroyedByExplosion(World level, BlockPos pos, Explosion explosion) {
        TileScripted tile;
        if (!level.isClient && EventHooks.onScriptBlockExploded(tile = (TileScripted)level.getBlockEntity(pos))) {
            return;
        }
        super.onDestroyedByExplosion(level, pos, explosion);
    }

    public void neighborUpdate(BlockState state, World level, BlockPos pos, Block neighborBlock, BlockPos pos2, boolean isMoving) {
        if (level.isClient) {
            return;
        }
        TileScripted tile = (TileScripted)level.getBlockEntity(pos);
        EventHooks.onScriptBlockNeighborChanged(tile, pos2);
        int power = 0;
        for (Direction enumfacing : Direction.values()) {
            int p = level.getEmittedRedstonePower(pos.offset(enumfacing), enumfacing);
            if (p <= power) continue;
            power = p;
        }
        if (tile.prevPower != power && tile.powering <= 0) {
            tile.newPower = power;
        }
    }

    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    public int getWeakRedstonePower(BlockState state, BlockView worldIn, BlockPos pos, Direction side) {
        return this.getStrongRedstonePower(state, worldIn, pos, side);
    }

    public int getStrongRedstonePower(BlockState state, BlockView level, BlockPos pos, Direction side) {
        TileScripted tile = (TileScripted)level.getBlockEntity(pos);
        if (tile != null) {
            return tile.activePowering;
        }
        return 0;
    }

    public boolean isTransparent(BlockState state, BlockView level, BlockPos pos) {
        return super.isTransparent(state, level, pos);
    }

    public int getOpacity(BlockState state, BlockView level, BlockPos pos) {
        TileScripted tile = (TileScripted)level.getBlockEntity(pos);
        if (tile == null) {
            return 0;
        }
        return tile.lightValue;
    }

    public boolean canPathfindThrough(BlockState state, BlockView level, BlockPos pos, NavigationType type) {
        TileScripted tile = (TileScripted)level.getBlockEntity(pos);
        if (tile != null) {
            return tile.isPassible;
        }
        return false;
    }

    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView level, BlockPos pos) {
        TileScripted tile = (TileScripted)level.getBlockEntity(pos);
        float f = -1.0f;
        if (tile != null) {
            f = tile.blockHardness;
        }
        if (f == -1.0f) {
            return 0.0f;
        }
        int i = player.canHarvest(state) ? 30 : 100;
        return player.getBlockBreakingSpeed(state) / f / (float)i;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return BlockScripted.checkType(type, CustomBlocks.tile_scripted, TileScripted::tick);
    }
}

