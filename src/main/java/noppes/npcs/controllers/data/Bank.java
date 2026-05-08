/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.controllers.data;

import java.util.HashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.NBTTags;
import noppes.npcs.NpcMiscInventory;

public class Bank {
    public int id = -1;
    public String name = "";
    public HashMap<Integer, Integer> slotTypes = new HashMap();
    public int startSlots = 1;
    public int maxSlots = 6;
    public NpcMiscInventory currencyInventory = new NpcMiscInventory(6);
    public NpcMiscInventory upgradeInventory = new NpcMiscInventory(6);

    public Bank() {
        for (int i = 0; i < 6; ++i) {
            this.slotTypes.put(i, 0);
        }
    }

    public void addAdditionalSaveData(NbtCompound nbttagcompound) {
        nbttagcompound.putInt("BankID", this.id);
        nbttagcompound.put("BankCurrency", (NbtElement)this.currencyInventory.getToNBT());
        nbttagcompound.put("BankUpgrade", (NbtElement)this.upgradeInventory.getToNBT());
        nbttagcompound.putString("Username", this.name);
        nbttagcompound.putInt("MaxSlots", this.maxSlots);
        nbttagcompound.putInt("StartSlots", this.startSlots);
        nbttagcompound.put("BankTypes", (NbtElement)NBTTags.nbtIntegerIntegerMap(this.slotTypes));
    }

    public void readAdditionalSaveData(NbtCompound nbttagcompound) {
        this.id = nbttagcompound.getInt("BankID");
        this.name = nbttagcompound.getString("Username");
        this.startSlots = nbttagcompound.getInt("StartSlots");
        this.maxSlots = nbttagcompound.getInt("MaxSlots");
        this.slotTypes = NBTTags.getIntegerIntegerMap(nbttagcompound.getList("BankTypes", 10));
        this.currencyInventory.setFromNBT(nbttagcompound.getCompound("BankCurrency"));
        this.upgradeInventory.setFromNBT(nbttagcompound.getCompound("BankUpgrade"));
    }

    public boolean isUpgraded(int slot) {
        return this.slotTypes.get(slot) != null && this.slotTypes.get(slot) == 2;
    }

    public boolean canBeUpgraded(int slot) {
        if (this.upgradeInventory.getStack(slot) == null || this.upgradeInventory.getStack(slot).isEmpty()) {
            return false;
        }
        return this.slotTypes.get(slot) == null || this.slotTypes.get(slot) == 0;
    }

    public int getMaxSlots() {
        for (int i = 0; i < this.maxSlots; ++i) {
            if (this.currencyInventory.getStack(i) != null && !this.currencyInventory.getStack(i).isEmpty() || i <= this.startSlots - 1) continue;
            return i;
        }
        return this.maxSlots;
    }
}

