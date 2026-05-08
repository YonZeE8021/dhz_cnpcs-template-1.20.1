/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.PathAwareEntity
 *  net.minecraft.world.World
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.entity.EntityDimensions
 *  net.minecraft.entity.EntityPose
 */
package noppes.npcs.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import noppes.npcs.ModelData;
import noppes.npcs.ModelEyeData;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.client.parts.MpmPartData;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.entity.EntityNPCFlying;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityCustomNpc
extends EntityNPCFlying {
    public ModelData modelData = new ModelData(this);

    public EntityCustomNpc(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound compound) {
        if (compound.contains("NpcModelData")) {
            this.modelData.load(compound.getCompound("NpcModelData"));
        }
        super.readCustomDataFromNbt(compound);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        compound.put("NpcModelData", (NbtElement)this.modelData.save());
    }

    public boolean saveSelfNbt(NbtCompound compound) {
        String s;
        boolean bo = super.saveSelfNbt(compound);
        if (bo && (s = this.getSavedEntityId()).equals("minecraft:customnpcs.customnpc")) {
            compound.putString("id", "customnpcs:customnpc");
        }
        return bo;
    }

    @Override
    public void tick() {
        LivingEntity entity;
        super.tick();
        if (this.isClientSide() && (entity = this.modelData.getEntity(this)) != null) {
            try {
                entity.tick();
            }
            catch (Exception exception) {
                // empty catch block
            }
            EntityUtil.Copy((LivingEntity)this, entity);
        }
        for (MpmPartData pd : this.modelData.mpmParts) {
            if (!(pd instanceof ModelEyeData)) continue;
            ((ModelEyeData)pd).update((LivingEntity)this);
        }
    }

    public boolean startRiding(Entity par1Entity, boolean force) {
        boolean b = super.startRiding(par1Entity, force);
        this.calculateDimensions();
        return b;
    }

    public void calculateDimensions() {
        LivingEntity entity = this.modelData.getEntity(this);
        if (entity != null) {
            entity.calculateDimensions();
        }
        super.calculateDimensions();
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pos) {
        LivingEntity entity = this.modelData.getEntity(this);
        if (entity == null) {
            float height = 1.9f - this.modelData.getBodyY() + (this.modelData.getPartConfig((EnumParts)EnumParts.HEAD).scaleY - 1.0f) / 2.0f;
            if (this.baseSize.height != height) {
                this.baseSize = new EntityDimensions(this.baseSize.width, height, false);
            }
            return super.getDimensions(pos);
        }
        EntityDimensions size = entity.getDimensions(pos);
        if (entity instanceof EntityNPCInterface) {
            return size.scaled((float)this.display.getSize() * 0.2f);
        }
        float width = size.width / 5.0f * (float)this.display.getSize();
        float height = size.height / 5.0f * (float)this.display.getSize();
        if (width < 0.1f) {
            width = 0.1f;
        }
        if (height < 0.1f) {
            height = 0.1f;
        }
        if (this.display.getHitboxState() == 1 || this.isKilled() && this.stats.hideKilledBody) {
            width = 1.0E-5f;
        }
        return new EntityDimensions(width, height, false);
    }

    public double getMountedHeightOffset() {
        LivingEntity entity = this.modelData.getEntity(this);
        if (entity != null) {
            return entity.getMountedHeightOffset() / 5.0 * (double)this.display.getSize();
        }
        return super.getMountedHeightOffset();
    }
}

