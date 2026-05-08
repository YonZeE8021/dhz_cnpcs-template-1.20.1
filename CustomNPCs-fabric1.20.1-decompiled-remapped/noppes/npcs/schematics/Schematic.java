/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.block.BlockState
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.Identifier
 *  net.minecraft.resource.Resource
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.schematics;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.resource.Resource;
import net.minecraft.registry.Registries;
import noppes.npcs.CustomNpcs;
import noppes.npcs.schematics.ISchematic;

public class Schematic
implements ISchematic {
    private static final HashMap<String, BlockState> staticBlockIds = new HashMap();
    public String name;
    public short width;
    public short height;
    public short length;
    private NbtList entityList;
    public NbtList tileList;
    public short[] blockArray;
    public byte[] blockDataArray;
    public HashMap<String, BlockState> blockIds = staticBlockIds;

    private static <T extends Comparable<T>> BlockState setValue(BlockState state, Property<T> prop, String val) {
        Optional optional = prop.parse(val);
        if (optional.isPresent()) {
            return (BlockState)state.with(prop, (Comparable)optional.get());
        }
        return state;
    }

    public Schematic(String name) {
        this.name = name;
    }

    public void load(NbtCompound compound) {
        this.width = compound.getShort("Width");
        this.height = compound.getShort("Height");
        this.length = compound.getShort("Length");
        byte[] addId = compound.contains("AddBlocks") ? compound.getByteArray("AddBlocks") : new byte[]{};
        this.setBlockBytes(compound.getByteArray("Blocks"), addId);
        this.blockDataArray = compound.getByteArray("Data");
        this.entityList = compound.getList("Entities", 10);
        this.tileList = compound.getList("TileEntities", 10);
        if (compound.contains("BlockIDs", 10)) {
            NbtCompound comp = compound.getCompound("BlockIDs");
            this.blockIds = new HashMap();
            for (String idStr : comp.getKeys()) {
                String key = comp.getString(idStr);
                try {
                    int id = Integer.parseInt(idStr);
                    Block block = (Block)Registries.BLOCK.get(new Identifier(key));
                    if (block == null) continue;
                    this.blockIds.put(id + ":0", block.getDefaultState());
                }
                catch (NumberFormatException e) {}
            }
        }
    }

    @Override
    public NbtCompound getNBT() {
        NbtCompound compound = new NbtCompound();
        compound.putShort("Width", this.width);
        compound.putShort("Height", this.height);
        compound.putShort("Length", this.length);
        byte[][] arr = this.getBlockBytes();
        compound.putByteArray("Blocks", arr[0]);
        if (arr.length > 1) {
            compound.putByteArray("AddBlocks", arr[1]);
        }
        compound.putByteArray("Data", this.blockDataArray);
        compound.put("TileEntities", (NbtElement)this.tileList);
        NbtCompound comp = new NbtCompound();
        for (Map.Entry<String, BlockState> entry : this.blockIds.entrySet()) {
            comp.putString("" + Block.getRawIdFromState((BlockState)entry.getValue()), Registries.BLOCK.getId((Object)entry.getValue().getBlock()).toString());
        }
        compound.put("BlockIDs", (NbtElement)comp);
        return compound;
    }

    public void setBlockBytes(byte[] blockId, byte[] addId) {
        this.blockArray = new short[blockId.length];
        for (int index = 0; index < blockId.length; ++index) {
            short id = (short)(blockId[index] & 0xFF);
            if (index >> 1 < addId.length) {
                id = (index & 1) == 0 ? (short)(id + (short)((addId[index >> 1] & 0xF) << 8)) : (short)(id + (short)((addId[index >> 1] & 0xF0) << 4));
            }
            this.blockArray[index] = id;
        }
    }

    public byte[][] getBlockBytes() {
        byte[] blocks = new byte[this.blockArray.length];
        byte[] addBlocks = null;
        for (int i = 0; i < blocks.length; ++i) {
            short id = this.blockArray[i];
            if (id > 255) {
                if (addBlocks == null) {
                    addBlocks = new byte[(blocks.length >> 1) + 1];
                }
                addBlocks[i >> 1] = (i & 1) == 0 ? (byte)(addBlocks[i >> 1] & 0xF0 | id >> 8 & 0xF) : (byte)(addBlocks[i >> 1] & 0xF | (id >> 8 & 0xF) << 4);
            }
            blocks[i] = (byte)id;
        }
        if (addBlocks == null) {
            return new byte[][]{blocks};
        }
        return new byte[][]{blocks, addBlocks};
    }

    public int xyzToIndex(int x, int y, int z) {
        return (y * this.length + z) * this.width + x;
    }

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        return this.getBlockState(this.xyzToIndex(x, y, z));
    }

    @Override
    public BlockState getBlockState(int i) {
        BlockState b = this.blockIds.get(this.blockArray[i] + ":" + this.blockDataArray[i]);
        if (b == null) {
            return Blocks.field_10124.getDefaultState();
        }
        return b;
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
        if (this.tileList == null) {
            return 0;
        }
        return this.tileList.size();
    }

    @Override
    public NbtCompound getBlockEntity(int i) {
        return this.tileList.getCompound(i);
    }

    @Override
    public String getName() {
        return this.name;
    }

    static {
        Identifier resource = new Identifier("customnpcs", "legacy_blockids.json");
        Resource ir = CustomNpcs.Server.getResourceManager().getResource(resource).orElse(null);
        if (ir != null) {
            try {
                InputStream stream = ir.getInputStream();
                InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
                JsonObject result = ((JsonObject)new Gson().fromJson((Reader)reader, JsonObject.class)).getAsJsonObject("blocks");
                for (Map.Entry entry : result.entrySet()) {
                    Block block;
                    String val = ((JsonElement)entry.getValue()).getAsString();
                    String[] properties = null;
                    if (val.indexOf(91) > 0) {
                        properties = val.substring(val.indexOf(91) + 1, val.length() - 1).split(",");
                        val = val.substring(0, val.indexOf(91));
                    }
                    if ((block = (Block)Registries.BLOCK.get(new Identifier(val))) == null) continue;
                    BlockState state = block.getDefaultState();
                    if (properties != null) {
                        for (Property prop : state.getProperties()) {
                            for (String r : properties) {
                                if (!r.startsWith(prop.getName() + "=")) continue;
                                state = Schematic.setValue(state, prop, r.split("=")[1]);
                            }
                        }
                    }
                    staticBlockIds.put((String)entry.getKey(), state);
                }
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }
}

