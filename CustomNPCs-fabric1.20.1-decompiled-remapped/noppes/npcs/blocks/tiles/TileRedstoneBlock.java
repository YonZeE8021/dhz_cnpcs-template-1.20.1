/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.World
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.block.BlockState
 *  net.minecraft.state.property.Property
 */
package noppes.npcs.blocks.tiles;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.BlockNpcRedstone;
import noppes.npcs.blocks.tiles.TileNpcEntity;
import noppes.npcs.controllers.data.Availability;

public class TileRedstoneBlock
extends TileNpcEntity {
    public int onRange = 12;
    public int offRange = 20;
    public int onRangeX = 12;
    public int onRangeY = 12;
    public int onRangeZ = 12;
    public int offRangeX = 20;
    public int offRangeY = 20;
    public int offRangeZ = 20;
    public boolean isDetailed = false;
    public Availability availability = new Availability();
    public boolean isActivated = false;
    private int ticks = 10;

    public TileRedstoneBlock(BlockPos pos, BlockState state) {
        super(CustomBlocks.tile_redstoneblock, pos, state);
    }

    public static void tick(World level, BlockPos pos, BlockState state, TileRedstoneBlock tile) {
        if (tile.world.isClient) {
            return;
        }
        --tile.ticks;
        if (tile.ticks > 0) {
            return;
        }
        tile.ticks = tile.onRange > 10 ? 20 : 10;
        Block block = state.getBlock();
        if (block == null || !(block instanceof BlockNpcRedstone)) {
            return;
        }
        if (CustomNpcs.FreezeNPCs) {
            if (tile.isActivated) {
                tile.setActive(block, false);
            }
            return;
        }
        if (!tile.isActivated) {
            int z;
            int y;
            int x = tile.isDetailed ? tile.onRangeX : tile.onRange;
            List<PlayerEntity> list = tile.getPlayerList(x, y = tile.isDetailed ? tile.onRangeY : tile.onRange, z = tile.isDetailed ? tile.onRangeZ : tile.onRange);
            if (list.isEmpty()) {
                return;
            }
            for (PlayerEntity player : list) {
                if (!tile.availability.isAvailable(player)) continue;
                tile.setActive(block, true);
                return;
            }
        } else {
            int x = tile.isDetailed ? tile.offRangeX : tile.offRange;
            int y = tile.isDetailed ? tile.offRangeY : tile.offRange;
            int z = tile.isDetailed ? tile.offRangeZ : tile.offRange;
            List<PlayerEntity> list = tile.getPlayerList(x, y, z);
            for (PlayerEntity player : list) {
                if (!tile.availability.isAvailable(player)) continue;
                return;
            }
            tile.setActive(block, false);
        }
    }

    private void setActive(Block block, boolean bo) {
        this.isActivated = bo;
        BlockState state = (BlockState)block.getDefaultState().with((Property)BlockNpcRedstone.ACTIVE, (Comparable)Boolean.valueOf(this.isActivated));
        this.world.setBlockState(this.pos, state, 2);
        this.markDirty();
        this.world.updateListeners(this.pos, state, state, 3);
        block.onBlockAdded(state, this.world, this.pos, state, false);
    }

    private List<PlayerEntity> getPlayerList(int x, int y, int z) {
        return this.world.getNonSpectatingEntities(PlayerEntity.class, new Box((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)(this.pos.getY() + 1), (double)(this.pos.getZ() + 1)).expand((double)x, (double)y, (double)z));
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        this.onRange = compound.getInt("BlockOnRange");
        this.offRange = compound.getInt("BlockOffRange");
        this.isDetailed = compound.getBoolean("BlockIsDetailed");
        if (compound.contains("BlockOnRangeX")) {
            this.isDetailed = true;
            this.onRangeX = compound.getInt("BlockOnRangeX");
            this.onRangeY = compound.getInt("BlockOnRangeY");
            this.onRangeZ = compound.getInt("BlockOnRangeZ");
            this.offRangeX = compound.getInt("BlockOffRangeX");
            this.offRangeY = compound.getInt("BlockOffRangeY");
            this.offRangeZ = compound.getInt("BlockOffRangeZ");
        }
        if (compound.contains("BlockActivated")) {
            this.isActivated = compound.getBoolean("BlockActivated");
        }
        this.availability.load(compound);
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        compound.putInt("BlockOnRange", this.onRange);
        compound.putInt("BlockOffRange", this.offRange);
        compound.putBoolean("BlockActivated", this.isActivated);
        compound.putBoolean("BlockIsDetailed", this.isDetailed);
        if (this.isDetailed) {
            compound.putInt("BlockOnRangeX", this.onRangeX);
            compound.putInt("BlockOnRangeY", this.onRangeY);
            compound.putInt("BlockOnRangeZ", this.onRangeZ);
            compound.putInt("BlockOffRangeX", this.offRangeX);
            compound.putInt("BlockOffRangeY", this.offRangeY);
            compound.putInt("BlockOffRangeZ", this.offRangeZ);
        }
        this.availability.save(compound);
        super.writeNbt(compound);
    }
}

