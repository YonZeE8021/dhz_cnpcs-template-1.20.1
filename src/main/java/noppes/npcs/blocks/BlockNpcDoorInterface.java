/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraft.block.DoorBlock
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.block.BlockEntityProvider
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.BlockState
 *  net.minecraft.stat.Stats
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.BlockSetType
 *  net.minecraft.loot.context.LootContextParameterSet$Builder
 */
package noppes.npcs.blocks;

import java.util.Collections;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.DoorBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.stat.Stats;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockSetType;
import net.minecraft.loot.context.LootContextParameterSet;

public abstract class BlockNpcDoorInterface
extends DoorBlock
implements BlockEntityProvider {
    public BlockNpcDoorInterface(AbstractBlock.Settings properties) {
        super(properties, BlockSetType.STONE);
    }

    public void onStateReplaced(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onStateReplaced(state, level, pos, newState, isMoving);
        level.removeBlockEntity(pos);
    }

    public List<ItemStack> getDroppedStacks(BlockState p_287732_, LootContextParameterSet.Builder p_287596_) {
        return Collections.emptyList();
    }

    public void afterBreak(World p_180657_1_, PlayerEntity p_180657_2_, BlockPos p_180657_3_, BlockState p_180657_4_, BlockEntity p_180657_5_, ItemStack p_180657_6_) {
        p_180657_2_.incrementStat(Stats.MINED.getOrCreateStat(this));
        p_180657_2_.addExhaustion(0.005f);
        BlockNpcDoorInterface.dropStacks((BlockState)p_180657_4_, (World)p_180657_1_, (BlockPos)p_180657_3_, (BlockEntity)p_180657_5_, (Entity)p_180657_2_, (ItemStack)p_180657_6_);
    }
}

