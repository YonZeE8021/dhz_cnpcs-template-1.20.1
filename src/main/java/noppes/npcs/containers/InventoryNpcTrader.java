/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.collection.DefaultedList
 */
package noppes.npcs.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.containers.ContainerNPCTrader;

public class InventoryNpcTrader
implements Inventory {
    private String inventoryTitle;
    private int slotsCount;
    public final DefaultedList<ItemStack> inventoryContents;
    private ContainerNPCTrader con;

    public InventoryNpcTrader(String s, int i, ContainerNPCTrader con) {
        this.con = con;
        this.inventoryTitle = s;
        this.slotsCount = i;
        this.inventoryContents = DefaultedList.ofSize((int)i, ItemStack.EMPTY);
    }

    public ItemStack getStack(int i) {
        ItemStack toBuy = (ItemStack)this.inventoryContents.get(i);
        if (NoppesUtilServer.IsItemStackNull(toBuy)) {
            return ItemStack.EMPTY;
        }
        return toBuy.copy();
    }

    public ItemStack removeStack(int i, int j) {
        ItemStack stack = (ItemStack)this.inventoryContents.get(i);
        if (!NoppesUtilServer.IsItemStackNull(stack)) {
            return stack.copy();
        }
        return ItemStack.EMPTY;
    }

    public void setStack(int i, ItemStack itemstack) {
        if (!itemstack.isEmpty()) {
            this.inventoryContents.set(i, itemstack.copy());
        }
        this.markDirty();
    }

    public int size() {
        return this.slotsCount;
    }

    public int getMaxCountPerStack() {
        return 64;
    }

    public boolean canPlayerUse(PlayerEntity entityplayer) {
        return true;
    }

    public ItemStack removeStack(int i) {
        return (ItemStack)this.inventoryContents.set(i, ItemStack.EMPTY);
    }

    public boolean isValid(int i, ItemStack itemstack) {
        return true;
    }

    public void markDirty() {
        this.con.onContentChanged(this);
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

