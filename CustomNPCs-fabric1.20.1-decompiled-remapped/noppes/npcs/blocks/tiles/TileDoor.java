/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 *  net.minecraft.block.Block
 *  net.minecraft.block.DoorBlock
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.block.BlockState
 *  net.minecraft.util.Identifier
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.blocks.tiles;

import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import noppes.npcs.CustomBlocks;
import noppes.npcs.blocks.tiles.TileNpcEntity;

public class TileDoor
extends TileNpcEntity {
    public int tickCount = 0;
    public Block blockModel = CustomBlocks.scripted_door;
    public boolean needsClientUpdate = false;

    public TileDoor(BlockEntityType<?> p_i48289_1_, BlockPos pos, BlockState state) {
        super(p_i48289_1_, pos, state);
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        this.setDoorNBT(compound);
    }

    public void setDoorNBT(NbtCompound compound) {
        this.blockModel = (Block)Registries.BLOCK.get(new Identifier(compound.getString("ScriptDoorBlockModel")));
        if (this.blockModel == null || !(this.blockModel instanceof DoorBlock)) {
            this.blockModel = CustomBlocks.scripted_door;
        }
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        this.getDoorNBT(compound);
        super.writeNbt(compound);
    }

    public NbtCompound getDoorNBT(NbtCompound compound) {
        compound.putString("ScriptDoorBlockModel", String.valueOf(Registries.BLOCK.getId((Object)this.blockModel)));
        return compound;
    }

    public void setItemModel(Block block) {
        if (block == null || !(block instanceof DoorBlock)) {
            block = CustomBlocks.scripted_door;
        }
        if (this.blockModel == block) {
            return;
        }
        this.blockModel = block;
        this.needsClientUpdate = true;
    }

    public static void tick(World level, BlockPos pos, BlockState state, TileDoor tile) {
        ++tile.tickCount;
        if (tile.tickCount >= 10) {
            tile.tickCount = 0;
            if (tile.needsClientUpdate) {
                tile.markDirty();
                level.setBlockState(pos, state);
                tile.needsClientUpdate = false;
            }
        }
    }

    public BlockEntityUpdateS2CPacket getUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound compound = new NbtCompound();
        compound.putInt("x", this.pos.getX());
        compound.putInt("y", this.pos.getY());
        compound.putInt("z", this.pos.getZ());
        this.getDoorNBT(compound);
        return compound;
    }
}

