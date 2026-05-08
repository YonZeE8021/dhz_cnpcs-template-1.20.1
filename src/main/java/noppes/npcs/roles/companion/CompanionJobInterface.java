/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.roles.companion;

import net.minecraft.nbt.NbtCompound;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.entity.EntityNPCInterface;

public abstract class CompanionJobInterface {
    public EntityNPCInterface npc;

    public abstract NbtCompound getNBT();

    public abstract void setNBT(NbtCompound var1);

    public abstract EnumCompanionJobs getType();

    public void onUpdate() {
    }

    public boolean isSelfSufficient() {
        return false;
    }
}

