/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.util.Identifier
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.registry.Registries
 */
package noppes.npcs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registries;
import noppes.npcs.ModelDataShared;
import noppes.npcs.controllers.CobblemonHelper;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.util.LogWriter;

public class ModelData
extends ModelDataShared {
    public boolean simpleRender = false;
    public EntityCustomNpc npc;

    public ModelData(EntityCustomNpc npc) {
        this.npc = npc;
    }

    public LivingEntity getEntity(EntityNPCInterface npc) {
        if (!this.hasEntity()) {
            return null;
        }
        if (this.entity == null) {
            try {
                this.entity = (LivingEntity)((EntityType)Registries.ENTITY_TYPE.get(this.getEntityName())).create(npc.getWorld());
                NbtCompound comp = new NbtCompound();
                this.entity.writeCustomDataToNbt(comp);
                if (PixelmonHelper.isPixelmon((Entity)this.entity) && !this.extra.contains("Name")) {
                    this.extra.putString("Name", "abra");
                }
                comp = comp.copyFrom(this.extra);
                try {
                    this.entity.readCustomDataFromNbt(comp);
                    if (PixelmonHelper.isPixelmon((Entity)this.entity)) {
                        PixelmonHelper.initEntity(this.entity, this.extra.getString("Name"));
                    }
                    if (CobblemonHelper.isPokemon((Entity)this.entity)) {
                        CobblemonHelper.setType((Entity)this.entity, new Identifier(this.extra.getString("CobblemonModel")));
                    }
                }
                catch (Exception e) {
                    LogWriter.except(e);
                }
                this.entity.setInvulnerable(true);
                this.entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue((double)npc.getMaxHealth());
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    this.entity.equipStack(slot, npc.getEquippedStack(slot));
                }
            }
            catch (Exception e) {
                LogWriter.except(e);
            }
        }
        return this.entity;
    }

    public ModelData copy() {
        ModelData data = new ModelData(this.npc);
        data.load(this.save());
        return data;
    }

    @Override
    public NbtCompound save() {
        NbtCompound compound = super.save();
        compound.putBoolean("SimpleRender", this.simpleRender);
        return compound;
    }

    @Override
    public void load(NbtCompound compound) {
        super.load(compound);
        this.simpleRender = compound.getBoolean("SimpleRender");
    }

    public void setExtra(LivingEntity entity, String key, String value) {
        if ((key = key.toLowerCase()).equalsIgnoreCase("name") && PixelmonHelper.isPixelmon((Entity)entity)) {
            this.extra.putString("Name", value);
        }
        if (key.equalsIgnoreCase("cobblemonmodel") && CobblemonHelper.isPokemon((Entity)entity)) {
            this.extra.putString("CobblemonModel", value);
        }
        this.clearEntity();
    }

    @Override
    public LivingEntity getOwner() {
        return this.npc;
    }

    public static ModelData get(EntityCustomNpc npc) {
        return npc.modelData;
    }
}

