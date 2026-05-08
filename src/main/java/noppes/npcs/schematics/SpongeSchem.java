/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  net.minecraft.datafixer.TypeReferences
 *  net.minecraft.world.World
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.command.argument.BlockArgumentParser
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.block.BlockEntityProvider
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtOps
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.BlockState
 *  net.minecraft.datafixer.Schemas
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.schematics;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtElement;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.datafixer.Schemas;
import net.minecraft.registry.Registries;
import noppes.npcs.CustomBlocks;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.schematics.ISchematic;
import noppes.npcs.shared.common.CommonUtil;

public class SpongeSchem
implements ISchematic {
    public static final int latestDataVersion = 2586;
    public String name;
    public short width;
    public short height;
    public short length;
    public long timestamp = System.currentTimeMillis();
    public int[] data;
    public Map<Integer, BlockState> palette = new HashMap<Integer, BlockState>();
    public List<NbtCompound> tileData = new ArrayList<NbtCompound>();

    public SpongeSchem(String name) {
        this.name = name;
    }

    @Override
    public short getWidth() {
        return this.width;
    }

    @Override
    public short getHeight() {
        return this.height;
    }

    @Override
    public short getLength() {
        return this.length;
    }

    @Override
    public int getBlockEntityDimensions() {
        return this.tileData.size();
    }

    @Override
    public NbtCompound getBlockEntity(int i) {
        return this.tileData.get(i);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        return this.getBlockState(this.xyzToIndex(x, y, z));
    }

    public int xyzToIndex(int x, int y, int z) {
        return (y * this.length + z) * this.width + x;
    }

    @Override
    public BlockState getBlockState(int i) {
        return this.palette.get(this.data[i]);
    }

    @Override
    public NbtCompound getNBT() {
        NbtCompound root = new NbtCompound();
        NbtCompound compound = new NbtCompound();
        root.put("", (NbtElement)compound);
        NbtCompound data = new NbtCompound();
        compound.put("Schematic", (NbtElement)data);
        data.putInt("Width", (int)this.width);
        data.putInt("Height", (int)this.height);
        data.putInt("Length", (int)this.length);
        data.putInt("Version", 3);
        data.putInt("DataVersion", 2586);
        NbtCompound metadata = new NbtCompound();
        metadata.putLong("Date", this.timestamp);
        data.put("Metadata", (NbtElement)metadata);
        NbtCompound blockData = new NbtCompound();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            int blockId = this.data[i];
            while ((blockId & 0xFFFFFF80) != 0) {
                buffer.write(blockId & 0x7F | 0x80);
                blockId >>>= 7;
            }
            buffer.write(blockId);
        }
        blockData.putByteArray("Data", buffer.toByteArray());
        NbtCompound palletteNBT = new NbtCompound();
        for (Map.Entry<Integer, BlockState> en : this.palette.entrySet()) {
            palletteNBT.putInt(BlockArgumentParser.stringifyBlockState((BlockState)en.getValue()), en.getKey().intValue());
        }
        blockData.put("Palette", (NbtElement)palletteNBT);
        NbtList tileNBT = new NbtList();
        for (NbtCompound tile : this.tileData) {
            tile = tile.copy();
            tile.putIntArray("Pos", new int[]{tile.getInt("x"), tile.getInt("y"), tile.getInt("z")});
            tile.putString("Id", tile.getString("id"));
            tile.remove("x");
            tile.remove("y");
            tile.remove("z");
            tile.remove("id");
            tileNBT.add(tile);
        }
        blockData.put("BlockEntities", (NbtElement)tileNBT);
        data.put("Blocks", (NbtElement)blockData);
        return root;
    }

    public void load(NbtCompound compound) {
        int version;
        if (compound.getSize() == 1) {
            compound = compound.getCompound("").getCompound("Schematic");
        }
        this.width = compound.getShort("Width");
        this.height = compound.getShort("Height");
        this.length = compound.getShort("Length");
        NbtCompound metadata = compound.getCompound("Metadata");
        this.timestamp = 0L;
        if (!metadata.isEmpty()) {
            this.timestamp = metadata.getLong("Date");
        }
        int dataVersion = 1631;
        if (compound.contains("DataVersion")) {
            dataVersion = compound.getInt("DataVersion");
            if (dataVersion > 2586) {
                // empty if block
            }
            if (dataVersion < 2586) {
                // empty if block
            }
        }
        if ((version = compound.getInt("Version")) < 3) {
            this.palette = this.readPalette(compound.getCompound("Palette"), dataVersion);
            NbtList tileEntities = compound.getList("BlockEntities", 10);
            if (tileEntities.isEmpty()) {
                tileEntities = compound.getList("TileEntities", 10);
            }
            this.tileData = this.readTileData(tileEntities, dataVersion);
            this.data = this.readBlockData(compound.getByteArray("BlockData"));
        } else {
            NbtCompound blocks = compound.getCompound("Blocks");
            this.palette = this.readPalette(blocks.getCompound("Palette"), dataVersion);
            this.tileData = this.readTileData(blocks.getList("BlockEntities", 10), dataVersion);
            this.data = this.readBlockData(blocks.getByteArray("Data"));
        }
    }

    private int[] readBlockData(byte[] bytes) {
        int[] data = new int[this.width * this.length * this.height];
        int index = 0;
        int i = 0;
        while (i < bytes.length) {
            int value = 0;
            int varintLength = 0;
            while (true) {
                value |= (bytes[i] & 0x7F) << varintLength++ * 7;
                if (varintLength > 5) {
                    throw new CustomNPCsException("VarInt too big (probably corrupted data)", new Object[0]);
                }
                if ((bytes[i] & 0x80) != 128) {
                    ++i;
                    break;
                }
                ++i;
            }
            data[index] = value;
            ++index;
        }
        return data;
    }

    private Map<Integer, BlockState> readPalette(NbtCompound comp, int dataVersion) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        HashMap<Integer, BlockState> palette = new HashMap<Integer, BlockState>();
        for (String blockState : comp.getKeys()) {
            int id = comp.getInt(blockState);
            if (dataVersion < 2586) {
                NbtCompound stateNBT = SpongeSchem.stateToNBT(blockState);
                Dynamic dynamic = new Dynamic((DynamicOps)NbtOps.INSTANCE, stateNBT);
                stateNBT = (NbtCompound)Schemas.getFixer().update(TypeReferences.BLOCK_STATE, dynamic, dataVersion, 2586).getValue();
                blockState = this.nbtToState(stateNBT);
            }
            map.put(blockState, id);
        }
        for (Block block : Registries.BLOCK) {
            block.getStateManager().getStates().forEach(state -> {
                String name = BlockArgumentParser.stringifyBlockState((BlockState)state);
                if (map.containsKey(name)) {
                    int id = (Integer)map.remove(name);
                    palette.put(id, (BlockState)state);
                }
            });
        }
        Iterator iterator = map.values().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            palette.put(id, Blocks.AIR.getDefaultState());
        }
        return palette;
    }

    private List<NbtCompound> readTileData(NbtList list, int dataVersion) {
        ArrayList<NbtCompound> tileData = new ArrayList<NbtCompound>();
        if (list.isEmpty()) {
            return tileData;
        }
        for (int i = 0; i < list.size(); ++i) {
            NbtCompound data = list.getCompound(i);
            int[] posArr = data.getIntArray("Pos");
            BlockPos pos = new BlockPos(posArr[0], posArr[1], posArr[2]);
            data.putInt("x", pos.getX());
            data.putInt("y", pos.getY());
            data.putInt("z", pos.getZ());
            data.put("id", data.get("Id"));
            data.remove("Id");
            data.remove("Pos");
            if (dataVersion < 2586) {
                Dynamic dynamic = new Dynamic((DynamicOps)NbtOps.INSTANCE, data);
                data = (NbtCompound)Schemas.getFixer().update(TypeReferences.BLOCK_ENTITY, dynamic, dataVersion, 2586).getValue();
            } else {
                data = data.copy();
            }
            tileData.add(data);
        }
        return tileData;
    }

    private String nbtToState(NbtCompound tagCompound) {
        StringBuilder sb = new StringBuilder();
        sb.append(tagCompound.getString("Name"));
        if (tagCompound.contains("Properties", 10)) {
            sb.append('[');
            NbtCompound props = tagCompound.getCompound("Properties");
            sb.append(props.getKeys().stream().map(k -> k + "=" + props.getString(k).replace("\"", "")).collect(Collectors.joining(",")));
            sb.append(']');
        }
        return sb.toString();
    }

    private static NbtCompound stateToNBT(String blockState) {
        int propIdx = blockState.indexOf(91);
        NbtCompound tag = new NbtCompound();
        if (propIdx < 0) {
            tag.putString("Name", blockState);
        } else {
            String[] propArr;
            tag.putString("Name", blockState.substring(0, propIdx));
            NbtCompound propTag = new NbtCompound();
            String props = blockState.substring(propIdx + 1, blockState.length() - 1);
            for (String pair : propArr = props.split(",")) {
                String[] split = pair.split("=");
                propTag.putString(split[0], split[1]);
            }
            tag.put("Properties", (NbtElement)propTag);
        }
        return tag;
    }

    public static SpongeSchem Create(World level, String name, BlockPos pos, short height, short width, short length) {
        SpongeSchem schema = new SpongeSchem(name);
        schema.height = height;
        schema.width = width;
        schema.length = length;
        int size = height * width * length;
        CommonUtil.NotifyOPs(level.getServer(), "Creating schematic at: " + String.valueOf(pos) + " might lag slightly", new Object[0]);
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        schema.data = new int[size];
        int uniqueBlockId = 0;
        for (int i = 0; i < size; ++i) {
            int x = i % width;
            int z = (i - x) / width % length;
            int y = ((i - x) / width - z) / length;
            BlockState state = level.getBlockState(pos.add(x, y, z));
            if (state.getBlock() == CustomBlocks.copy) continue;
            String stateName = BlockArgumentParser.stringifyBlockState((BlockState)state);
            Integer blockId = (Integer)map.get(stateName);
            if (!map.containsKey(stateName)) {
                blockId = uniqueBlockId++;
                map.put(stateName, blockId);
            }
            schema.palette.put(blockId, state);
            schema.data[i] = blockId;
            if (!(state.getBlock() instanceof BlockEntityProvider)) continue;
            BlockEntity tile = level.getBlockEntity(pos.add(x, y, z));
            NbtCompound compound = tile.createNbtWithIdentifyingData();
            compound.putInt("x", x);
            compound.putInt("y", y);
            compound.putInt("z", z);
            schema.tileData.add(compound);
        }
        return schema;
    }
}

