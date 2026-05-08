/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.world.World
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.entity.EntityDimensions
 *  net.minecraft.entity.EntityPose
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.registry.Registries;
import noppes.npcs.CustomEntities;
import noppes.npcs.ModelData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityNPCGolem
extends EntityNPCInterface {
    public EntityNPCGolem(EntityType<? extends EntityNPCInterface> type, World world) {
        super(type, world);
        this.display.setSkinTexture("customnpcs:textures/entity/golem/irongolem.png");
        this.baseSize = new EntityDimensions(1.4f, 2.5f, false);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pos) {
        this.currentAnimation = (Integer)this.dataTracker.get(Animation);
        if (this.currentAnimation == 2) {
            return new EntityDimensions(0.5f, 0.5f, false);
        }
        if (this.currentAnimation == 1) {
            return new EntityDimensions(1.4f, 2.0f, false);
        }
        return new EntityDimensions(1.4f, 2.5f, false);
    }

    @Override
    public void tick() {
        this.discard();
        this.setAiDisabled(true);
        if (!this.getWorld().isClient) {
            NbtCompound compound = new NbtCompound();
            this.writeCustomDataToNbt(compound);
            EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, this.getWorld());
            npc.readCustomDataFromNbt(compound);
            ModelData data = npc.modelData;
            data.setEntity(Registries.ENTITY_TYPE.getId(CustomEntities.entityNPCGolem));
            this.getWorld().spawnEntity((Entity)npc);
        }
        super.tick();
    }
}

