/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.schematics.SchematicWrapper;

public class SPacketSchematicsTileBuild
extends PacketServerBasic {
    private BlockPos pos;

    public SPacketSchematicsTileBuild(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.builder_item || item.getItem() == CustomBlocks.copy_item;
    }

    public static void encode(SPacketSchematicsTileBuild msg, PacketByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static SPacketSchematicsTileBuild decode(PacketByteBuf buf) {
        return new SPacketSchematicsTileBuild(buf.readBlockPos());
    }

    @Override
    protected void handle() {
        TileBuilder tile = (TileBuilder)this.player.getWorld().getBlockEntity(this.pos);
        SchematicWrapper schem = tile.getSchematic();
        schem.init(this.pos.add(1, tile.yOffest, 1), this.player.getWorld(), tile.rotation * 90);
        SchematicController.Instance.build(tile.getSchematic(), this.player.getCommandSource());
        this.player.getWorld().removeBlock(this.pos, false);
    }
}

