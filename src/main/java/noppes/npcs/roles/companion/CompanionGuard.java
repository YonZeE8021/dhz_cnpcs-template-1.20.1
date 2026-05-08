/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.mob.CreeperEntity
 *  net.minecraft.entity.mob.HostileEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.roles.companion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.companion.CompanionJobInterface;

public class CompanionGuard
extends CompanionJobInterface {
    public boolean isStanding = false;

    @Override
    public NbtCompound getNBT() {
        NbtCompound compound = new NbtCompound();
        compound.putBoolean("CompanionGuardStanding", this.isStanding);
        return compound;
    }

    @Override
    public void setNBT(NbtCompound compound) {
        this.isStanding = compound.getBoolean("CompanionGuardStanding");
    }

    public boolean isEntityApplicable(Entity entity) {
        if (entity instanceof PlayerEntity || entity instanceof EntityNPCInterface) {
            return false;
        }
        if (entity instanceof CreeperEntity) {
            return false;
        }
        return entity instanceof HostileEntity;
    }

    @Override
    public boolean isSelfSufficient() {
        return this.isStanding;
    }

    @Override
    public EnumCompanionJobs getType() {
        return EnumCompanionJobs.GUARD;
    }
}

