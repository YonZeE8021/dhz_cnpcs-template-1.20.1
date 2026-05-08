/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.predicate.entity.EntityPredicates
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.AnimalEntity
 *  net.minecraft.entity.LightningEntity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.mob.HostileEntity
 *  net.minecraft.entity.passive.VillagerEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.PersistentProjectileEntity
 *  net.minecraft.entity.projectile.thrown.ThrownEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.world.World
 *  net.minecraft.world.World$ExplosionSourceType
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleType
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.AbstractNbtNumber
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.server.world.ServerChunkManager
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.text.MutableText
 *  net.minecraft.world.level.ServerWorldProperties
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.api.wrapper;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.text.MutableText;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.Registries;
import noppes.npcs.EventHooks;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IDimension;
import noppes.npcs.api.INbt;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IScoreboard;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.block.IBlock;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.BlockPosWrapper;
import noppes.npcs.api.wrapper.DimensionWrapper;
import noppes.npcs.api.wrapper.ScoreboardWrapper;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityProjectile;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketPlaySound;

public class WorldWrapper
implements IWorld {
    public static Map<String, Object> tempData = new HashMap<String, Object>();
    public ServerWorld level;
    public IDimension dimension;
    private IData tempdata = new IData(){

        @Override
        public void put(String key, Object value) {
            tempData.put(key, value);
        }

        @Override
        public Object get(String key) {
            return tempData.get(key);
        }

        @Override
        public void remove(String key) {
            tempData.remove(key);
        }

        @Override
        public boolean has(String key) {
            return tempData.containsKey(key);
        }

        @Override
        public void clear() {
            tempData.clear();
        }

        @Override
        public String[] getKeys() {
            return tempData.keySet().toArray(new String[tempData.size()]);
        }
    };
    private IData storeddata = new IData(){

        @Override
        public void put(String key, Object value) {
            NbtCompound compound = ScriptController.Instance.compound;
            if (value instanceof Number) {
                compound.putDouble(key, ((Number)value).doubleValue());
            } else if (value instanceof String) {
                compound.putString(key, (String)value);
            }
            ScriptController.Instance.shouldSave = true;
        }

        @Override
        public Object get(String key) {
            NbtCompound compound = ScriptController.Instance.compound;
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
            ScriptController.Instance.compound.remove(key);
            ScriptController.Instance.shouldSave = true;
        }

        @Override
        public boolean has(String key) {
            return ScriptController.Instance.compound.contains(key);
        }

        @Override
        public void clear() {
            ScriptController.Instance.compound = new NbtCompound();
            ScriptController.Instance.shouldSave = true;
        }

        @Override
        public String[] getKeys() {
            return ScriptController.Instance.compound.getKeys().toArray(new String[ScriptController.Instance.compound.getKeys().size()]);
        }
    };

    private WorldWrapper(World level) {
        this.level = (ServerWorld)level;
        this.dimension = new DimensionWrapper(level.getRegistryKey().getValue(), level.getDimension());
    }

    @Override
    public ServerWorld getMCLevel() {
        return this.level;
    }

    @Override
    public IEntity[] getNearbyEntities(int x, int y, int z, int range, int type) {
        return this.getNearbyEntities(new BlockPosWrapper(new BlockPos(x, y, z)), range, type);
    }

    @Override
    public IEntity[] getNearbyEntities(IPos pos, int range, int type) {
        Box bb = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(pos.getMCBlockPos()).expand((double)range, (double)range, (double)range);
        List<Entity> entities = this.level.getNonSpectatingEntities(this.getClassForType(type), bb);
        ArrayList<IEntity> list = new ArrayList<IEntity>();
        for (Entity living : entities) {
            list.add(NpcAPI.Instance().getIEntity(living));
        }
        return list.toArray(new IEntity[list.size()]);
    }

    @Override
    public IEntity[] getAllEntities(int type) {
        List<Entity> entities = this.getEntities(this.getClassForType(type), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR);
        ArrayList<IEntity> list = new ArrayList<IEntity>();
        for (Entity living : entities) {
            list.add(NpcAPI.Instance().getIEntity(living));
        }
        return list.toArray(new IEntity[list.size()]);
    }

    public List<Entity> getEntities(Class<?> entityTypeIn, Predicate<? super Entity> predicateIn) {
        ArrayList<Entity> list = Lists.newArrayList();
        ServerChunkManager serverchunkprovider = this.level.getChunkManager();
        for (Entity entity : this.level.getEntityLookup().iterate()) {
            if (!entityTypeIn.isAssignableFrom(entity.getClass()) || !serverchunkprovider.isChunkLoaded(MathHelper.floor((double)entity.getX()) >> 4, MathHelper.floor((double)entity.getZ()) >> 4) || !predicateIn.test((Entity)entity)) continue;
            list.add(entity);
        }
        return list;
    }

    @Override
    public IEntity getClosestEntity(int x, int y, int z, int range, int type) {
        return this.getClosestEntity(new BlockPosWrapper(new BlockPos(x, y, z)), range, type);
    }

    @Override
    public IEntity getClosestEntity(IPos pos, int range, int type) {
        Box bb = new Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0).offset(pos.getMCBlockPos()).expand((double)range, (double)range, (double)range);
        List<Entity> entities = this.level.getNonSpectatingEntities(this.getClassForType(type), bb);
        double distance = range * range * range;
        Entity entity = null;
        for (Entity e : entities) {
            double r = pos.getMCBlockPos().getSquaredDistance((Vec3i)e.getBlockPos());
            if (entity == null) {
                distance = r;
                entity = e;
                continue;
            }
            if (!(r < distance)) continue;
            distance = r;
            entity = e;
        }
        return NpcAPI.Instance().getIEntity(entity);
    }

    @Override
    public IEntity getEntity(String uuid) {
        try {
            UUID id = UUID.fromString(uuid);
            Entity e = this.level.getEntity(id);
            if (e == null) {
                e = this.level.getPlayerByUuid(id);
            }
            if (e == null) {
                return null;
            }
            return NpcAPI.Instance().getIEntity(e);
        }
        catch (Exception e) {
            throw new CustomNPCsException("Given uuid was invalid " + uuid, new Object[0]);
        }
    }

    @Override
    public IEntity createEntityFromNBT(INbt nbt) {
        Entity entity = EntityType.getEntityFromNbt((NbtCompound)nbt.getMCNBT(), (World)this.level).orElse(null);
        if (entity == null) {
            throw new CustomNPCsException("Failed to create an entity from given NBT", new Object[0]);
        }
        return NpcAPI.Instance().getIEntity(entity);
    }

    @Override
    public IEntity createEntity(String id) {
        Identifier resource = new Identifier(id);
        EntityType type = (EntityType)Registries.ENTITY_TYPE.get(resource);
        Entity entity = type.create((World)this.level);
        if (entity == null) {
            throw new CustomNPCsException("Failed to create an entity from given id: " + id, new Object[0]);
        }
        entity.setPosition(0.0, 1.0, 0.0);
        return NpcAPI.Instance().getIEntity(entity);
    }

    @Override
    public IPlayer getPlayer(String name) {
        for (PlayerEntity entityplayer : this.level.getPlayers()) {
            if (!name.equals(entityplayer.getName().getString())) continue;
            return (IPlayer)NpcAPI.Instance().getIEntity((Entity)entityplayer);
        }
        return null;
    }

    private Class getClassForType(int type) {
        if (type == -1) {
            return Entity.class;
        }
        if (type == 5) {
            return LivingEntity.class;
        }
        if (type == 1) {
            return PlayerEntity.class;
        }
        if (type == 4) {
            return AnimalEntity.class;
        }
        if (type == 3) {
            return HostileEntity.class;
        }
        if (type == 2) {
            return EntityNPCInterface.class;
        }
        if (type == 6) {
            return ItemEntity.class;
        }
        if (type == 7) {
            return EntityProjectile.class;
        }
        if (type == 11) {
            return ThrownEntity.class;
        }
        if (type == 10) {
            return PersistentProjectileEntity.class;
        }
        if (type == 8) {
            return PixelmonHelper.getPixelmonClass();
        }
        if (type == 9) {
            return VillagerEntity.class;
        }
        return Entity.class;
    }

    @Override
    public long getTime() {
        return this.level.getTimeOfDay();
    }

    @Override
    public void setTime(long time) {
        this.level.setTimeOfDay(time);
    }

    @Override
    public long getTotalTime() {
        return this.level.getTime();
    }

    @Override
    public IBlock getBlock(int x, int y, int z) {
        return NpcAPI.Instance().getIBlock((World)this.level, new BlockPos(x, y, z));
    }

    @Override
    public IBlock getBlock(IPos pos) {
        return NpcAPI.Instance().getIBlock((World)this.level, pos.getMCBlockPos());
    }

    public boolean isChunkLoaded(int x, int z) {
        return this.level.getChunkManager().isChunkLoaded(x >> 4, z >> 4);
    }

    @Override
    public void setBlock(int x, int y, int z, String name, int meta) {
        this.setBlock(NpcAPI.Instance().getIPos(x, y, z), name);
    }

    @Override
    public IBlock setBlock(IPos pos, String name) {
        Block block = (Block)Registries.BLOCK.get(new Identifier(name));
        if (block == null) {
            throw new CustomNPCsException("There is no such block: %s", name);
        }
        this.level.setBlockState(pos.getMCBlockPos(), block.getDefaultState(), 2);
        return NpcAPI.Instance().getIBlock((World)this.level, pos.getMCBlockPos());
    }

    @Override
    public void removeBlock(int x, int y, int z) {
        this.level.removeBlock(new BlockPos(x, y, z), false);
    }

    @Override
    public void removeBlock(IPos pos) {
        this.level.removeBlock(pos.getMCBlockPos(), false);
    }

    @Override
    public float getLightValue(int x, int y, int z) {
        return (float)this.level.getLuminance(new BlockPos(x, y, z)) / 16.0f;
    }

    @Override
    public IBlock getSpawnPoint() {
        BlockPos pos = this.level.getSpawnPos();
        if (pos == null) {
            pos = this.level.getSpawnPos();
        }
        return NpcAPI.Instance().getIBlock((World)this.level, pos);
    }

    @Override
    public void setSpawnPoint(IBlock block) {
        ServerWorldProperties info = (ServerWorldProperties)this.level.getLevelProperties();
        info.setSpawnPos(new BlockPos(block.getX(), block.getY(), block.getZ()), 0.0f);
    }

    @Override
    public boolean isDay() {
        return this.level.getTimeOfDay() % 24000L < 12000L;
    }

    @Override
    public boolean isRaining() {
        return this.level.getLevelProperties().isRaining();
    }

    @Override
    public void setRaining(boolean bo) {
        ServerWorldProperties data = (ServerWorldProperties)this.level.getLevelProperties();
        if (bo) {
            data.setRaining(true);
            data.setRainTime(120000000);
        } else {
            data.setRaining(false);
            data.setRainTime(0);
        }
    }

    @Override
    public void thunderStrike(double x, double y, double z) {
        LightningEntity bolt = (LightningEntity)EntityType.LIGHTNING_BOLT.create((World)this.level);
        bolt.refreshPositionAfterTeleport(x, y, z);
        bolt.setCosmetic(false);
        this.level.spawnEntity((Entity)bolt);
    }

    @Override
    public void spawnParticle(String particle, double x, double y, double z, double dx, double dy, double dz, double speed, int count) {
        ParticleType type = (ParticleType)Registries.PARTICLE_TYPE.get(new Identifier(particle));
        if (type == null) {
            throw new CustomNPCsException("Unknown particle type: " + particle, new Object[0]);
        }
        this.level.spawnParticles((ParticleEffect)type, x, y, z, count, dx, dy, dz, speed);
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
    public IItemStack createItem(String name, int size) {
        Item item = (Item)Registries.ITEM.get(new Identifier(name));
        if (item == null) {
            throw new CustomNPCsException("Unknown item id: " + name, new Object[0]);
        }
        return NpcAPI.Instance().getIItemStack(new ItemStack((ItemConvertible)item, size));
    }

    @Override
    public IItemStack createItemFromNbt(INbt nbt) {
        ItemStack item = ItemStack.fromNbt((NbtCompound)nbt.getMCNBT());
        if (item.isEmpty()) {
            throw new CustomNPCsException("Failed to create an item from given NBT", new Object[0]);
        }
        return NpcAPI.Instance().getIItemStack(item);
    }

    @Override
    public void explode(double x, double y, double z, float range, boolean fire, boolean grief) {
        this.level.createExplosion(null, x, y, z, range, fire, grief ? World.ExplosionSourceType.TNT : World.ExplosionSourceType.NONE);
    }

    @Override
    public IPlayer[] getAllPlayers() {
        List list = this.level.getServer().getPlayerManager().getPlayerList();
        IPlayer[] arr = new IPlayer[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            arr[i] = (IPlayer)NpcAPI.Instance().getIEntity((Entity)list.get(i));
        }
        return arr;
    }

    @Override
    public String getBiomeName(int x, int z) {
        try {
            return ((RegistryKey)this.level.getBiome(new BlockPos(x, 0, z)).getKey().get()).getValue().toString();
        }
        catch (Exception e) {
            return "";
        }
    }

    @Override
    public IEntity spawnClone(double x, double y, double z, int tab, String name) {
        return NpcAPI.Instance().getClones().spawn(x, y, z, tab, name, this);
    }

    @Override
    public void spawnEntity(IEntity entity) {
        if (entity == null) {
            throw new CustomNPCsException("Entity given was null", new Object[0]);
        }
        Entity e = (Entity)entity.getMCEntity();
        if (this.level.getEntity(e.getUuid()) != null) {
            throw new CustomNPCsException("Entity with this UUID already exists", new Object[0]);
        }
        e.setPosition(e.getX(), e.getY(), e.getZ());
        this.level.spawnEntity(e);
    }

    @Override
    public IEntity getClone(int tab, String name) {
        return NpcAPI.Instance().getClones().get(tab, name, this);
    }

    @Override
    public IScoreboard getScoreboard() {
        return new ScoreboardWrapper(this.level.getServer());
    }

    @Override
    public void broadcast(String message) {
        MutableText text = Text.literal((String)message);
        for (PlayerEntity p : this.level.getPlayers(e -> true)) {
            p.sendMessage((Text)text);
        }
    }

    @Override
    public int getRedstonePower(int x, int y, int z) {
        return this.level.getReceivedStrongRedstonePower(new BlockPos(x, y, z));
    }

    @Deprecated
    public static WorldWrapper createNew(ServerWorld level) {
        return new WorldWrapper((World)level);
    }

    @Override
    public IDimension getDimension() {
        return this.dimension;
    }

    @Override
    public String getName() {
        return ((ServerWorldProperties)this.level.getLevelProperties()).getLevelName();
    }

    @Override
    public BlockPos getMCBlockPos(int x, int y, int z) {
        return new BlockPos(x, y, z);
    }

    @Override
    public void playSoundAt(IPos pos, String sound, float volume, float pitch) {
        BlockPos bp = pos.getMCBlockPos();
        Packets.sendNearby((World)this.level, bp, 16, new PacketPlaySound(sound, bp, volume, pitch));
    }

    @Override
    public void trigger(int id, Object ... arguments) {
        EventHooks.onScriptTriggerEvent(ScriptController.Instance.forgeScripts, id, this, BlockPosWrapper.ZERO, null, arguments);
    }
}

