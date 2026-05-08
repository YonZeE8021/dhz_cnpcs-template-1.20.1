/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.roles.companion;

import net.minecraft.nbt.NbtCompound;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.roles.companion.CompanionJobInterface;

public class CompanionFarmer
extends CompanionJobInterface {
    public boolean isStanding = false;

    @Override
    public NbtCompound getNBT() {
        NbtCompound compound = new NbtCompound();
        compound.putBoolean("CompanionFarmerStanding", this.isStanding);
        return compound;
    }

    @Override
    public void setNBT(NbtCompound compound) {
        this.isStanding = compound.getBoolean("CompanionFarmerStanding");
    }

    @Override
    public EnumCompanionJobs getType() {
        return EnumCompanionJobs.FARMER;
    }

    @Override
    public boolean isSelfSufficient() {
        return this.isStanding;
    }

    @Override
    public void onUpdate() {
    }
}

