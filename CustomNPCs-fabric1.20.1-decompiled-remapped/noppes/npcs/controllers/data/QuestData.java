/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.controllers.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.controllers.data.Quest;

public class QuestData {
    public Quest quest;
    public boolean isCompleted;
    public NbtCompound extraData = new NbtCompound();

    public QuestData(Quest quest) {
        this.quest = quest;
    }

    public void addAdditionalSaveData(NbtCompound nbttagcompound) {
        nbttagcompound.putBoolean("QuestCompleted", this.isCompleted);
        nbttagcompound.put("ExtraData", (NbtElement)this.extraData);
    }

    public void readAdditionalSaveData(NbtCompound nbttagcompound) {
        this.isCompleted = nbttagcompound.getBoolean("QuestCompleted");
        this.extraData = nbttagcompound.getCompound("ExtraData");
    }
}

