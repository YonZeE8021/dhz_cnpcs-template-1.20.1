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
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.containers.ContainerNPCBankInterface;

public class SlotNpcBankCurrency
extends Slot {
    public ItemStack item = ItemStack.EMPTY;

    public SlotNpcBankCurrency(ContainerNPCBankInterface containerplayer, Inventory iinventory, int i, int j, int k) {
        super(iinventory, i, j, k);
    }

    public int getMaxItemCount() {
        return 64;
    }

    public boolean canInsert(ItemStack itemstack) {
        if (NoppesUtilServer.IsItemStackNull(itemstack)) {
            return false;
        }
        return this.item.getItem() == itemstack.getItem();
    }
}

