/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.item.ItemStack
 */
package noppes.npcs.api.wrapper;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;

public class ContainerWrapper
implements IContainer {
    private Inventory inventory;
    private ScreenHandler container;

    public ContainerWrapper(Inventory inventory) {
        this.inventory = inventory;
    }

    public ContainerWrapper(ScreenHandler container) {
        this.container = container;
    }

    @Override
    public int getSize() {
        if (this.inventory != null) {
            return this.inventory.size();
        }
        return this.container.slots.size();
    }

    @Override
    public IItemStack getSlot(int slot) {
        if (slot < 0 || slot >= this.getSize()) {
            throw new CustomNPCsException("Slot is out of range " + slot, new Object[0]);
        }
        if (this.inventory != null) {
            return NpcAPI.Instance().getIItemStack(this.inventory.getStack(slot));
        }
        return NpcAPI.Instance().getIItemStack(this.container.getSlot(slot).getStack());
    }

    @Override
    public void setSlot(int slot, IItemStack item) {
        ItemStack itemstack;
        if (slot < 0 || slot >= this.getSize()) {
            throw new CustomNPCsException("Slot is out of range " + slot, new Object[0]);
        }
        ItemStack class_17992 = itemstack = item == null ? ItemStack.EMPTY : item.getMCItemStack();
        if (this.inventory != null) {
            this.inventory.setStack(slot, itemstack);
        } else {
            this.container.setStackInSlot(slot, this.container.getRevision(), itemstack);
            this.container.sendContentUpdates();
        }
    }

    @Override
    public int count(IItemStack item, boolean ignoreDamage, boolean ignoreNBT) {
        int count = 0;
        for (int i = 0; i < this.getSize(); ++i) {
            IItemStack toCompare = this.getSlot(i);
            if (!NoppesUtilPlayer.compareItems(item.getMCItemStack(), toCompare.getMCItemStack(), ignoreDamage, ignoreNBT)) continue;
            count += toCompare.getStackSize();
        }
        return count;
    }

    @Override
    public Inventory getMCInventory() {
        return this.inventory;
    }

    @Override
    public ScreenHandler getMCContainer() {
        return this.container;
    }

    @Override
    public IItemStack[] getItems() {
        IItemStack[] items = new IItemStack[this.getSize()];
        for (int i = 0; i < this.getSize(); ++i) {
            items[i] = this.getSlot(i);
        }
        return items;
    }
}

