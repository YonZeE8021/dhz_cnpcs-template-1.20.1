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

public class EntityNpcSlime
extends EntityNPCInterface {
    public EntityNpcSlime(EntityType<? extends EntityNPCInterface> type, World world) {
        super(type, world);
        this.scaleX = 2.0f;
        this.scaleY = 2.0f;
        this.scaleZ = 2.0f;
        this.display.setSkinTexture("customnpcs:textures/entity/slime/slime.png");
        this.baseSize = new EntityDimensions(0.8f, 0.8f, false);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pos) {
        return new EntityDimensions(0.8f, 0.8f, false);
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
            data.setEntity(Registries.ENTITY_TYPE.getId(CustomEntities.entityNpcSlime));
            this.getWorld().spawnEntity((Entity)npc);
        }
        super.tick();
    }
}

