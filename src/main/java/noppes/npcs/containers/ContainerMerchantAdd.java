/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.inventory.SimpleInventory
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.village.Merchant
 *  net.minecraft.world.World
 */
package noppes.npcs.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.village.Merchant;
import net.minecraft.world.World;
import noppes.npcs.CustomContainer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.ServerEventsHandler;
import noppes.npcs.containers.ContainerNpcInterface;

public class ContainerMerchantAdd
extends ContainerNpcInterface {
    private Merchant theMerchant = ServerEventsHandler.Merchant;
    private SimpleInventory merchantInventory;
    private final World level;

    public ContainerMerchantAdd(int containerId, PlayerInventory playerInventory) {
        super(CustomContainer.container_merchantadd, containerId, playerInventory);
        int i;
        this.level = playerInventory.player.getWorld();
        this.merchantInventory = new SimpleInventory(3);
        this.addSlot(new Slot((Inventory)this.merchantInventory, 0, 36, 53));
        this.addSlot(new Slot((Inventory)this.merchantInventory, 1, 62, 53));
        this.addSlot(new Slot((Inventory)this.merchantInventory, 2, 120, 53));
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot((Inventory)playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot((Inventory)playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity par1Player, int limbSwingAmount) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(limbSwingAmount);
        if (slot != null && slot.hasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (limbSwingAmount != 0 && limbSwingAmount != 1 && limbSwingAmount != 2 ? (limbSwingAmount >= 3 && limbSwingAmount < 30 ? !this.insertItem(itemstack1, 30, 39, false) : limbSwingAmount >= 30 && limbSwingAmount < 39 && !this.insertItem(itemstack1, 3, 30, false)) : !this.insertItem(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0) {
                slot.setStackNoCallbacks(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(par1Player, itemstack1);
        }
        return itemstack;
    }

    public void onClosed(PlayerEntity par1Player) {
        super.onClosed(par1Player);
        super.onClosed(par1Player);
        if (!this.level.isClient) {
            ItemStack itemstack = this.merchantInventory.removeStack(0);
            if (!NoppesUtilServer.IsItemStackNull(itemstack)) {
                par1Player.dropItem(itemstack, false);
            }
            if (!NoppesUtilServer.IsItemStackNull(itemstack = this.merchantInventory.removeStack(1))) {
                par1Player.dropItem(itemstack, false);
            }
        }
    }
}

