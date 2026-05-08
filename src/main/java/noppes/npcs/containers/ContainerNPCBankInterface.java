/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.screen.ScreenHandlerType
 */
package noppes.npcs.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.containers.ContainerNpcInterface;
import noppes.npcs.containers.InventoryNPC;
import noppes.npcs.containers.SlotNpcBankCurrency;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.PlayerBankData;

public class ContainerNPCBankInterface
extends ContainerNpcInterface {
    public InventoryNPC currencyMatrix;
    public SlotNpcBankCurrency currency;
    public int slot = 0;
    public int bankid;
    private PlayerBankData data;

    public ContainerNPCBankInterface(ScreenHandlerType type, int containerId, PlayerInventory playerInventory, int slot, int bankid) {
        super(type, containerId, playerInventory);
        this.bankid = bankid;
        this.slot = slot;
        this.currencyMatrix = new InventoryNPC("currency", 1, this);
        if (!this.isAvailable() || this.canBeUpgraded()) {
            this.currency = new SlotNpcBankCurrency(this, this.currencyMatrix, 0, 80, 29);
            this.addSlot(this.currency);
        }
        NpcMiscInventory items = new NpcMiscInventory(54);
        if (!this.player.getWorld().isClient) {
            this.data = PlayerDataController.instance.getBankData(this.player, bankid);
            items = this.data.getBankOrDefault((int)bankid).itemSlots.get(slot);
        }
        int xOffset = this.xOffset();
        for (int j = 0; j < this.getRowNumber(); ++j) {
            for (int i1 = 0; i1 < 9; ++i1) {
                int id = i1 + j * 9;
                this.addSlot(new Slot((Inventory)items, id, 8 + i1 * 18, 17 + xOffset + j * 18));
            }
        }
        if (this.isUpgraded()) {
            xOffset += 54;
        }
        for (int k = 0; k < 3; ++k) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot((Inventory)playerInventory, j1 + k * 9 + 9, 8 + j1 * 18, 86 + xOffset + k * 18));
            }
        }
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot((Inventory)playerInventory, l, 8 + l * 18, 144 + xOffset));
        }
    }

    public synchronized void setCurrency(ItemStack item) {
        this.currency.item = item;
    }

    public int getRowNumber() {
        return 0;
    }

    public int xOffset() {
        return 0;
    }

    public void onContentChanged(Inventory inv) {
    }

    public boolean isAvailable() {
        return false;
    }

    public boolean isUpgraded() {
        return false;
    }

    public boolean canBeUpgraded() {
        return false;
    }

    @Override
    public ItemStack quickMove(PlayerEntity par1Player, int i) {
        return ItemStack.EMPTY;
    }

    public void onClosed(PlayerEntity entityplayer) {
        super.onClosed(entityplayer);
        if (!entityplayer.getWorld().isClient) {
            ItemStack var3 = this.currencyMatrix.getStack(0);
            this.currencyMatrix.setStack(0, ItemStack.EMPTY);
            if (!NoppesUtilServer.IsItemStackNull(var3)) {
                entityplayer.dropItem(var3, false);
            }
        }
    }
}

