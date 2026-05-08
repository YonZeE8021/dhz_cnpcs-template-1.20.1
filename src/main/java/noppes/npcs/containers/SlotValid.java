/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.ItemStack
 */
package noppes.npcs.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;

public class SlotValid
extends Slot {
    private boolean canPutIn = true;

    public SlotValid(Inventory par1iInventory, int limbSwingAmount, int par3, int par4) {
        super(par1iInventory, limbSwingAmount, par3, par4);
    }

    public SlotValid(Inventory par1iInventory, int limbSwingAmount, int par3, int par4, boolean bo) {
        super(par1iInventory, limbSwingAmount, par3, par4);
        this.canPutIn = bo;
    }

    public boolean canInsert(ItemStack itemstack) {
        return this.canPutIn && this.inventory.isValid(0, itemstack);
    }
}

