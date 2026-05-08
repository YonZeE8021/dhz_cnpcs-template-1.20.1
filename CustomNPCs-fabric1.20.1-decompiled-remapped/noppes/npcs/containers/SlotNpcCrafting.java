/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.slot.CraftingResultSlot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.inventory.RecipeInputInventory
 */
package noppes.npcs.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.inventory.RecipeInputInventory;
import noppes.npcs.NoppesUtilServer;

public class SlotNpcCrafting
extends CraftingResultSlot {
    private final RecipeInputInventory craftMatrix;

    public SlotNpcCrafting(PlayerEntity player, RecipeInputInventory craftingInventory, Inventory inventory, int slotIndex, int x, int y) {
        super(player, craftingInventory, inventory, slotIndex, x, y);
        this.craftMatrix = craftingInventory;
    }

    public void onTakeItem(PlayerEntity player, ItemStack itemStack) {
        this.onCrafted(itemStack);
        for (int i = 0; i < this.craftMatrix.size(); ++i) {
            ItemStack itemstack2;
            ItemStack itemstack1 = this.craftMatrix.getStack(i);
            if (NoppesUtilServer.IsItemStackNull(itemstack1)) continue;
            this.craftMatrix.removeStack(i, 1);
            if (!itemstack1.getItem().hasRecipeRemainder() || !NoppesUtilServer.IsItemStackNull(itemstack2 = new ItemStack((ItemConvertible)itemstack1.getItem().getRecipeRemainder())) && itemstack2.isDamageable() && itemstack2.getDamage() > itemstack2.getMaxDamage() || player.getInventory().insertStack(itemstack2)) continue;
            if (NoppesUtilServer.IsItemStackNull(this.craftMatrix.getStack(i))) {
                this.craftMatrix.setStack(i, itemstack2);
                continue;
            }
            player.dropItem(itemstack2, false);
        }
    }
}

