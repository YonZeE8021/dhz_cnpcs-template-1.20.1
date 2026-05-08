/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.block.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.BlockState
 *  net.minecraft.world.EmptyBlockView
 */
package noppes.npcs.schematics;

import java.util.List;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.world.EmptyBlockView;
import noppes.npcs.schematics.BlueprintUtil;
import noppes.npcs.schematics.ISchematic;

public class Blueprint
implements ISchematic {
    private List<String> requiredMods;
    private short sizeX;
    private short sizeY;
    private short sizeZ;
    private short palleteSize;
    private BlockState[] pallete;
    private String name;
    private String[] architects;
    private short[][][] structure;
    private NbtCompound[] tileEntities;

    public Blueprint(short sizeX, short sizeY, short sizeZ, short palleteSize, BlockState[] pallete, short[][][] structure, NbtCompound[] tileEntities, List<String> requiredMods) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.palleteSize = palleteSize;
        this.pallete = pallete;
        this.structure = structure;
        this.tileEntities = tileEntities;
        this.requiredMods = requiredMods;
    }

    public void build(World level, BlockPos pos) {
        BlockState state;
        short x;
        short z;
        short y;
        BlockState[] pallete = this.getPallete();
        short[][][] structure = this.getStructure();
        for (y = 0; y < this.getSizeY(); y = (short)(y + 1)) {
            for (z = 0; z < this.getSizeZ(); z = (short)(z + 1)) {
                for (x = 0; x < this.getSizeX(); x = (short)(x + 1)) {
                    state = pallete[structure[y][z][x] & 0xFFFF];
                    if (state.getBlock() == Blocks.STRUCTURE_VOID || !state.isFullCube((BlockView)EmptyBlockView.INSTANCE, BlockPos.ORIGIN)) continue;
                    level.setBlockState(pos.add((int)x, (int)y, (int)z), state, 2);
                }
            }
        }
        for (y = 0; y < this.getSizeY(); y = (short)(y + 1)) {
            for (z = 0; z < this.getSizeZ(); z = (short)(z + 1)) {
                for (x = 0; x < this.getSizeX(); x = (short)(x + 1)) {
                    state = pallete[structure[y][z][x]];
                    if (state.getBlock() == Blocks.STRUCTURE_VOID || state.isFullCube((BlockView)EmptyBlockView.INSTANCE, BlockPos.ORIGIN)) continue;
                    level.setBlockState(pos.add((int)x, (int)y, (int)z), state, 2);
                }
            }
        }
        if (this.getTileEntities() != null) {
            for (NbtCompound tag : this.getTileEntities()) {
                BlockEntity te = level.getBlockEntity(pos.add((int)tag.getShort("x"), (int)tag.getShort("y"), (int)tag.getShort("z")));
                tag.putInt("x", pos.getX() + tag.getShort("x"));
                tag.putInt("y", pos.getY() + tag.getShort("y"));
                tag.putInt("z", pos.getZ() + tag.getShort("z"));
                te.readNbt(tag);
            }
        }
    }

    public short getSizeX() {
        return this.sizeX;
    }

    public short getSizeY() {
        return this.sizeY;
    }

    public short getSizeZ() {
        return this.sizeZ;
    }

    public short getPalleteSize() {
        return this.palleteSize;
    }

    public BlockState[] getPallete() {
        return this.pallete;
    }

    public short[][][] getStructure() {
        return this.structure;
    }

    public NbtCompound[] getTileEntities() {
        return this.tileEntities;
    }

    public List<String> getRequiredMods() {
        return this.requiredMods;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getArchitects() {
        return this.architects;
    }

    public void setArchitects(String[] architects) {
        this.architects = architects;
    }

    @Override
    public short getWidth() {
        return this.getSizeX();
    }

    @Override
    public short getHeight() {
        return this.getSizeZ();
    }

    @Override
    public short getLength() {
        return this.getSizeY();
    }

    @Override
    public int getBlockEntityDimensions() {
        return this.tileEntities.length;
    }

    @Override
    public NbtCompound getBlockEntity(int i) {
        return this.tileEntities[i];
    }

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        return this.pallete[this.structure[y][z][x]];
    }

    @Override
    public BlockState getBlockState(int i) {
        int x = i % this.getWidth();
        int z = (i - x) / this.getWidth() % this.getLength();
        int y = ((i - x) / this.getWidth() - z) / this.getLength();
        return this.getBlockState(x, y, z);
    }

    @Override
    public NbtCompound getNBT() {
        return BlueprintUtil.writeBlueprintToNBT(this);
    }
}

