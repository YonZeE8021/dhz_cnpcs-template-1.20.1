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
import noppes.npcs.roles.JobItemGiver;

public class ContainerNpcItemGiver
extends ScreenHandler {
    private JobItemGiver role;

    public ContainerNpcItemGiver(int containerId, PlayerInventory playerInventory, int entityId) {
        super(CustomContainer.container_itemgiver, containerId);
        int j1;
        EntityNPCInterface npc = (EntityNPCInterface)playerInventory.player.getWorld().getEntityById(entityId);
        this.role = (JobItemGiver)npc.job;
        for (j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot((Inventory)this.role.inventory, j1, 6 + j1 * 18, 90));
        }
        for (int i1 = 0; i1 < 3; ++i1) {
            for (int l1 = 0; l1 < 9; ++l1) {
                this.addSlot(new Slot((Inventory)playerInventory, l1 + i1 * 9 + 9, 6 + l1 * 18, 116 + i1 * 18));
            }
        }
        for (j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot((Inventory)playerInventory, j1, 6 + j1 * 18, 174));
        }
    }

    public ItemStack quickMove(PlayerEntity par1Player, int i) {
        return ItemStack.EMPTY;
    }

    public boolean canUse(PlayerEntity entityplayer) {
        return true;
    }
}

