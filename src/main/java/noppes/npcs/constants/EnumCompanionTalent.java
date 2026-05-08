/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 */
package noppes.npcs.constants;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemConvertible;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;

public enum EnumCompanionTalent {
    INVENTORY(Item.fromBlock((Block)Blocks.CRAFTING_TABLE)),
    ARMOR(Items.IRON_CHESTPLATE),
    SWORD(Items.DIAMOND_SWORD),
    RANGED(Items.BOW),
    ACROBATS(Items.LEATHER_BOOTS),
    INTEL(Items.BOOK);

    public ItemStack item;

    private EnumCompanionTalent(Item item) {
        this.item = new ItemStack((ItemConvertible)item);
    }
}

