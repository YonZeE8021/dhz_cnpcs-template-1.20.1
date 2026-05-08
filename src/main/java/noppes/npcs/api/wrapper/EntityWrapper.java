/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.Language
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.AbstractNbtNumber
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.text.Text
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket
 *  net.minecraft.util.Identifier
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.registry.tag.BlockTags
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.entry.RegistryEntry$Reference
 *  net.minecraft.registry.RegistryKeys
 */
package noppes.npcs.api.wrapper;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Language;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.RegistryKeys;
import noppes.npcs.NpcDamageSource;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.INbt;
import noppes.npcs.api.IPos;
import noppes.npcs.api.IRayTrace;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityItem;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.BlockPosWrapper;
import noppes.npcs.api.wrapper.RayTraceWrapper;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.entity.data.IEntityPersistentData;
import noppes.npcs.mixin.EntityIMixin;

public class EntityWrapper<T extends Entity>
implements IEntity {
    protected T entity;
    private Map<String, Object> tempData = new HashMap<String, Object>();
    private IWorld levelWrapper;
    private final IData tempdata = new IData(){

        @Override
        public void put(String key, Object value) {
            EntityWrapper.this.tempData.put(key, value);
        }

        @Override
        public Object get(String key) {
            return EntityWrapper.this.tempData.get(key);
        }

        @Override
        public void remove(String key) {
            EntityWrapper.this.tempData.remove(key);
        }

        @Override
        public boolean has(String key) {
            return EntityWrapper.this.tempData.containsKey(key);
        }

        @Override
        public void clear() {
            EntityWrapper.this.tempData.clear();
        }

        @Override
        public String[] getKeys() {
            return EntityWrapper.this.tempData.keySet().toArray(new String[EntityWrapper.this.tempData.size()]);
        }
    };
    private final IData storeddata = new IData(){

        @Override
        public void put(String key, Object value) {
            NbtCompound compound = this.getStoredCompound();
            if (value instanceof Number) {
                compound.putDouble(key, ((Number)value).doubleValue());
            } else if (value instanceof String) {
                compound.putString(key, (String)value);
            }
            this.saveStoredCompound(compound);
        }

        @Override
        public Object get(String key) {
            NbtCompound compound = this.getStoredCompound();
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
            NbtCompound compound = this.getStoredCompound();
            compound.remove(key);
            this.saveStoredCompound(compound);
        }

        @Override
        public boolean has(String key) {
            return this.getStoredCompound().contains(key);
        }

        @Override
        public void clear() {
            ((IEntityPersistentData)EntityWrapper.this.entity).getPersistentData().remove("CNPCStoredData");
        }

        private NbtCompound getStoredCompound() {
            NbtCompound compound = ((IEntityPersistentData)EntityWrapper.this.entity).getPersistentData().getCompound("CNPCStoredData");
            if (compound == null) {
                compound = new NbtCompound();
                ((IEntityPersistentData)EntityWrapper.this.entity).getPersistentData().put("CNPCStoredData", (NbtElement)compound);
            }
            return compound;
        }

        private void saveStoredCompound(NbtCompound compound) {
            ((IEntityPersistentData)EntityWrapper.this.entity).getPersistentData().put("CNPCStoredData", (NbtElement)compound);
        }

        @Override
        public String[] getKeys() {
            NbtCompound compound = this.getStoredCompound();
            return compound.getKeys().toArray(new String[compound.getKeys().size()]);
        }
    };

    public EntityWrapper(T entity) {
        this.entity = entity;
        this.levelWrapper = NpcAPI.Instance().getIWorld((ServerWorld)entity.getWorld());
    }

    @Override
    public double getX() {
        return this.entity.getX();
    }

    @Override
    public void setX(double x) {
        this.entity.setPosition(x, this.entity.getY(), this.entity.getZ());
    }

    @Override
    public double getY() {
        return this.entity.getY();
    }

    @Override
    public void setY(double y) {
        this.entity.setPosition(this.entity.getX(), y, this.entity.getZ());
    }

    @Override
    public double getZ() {
        return this.entity.getZ();
    }

    @Override
    public void setZ(double z) {
        this.entity.setPosition(this.entity.getX(), this.entity.getY(), z);
    }

    @Override
    public int getBlockX() {
        return MathHelper.floor((double)this.entity.getX());
    }

    @Override
    public int getBlockY() {
        return MathHelper.floor((double)this.entity.getY());
    }

    @Override
    public int getBlockZ() {
        return MathHelper.floor((double)this.entity.getZ());
    }

    @Override
    public String getEntityName() {
        String s = this.entity.getType().getTranslationKey();
        return Language.getInstance().get(s);
    }

    @Override
    public String getName() {
        return this.entity.getName().getString();
    }

    @Override
    public void setName(String name) {
        this.entity.setCustomName((Text)Text.literal((String)name));
    }

    @Override
    public boolean hasCustomName() {
        return this.entity.hasCustomName();
    }

    @Override
    public void setPosition(double x, double y, double z) {
        this.entity.setPosition(x, y, z);
    }

    @Override
    public IWorld getWorld() {
        if (this.entity.getWorld() != this.levelWrapper.getMCLevel()) {
            this.levelWrapper = NpcAPI.Instance().getIWorld((ServerWorld)this.entity.getWorld());
        }
        return this.levelWrapper;
    }

    @Override
    public boolean isAlive() {
        return this.entity.isAlive();
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
    public long getAge() {
        return ((Entity)this.entity).age;
    }

    @Override
    public void damage(float amount) {
        if (this.getType() == 1 && (((IPlayer)(this)).getGamemode() == 1 || ((IPlayer)(this)).getGamemode() == 3)) {
            return;
        }
        this.entity.damage(this.entity.getDamageSources().genericKill(), amount);
    }

    @Override
    public void damage(float damage, IEntity source) {
        if (source.getMCEntity() instanceof PlayerEntity) {
            this.entity.damage(this.entity.getDamageSources().playerAttack((PlayerEntity)source.getMCEntity()), damage);
        } else {
            RegistryEntry.Reference damageTypeHolder = this.entity.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(NpcDamageSource.NPC);
            this.entity.damage(new DamageSource((RegistryEntry)damageTypeHolder, source.getMCEntity()), damage);
        }
    }

    @Override
    public void despawn() {
        this.entity.discard();
    }

    @Override
    public void spawn() {
        if (this.levelWrapper.getMCLevel().getEntity(this.entity.getUuid()) != null) {
            throw new CustomNPCsException("Entity is already spawned", new Object[0]);
        }
        ((EntityIMixin)this.entity).removal(null);
        this.levelWrapper.getMCLevel().spawnEntity(this.entity);
    }

    @Override
    public void kill() {
        this.entity.kill();
    }

    @Override
    public boolean inWater() {
        return this.entity.isTouchingWater();
    }

    @Override
    public boolean inLava() {
        return this.entity.isInLava();
    }

    @Override
    public boolean inFire() {
        return this.entity.getWorld().getStatesInBox(this.entity.getBoundingBox()).anyMatch(state -> state.isIn(BlockTags.FIRE));
    }

    @Override
    public boolean isBurning() {
        return this.entity.isOnFire();
    }

    @Override
    public void setBurning(int ticks) {
        this.entity.setFireTicks(ticks);
    }

    @Override
    public void extinguish() {
        this.entity.extinguish();
    }

    @Override
    public String getTypeName() {
        return this.entity.getSavedEntityId();
    }

    @Override
    public IEntityItem dropItem(IItemStack item) {
        return (IEntityItem)NpcAPI.Instance().getIEntity((Entity)this.entity.dropStack(item.getMCItemStack(), 0.0f));
    }

    @Override
    public IEntity[] getRiders() {
        List list = this.entity.getPassengerList();
        IEntity[] riders = new IEntity[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            riders[i] = NpcAPI.Instance().getIEntity((Entity)list.get(i));
        }
        return riders;
    }

    @Override
    public IRayTrace rayTraceBlock(double distance, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox) {
        Vec3d vec3d = this.entity.getCameraPosVec(1.0f);
        Vec3d vec3d1 = this.entity.getRotationVec(1.0f);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
        BlockHitResult result = this.entity.getWorld().raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.OUTLINE, stopOnLiquid ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, this.entity));
        if (result.getType() == HitResult.Type.MISS) {
            return null;
        }
        BlockHitResult br = result;
        return new RayTraceWrapper(NpcAPI.Instance().getIBlock(this.entity.getWorld(), br.getBlockPos()), br.getSide().getId());
    }

    @Override
    public IEntity[] rayTraceEntities(double distance, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox) {
        Vec3d vec3d = this.entity.getCameraPosVec(1.0f);
        Vec3d vec3d1 = this.entity.getRotationVec(1.0f);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
        BlockHitResult result = this.entity.getWorld().raycast(new RaycastContext(vec3d, vec3d2, RaycastContext.ShapeType.COLLIDER, stopOnLiquid ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, this.entity));
        if (result.getType() != HitResult.Type.MISS) {
            vec3d2 = result.getPos();
        }
        return this.findEntityOnPath(distance, vec3d, vec3d2);
    }

    private IEntity[] findEntityOnPath(double distance, Vec3d vec3d, Vec3d vec3d1) {
        List<Entity> list = this.entity.getWorld().getOtherEntities(this.entity, this.entity.getBoundingBox().expand(distance));
        ArrayList<IEntity> result = new ArrayList<IEntity>();
        for (Entity entity1 : list) {
            Box axisalignedbb;
            Optional optional;
            if (entity1 == this.entity || !(optional = (axisalignedbb = entity1.getBoundingBox().expand((double)entity1.getTargetingMargin())).raycast(vec3d, vec3d1)).isPresent()) continue;
            result.add(NpcAPI.Instance().getIEntity(entity1));
        }
        result.sort((o1, o2) -> {
            double d2;
            double d1 = this.entity.squaredDistanceTo(o1.getMCEntity());
            if (d1 == (d2 = this.entity.squaredDistanceTo(o2.getMCEntity()))) {
                return 0;
            }
            return d1 > d2 ? 1 : -1;
        });
        return result.toArray(new IEntity[result.size()]);
    }

    @Override
    public IEntity[] getAllRiders() {
        ImmutableList list = ImmutableList.copyOf((Iterable)this.entity.getPassengersDeep());
        IEntity[] riders = new IEntity[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            riders[i] = NpcAPI.Instance().getIEntity((Entity)list.get(i));
        }
        return riders;
    }

    @Override
    public void addRider(IEntity entity) {
        if (entity != null) {
            entity.getMCEntity().startRiding(this.entity, true);
        }
    }

    @Override
    public void clearRiders() {
        this.entity.removeAllPassengers();
    }

    @Override
    public IEntity getMount() {
        return NpcAPI.Instance().getIEntity(this.entity.getVehicle());
    }

    @Override
    public void setMount(IEntity entity) {
        if (entity == null) {
            this.entity.stopRiding();
        } else {
            this.entity.startRiding(entity.getMCEntity(), true);
        }
    }

    @Override
    public void setRotation(float rotation) {
        this.entity.setYaw(rotation);
    }

    @Override
    public float getRotation() {
        return this.entity.getYaw();
    }

    @Override
    public void setPitch(float rotation) {
        this.entity.setPitch(rotation);
    }

    @Override
    public float getPitch() {
        return this.entity.getPitch();
    }

    @Override
    public void knockback(int power, float direction) {
        float v = direction * (float)Math.PI / 180.0f;
        this.entity.addVelocity((double)(-MathHelper.sin((float)v) * (float)power), 0.1 + (double)((float)power * 0.04f), (double)(MathHelper.cos((float)v) * (float)power));
        this.entity.setVelocity(this.entity.getVelocity().multiply(0.6, 1.0, 0.6));
        ((Entity)this.entity).velocityModified = true;
    }

    @Override
    public boolean isSneaking() {
        return this.entity.isInSneakingPose();
    }

    @Override
    public boolean isSprinting() {
        return this.entity.isSprinting();
    }

    @Override
    public T getMCEntity() {
        return this.entity;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public boolean typeOf(int type) {
        return type == this.getType();
    }

    @Override
    public String getUUID() {
        return this.entity.getUuid().toString();
    }

    @Override
    public String generateNewUUID() {
        UUID id = UUID.randomUUID();
        this.entity.setUuid(id);
        return id.toString();
    }

    @Override
    public INbt getNbt() {
        return NpcAPI.Instance().getINbt(((IEntityPersistentData)this.entity).getPersistentData());
    }

    @Override
    public void storeAsClone(int tab, String name) {
        NbtCompound compound = new NbtCompound();
        if (!this.entity.saveSelfNbt(compound)) {
            throw new CustomNPCsException("Cannot store dead entities", new Object[0]);
        }
        ServerCloneController.Instance.addClone(compound, name, tab);
    }

    @Override
    public INbt getEntityNbt() {
        NbtCompound compound = new NbtCompound();
        this.entity.writeNbt(compound);
        Identifier resourcelocation = EntityType.getId((EntityType)this.entity.getType());
        if (this.getType() == 1) {
            resourcelocation = new Identifier("player");
        }
        if (resourcelocation != null) {
            compound.putString("id", resourcelocation.toString());
        }
        return NpcAPI.Instance().getINbt(compound);
    }

    @Override
    public void setEntityNbt(INbt nbt) {
        this.entity.readNbt(nbt.getMCNBT());
    }

    @Override
    public void playAnimation(int type) {
        this.levelWrapper.getMCLevel().getChunkManager().sendToNearbyPlayers(this.entity, (Packet)new EntityAnimationS2CPacket(this.entity, type));
    }

    @Override
    public float getHeight() {
        return this.entity.getHeight();
    }

    @Override
    public float getEyeHeight() {
        return this.entity.getStandingEyeHeight();
    }

    @Override
    public float getWidth() {
        return this.entity.getWidth();
    }

    @Override
    public IPos getPos() {
        return new BlockPosWrapper(this.entity.getBlockPos());
    }

    @Override
    public void setPos(IPos pos) {
        this.entity.setPosition((double)((float)pos.getX() + 0.5f), (double)pos.getY(), (double)((float)pos.getZ() + 0.5f));
    }

    @Override
    public String[] getTags() {
        return this.entity.getCommandTags().toArray(new String[this.entity.getCommandTags().size()]);
    }

    @Override
    public void addTag(String tag) {
        this.entity.addCommandTag(tag);
    }

    @Override
    public boolean hasTag(String tag) {
        return this.entity.getCommandTags().contains(tag);
    }

    @Override
    public void removeTag(String tag) {
        this.entity.removeScoreboardTag(tag);
    }

    @Override
    public double getMotionX() {
        return this.entity.getVelocity().x;
    }

    @Override
    public double getMotionY() {
        return this.entity.getVelocity().y;
    }

    @Override
    public double getMotionZ() {
        return this.entity.getVelocity().z;
    }

    @Override
    public void setMotionX(double motion) {
        Vec3d mo = this.entity.getVelocity();
        if (mo.x == motion) {
            return;
        }
        this.entity.setVelocity(motion, mo.y, mo.z);
        ((Entity)this.entity).velocityModified = true;
    }

    @Override
    public void setMotionY(double motion) {
        Vec3d mo = this.entity.getVelocity();
        if (mo.y == motion) {
            return;
        }
        this.entity.setVelocity(mo.x, motion, mo.z);
        ((Entity)this.entity).velocityModified = true;
    }

    @Override
    public void setMotionZ(double motion) {
        Vec3d mo = this.entity.getVelocity();
        if (mo.z == motion) {
            return;
        }
        this.entity.setVelocity(mo.x, mo.y, motion);
        ((Entity)this.entity).velocityModified = true;
    }
}

