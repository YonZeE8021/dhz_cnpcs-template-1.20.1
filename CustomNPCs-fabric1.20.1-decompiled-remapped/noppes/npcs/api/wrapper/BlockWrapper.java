/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.entity.FakePlayer
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.util.Hand
 *  net.minecraft.util.Nameable
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.World
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.AbstractNbtNumber
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.BlockState
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.Identifier
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.api.wrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.Hand;
import net.minecraft.util.Nameable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.registry.Registries;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.INbt;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.wrapper.BlockPosWrapper;
import noppes.npcs.api.wrapper.BlockScriptedDoorWrapper;
import noppes.npcs.api.wrapper.BlockScriptedWrapper;
import noppes.npcs.blocks.BlockScripted;
import noppes.npcs.blocks.BlockScriptedDoor;
import noppes.npcs.blocks.tiles.TileNpcEntity;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.IEntityPersistentData;
import noppes.npcs.mixin.EntityIMixin;
import noppes.npcs.shared.common.util.LRUHashMap;

public class BlockWrapper
implements IBlock {
    private static final Map<String, BlockWrapper> blockCache = new LRUHashMap<String, BlockWrapper>(400);
    protected final IWorld level;
    protected final Block block;
    protected final BlockPos pos;
    protected final BlockPosWrapper bPos;
    protected BlockEntity tile;
    protected TileNpcEntity storage;
    private final IData tempdata = new IData(){

        @Override
        public void remove(String key) {
            if (BlockWrapper.this.storage == null) {
                return;
            }
            BlockWrapper.this.storage.tempData.remove(key);
        }

        @Override
        public void put(String key, Object value) {
            if (BlockWrapper.this.storage == null) {
                return;
            }
            BlockWrapper.this.storage.tempData.put(key, value);
        }

        @Override
        public boolean has(String key) {
            if (BlockWrapper.this.storage == null) {
                return false;
            }
            return BlockWrapper.this.storage.tempData.containsKey(key);
        }

        @Override
        public Object get(String key) {
            if (BlockWrapper.this.storage == null) {
                return null;
            }
            return BlockWrapper.this.storage.tempData.get(key);
        }

        @Override
        public void clear() {
            if (BlockWrapper.this.storage == null) {
                return;
            }
            BlockWrapper.this.storage.tempData.clear();
        }

        @Override
        public String[] getKeys() {
            return BlockWrapper.this.storage.tempData.keySet().toArray(new String[BlockWrapper.this.storage.tempData.size()]);
        }
    };
    private final IData storeddata = new IData(){

        @Override
        public void put(String key, Object value) {
            NbtCompound compound = this.getNBT();
            if (compound == null) {
                return;
            }
            if (value instanceof Number) {
                compound.putDouble(key, ((Number)value).doubleValue());
            } else if (value instanceof String) {
                compound.putString(key, (String)value);
            }
        }

        @Override
        public Object get(String key) {
            NbtCompound compound = this.getNBT();
            if (compound == null) {
                return null;
            }
            if (!compound.contains(key)) {
                return null;
            }
            NbtElement base = compound.get(key);
            if (base instanceof AbstractNbtNumber) {
                return ((AbstractNbtNumber)base).doubleValue();
            }
            return base.asString();
        }

        @Override
        public void remove(String key) {
            NbtCompound compound = this.getNBT();
            if (compound == null) {
                return;
            }
            compound.remove(key);
        }

        @Override
        public boolean has(String key) {
            NbtCompound compound = this.getNBT();
            if (compound == null) {
                return false;
            }
            return compound.contains(key);
        }

        @Override
        public void clear() {
            if (BlockWrapper.this.tile == null) {
                return;
            }
            ((IEntityPersistentData)BlockWrapper.this.tile).getPersistentData().put("CustomNPCsData", (NbtElement)new NbtCompound());
        }

        private NbtCompound getNBT() {
            if (BlockWrapper.this.tile == null) {
                return null;
            }
            NbtCompound compound = ((IEntityPersistentData)BlockWrapper.this.tile).getPersistentData().getCompound("CustomNPCsData");
            if (compound.isEmpty() && !((IEntityPersistentData)BlockWrapper.this.tile).getPersistentData().contains("CustomNPCsData")) {
                ((IEntityPersistentData)BlockWrapper.this.tile).getPersistentData().put("CustomNPCsData", (NbtElement)compound);
            }
            return compound;
        }

        @Override
        public String[] getKeys() {
            NbtCompound compound = this.getNBT();
            if (compound == null) {
                return new String[0];
            }
            return compound.getKeys().toArray(new String[compound.getKeys().size()]);
        }
    };

    protected BlockWrapper(World level, Block block, BlockPos pos) {
        this.level = NpcAPI.Instance().getIWorld((ServerWorld)level);
        this.block = block;
        this.pos = pos;
        this.bPos = new BlockPosWrapper(pos);
        this.setTile(level.getBlockEntity(pos));
    }

    @Override
    public int getX() {
        return this.pos.getX();
    }

    @Override
    public int getY() {
        return this.pos.getY();
    }

    @Override
    public int getZ() {
        return this.pos.getZ();
    }

    @Override
    public IPos getPos() {
        return this.bPos;
    }

    @Override
    public Object getProperty(String name) {
        BlockState state = this.getMCBlockState();
        for (Property p : state.getProperties()) {
            if (!p.getName().equalsIgnoreCase(name)) continue;
            return state.get(p);
        }
        throw new CustomNPCsException("Unknown property: " + name, new Object[0]);
    }

    @Override
    public void setProperty(String name, Object val) {
        if (!(val instanceof Comparable)) {
            throw new CustomNPCsException("Not a valid property value: " + String.valueOf(val), new Object[0]);
        }
        BlockState state = this.getMCBlockState();
        for (Property p : state.getProperties()) {
            if (!p.getName().equalsIgnoreCase(name)) continue;
            this.setPropertyValue(state, p, (Comparable)val);
            return;
        }
        throw new CustomNPCsException("Unknown property: " + name, new Object[0]);
    }

    private <T extends Comparable<T>> void setPropertyValue(BlockState state, Property<T> p, Comparable<?> c) {
        this.level.getMCLevel().setBlockState(this.pos, (BlockState)state.with(p, (Comparable)p.getType().cast(c)), 3);
    }

    @Override
    public String[] getProperties() {
        Collection props = this.getMCBlockState().getProperties();
        ArrayList<String> list = new ArrayList<String>();
        for (Property prop : props) {
            list.add(prop.getName());
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public void remove() {
        this.level.getMCLevel().removeBlock(this.pos, false);
    }

    @Override
    public boolean isRemoved() {
        BlockState state = this.level.getMCLevel().getBlockState(this.pos);
        if (state == null) {
            return true;
        }
        return state.getBlock() != this.block;
    }

    @Override
    public boolean isAir() {
        return this.level.getMCLevel().getBlockState(this.pos).isAir();
    }

    @Override
    public BlockWrapper setBlock(String name) {
        Block block = (Block)Registries.BLOCK.get(new Identifier(name));
        if (block == null) {
            return this;
        }
        this.level.getMCLevel().setBlockState(this.pos, block.getDefaultState(), 2);
        return new BlockWrapper((World)this.level.getMCLevel(), block, this.pos);
    }

    @Override
    public BlockWrapper setBlock(IBlock block) {
        this.level.getMCLevel().setBlockState(this.pos, block.getMCBlock().getDefaultState(), 2);
        return new BlockWrapper((World)this.level.getMCLevel(), block.getMCBlock(), this.pos);
    }

    @Override
    public boolean isContainer() {
        if (this.tile == null || !(this.tile instanceof Inventory)) {
            return false;
        }
        return ((Inventory)this.tile).size() > 0;
    }

    @Override
    public IContainer getContainer() {
        if (!this.isContainer()) {
            throw new CustomNPCsException("This block is not a container", new Object[0]);
        }
        return NpcAPI.Instance().getIContainer((Inventory)this.tile);
    }

    @Override
    public IData getTempdata() {
        return this.tempdata;
    }

    @Override
    public IData getStoreddata() {
        return this.storeddata;
    }

    @Override
    public String getName() {
        return Registries.BLOCK.getId((Object)this.block).toString();
    }

    @Override
    public String getDisplayName() {
        if (this.tile == null || !(this.tile instanceof Nameable)) {
            return this.getName();
        }
        return ((Nameable)this.tile).getDisplayName().getString();
    }

    @Override
    public IWorld getWorld() {
        return this.level;
    }

    @Override
    public Block getMCBlock() {
        return this.block;
    }

    @Deprecated
    public static IBlock createNew(World level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        String key = state.toString() + pos.toString();
        BlockWrapper b = blockCache.get(key);
        if (b != null) {
            b.setTile(level.getBlockEntity(pos));
            return b;
        }
        b = block instanceof BlockScripted ? new BlockScriptedWrapper(level, block, pos) : (block instanceof BlockScriptedDoor ? new BlockScriptedDoorWrapper(level, block, pos) : new BlockWrapper(level, block, pos));
        blockCache.put(key, b);
        return b;
    }

    public static void clearCache() {
        blockCache.clear();
    }

    @Override
    public boolean hasTileEntity() {
        return this.tile != null;
    }

    protected void setTile(BlockEntity tile) {
        this.tile = tile;
        if (tile instanceof TileNpcEntity) {
            this.storage = (TileNpcEntity)tile;
        }
    }

    @Override
    public INbt getBlockEntityNBT() {
        NbtCompound compound = this.tile.createNbt();
        return NpcAPI.Instance().getINbt(compound);
    }

    @Override
    public void setTileEntityNBT(INbt nbt) {
        this.tile.readNbt(nbt.getMCNBT());
        this.tile.markDirty();
        BlockState state = this.level.getMCLevel().getBlockState(this.pos);
        this.level.getMCLevel().updateListeners(this.pos, state, state, 3);
    }

    @Override
    public BlockEntity getMCTileEntity() {
        return this.tile;
    }

    @Override
    public BlockState getMCBlockState() {
        return this.level.getMCLevel().getBlockState(this.pos);
    }

    @Override
    public void blockEvent(int type, int data) {
        this.level.getMCLevel().addSyncedBlockEvent(this.pos, this.getMCBlock(), type, data);
    }

    @Override
    public void interact(int side) {
        FakePlayer player = EntityNPCInterface.GenericPlayer;
        ServerWorld w = this.level.getMCLevel();
        ((EntityIMixin)player).setLevel((World)w);
        player.setPosition((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ());
        this.getMCBlockState().onUse((World)w, (PlayerEntity)EntityNPCInterface.CommandPlayer, Hand.field_5808, new BlockHitResult(Vec3d.ZERO, Direction.byId((int)side), this.pos, true));
    }
}

