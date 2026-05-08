/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ActionResult
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraft.block.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.sound.BlockSoundGroup
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.BlockState
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.block.AbstractBlock
 *  net.minecraft.block.AbstractBlock$Settings
 *  net.minecraft.block.entity.BlockEntityTicker
 */
package noppes.npcs.blocks;

import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockRenderType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.entity.BlockEntityTicker;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.blocks.BlockInterface;
import noppes.npcs.blocks.tiles.TileWaypoint;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;

public class BlockWaypoint
extends BlockInterface {
    public BlockWaypoint() {
        super(AbstractBlock.Settings.copy((AbstractBlock)Blocks.field_10499).sounds(BlockSoundGroup.METAL));
    }

    public ActionResult onUse(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult ray) {
        if (level.isClient) {
            return ActionResult.PASS;
        }
        ItemStack currentItem = player.getInventory().getMainHandStack();
        if (currentItem != null && currentItem.getItem() == CustomItems.wand && CustomNpcsPermissions.hasPermission((ServerPlayerEntity)player, CustomNpcsPermissions.EDIT_BLOCKS)) {
            SPacketGuiOpen.sendOpenGui(player, EnumGuiType.Waypoint, null, pos);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public void onPlaced(World level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack item) {
        if (!level.isClient && entity instanceof PlayerEntity) {
            SPacketGuiOpen.sendOpenGui((PlayerEntity)entity, EnumGuiType.Waypoint, null, pos);
        }
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileWaypoint(pos, state);
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.field_11458;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World level, BlockState state, BlockEntityType<T> type) {
        return BlockWaypoint.checkType(type, CustomBlocks.tile_waypoint, TileWaypoint::tick);
    }
}

