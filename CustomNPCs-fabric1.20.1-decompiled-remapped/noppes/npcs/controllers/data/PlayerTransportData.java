/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.controllers.data;

import java.util.HashSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;

public class PlayerTransportData {
    public HashSet<Integer> transports = new HashSet();

    public void loadNBTData(NbtCompound compound) {
        HashSet<Integer> dialogsRead = new HashSet<Integer>();
        if (compound == null) {
            return;
        }
        NbtList list = compound.getList("TransportData", 10);
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); ++i) {
            NbtCompound nbttagcompound = list.getCompound(i);
            dialogsRead.add(nbttagcompound.getInt("Transport"));
        }
        this.transports = dialogsRead;
    }

    public void saveNBTData(NbtCompound compound) {
        NbtList list = new NbtList();
        for (int dia : this.transports) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putInt("Transport", dia);
            list.add((Object)nbttagcompound);
        }
        compound.put("TransportData", (NbtElement)list);
    }
}

