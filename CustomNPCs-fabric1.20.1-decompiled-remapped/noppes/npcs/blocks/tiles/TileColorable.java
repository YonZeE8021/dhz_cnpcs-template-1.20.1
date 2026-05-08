/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.block.BlockState
 */
package noppes.npcs.blocks.tiles;

import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.block.BlockState;
import noppes.npcs.blocks.tiles.TileNpcEntity;

public class TileColorable
extends TileNpcEntity {
    public int color = 14;
    public int rotation;

    public TileColorable(BlockEntityType<?> p_i48289_1_, BlockPos pos, BlockState state) {
        super(p_i48289_1_, pos, state);
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        this.color = compound.getInt("BannerColor");
        this.rotation = compound.getInt("BannerRotation");
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        compound.putInt("BannerColor", this.color);
        compound.putInt("BannerRotation", this.rotation);
        super.writeNbt(compound);
    }

    public boolean canUpdate() {
        return false;
    }

    public BlockEntityUpdateS2CPacket getUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound compound = new NbtCompound();
        this.writeNbt(compound);
        compound.remove("Items");
        compound.remove("ExtraData");
        return compound;
    }

    public int powerProvided() {
        return 0;
    }
}

