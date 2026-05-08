/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  org.apache.commons.lang3.RandomStringUtils
 */
package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntityLiving;
import noppes.npcs.api.entity.data.role.IJobSpawner;
import noppes.npcs.controllers.data.CloneSpawnData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.IEntityPersistentData;
import noppes.npcs.packets.server.SPacketToolMobSpawner;
import noppes.npcs.roles.JobInterface;
import org.apache.commons.lang3.RandomStringUtils;

public class JobSpawner
extends JobInterface
implements IJobSpawner {
    public Map<Integer, CloneSpawnData> data = new HashMap<Integer, CloneSpawnData>();
    private int number = 0;
    public List<LivingEntity> spawned = new ArrayList<LivingEntity>();
    private Map<String, Long> cooldown = new HashMap<String, Long>();
    private String id = RandomStringUtils.random((int)8, (boolean)true, (boolean)true);
    public boolean doesntDie = false;
    public int spawnType = 0;
    public int xOffset = 0;
    public int yOffset = 0;
    public int zOffset = 0;
    private LivingEntity target;
    public boolean despawnOnTargetLost = true;

    public JobSpawner(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public NbtCompound save(NbtCompound compound) {
        compound.put("SpawnerData", (NbtElement)CloneSpawnData.save(this.data));
        compound.putString("SpawnerId", this.id);
        compound.putBoolean("SpawnerDoesntDie", this.doesntDie);
        compound.putInt("SpawnerType", this.spawnType);
        compound.putInt("SpawnerXOffset", this.xOffset);
        compound.putInt("SpawnerYOffset", this.yOffset);
        compound.putInt("SpawnerZOffset", this.zOffset);
        compound.putBoolean("DespawnOnTargetLost", this.despawnOnTargetLost);
        return compound;
    }

    public String getTitle(int slot) {
        CloneSpawnData sd = this.data.get(slot);
        if (sd == null) {
            return "gui.selectnpc";
        }
        return sd.tab + ": " + sd.name;
    }

    @Override
    public void load(NbtCompound compound) {
        this.data = CloneSpawnData.load(compound.getList("SpawnerData", 10));
        this.id = compound.getString("SpawnerId");
        this.doesntDie = compound.getBoolean("SpawnerDoesntDie");
        this.spawnType = compound.getInt("SpawnerType");
        this.xOffset = compound.getInt("SpawnerXOffset");
        this.yOffset = compound.getInt("SpawnerYOffset");
        this.zOffset = compound.getInt("SpawnerZOffset");
        this.despawnOnTargetLost = compound.getBoolean("DespawnOnTargetLost");
    }

    public void setJobCompound(int slot, int tab, String name) {
        this.data.put(slot, new CloneSpawnData(tab, name));
    }

    public void remove(int i) {
        this.data.remove(i);
    }

    @Override
    public void aiUpdateTask() {
        if (this.spawned.isEmpty()) {
            if (this.spawnType == 0 && this.spawnEntity(this.number) == null && !this.doesntDie) {
                this.npc.discard();
            }
            if (this.spawnType == 1) {
                if (this.number >= 6 && !this.doesntDie) {
                    this.npc.discard();
                } else {
                    this.spawnEntity(0);
                    this.spawnEntity(1);
                    this.spawnEntity(2);
                    this.spawnEntity(3);
                    this.spawnEntity(4);
                    this.spawnEntity(5);
                    this.number = 6;
                }
            }
            if (this.spawnType == 2) {
                ArrayList<NbtCompound> list = new ArrayList<NbtCompound>();
                for (CloneSpawnData d : this.data.values()) {
                    NbtCompound c = d.getCompound();
                    if (c == null || !c.contains("id")) continue;
                    list.add(c);
                }
                if (!list.isEmpty()) {
                    NbtCompound compound = (NbtCompound)list.get(this.npc.getRandom().nextInt(list.size()));
                    this.spawnEntity(compound);
                } else if (!this.doesntDie) {
                    this.npc.discard();
                }
            }
        } else {
            this.checkSpawns();
        }
    }

    public void checkSpawns() {
        Iterator<LivingEntity> iterator = this.spawned.iterator();
        while (iterator.hasNext()) {
            LivingEntity spawn = iterator.next();
            if (this.shouldDelete(spawn)) {
                spawn.discard();
                iterator.remove();
                continue;
            }
            this.checkTarget(spawn);
        }
    }

    public void checkTarget(LivingEntity entity) {
        if (entity instanceof MobEntity) {
            MobEntity liv = (MobEntity)entity;
            if (liv.getTarget() == null || this.npc.getRandom().nextInt(100) == 1) {
                liv.setTarget(this.target);
            }
        } else if (entity.getAttacker() == null || this.npc.getRandom().nextInt(100) == 1) {
            entity.setAttacker(this.target);
        }
    }

    public boolean shouldDelete(LivingEntity entity) {
        return !this.npc.isInRange((Entity)entity, 60.0) || entity.isRemoved() || entity.getHealth() <= 0.0f || this.despawnOnTargetLost && this.target == null;
    }

    private LivingEntity getTarget() {
        LivingEntity target = this.getTarget((LivingEntity)this.npc);
        if (target != null) {
            return target;
        }
        for (LivingEntity entity : this.spawned) {
            target = this.getTarget(entity);
            if (target == null) continue;
            return target;
        }
        return null;
    }

    private LivingEntity getTarget(LivingEntity entity) {
        if (entity instanceof MobEntity) {
            this.target = ((MobEntity)entity).getTarget();
            if (this.target != null && !this.target.isRemoved() && this.target.getHealth() > 0.0f) {
                return this.target;
            }
        }
        this.target = entity.getAttacker();
        if (this.target != null && !this.target.isRemoved() && this.target.getHealth() > 0.0f) {
            return this.target;
        }
        return null;
    }

    private void setTarget(LivingEntity base, LivingEntity target) {
        if (base instanceof MobEntity) {
            ((MobEntity)base).setTarget(target);
        } else {
            base.setAttacker(target);
        }
    }

    @Override
    public boolean aiShouldExecute() {
        if (this.data.isEmpty() || this.npc.isKilled()) {
            return false;
        }
        this.target = this.getTarget();
        if (this.npc.getRandom().nextInt(30) == 1 && this.spawned.isEmpty()) {
            this.spawned = this.getNearbySpawned();
        }
        if (!this.spawned.isEmpty()) {
            this.checkSpawns();
        }
        return this.target != null;
    }

    @Override
    public boolean aiContinueExecute() {
        return this.aiShouldExecute();
    }

    @Override
    public void stop() {
        this.reset();
    }

    @Override
    public void aiStartExecuting() {
        this.number = 0;
        for (LivingEntity entity : this.spawned) {
            int i = ((IEntityPersistentData)entity).getPersistentData().getInt("NpcSpawnerNr");
            if (i > this.number) {
                this.number = i;
            }
            this.setTarget(entity, this.npc.getTarget());
        }
    }

    @Override
    public void reset() {
        this.number = 0;
        if (this.spawned.isEmpty()) {
            this.spawned = this.getNearbySpawned();
        }
        this.target = null;
        this.checkSpawns();
    }

    @Override
    public void killed() {
        this.reset();
    }

    private LivingEntity spawnEntity(NbtCompound compound) {
        double z;
        double y;
        if (compound == null || !compound.contains("id")) {
            return null;
        }
        double x = this.npc.getX() + (double)this.xOffset - 0.5 + (double)this.npc.getRandom().nextFloat();
        Entity entity = SPacketToolMobSpawner.spawnClone(compound, x, y = this.npc.getY() + (double)this.yOffset, z = this.npc.getZ() + (double)this.zOffset - 0.5 + (double)this.npc.getRandom().nextFloat(), this.npc.getWorld());
        if (entity == null || !(entity instanceof LivingEntity)) {
            return null;
        }
        LivingEntity living = (LivingEntity)entity;
        ((IEntityPersistentData)living).getPersistentData().putString("NpcSpawnerId", this.id);
        ((IEntityPersistentData)living).getPersistentData().putInt("NpcSpawnerNr", this.number);
        this.setTarget(living, this.npc.getTarget());
        living.setPosition(x, y, z);
        if (living instanceof EntityNPCInterface) {
            EntityNPCInterface snpc = (EntityNPCInterface)living;
            snpc.stats.spawnCycle = 4;
            snpc.stats.respawnTime = 0;
            snpc.ais.returnToStart = false;
        }
        this.spawned.add(living);
        return living;
    }

    private NbtCompound getCompound(int i) {
        for (Map.Entry<Integer, CloneSpawnData> entry : this.data.entrySet()) {
            NbtCompound compound;
            if (i > entry.getKey() || (compound = entry.getValue().getCompound()) == null || !compound.contains("id")) continue;
            this.number = entry.getKey() + 1;
            return compound;
        }
        return null;
    }

    private List<LivingEntity> getNearbySpawned() {
        ArrayList<LivingEntity> spawnList = new ArrayList<LivingEntity>();
        List list = this.npc.getWorld().getNonSpectatingEntities(LivingEntity.class, this.npc.getBoundingBox().expand(40.0, 40.0, 40.0));
        for (LivingEntity entity : list) {
            if (!((IEntityPersistentData)entity).getPersistentData().getString("NpcSpawnerId").equals(this.id) || entity.isRemoved()) continue;
            spawnList.add(entity);
        }
        return spawnList;
    }

    public boolean isOnCooldown(String name) {
        if (!this.cooldown.containsKey(name)) {
            return false;
        }
        long time = this.cooldown.get(name);
        return System.currentTimeMillis() < time + 1200000L;
    }

    @Override
    public IEntityLiving spawnEntity(int i) {
        NbtCompound compound = this.getCompound(i);
        if (compound == null) {
            return null;
        }
        LivingEntity base = this.spawnEntity(compound);
        if (base == null) {
            return null;
        }
        return (IEntityLiving)NpcAPI.Instance().getIEntity((Entity)base);
    }

    @Override
    public void removeAllSpawned() {
        for (LivingEntity entity : this.spawned) {
            entity.discard();
        }
        this.spawned = new ArrayList<LivingEntity>();
    }

    @Override
    public int getType() {
        return 6;
    }
}

