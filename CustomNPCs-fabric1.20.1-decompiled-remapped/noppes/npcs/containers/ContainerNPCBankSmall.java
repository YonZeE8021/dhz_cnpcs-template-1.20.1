/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerInventory
 */
package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerInventory;
import noppes.npcs.CustomContainer;
import noppes.npcs.containers.ContainerNPCBankInterface;

public class ContainerNPCBankSmall
extends ContainerNPCBankInterface {
    public ContainerNPCBankSmall(int containerId, PlayerInventory playerInventory, int slot, int bankid) {
        super(CustomContainer.container_banksmall, containerId, playerInventory, slot, bankid);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public int getRowNumber() {
        return 3;
    }
}

