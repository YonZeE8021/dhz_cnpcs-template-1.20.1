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

public class PlayerDialogData {
    public HashSet<Integer> dialogsRead = new HashSet();

    public void loadNBTData(NbtCompound compound) {
        HashSet<Integer> dialogsRead = new HashSet<Integer>();
        if (compound == null) {
            return;
        }
        NbtList list = compound.getList("DialogData", 10);
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); ++i) {
            NbtCompound nbttagcompound = list.getCompound(i);
            dialogsRead.add(nbttagcompound.getInt("Dialog"));
        }
        this.dialogsRead = dialogsRead;
    }

    public void saveNBTData(NbtCompound compound) {
        NbtList list = new NbtList();
        for (int dia : this.dialogsRead) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putInt("Dialog", dia);
            list.add((Object)nbttagcompound);
        }
        compound.put("DialogData", (NbtElement)list);
    }
}

