/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 */
package noppes.npcs;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import noppes.npcs.NoppesUtilServer;

public class NoppesUtilPlayer {
    public static boolean compareItems(ItemStack item, ItemStack item2, boolean ignoreDamage, boolean ignoreNBT) {
        if (NoppesUtilServer.IsItemStackNull(item) || NoppesUtilServer.IsItemStackNull(item2)) {
            return false;
        }
        return NoppesUtilPlayer.compareItemDetails(item, item2, ignoreDamage, ignoreNBT);
    }

    private static boolean compareItemDetails(ItemStack item, ItemStack item2, boolean ignoreDamage, boolean ignoreNBT) {
        if (item.getItem() != item2.getItem()) {
            return false;
        }
        if (!ignoreDamage && item.getDamage() != -1 && item.getDamage() != item2.getDamage()) {
            return false;
        }
        if (!(ignoreNBT || item.getNbt() == null || item2.getNbt() != null && item.getNbt().equals(item2.getNbt()))) {
            return false;
        }
        return ignoreNBT || item2.getNbt() == null || item.getNbt() != null;
    }

    public static boolean compareItems(PlayerEntity player, ItemStack item, boolean ignoreDamage, boolean ignoreNBT) {
        int size = 0;
        for (int i = 0; i < player.getInventory().size(); ++i) {
            ItemStack is = player.getInventory().getStack(i);
            if (NoppesUtilServer.IsItemStackNull(is) || !NoppesUtilPlayer.compareItems(item, is, ignoreDamage, ignoreNBT)) continue;
            size += is.getCount();
        }
        return size >= item.getCount();
    }

    public static void consumeItem(PlayerEntity player, ItemStack item, boolean ignoreDamage, boolean ignoreNBT) {
        if (NoppesUtilServer.IsItemStackNull(item)) {
            return;
        }
        int size = item.getCount();
        for (int i = 0; i < player.getInventory().size(); ++i) {
            ItemStack is = player.getInventory().getStack(i);
            if (NoppesUtilServer.IsItemStackNull(is) || !NoppesUtilPlayer.compareItems(item, is, ignoreDamage, ignoreNBT)) continue;
            if (size >= is.getCount()) {
                size -= is.getCount();
                player.getInventory().setStack(i, ItemStack.EMPTY);
                continue;
            }
            is.split(size);
            break;
        }
    }

    public static List<ItemStack> countStacks(Inventory inv, boolean ignoreDamage, boolean ignoreNBT) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack item = inv.getStack(i);
            if (NoppesUtilServer.IsItemStackNull(item)) continue;
            boolean found = false;
            for (ItemStack is : list) {
                if (!NoppesUtilPlayer.compareItems(item, is, ignoreDamage, ignoreNBT)) continue;
                is.setCount(is.getCount() + item.getCount());
                found = true;
                break;
            }
            if (found) continue;
            list.add(item.copy());
        }
        return list;
    }
}

