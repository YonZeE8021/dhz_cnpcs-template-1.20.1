/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package noppes.npcs.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import noppes.npcs.roles.RoleFollower;

class SlotNpcMercenaryCurrency
extends Slot {
    RoleFollower role;

    public SlotNpcMercenaryCurrency(RoleFollower role, Inventory inv, int i, int j, int k) {
        super(inv, i, j, k);
        this.role = role;
    }

    public int getMaxItemCount() {
        return 64;
    }

    public boolean canInsert(ItemStack itemstack) {
        Item item = itemstack.getItem();
        for (ItemStack is : this.role.inventory.items) {
            if (item != is.getItem()) continue;
            return true;
        }
        return false;
    }
}

