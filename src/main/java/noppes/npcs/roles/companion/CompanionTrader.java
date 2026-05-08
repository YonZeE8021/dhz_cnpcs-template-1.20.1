/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.roles.companion;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.roles.companion.CompanionJobInterface;

public class CompanionTrader
extends CompanionJobInterface {
    @Override
    public NbtCompound getNBT() {
        NbtCompound compound = new NbtCompound();
        return compound;
    }

    @Override
    public void setNBT(NbtCompound compound) {
    }

    public void interact(PlayerEntity player) {
        NoppesUtilServer.sendOpenGui(player, EnumGuiType.CompanionTrader, this.npc);
    }

    @Override
    public EnumCompanionJobs getType() {
        return EnumCompanionJobs.SHOP;
    }
}

