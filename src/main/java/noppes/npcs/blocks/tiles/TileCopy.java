/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.block.BlockState
 */
package noppes.npcs.blocks.tiles;

import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.block.BlockState;
import noppes.npcs.CustomBlocks;

public class TileCopy
extends BlockEntity {
    public short length = (short)10;
    public short width = (short)10;
    public short height = (short)10;
    public String name = "";

    public TileCopy(BlockPos pos, BlockState state) {
        super(CustomBlocks.tile_copy, pos, state);
    }

    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        this.length = compound.getShort("Length");
        this.width = compound.getShort("Width");
        this.height = compound.getShort("Height");
        this.name = compound.getString("Name");
    }

    public void writeNbt(NbtCompound compound) {
        compound.putShort("Length", this.length);
        compound.putShort("Width", this.width);
        compound.putShort("Height", this.height);
        compound.putString("Name", this.name);
        super.writeNbt(compound);
    }

    public BlockEntityUpdateS2CPacket getUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound compound = new NbtCompound();
        compound.putInt("x", this.pos.getX());
        compound.putInt("y", this.pos.getY());
        compound.putInt("z", this.pos.getZ());
        compound.putShort("Length", this.length);
        compound.putShort("Width", this.width);
        compound.putShort("Height", this.height);
        return compound;
    }
}

