/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ArmorItem
 *  net.minecraft.item.ItemStack
 */
package noppes.npcs.api.wrapper;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import noppes.npcs.api.item.IItemArmor;
import noppes.npcs.api.wrapper.ItemStackWrapper;

public class ItemArmorWrapper
extends ItemStackWrapper
implements IItemArmor {
    protected ArmorItem armor;

    protected ItemArmorWrapper(ItemStack item) {
        super(item);
        this.armor = (ArmorItem)item.getItem();
    }

    @Override
    public int getType() {
        return 3;
    }

    @Override
    public int getArmorSlot() {
        return this.armor.getSlotType().getEntitySlotId();
    }

    @Override
    public String getArmorMaterial() {
        return this.armor.getMaterial().getName();
    }
}

