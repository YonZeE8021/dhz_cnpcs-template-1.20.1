/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.ItemStack
 */
package noppes.npcs.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.controllers.data.Bank;

public class ContainerManageBanks
extends ScreenHandler {
    public Bank bank = new Bank();

    public ContainerManageBanks(int containerId, PlayerInventory playerInventory) {
        super(CustomContainer.container_managebanks, containerId);
        int y;
        int x;
        int i;
        for (i = 0; i < 6; ++i) {
            x = 36;
            y = 38;
            this.addSlot(new Slot((Inventory)this.bank.currencyInventory, i, x, y += i * 22));
        }
        for (i = 0; i < 6; ++i) {
            x = 142;
            y = 38;
            this.addSlot(new Slot((Inventory)this.bank.upgradeInventory, i, x, y += i * 22));
        }
        for (int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot((Inventory)playerInventory, j1, 8 + j1 * 18, 171));
        }
    }

    public ItemStack quickMove(PlayerEntity par1Player, int i) {
        return ItemStack.EMPTY;
    }

    public boolean canUse(PlayerEntity entityplayer) {
        return true;
    }

    public void setBank(Bank bank2) {
        for (int i = 0; i < 6; ++i) {
            this.bank.currencyInventory.setStack(i, bank2.currencyInventory.getStack(i));
            this.bank.upgradeInventory.setStack(i, bank2.upgradeInventory.getStack(i));
        }
    }
}

