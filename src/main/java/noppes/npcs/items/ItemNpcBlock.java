/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.Item$Settings
 *  net.minecraft.block.Block
 */
package noppes.npcs.items;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.block.Block;

public class ItemNpcBlock
extends BlockItem {
    public final Block block;

    public ItemNpcBlock(Block block, Item.Settings builder) {
        super(block, builder);
        this.block = block;
    }
}

