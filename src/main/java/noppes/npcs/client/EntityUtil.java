/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.ChickenEntity
 *  net.minecraft.entity.boss.dragon.EnderDragonEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.World
 *  net.minecraft.entity.data.DataTracker$Entry
 *  net.minecraft.entity.data.DataTracker$SerializedEntry
 *  net.minecraft.util.Identifier
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import noppes.npcs.client.ISynchedEntityData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.EntityIMixin;
import noppes.npcs.mixin.EntityLivingIMixin;
import noppes.npcs.mixin.WalkAnimationStateMixin;
import noppes.npcs.shared.common.util.LogWriter;

public class EntityUtil {
    private static HashMap<EntityType<? extends Entity>, Class> entityClasses = new HashMap();

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void applySerializedEntry(DataTracker tracker, DataTracker.Entry<?> entry) {
        tracker.set((TrackedData)entry.getData(), ((DataTracker.SerializedEntry)entry.get()).value());
    }

    public static void Copy(LivingEntity copied, LivingEntity entity) {
        EntityNPCInterface npc;
        ((EntityIMixin)entity).setLevel(copied.getWorld());
        entity.deathTime = copied.deathTime;
        entity.horizontalSpeed = copied.horizontalSpeed;
        entity.prevHorizontalSpeed = copied.horizontalSpeed;
        entity.distanceTraveled = copied.distanceTraveled;
        entity.forwardSpeed = copied.forwardSpeed;
        entity.sidewaysSpeed = copied.sidewaysSpeed;
        entity.setOnGround(copied.isOnGround());
        entity.fallDistance = copied.fallDistance;
        entity.setJumping(((EntityLivingIMixin)copied).jumping());
        List<DataTracker.Entry<?>> copiedData = ((ISynchedEntityData)copied.getDataTracker()).getAll();
        List<DataTracker.Entry<?>> data = ((ISynchedEntityData)entity.getDataTracker()).getAll();
        for (DataTracker.Entry<?> entry : copiedData) {
            if (!data.stream().anyMatch(e -> e.getData() == entry.getData()) || !(entry.get() instanceof DataTracker.SerializedEntry)) continue;
            applySerializedEntry(entity.getDataTracker(), entry);
        }
        entity.prevX = copied.prevX;
        entity.prevY = copied.prevY;
        entity.prevZ = copied.prevZ;
        entity.setPosition(copied.getX(), copied.getY(), copied.getZ());
        entity.lastRenderX = copied.lastRenderX;
        entity.lastRenderY = copied.lastRenderY;
        entity.lastRenderZ = copied.lastRenderZ;
        entity.setVelocity(copied.getVelocity());
        entity.setPitch(copied.getPitch());
        entity.setYaw(copied.getYaw());
        entity.prevPitch = copied.prevPitch;
        entity.prevYaw = copied.prevYaw;
        entity.headYaw = copied.headYaw;
        entity.prevHeadYaw = copied.prevHeadYaw;
        entity.bodyYaw = copied.bodyYaw;
        entity.prevBodyYaw = copied.prevBodyYaw;
        ((EntityLivingIMixin)entity).useItemRemaining(copied.getItemUseTimeLeft());
        ((WalkAnimationStateMixin)entity.limbAnimator).setPosition(copied.limbAnimator.getPos());
        ((EntityLivingIMixin)entity).animStep(((EntityLivingIMixin)copied).animStep());
        ((EntityLivingIMixin)entity).animStepO(((EntityLivingIMixin)copied).animStepO());
        ((EntityLivingIMixin)entity).swimAmount(((EntityLivingIMixin)copied).swimAmount());
        ((EntityLivingIMixin)entity).swimAmountO(((EntityLivingIMixin)copied).swimAmountO());
        entity.handSwinging = copied.handSwinging;
        entity.handSwingTicks = copied.handSwingTicks;
        entity.limbAnimator.setSpeed(copied.limbAnimator.getSpeed());
        ((WalkAnimationStateMixin)entity.limbAnimator).setSpeedOld(((WalkAnimationStateMixin)copied.limbAnimator).getSpeedOld());
        entity.handSwingProgress = copied.handSwingProgress;
        entity.lastHandSwingProgress = copied.lastHandSwingProgress;
        entity.age = copied.age;
        entity.setHealth(Math.min(copied.getHealth(), entity.getMaxHealth()));
        entity.hurtTime = copied.hurtTime;
        entity.deathTime = copied.deathTime;
        if (entity instanceof PlayerEntity && copied instanceof PlayerEntity) {
            PlayerEntity ePlayer = (PlayerEntity)entity;
            PlayerEntity cPlayer = (PlayerEntity)copied;
            ePlayer.strideDistance = cPlayer.strideDistance;
            ePlayer.prevStrideDistance = cPlayer.prevStrideDistance;
            ePlayer.prevCapeX = cPlayer.prevCapeX;
            ePlayer.prevCapeY = cPlayer.prevCapeY;
            ePlayer.prevCapeZ = cPlayer.prevCapeZ;
            ePlayer.capeX = cPlayer.capeX;
            ePlayer.capeY = cPlayer.capeY;
            ePlayer.capeZ = cPlayer.capeZ;
        }
        if (entity instanceof EnderDragonEntity) {
            entity.setPitch(entity.getPitch() + 180.0f);
        }
        ((EntityIMixin)entity).removal(((EntityIMixin)copied).removal());
        entity.deathTime = copied.deathTime;
        entity.age = copied.age;
        if (entity instanceof EnderDragonEntity) {
            entity.setYaw(entity.getYaw() + 180.0f);
        }
        if (entity instanceof ChickenEntity) {
            ((ChickenEntity)entity).flapProgress = copied.isOnGround() ? 0.0f : 1.0f;
        }
        for (EquipmentSlot class_13042 : EquipmentSlot.values()) {
            entity.equipStack(class_13042, copied.getEquippedStack(class_13042));
        }
        if (copied instanceof EntityNPCInterface && entity instanceof EntityNPCInterface) {
            npc = (EntityNPCInterface)copied;
            EntityNPCInterface target = (EntityNPCInterface)entity;
            target.textureLocation = npc.textureLocation;
            target.textureGlowLocation = npc.textureGlowLocation;
            target.textureCloakLocation = npc.textureCloakLocation;
            target.display = npc.display;
            target.inventory = npc.inventory;
            if (npc.job.getType() == 9) {
                target.job = npc.job;
            }
            if (target.currentAnimation != npc.currentAnimation) {
                target.currentAnimation = npc.currentAnimation;
                npc.calculateDimensions();
            }
            target.setDataWatcher(npc.getDataTracker());
        }
        if (entity instanceof EntityCustomNpc && copied instanceof EntityCustomNpc) {
            npc = (EntityCustomNpc)copied;
            EntityCustomNpc target = (EntityCustomNpc)entity;
            target.modelData = ((EntityCustomNpc)npc).modelData.copy();
            target.modelData.setEntity(null);
        }
    }

