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
    INVENTORY(Item.fromBlock((Block)Blocks.field_9980)),
    ARMOR(Items.field_8523),
    SWORD(Items.field_8802),
    RANGED(Items.field_8102),
    ACROBATS(Items.field_8370),
    INTEL(Items.field_8529);

    public ItemStack item;

    private EnumCompanionTalent(Item item) {
        this.item = new ItemStack((ItemConvertible)item);
    }
}

