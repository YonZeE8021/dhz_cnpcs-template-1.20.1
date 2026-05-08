/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.BlockView
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.world.World
 *  net.minecraft.block.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.block.BlockEntityProvider
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtHelper
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.BlockState
 *  net.minecraft.world.EmptyBlockView
 *  net.minecraft.state.property.DirectionProperty
 *  net.minecraft.state.property.Property
 */
package noppes.npcs.schematics;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtElement;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.world.EmptyBlockView;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Property;
import noppes.npcs.schematics.ISchematic;
import noppes.npcs.schematics.Schematic;

public class SchematicWrapper {
    public static final int buildSize = 10000;
    private BlockPos offset = BlockPos.ORIGIN;
    private BlockPos start = BlockPos.ORIGIN;
    public ISchematic schema;
    public int buildPos;
    public int size;
    public int rotation = 0;
    private World level;
    public boolean isBuilding = false;
    public boolean firstLayer = true;
    private Map<ChunkPos, NbtCompound>[] tileEntities;

    public SchematicWrapper(ISchematic schematic) {
        this.schema = schematic;
        this.size = schematic.getWidth() * schematic.getHeight() * schematic.getLength();
        this.tileEntities = new Map[schematic.getHeight()];
        for (int i = 0; i < schematic.getBlockEntityDimensions(); ++i) {
            NbtCompound teTag = schematic.getBlockEntity(i);
            int x = teTag.getInt("x");
            int y = teTag.getInt("y");
            int z = teTag.getInt("z");
            Map<ChunkPos, NbtCompound> map = this.tileEntities[y];
            if (map == null) {
                this.tileEntities[y] = map = new HashMap<ChunkPos, NbtCompound>();
            }
            map.put(new ChunkPos(x, z), teTag);
        }
    }

    public void load(Schematic s) {
    }

    public void init(BlockPos pos, World level, int rotation) {
        this.start = pos;
        this.level = level;
        this.rotation = rotation;
    }

    public void offset(int x, int y, int z) {
        this.offset = new BlockPos(x, y, z);
    }

    public void build() {
        if (this.level == null || !this.isBuilding) {
            return;
        }
        long endPos = this.buildPos + 10000;
        if (endPos > (long)this.size) {
            endPos = this.size;
        }
        while ((long)this.buildPos < endPos) {
            int x = this.buildPos % this.schema.getWidth();
            int z = (this.buildPos - x) / this.schema.getWidth() % this.schema.getLength();
            int y = ((this.buildPos - x) / this.schema.getWidth() - z) / this.schema.getLength();
            if (this.firstLayer) {
                this.place(x, y, z, 1);
            } else {
                this.place(x, y, z, 2);
            }
            ++this.buildPos;
        }
        if (this.buildPos >= this.size) {
            if (this.firstLayer) {
                this.firstLayer = false;
                this.buildPos = 0;
            } else {
                this.isBuilding = false;
            }
        }
    }

    public void place(int x, int y, int z, int flag) {
        NbtCompound comp;
        BlockEntity tile;
        BlockState state = this.schema.getBlockState(x, y, z);
        if (state == null || flag == 1 && !state.isFullCube((BlockView)EmptyBlockView.field_12294, BlockPos.ORIGIN) && state.getBlock() != Blocks.field_10124 || flag == 2 && (state.isFullCube((BlockView)EmptyBlockView.field_12294, BlockPos.ORIGIN) || state.getBlock() == Blocks.field_10124)) {
            return;
        }
        int rotation = this.rotation / 90;
        BlockPos pos = this.start.add((Vec3i)this.rotatePos(x, y, z, rotation));
        state = this.rotationState(state, rotation);
        this.level.setBlockState(pos, state, 2);
        if (state.getBlock() instanceof BlockEntityProvider && (tile = this.level.getBlockEntity(pos)) != null && (comp = this.getBlockEntity(x, y, z, pos)) != null) {
            tile.readNbt(comp);
        }
    }

    public BlockState rotationState(BlockState state, int rotation) {
        if (rotation == 0) {
            return state;
        }
        for (Property prop : state.getProperties()) {
            Direction direction;
            if (!(prop instanceof DirectionProperty) || (direction = (Direction)state.get(prop)) == Direction.field_11036 || direction == Direction.field_11033) continue;
            for (int i = 0; i < rotation; ++i) {
                direction = direction.rotateYClockwise();
            }
            return (BlockState)state.with(prop, (Comparable)direction);
        }
        return state;
    }

    public NbtCompound getBlockEntity(int x, int y, int z, BlockPos pos) {
        if (y >= this.tileEntities.length || this.tileEntities[y] == null) {
            return null;
        }
        NbtCompound compound = this.tileEntities[y].get(new ChunkPos(x, z));
        if (compound == null) {
            return null;
        }
        compound = compound.copy();
        compound.putInt("x", pos.getX());
        compound.putInt("y", pos.getY());
        compound.putInt("z", pos.getZ());
        return compound;
    }

    public NbtCompound getNBTSmall() {
        NbtCompound compound = new NbtCompound();
        compound.putShort("Width", this.schema.getWidth());
        compound.putShort("Height", this.schema.getHeight());
        compound.putShort("Length", this.schema.getLength());
        compound.putString("SchematicName", this.schema.getName());
        NbtList list = new NbtList();
        for (int i = 0; i < this.size && i < 25000; ++i) {
            BlockState state = this.schema.getBlockState(i);
            if (state.getBlock() == Blocks.field_10124 || state.getBlock() == Blocks.field_10369) {
                list.add((Object)new NbtCompound());
                continue;
            }
            list.add((Object)NbtHelper.fromBlockState((BlockState)this.schema.getBlockState(i)));
        }
        compound.put("Data", (NbtElement)list);
        return compound;
    }

    public BlockPos rotatePos(int x, int y, int z, int rotation) {
        if (rotation == 1) {
            return new BlockPos(this.schema.getLength() - z - 1, y, x);
        }
        if (rotation == 2) {
            return new BlockPos(this.schema.getWidth() - x - 1, y, this.schema.getLength() - z - 1);
        }
        if (rotation == 3) {
            return new BlockPos(z, y, this.schema.getWidth() - x - 1);
        }
        return new BlockPos(x, y, z);
    }

    public int getPercentage() {
        double l = this.buildPos + (this.firstLayer ? 0 : this.size);
        return (int)(l / (double)this.size * 50.0);
    }
}

