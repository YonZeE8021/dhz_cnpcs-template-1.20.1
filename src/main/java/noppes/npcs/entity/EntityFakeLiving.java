/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.util.Arm
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package noppes.npcs.entity;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Arm;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import noppes.npcs.CustomEntities;

public class EntityFakeLiving
extends LivingEntity {
    public EntityFakeLiving(World par1Level) {
        super(CustomEntities.entityCustomNpc, par1Level);
    }

    public Iterable<ItemStack> getArmorItems() {
        return null;
    }

    public ItemStack getEquippedStack(EquipmentSlot slotIn) {
        return null;
    }

    public void equipStack(EquipmentSlot slotIn, ItemStack stack) {
    }

    public Arm getMainArm() {
        return null;
    }
}

