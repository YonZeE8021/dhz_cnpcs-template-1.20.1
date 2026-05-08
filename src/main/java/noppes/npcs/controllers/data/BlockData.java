/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.block.BlockState
 *  net.minecraft.util.Identifier
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.controllers.data;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

public class BlockData {
    public BlockPos pos;
    public BlockState state;
    public NbtCompound tile;
    private ItemStack stack;

    public BlockData(BlockPos pos, BlockState state, NbtCompound tile) {
        this.pos = pos;
        this.state = state;
        this.tile = tile;
    }

    public NbtCompound getNBT() {
        NbtCompound compound = new NbtCompound();
        compound.putInt("BuildX", this.pos.getX());
        compound.putInt("BuildY", this.pos.getY());
        compound.putInt("BuildZ", this.pos.getZ());
        compound.putString("Block", Registries.BLOCK.getId(this.state.getBlock()).toString());
        if (this.tile != null) {
            compound.put("Tile", (NbtElement)this.tile);
        }
        return compound;
    }

    public static BlockData getData(NbtCompound compound) {
        BlockPos pos = new BlockPos(compound.getInt("BuildX"), compound.getInt("BuildY"), compound.getInt("BuildZ"));
        Block b = (Block)Registries.BLOCK.get(new Identifier(compound.getString("Block")));
        if (b == null) {
            return null;
        }
        NbtCompound tile = null;
        if (compound.contains("Tile")) {
            tile = compound.getCompound("Tile");
        }
        return new BlockData(pos, b.getDefaultState(), tile);
    }

    public ItemStack getStack() {
        if (this.stack == null) {
            this.stack = new ItemStack((ItemConvertible)this.state.getBlock(), 1);
        }
        return this.stack;
    }
}

