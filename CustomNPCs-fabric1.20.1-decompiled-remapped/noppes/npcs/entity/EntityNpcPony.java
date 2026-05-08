/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.world.World
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.util.Identifier
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import noppes.npcs.CustomEntities;
import noppes.npcs.ModelData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityNpcPony
extends EntityNPCInterface {
    public boolean isPegasus = false;
    public boolean isUnicorn = false;
    public boolean isFlying = false;
    public Identifier checked = null;

    public EntityNpcPony(EntityType<? extends EntityNPCInterface> type, World world) {
        super(type, world);
        this.display.setSkinTexture("customnpcs:textures/entity/ponies/minelpderpyhooves.png");
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
            data.setEntity(Registries.ENTITY_TYPE.getId(CustomEntities.entityNpcPony));
            this.getWorld().spawnEntity((Entity)npc);
        }
        super.tick();
    }
}

