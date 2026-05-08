/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.BlockEntityProvider
 *  net.minecraft.block.AbstractBlock$Settings
 */
package noppes.npcs.blocks;

import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.AbstractBlock;

public abstract class BlockInterface
extends BlockWithEntity
implements BlockEntityProvider {
    protected BlockInterface(AbstractBlock.Settings properties) {
        super(properties);
    }
}

