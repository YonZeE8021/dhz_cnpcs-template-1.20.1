/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.AnimalEntity
 *  net.minecraft.entity.passive.AbstractHorseEntity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.mob.HostileEntity
 *  net.minecraft.entity.passive.VillagerEntity
 *  net.minecraft.entity.projectile.PersistentProjectileEntity
 *  net.minecraft.entity.projectile.thrown.ThrownEntity
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.Identifier
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.api.wrapper;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.Identifier;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.wrapper.AnimalWrapper;
import noppes.npcs.api.wrapper.ArrowWrapper;
import noppes.npcs.api.wrapper.EntityItemWrapper;
import noppes.npcs.api.wrapper.EntityLivingBaseWrapper;
import noppes.npcs.api.wrapper.EntityLivingWrapper;
import noppes.npcs.api.wrapper.EntityWrapper;
import noppes.npcs.api.wrapper.MonsterWrapper;
import noppes.npcs.api.wrapper.PixelmonWrapper;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.api.wrapper.ProjectileWrapper;
import noppes.npcs.api.wrapper.ThrowableWrapper;
import noppes.npcs.api.wrapper.VillagerWrapper;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.entity.EntityProjectile;

public class WrapperEntityData {
    private static Map<Integer, WrapperEntityData> dataMap = new HashMap<Integer, WrapperEntityData>();
    public IEntity base;
    private static WrapperEntityData backup = new WrapperEntityData(null);
    private static final Identifier key = new Identifier("customnpcs", "entitydata");

    public WrapperEntityData(IEntity base) {
        this.base = base;
    }

    public static IEntity get(Entity entity) {
        if (entity == null || entity.getPos() == Vec3d.ZERO) {
            return null;
        }
        WrapperEntityData data = dataMap.computeIfAbsent(entity.getId(), i -> backup);
        if (data == null || data == backup) {
            return WrapperEntityData.getData((Entity)entity).base;
        }
        return data.base;
    }

    private static WrapperEntityData getData(Entity entity) {
        if (entity == null || entity.getWorld() == null || entity.getWorld().isClient) {
            return null;
        }
        if (entity instanceof ServerPlayerEntity) {
            return new WrapperEntityData(new PlayerWrapper<ServerPlayerEntity>((ServerPlayerEntity)entity));
        }
        if (PixelmonHelper.isPixelmon(entity)) {
            return new WrapperEntityData(new PixelmonWrapper<AbstractHorseEntity>((AbstractHorseEntity)entity));
        }
        if (entity instanceof VillagerEntity) {
            return new WrapperEntityData(new VillagerWrapper<VillagerEntity>((VillagerEntity)entity));
        }
        if (entity instanceof AnimalEntity) {
            return new WrapperEntityData(new AnimalWrapper<AnimalEntity>((AnimalEntity)entity));
        }
        if (entity instanceof HostileEntity) {
            return new WrapperEntityData(new MonsterWrapper<HostileEntity>((HostileEntity)entity));
        }
        if (entity instanceof MobEntity) {
            return new WrapperEntityData(new EntityLivingWrapper<MobEntity>((MobEntity)entity));
        }
        if (entity instanceof LivingEntity) {
            return new WrapperEntityData(new EntityLivingBaseWrapper<LivingEntity>((LivingEntity)entity));
        }
        if (entity instanceof ItemEntity) {
            return new WrapperEntityData(new EntityItemWrapper<ItemEntity>((ItemEntity)entity));
        }
        if (entity instanceof EntityProjectile) {
            return new WrapperEntityData(new ProjectileWrapper<EntityProjectile>((EntityProjectile)entity));
        }
        if (entity instanceof ThrownEntity) {
            return new WrapperEntityData(new ThrowableWrapper<ThrownEntity>((ThrownEntity)entity));
        }
        if (entity instanceof PersistentProjectileEntity) {
            return new WrapperEntityData(new ArrowWrapper<PersistentProjectileEntity>((PersistentProjectileEntity)entity));
        }
        return new WrapperEntityData(new EntityWrapper<Entity>(entity));
    }
}

