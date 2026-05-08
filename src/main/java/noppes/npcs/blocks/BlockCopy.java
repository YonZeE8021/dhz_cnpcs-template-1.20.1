/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ActionResult
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraft.block.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.sound.BlockSoundGroup
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.BlockState
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.block.AbstractBlock
 *  net.minecraft.block.AbstractBlock$Settings
 */
package noppes.npcs.blocks;

import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.block.AbstractBlock;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.BlockInterface;
import noppes.npcs.blocks.tiles.TileCopy;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;

public class BlockCopy
extends BlockInterface {
    public BlockCopy() {
        super(AbstractBlock.Settings.copy((AbstractBlock)Blocks.BARRIER).sounds(BlockSoundGroup.STONE));
    }

    public ActionResult onUse(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult ray) {
        if (level.isClient) {
            return ActionResult.PASS;
        }
        ItemStack currentItem = player.getInventory().getMainHandStack();
        if (currentItem != null && currentItem.getItem() == CustomItems.wand) {
            SPacketGuiOpen.sendOpenGui(player, EnumGuiType.CopyBlock, null, pos);
        }
        return ActionResult.SUCCESS;
    }

    public BlockState getPlacementState(ItemPlacementContext context) {
        if (!context.getWorld().isClient) {
            SPacketGuiOpen.sendOpenGui(context.getPlayer(), EnumGuiType.CopyBlock, null, context.getBlockPos());
        }
        return this.getDefaultState();
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileCopy(pos, state);
    }
}

