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
import noppes.npcs.roles.RoleTrader;

public class ContainerNPCTraderSetup
extends ScreenHandler {
    public RoleTrader role;

    public ContainerNPCTraderSetup(int containerId, PlayerInventory playerInventory, int entityId) {
        super(CustomContainer.container_tradersetup, containerId);
        EntityNPCInterface npc = (EntityNPCInterface)playerInventory.player.getWorld().getEntityById(entityId);
        this.role = (RoleTrader)npc.role;
        for (int i = 0; i < 18; ++i) {
            int x = 7;
            int y = 15;
            this.addSlot(new Slot((Inventory)this.role.inventoryCurrency, i + 18, x += i % 3 * 94, y += i / 3 * 22));
            this.addSlot(new Slot((Inventory)this.role.inventoryCurrency, i, x + 18, y));
            this.addSlot(new Slot((Inventory)this.role.inventorySold, i, x + 43, y));
        }
        for (int i1 = 0; i1 < 3; ++i1) {
            for (int l1 = 0; l1 < 9; ++l1) {
                this.addSlot(new Slot((Inventory)playerInventory, l1 + i1 * 9 + 9, 48 + l1 * 18, 147 + i1 * 18));
            }
        }
        for (int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot((Inventory)playerInventory, j1, 48 + j1 * 18, 205));
        }
    }

    public ItemStack quickMove(PlayerEntity par1Player, int i) {
        return ItemStack.EMPTY;
    }

    public boolean canUse(PlayerEntity entityplayer) {
        return true;
    }
}

