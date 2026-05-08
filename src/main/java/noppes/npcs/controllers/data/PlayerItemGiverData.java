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
import noppes.npcs.roles.JobItemGiver;

public class PlayerItemGiverData {
    private HashMap<Integer, Long> itemgivers = new HashMap();
    private HashMap<Integer, Integer> chained = new HashMap();

    public void loadNBTData(NbtCompound compound) {
        this.chained = NBTTags.getIntegerIntegerMap(compound.getList("ItemGiverChained", 10));
        this.itemgivers = NBTTags.getIntegerLongMap(compound.getList("ItemGiversList", 10));
    }

    public void saveNBTData(NbtCompound compound) {
        compound.put("ItemGiverChained", (NbtElement)NBTTags.nbtIntegerIntegerMap(this.chained));
        compound.put("ItemGiversList", (NbtElement)NBTTags.nbtIntegerLongMap(this.itemgivers));
    }

    public boolean hasInteractedBefore(JobItemGiver jobItemGiver) {
        return this.itemgivers.containsKey(jobItemGiver.itemGiverId);
    }

    public long getTime(JobItemGiver jobItemGiver) {
        return this.itemgivers.get(jobItemGiver.itemGiverId);
    }

    public void setTime(JobItemGiver jobItemGiver, long day) {
        this.itemgivers.put(jobItemGiver.itemGiverId, day);
    }

    public int getItemIndex(JobItemGiver jobItemGiver) {
        if (this.chained.containsKey(jobItemGiver.itemGiverId)) {
            return this.chained.get(jobItemGiver.itemGiverId);
        }
        return 0;
    }

    public void setItemIndex(JobItemGiver jobItemGiver, int i) {
        this.chained.put(jobItemGiver.itemGiverId, i);
    }
}

