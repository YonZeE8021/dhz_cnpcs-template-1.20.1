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
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketSchematicsTileSet
extends PacketServerBasic {
    private BlockPos pos;
    private String name;

    public SPacketSchematicsTileSet(BlockPos pos, String name) {
        this.pos = pos;
        this.name = name;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.builder_item || item.getItem() == CustomBlocks.copy_item;
    }

    public static void encode(SPacketSchematicsTileSet msg, PacketByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeString(msg.name);
    }

    public static SPacketSchematicsTileSet decode(PacketByteBuf buf) {
        return new SPacketSchematicsTileSet(buf.readBlockPos(), buf.readString(Short.MAX_VALUE));
    }

    @Override
    protected void handle() {
        TileBuilder tile = (TileBuilder)this.player.getWorld().getBlockEntity(this.pos);
        tile.setSchematic(SchematicController.Instance.load(this.name));
        if (tile.hasSchematic()) {
            Packets.send(this.player, new PacketGuiData(tile.getSchematic().getNBTSmall()));
        }
    }
}

