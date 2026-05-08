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
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleFollower;

public class ContainerNPCFollowerSetup
extends ScreenHandler {
    private RoleFollower role;

    public ContainerNPCFollowerSetup(int containerId, PlayerInventory playerInventory, int entityId) {
        super(CustomContainer.container_followersetup, containerId);
        int i1;
        EntityNPCInterface npc = (EntityNPCInterface)playerInventory.player.getWorld().getEntityById(entityId);
        this.role = (RoleFollower)npc.role;
        for (i1 = 0; i1 < 3; ++i1) {
            this.addSlot(new Slot((Inventory)this.role.inventory, i1, 44, 39 + i1 * 25));
        }
        for (i1 = 0; i1 < 3; ++i1) {
            for (int l1 = 0; l1 < 9; ++l1) {
                this.addSlot(new Slot((Inventory)playerInventory, l1 + i1 * 9 + 9, 8 + l1 * 18, 113 + i1 * 18));
            }
        }
        for (int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot((Inventory)playerInventory, j1, 8 + j1 * 18, 171));
        }
    }

    public ItemStack quickMove(PlayerEntity par1Player, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(i);
        if (slot != null && slot.hasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (i >= 0 && i < 3 ? !this.insertItem(itemstack1, 3, 38, true) : (i >= 3 && i < 30 ? !this.insertItem(itemstack1, 30, 38, false) : (i >= 30 && i < 38 ? !this.insertItem(itemstack1, 3, 29, false) : !this.insertItem(itemstack1, 3, 38, false)))) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0) {
                slot.setStackNoCallbacks(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            if (itemstack1.getCount() != itemstack.getCount()) {
                slot.onTakeItem(par1Player, itemstack1);
            } else {
                return ItemStack.EMPTY;
            }
        }
        return itemstack;
    }

    public boolean canUse(PlayerEntity entityplayer) {
        return true;
    }
}

