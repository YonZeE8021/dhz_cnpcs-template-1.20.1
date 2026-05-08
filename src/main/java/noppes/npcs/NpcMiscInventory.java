/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventories
 *  net.minecraft.inventory.SimpleInventory
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.collection.DefaultedList
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs;

import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;

public class NpcMiscInventory
extends SimpleInventory {
    public final DefaultedList<ItemStack> items;
    public int stackLimit = 64;
    private int size;

    public NpcMiscInventory(int size) {
        super(new ItemStack[0]);
        this.size = size;
        this.items = DefaultedList.ofSize((int)size, ItemStack.EMPTY);
    }

    public NbtCompound getToNBT() {
        NbtCompound nbttagcompound = new NbtCompound();
        nbttagcompound.put("NpcMiscInv", (NbtElement)NBTTags.nbtItemStackList(this.items));
        return nbttagcompound;
    }

    public void setFromNBT(NbtCompound nbttagcompound) {
        NBTTags.getItemStackList(nbttagcompound.getList("NpcMiscInv", 10), this.items);
    }

    public int size() {
        return this.size;
    }

    public ItemStack getStack(int index) {
        return (ItemStack)this.items.get(index);
    }

    public ItemStack removeStack(int index, int count) {
        return Inventories.splitStack(this.items, (int)index, (int)count);
    }

    public boolean removeItem(ItemStack eating, int decrease) {
        for (int slot = 0; slot < this.items.size(); ++slot) {
            ItemStack item = (ItemStack)this.items.get(slot);
            if (item.isEmpty() || eating != item || item.getCount() < decrease) continue;
            item.split(decrease);
            if (item.getCount() <= 0) {
                this.items.set(slot, ItemStack.EMPTY);
            }
            return true;
        }
        return false;
    }

    public ItemStack removeStack(int var1) {
        return (ItemStack)this.items.set(var1, ItemStack.EMPTY);
    }

    public void setStack(int var1, ItemStack var2) {
        if (var1 >= this.size()) {
            return;
        }
        this.items.set(var1, var2);
    }

    public int getMaxCountPerStack() {
        return this.stackLimit;
    }

    public boolean canPlayerUse(PlayerEntity var1) {
        return true;
    }

    public boolean isValid(int i, ItemStack itemstack) {
        return true;
    }

    public void markDirty() {
    }

    public boolean addItemStack(ItemStack item) {
        ItemStack mergable;
        boolean merged = false;
        while (!(mergable = this.getMergableItem(item)).isEmpty() && mergable.getCount() > 0) {
            int size = mergable.getMaxCount() - mergable.getCount();
            if (size > item.getCount()) {
                mergable.setCount(mergable.getMaxCount());
                item.setCount(item.getCount() - size);
                merged = true;
                continue;
            }
            mergable.setCount(mergable.getCount() + item.getCount());
            item.setCount(0);
        }
        if (item.getCount() <= 0) {
            return true;
        }
        int slot = this.firstFreeSlot();
        if (slot >= 0) {
            this.items.set(slot, item.copy());
            item.setCount(0);
            return true;
        }
        return merged;
    }

    public ItemStack getMergableItem(ItemStack item) {
        for (ItemStack is : this.items) {
            if (!NoppesUtilPlayer.compareItems(item, is, false, false) || is.getCount() >= is.getMaxCount()) continue;
            return is;
        }
        return ItemStack.EMPTY;
    }

    public int firstFreeSlot() {
        for (int i = 0; i < this.size(); ++i) {
            if (!((ItemStack)this.items.get(i)).isEmpty()) continue;
            return i;
        }
        return -1;
    }

    public void setSize(int i) {
        this.size = i;
    }

    public void onOpen(PlayerEntity player) {
    }

    public void onClose(PlayerEntity player) {
    }

    public void clear() {
    }

    public boolean isEmpty() {
        for (int slot = 0; slot < this.size(); ++slot) {
            ItemStack item = this.getStack(slot);
            if (NoppesUtilServer.IsItemStackNull(item) || item.isEmpty()) continue;
            return false;
        }
        return true;
    }
}

