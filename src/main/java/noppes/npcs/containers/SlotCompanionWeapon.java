/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.ItemStack
 */
package noppes.npcs.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.roles.RoleCompanion;

class SlotCompanionWeapon
extends Slot {
    final RoleCompanion role;

    public SlotCompanionWeapon(RoleCompanion role, Inventory iinventory, int id, int x, int y) {
        super(iinventory, id, x, y);
        this.role = role;
    }

    public int getMaxItemCount() {
        return 1;
    }

    public boolean canInsert(ItemStack itemstack) {
        if (NoppesUtilServer.IsItemStackNull(itemstack)) {
            return false;
        }
        return this.role.canWearSword(NpcAPI.Instance().getIItemStack(itemstack));
    }
}

