/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventories
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.collection.DefaultedList
 */
package noppes.npcs.containers;

import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import noppes.npcs.NoppesUtilServer;

public class InventoryNPC
implements Inventory {
    private String inventoryTitle;
    private int slotsCount;
    public final DefaultedList<ItemStack> inventoryContents;
    private ScreenHandler con;

    public InventoryNPC(String s, int i, ScreenHandler con) {
        this.con = con;
        this.inventoryTitle = s;
        this.slotsCount = i;
        this.inventoryContents = DefaultedList.ofSize((int)i, ItemStack.EMPTY);
    }

    public ItemStack getStack(int i) {
        return (ItemStack)this.inventoryContents.get(i);
    }

    public ItemStack removeStack(int index, int count) {
        return Inventories.splitStack(this.inventoryContents, (int)index, (int)count);
    }

    public void setStack(int index, ItemStack stack) {
        this.inventoryContents.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
    }

    public int size() {
        return this.slotsCount;
    }

    public int getMaxCountPerStack() {
        return 64;
    }

    public boolean canPlayerUse(PlayerEntity entityplayer) {
        return false;
    }

    public ItemStack removeStack(int i) {
        return Inventories.removeStack(this.inventoryContents, (int)i);
    }

    public boolean isValid(int i, ItemStack itemstack) {
        return true;
    }

    public void markDirty() {
        this.con.onContentChanged((Inventory)this);
    }

    public void onOpen(PlayerEntity player) {
    }

    public void onClose(PlayerEntity player) {
    }

    public boolean isEmpty() {
        for (int slot = 0; slot < this.size(); ++slot) {
            ItemStack item = this.getStack(slot);
            if (NoppesUtilServer.IsItemStackNull(item) || item.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public void clear() {
    }
}

