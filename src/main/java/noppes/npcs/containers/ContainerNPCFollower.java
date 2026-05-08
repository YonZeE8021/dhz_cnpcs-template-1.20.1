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
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.containers.ContainerNpcInterface;
import noppes.npcs.containers.InventoryNPC;
import noppes.npcs.containers.SlotNpcMercenaryCurrency;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleFollower;

public class ContainerNPCFollower
extends ContainerNpcInterface {
    public InventoryNPC currencyMatrix;
    public RoleFollower role;

    public ContainerNPCFollower(int containerId, PlayerInventory playerInventory, int entityId) {
        super(CustomContainer.container_follower, containerId, playerInventory);
        EntityNPCInterface npc = (EntityNPCInterface)this.player.getWorld().getEntityById(entityId);
        this.role = (RoleFollower)npc.role;
        this.currencyMatrix = new InventoryNPC("currency", 1, this);
        this.addSlot(new SlotNpcMercenaryCurrency(this.role, this.currencyMatrix, 0, 26, 9));
        for (int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot((Inventory)this.player.getInventory(), j1, 8 + j1 * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity par1Player, int i) {
        return ItemStack.EMPTY;
    }

    public void onClosed(PlayerEntity entityplayer) {
        ItemStack itemstack;
        super.onClosed(entityplayer);
        if (!(entityplayer.getWorld().isClient || NoppesUtilServer.IsItemStackNull(itemstack = this.currencyMatrix.removeStack(0)) || entityplayer.getWorld().isClient)) {
            entityplayer.dropStack(itemstack, 0.0f);
        }
    }
}

