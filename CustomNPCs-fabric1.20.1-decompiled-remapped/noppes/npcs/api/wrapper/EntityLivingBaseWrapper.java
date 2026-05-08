/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.entity.attribute.EntityAttributes
 */
package noppes.npcs.api.wrapper;

import net.minecraft.util.Hand;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.attribute.EntityAttributes;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.IEntityLiving;
import noppes.npcs.api.entity.data.IMark;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.EntityWrapper;
import noppes.npcs.controllers.data.MarkData;

public class EntityLivingBaseWrapper<T extends LivingEntity>
extends EntityWrapper<T>
implements IEntityLiving {
    public EntityLivingBaseWrapper(T entity) {
        super(entity);
    }

    @Override
    public float getHealth() {
        return ((LivingEntity)this.entity).getHealth();
    }

    @Override
    public void setHealth(float health) {
        ((LivingEntity)this.entity).setHealth(health);
    }

    @Override
    public float getMaxHealth() {
        return ((LivingEntity)this.entity).getMaxHealth();
    }

    @Override
    public void setMaxHealth(float health) {
        if (health < 0.0f) {
            return;
        }
        ((LivingEntity)this.entity).getAttributeInstance(EntityAttributes.field_23716).setBaseValue((double)health);
    }

    @Override
    public boolean isAttacking() {
        return ((LivingEntity)this.entity).getAttacker() != null;
    }

    @Override
    public void setAttackTarget(IEntityLiving living) {
        if (living == null) {
            ((LivingEntity)this.entity).setAttacker(null);
        } else {
            ((LivingEntity)this.entity).setAttacker(living.getMCEntity());
        }
    }

    @Override
    public IEntityLiving getAttackTarget() {
        return (IEntityLiving)NpcAPI.Instance().getIEntity((Entity)((LivingEntity)this.entity).getAttacker());
    }

    @Override
    public IEntityLiving getLastAttacked() {
        return (IEntityLiving)NpcAPI.Instance().getIEntity((Entity)((LivingEntity)this.entity).getAttacking());
    }

    @Override
    public int getLastAttackedTime() {
        return ((LivingEntity)this.entity).getLastAttackTime();
    }

    @Override
    public boolean canSeeEntity(IEntity entity) {
        return ((LivingEntity)this.entity).canSee(entity.getMCEntity());
    }

    @Override
    public void swingMainhand() {
        ((LivingEntity)this.entity).swingHand(Hand.field_5808);
    }

    @Override
    public void swingOffhand() {
        ((LivingEntity)this.entity).swingHand(Hand.field_5810);
    }

    @Override
    public void addPotionEffect(int effect, int duration, int strength, boolean hideParticles) {
        StatusEffect p = StatusEffect.byRawId((int)effect);
        if (p == null) {
            return;
        }
        if (strength < 0) {
            strength = 0;
        } else if (strength > 255) {
            strength = 255;
        }
        if (duration < 0) {
            duration = 0;
        } else if (duration > 1000000) {
            duration = 1000000;
        }
        if (!p.isInstant()) {
            duration *= 20;
        }
        if (duration == 0) {
            ((LivingEntity)this.entity).removeStatusEffect(p);
        } else {
            ((LivingEntity)this.entity).addStatusEffect(new StatusEffectInstance(p, duration, strength, false, hideParticles));
        }
    }

    @Override
    public void clearPotionEffects() {
        ((LivingEntity)this.entity).clearStatusEffects();
    }

    @Override
    public int getPotionEffect(int effect) {
        StatusEffectInstance pf = ((LivingEntity)this.entity).getStatusEffect(StatusEffect.byRawId((int)effect));
        if (pf == null) {
            return -1;
        }
        return pf.getAmplifier();
    }

    @Override
    public IItemStack getMainhandItem() {
        return NpcAPI.Instance().getIItemStack(((LivingEntity)this.entity).getMainHandStack());
    }

    @Override
    public void setMainhandItem(IItemStack item) {
        ((LivingEntity)this.entity).setStackInHand(Hand.field_5808, item == null ? ItemStack.EMPTY : item.getMCItemStack());
    }

    @Override
    public IItemStack getOffhandItem() {
        return NpcAPI.Instance().getIItemStack(((LivingEntity)this.entity).getOffHandStack());
    }

    @Override
    public void setOffhandItem(IItemStack item) {
        ((LivingEntity)this.entity).setStackInHand(Hand.field_5810, item == null ? ItemStack.EMPTY : item.getMCItemStack());
    }

    @Override
    public IItemStack getArmor(int slot) {
        if (slot < 0 || slot > 3) {
            throw new CustomNPCsException("Wrong slot id:" + slot, new Object[0]);
        }
        return NpcAPI.Instance().getIItemStack(((LivingEntity)this.entity).getEquippedStack(this.getSlot(slot)));
    }

    @Override
    public void setArmor(int slot, IItemStack item) {
        if (slot < 0 || slot > 3) {
            throw new CustomNPCsException("Wrong slot id:" + slot, new Object[0]);
        }
        ((LivingEntity)this.entity).equipStack(this.getSlot(slot), item == null ? ItemStack.EMPTY : item.getMCItemStack());
    }

    private EquipmentSlot getSlot(int slot) {
        if (slot == 3) {
            return EquipmentSlot.field_6169;
        }
        if (slot == 2) {
            return EquipmentSlot.field_6174;
        }
        if (slot == 1) {
            return EquipmentSlot.field_6172;
        }
        if (slot == 0) {
            return EquipmentSlot.field_6166;
        }
        return null;
    }

    @Override
    public float getRotation() {
        return ((LivingEntity)this.entity).bodyYaw;
    }

    @Override
    public void setRotation(float rotation) {
        ((LivingEntity)this.entity).bodyYaw = rotation;
    }

    @Override
    public int getType() {
        return 5;
    }

    @Override
    public boolean typeOf(int type) {
        return type == 5 ? true : super.typeOf(type);
    }

    @Override
    public boolean isChild() {
        return ((LivingEntity)this.entity).isBaby();
    }

    @Override
    public IMark addMark(int type) {
        MarkData data = MarkData.get((LivingEntity)this.entity);
        return data.addMark(type);
    }

    @Override
    public void removeMark(IMark mark) {
        MarkData data = MarkData.get((LivingEntity)this.entity);
        data.marks.remove(mark);
        data.syncClients();
    }

    @Override
    public IMark[] getMarks() {
        MarkData data = MarkData.get((LivingEntity)this.entity);
        return data.marks.toArray(new IMark[data.marks.size()]);
    }

    @Override
    public float getMoveForward() {
        return ((LivingEntity)this.entity).forwardSpeed;
    }

    @Override
    public void setMoveForward(float move) {
        ((LivingEntity)this.entity).forwardSpeed = move;
    }

    @Override
    public float getMoveStrafing() {
        return ((LivingEntity)this.entity).sidewaysSpeed;
    }

    @Override
    public void setMoveStrafing(float move) {
        ((LivingEntity)this.entity).sidewaysSpeed = move;
    }

    @Override
    public float getMoveVertical() {
        return ((LivingEntity)this.entity).upwardSpeed;
    }

    @Override
    public void setMoveVertical(float move) {
        ((LivingEntity)this.entity).upwardSpeed = move;
    }
}

