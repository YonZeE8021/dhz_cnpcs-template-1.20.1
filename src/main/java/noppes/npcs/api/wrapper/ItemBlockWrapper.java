/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.block.Block
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.api.wrapper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import noppes.npcs.api.item.IItemBlock;
import noppes.npcs.api.wrapper.ItemStackWrapper;

public class ItemBlockWrapper
extends ItemStackWrapper
implements IItemBlock {
    protected String blockName;

    protected ItemBlockWrapper(ItemStack item) {
        super(item);
        Block b = Block.getBlockFromItem((Item)item.getItem());
        this.blockName = String.valueOf(Registries.BLOCK.getId(b));
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public String getBlockName() {
        return this.blockName;
    }
}

