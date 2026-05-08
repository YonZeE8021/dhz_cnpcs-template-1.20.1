/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.block.BlockEntityProvider
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.BlockState
 *  net.minecraft.world.EmptyBlockView
 */
package noppes.npcs.blocks.tiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.world.EmptyBlockView;
import noppes.npcs.CustomBlocks;
import noppes.npcs.NBTTags;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobBuilder;
import noppes.npcs.schematics.SchematicWrapper;

public class TileBuilder
extends BlockEntity {
    private SchematicWrapper schematic = null;
    public int rotation = 0;
    public int yOffest = 0;
    public boolean enabled = false;
    public boolean started = false;
    public boolean finished = false;
    public Availability availability = new Availability();
    private Stack<Integer> positions = new Stack();
    private Stack<Integer> positionsSecond = new Stack();
    public static BlockPos DrawPos = null;
    public static boolean Compiled = false;
    private int ticks = 20;

    public TileBuilder(BlockPos pos, BlockState state) {
        super(CustomBlocks.tile_builder, pos, state);
    }

    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        if (compound.contains("SchematicName")) {
            this.schematic = SchematicController.Instance.load(compound.getString("SchematicName"));
        }
        Stack<Integer> positions = new Stack<Integer>();
        positions.addAll(NBTTags.getIntegerList(compound.getList("Positions", 10)));
        this.positions = positions;
        positions = new Stack();
        positions.addAll(NBTTags.getIntegerList(compound.getList("PositionsSecond", 10)));
        this.positionsSecond = positions;
        this.readPartNBT(compound);
    }

    public void readPartNBT(NbtCompound compound) {
        this.rotation = compound.getInt("Rotation");
        this.yOffest = compound.getInt("YOffset");
        this.enabled = compound.getBoolean("Enabled");
        this.started = compound.getBoolean("Started");
        this.finished = compound.getBoolean("Finished");
        this.availability.load(compound.getCompound("Availability"));
    }

    public void writeNbt(NbtCompound compound) {
        super.writeNbt(compound);
        if (this.schematic != null) {
            compound.putString("SchematicName", this.schematic.schema.getName());
        }
        compound.put("Positions", (NbtElement)NBTTags.nbtIntegerCollection(new ArrayList<Integer>(this.positions)));
        compound.put("PositionsSecond", (NbtElement)NBTTags.nbtIntegerCollection(new ArrayList<Integer>(this.positionsSecond)));
        this.writePartNBT(compound);
    }

    public NbtCompound writePartNBT(NbtCompound compound) {
        compound.putInt("Rotation", this.rotation);
        compound.putInt("YOffset", this.yOffest);
        compound.putBoolean("Enabled", this.enabled);
        compound.putBoolean("Started", this.started);
        compound.putBoolean("Finished", this.finished);
        compound.put("Availability", (NbtElement)this.availability.save(new NbtCompound()));
        return compound;
    }

    @Environment(value=EnvType.CLIENT)
    public void setDrawSchematic(SchematicWrapper schematics) {
        this.schematic = schematics;
    }

    public void setSchematic(SchematicWrapper schematics) {
        this.schematic = schematics;
        if (schematics == null) {
            this.positions.clear();
            this.positionsSecond.clear();
            return;
        }
        Stack<Integer> positions = new Stack<Integer>();
        for (int y = 0; y < schematics.schema.getHeight(); ++y) {
            int x;
            int z;
            for (z = 0; z < schematics.schema.getLength() / 2; ++z) {
                for (x = 0; x < schematics.schema.getWidth() / 2; ++x) {
                    positions.add(0, this.xyzToIndex(x, y, z));
                }
            }
            for (z = 0; z < schematics.schema.getLength() / 2; ++z) {
                for (x = schematics.schema.getWidth() / 2; x < schematics.schema.getWidth(); ++x) {
                    positions.add(0, this.xyzToIndex(x, y, z));
                }
            }
            for (z = schematics.schema.getLength() / 2; z < schematics.schema.getLength(); ++z) {
                for (x = 0; x < schematics.schema.getWidth() / 2; ++x) {
                    positions.add(0, this.xyzToIndex(x, y, z));
                }
            }
            for (z = schematics.schema.getLength() / 2; z < schematics.schema.getLength(); ++z) {
                for (x = schematics.schema.getWidth() / 2; x < schematics.schema.getWidth(); ++x) {
                    positions.add(0, this.xyzToIndex(x, y, z));
                }
            }
        }
        this.positions = positions;
        this.positionsSecond.clear();
    }

    public int xyzToIndex(int x, int y, int z) {
        return (y * this.schematic.schema.getLength() + z) * this.schematic.schema.getWidth() + x;
    }

    public SchematicWrapper getSchematic() {
        return this.schematic;
    }

    public boolean hasSchematic() {
        return this.schematic != null;
    }

    public static void tick(World level, BlockPos pos, BlockState state, TileBuilder tile) {
        if (level.isClient || !tile.hasSchematic() || tile.finished) {
            return;
        }
        --tile.ticks;
        if (tile.ticks > 0) {
            return;
        }
        tile.ticks = 200;
        if (tile.positions.isEmpty() && tile.positionsSecond.isEmpty()) {
            tile.finished = true;
            return;
        }
        if (!tile.started) {
            for (PlayerEntity player : tile.getPlayerList()) {
                if (!tile.availability.isAvailable(player)) continue;
                tile.started = true;
                break;
            }
            if (!tile.started) {
                return;
            }
        }
        List list = level.getNonSpectatingEntities(EntityNPCInterface.class, new Box(pos, pos).expand(32.0, 32.0, 32.0));
        for (EntityNPCInterface npc : list) {
            if (npc.job.getType() != 10) continue;
            JobBuilder job = (JobBuilder)npc.job;
            if (job.build != null) continue;
            job.build = tile;
        }
    }

    private List<PlayerEntity> getPlayerList() {
        return this.world.getNonSpectatingEntities(PlayerEntity.class, new Box((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (double)(this.pos.getX() + 1), (double)(this.pos.getY() + 1), (double)(this.pos.getZ() + 1)).expand(10.0, 10.0, 10.0));
    }

    public Stack<BlockData> getBlock() {
        if (!this.enabled || this.finished || !this.hasSchematic()) {
            return null;
        }
        boolean bo = this.positions.isEmpty();
        Stack<BlockData> list = new Stack<BlockData>();
        int size = this.schematic.schema.getWidth() * this.schematic.schema.getLength() / 4;
        if (size > 30) {
            size = 30;
        }
        for (int i = 0; i < size; ++i) {
            if (this.positions.isEmpty() && !bo || this.positionsSecond.isEmpty() && bo) {
                return list;
            }
            int pos = bo ? this.positionsSecond.pop() : this.positions.pop();
            if (pos >= this.schematic.size) continue;
            int x = pos % this.schematic.schema.getWidth();
            int z = (pos - x) / this.schematic.schema.getWidth() % this.schematic.schema.getLength();
            int y = ((pos - x) / this.schematic.schema.getWidth() - z) / this.schematic.schema.getLength();
            BlockState state = this.schematic.schema.getBlockState(x, y, z);
            if (!state.isFullCube((BlockView)EmptyBlockView.field_12294, BlockPos.ORIGIN) && !bo && state.getBlock() != Blocks.field_10124) {
                this.positionsSecond.add(0, pos);
                continue;
            }
            BlockPos blockPos = this.getPos().add(1, this.yOffest, 1).add((Vec3i)this.schematic.rotatePos(x, y, z, this.rotation));
            BlockState original = this.world.getBlockState(blockPos);
            if (Block.getRawIdFromState((BlockState)state) == Block.getRawIdFromState((BlockState)original)) continue;
            state = this.schematic.rotationState(state, this.rotation);
            NbtCompound tile = null;
            if (state.getBlock() instanceof BlockEntityProvider) {
                tile = this.schematic.getBlockEntity(x, y, z, blockPos);
            }
            list.add(0, new BlockData(blockPos, state, tile));
        }
        return list;
    }

    public static void SetDrawPos(BlockPos pos) {
        DrawPos = pos;
        Compiled = false;
    }
}

