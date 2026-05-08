/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ActionResult
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemPlacementContext
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.sound.BlockSoundGroup
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.util.shape.VoxelShapes
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.block.BlockState
 *  net.minecraft.state.StateManager$Builder
 *  net.minecraft.state.property.IntProperty
 *  net.minecraft.state.property.Property
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.block.AbstractBlock
 *  net.minecraft.block.AbstractBlock$Settings
 */
package noppes.npcs.blocks;

import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Property;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.block.AbstractBlock;
import noppes.npcs.blocks.BlockInterface;
import noppes.npcs.blocks.tiles.TileMailbox;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiOpen;

public class BlockMailbox
extends BlockInterface {
    public static final IntProperty ROTATION = IntProperty.of((String)"rotation", (int)0, (int)3);
    public final int type;

    public BlockMailbox(int type) {
        super(AbstractBlock.Settings.copy((AbstractBlock)Blocks.IRON_BLOCK).sounds(BlockSoundGroup.METAL).strength(5.0f, 10.0f));
        this.type = type;
    }

    public String getTranslationKey() {
        return "block.customnpcs.npcmailbox";
    }

    public VoxelShape getCullingShape(BlockState p_196247_1_, BlockView p_196247_2_, BlockPos p_196247_3_) {
        return VoxelShapes.empty();
    }

    public boolean canPathfindThrough(BlockState p_196266_1_, BlockView p_196266_2_, BlockPos p_196266_3_, NavigationType p_196266_4_) {
        return false;
    }

    public ActionResult onUse(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult ray) {
        if (!level.isClient) {
            Packets.send((ServerPlayerEntity)player, new PacketGuiOpen(EnumGuiType.PlayerMailbox, pos));
        }
        return ActionResult.SUCCESS;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{ROTATION});
    }

    public BlockState getPlacementState(ItemPlacementContext context) {
        int l = MathHelper.floor((double)((double)(context.getPlayer().getYaw() * 4.0f / 360.0f) + 0.5)) & 3;
        return (BlockState)this.getDefaultState().with((Property)ROTATION, (Comparable)Integer.valueOf(l % 4));
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TileMailbox(pos, state).setModel(this.type);
    }
}

