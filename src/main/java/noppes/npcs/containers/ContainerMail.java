/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.ItemStack
 */
package noppes.npcs.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.containers.ContainerNpcInterface;
import noppes.npcs.containers.SlotValid;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerMailData;

public class ContainerMail
extends ContainerNpcInterface {
    public static PlayerMail staticmail = new PlayerMail();
    public PlayerMail mail = staticmail;
    public final boolean canEdit;
    public final boolean canSend;

    public ContainerMail(int containerId, PlayerInventory playerInventory, boolean canEdit, boolean canSend) {
        super(CustomContainer.container_mail, containerId, playerInventory);
        int j;
        int k;
        staticmail = new PlayerMail();
        this.canEdit = canEdit;
        this.canSend = canSend;
        playerInventory.onOpen(this.player);
        for (k = 0; k < 4; ++k) {
            this.addSlot(new SlotValid(this.mail, k, 179 + k * 24, 138, canEdit));
        }
        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot((Inventory)playerInventory, k + j * 9 + 9, 28 + k * 18, 175 + j * 18));
            }
        }
        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot((Inventory)playerInventory, j, 28 + j * 18, 230));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity par1Player, int limbSwingAmount) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(limbSwingAmount);
        if (slot != null && slot.hasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (limbSwingAmount < 4) {
                if (!this.insertItem(itemstack1, 4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.canEdit || !this.insertItem(itemstack1, 0, 4, false)) {
                return null;
            }
            if (itemstack1.getCount() == 0) {
                slot.setStackNoCallbacks(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemstack;
    }

    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if (!this.canEdit && !player.getWorld().isClient) {
            PlayerMailData data = PlayerData.get((PlayerEntity)player).mailData;
            for (PlayerMail mail : data.playermail) {
                if (mail.time != this.mail.time || !mail.sender.equals(this.mail.sender)) continue;
                mail.readNBT(this.mail.writeNBT());
                break;
            }
        }
    }
}

