/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.quests;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import noppes.npcs.api.handler.data.IQuestObjective;

public abstract class QuestInterface {
    public int questId;

    public abstract void addAdditionalSaveData(NbtCompound var1);

    public abstract void readAdditionalSaveData(NbtCompound var1);

    public abstract boolean isCompleted(PlayerEntity var1);

    public abstract void handleComplete(PlayerEntity var1);

    public abstract IQuestObjective[] getObjectives(PlayerEntity var1);
}

