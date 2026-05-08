/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.controllers.data;

import java.util.HashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.data.BankData;

public class PlayerBankData {
    public HashMap<Integer, BankData> banks = new HashMap();

    public void loadNBTData(NbtCompound compound) {
        HashMap<Integer, BankData> banks = new HashMap<Integer, BankData>();
        NbtList list = compound.getList("BankData", 10);
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); ++i) {
            NbtCompound nbttagcompound = list.getCompound(i);
            BankData data = new BankData();
            data.readNBT(nbttagcompound);
            banks.put(data.bankId, data);
        }
        this.banks = banks;
    }

    public void saveNBTData(NbtCompound playerData) {
        NbtList list = new NbtList();
        for (BankData data : this.banks.values()) {
            NbtCompound nbttagcompound = new NbtCompound();
            data.writeNBT(nbttagcompound);
            list.add((Object)nbttagcompound);
        }
        playerData.put("BankData", (NbtElement)list);
    }

    public BankData getBank(int bankId) {
        return this.banks.get(bankId);
    }

    public BankData getBankOrDefault(int bankId) {
        BankData data = this.banks.get(bankId);
        if (data != null) {
            return data;
        }
        Bank bank = BankController.getInstance().getBank(bankId);
        return this.banks.get(bank.id);
    }

    public boolean hasBank(int bank) {
        return this.banks.containsKey(bank);
    }

    public void loadNew(int bank) {
        BankData data = new BankData();
        data.bankId = bank;
        this.banks.put(bank, data);
    }
}