    private <T> void setData(LivingEntity entity, List<DataTracker.Entry<T>> copiedData, List<DataTracker.Entry<T>> data) {
        for (DataTracker.Entry entry : copiedData) {
            if (!data.stream().anyMatch(e -> e.getData() == entry.getData())) continue;
            entity.getDataTracker().set(entry.getData(), entry.get());
        }
    }

    public static void setRecentlyHit(LivingEntity entity) {
        ((EntityLivingIMixin)entity).lastHurtByPlayerTime(100);
    }

    public static HashMap<EntityType<? extends Entity>, Class> getAllEntitiesClasses(World level) {
        if (!entityClasses.isEmpty()) {
            return entityClasses;
        }
        HashMap<EntityType<? extends Entity>, Class> data = new HashMap<EntityType<? extends Entity>, Class>();
        for (EntityType ent : Registries.ENTITY_TYPE) {
            try {
                Entity e = ent.create(level);
                if (e == null) continue;
                if (LivingEntity.class.isAssignableFrom(e.getClass())) {
                    data.put((EntityType<? extends Entity>)ent, e.getClass());
                }
                e.discard();
            }
            catch (Exception exception) {}
        }
        entityClasses = data;
        return entityClasses;
    }

    public static HashMap<EntityType<? extends Entity>, Class> getAllEntitiesClassesNoNpcs(World level) {
        HashMap<EntityType<? extends Entity>, Class> data = new HashMap<EntityType<? extends Entity>, Class>(EntityUtil.getAllEntitiesClasses(level));
        Iterator<Map.Entry<EntityType<? extends Entity>, Class>> ita = data.entrySet().iterator();
        while (ita.hasNext()) {
            Map.Entry<EntityType<? extends Entity>, Class> entry = ita.next();
            if (!EntityNPCInterface.class.isAssignableFrom(entry.getValue()) && LivingEntity.class.isAssignableFrom(entry.getValue())) continue;
            ita.remove();
        }
        return data;
    }

    public static HashMap<String, Identifier> getAllEntities(World level, boolean withNpcs) {
        HashMap<String, Identifier> data = new HashMap<String, Identifier>();
        for (EntityType ent : Registries.ENTITY_TYPE) {
            try {
                Entity e = ent.create(level);
                if (e == null) continue;
                if (LivingEntity.class.isAssignableFrom(e.getClass()) && (withNpcs || !EntityNPCInterface.class.isAssignableFrom(e.getClass()))) {
                    data.put(ent.getTranslationKey(), Registries.ENTITY_TYPE.getId(ent));
                }
                e.discard();
            }
            catch (Throwable e) {
                LogWriter.except(e);
            }
        }
        return data;
    }
}

