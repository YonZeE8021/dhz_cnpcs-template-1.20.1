/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import java.util.Vector;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.client.PacketGuiScrollList;

public class SPacketSchematicsTileGet
extends PacketServerBasic {
    private BlockPos pos;

    public SPacketSchematicsTileGet(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.builder_item || item.getItem() == CustomBlocks.copy_item;
    }

    public static void encode(SPacketSchematicsTileGet msg, PacketByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static SPacketSchematicsTileGet decode(PacketByteBuf buf) {
        return new SPacketSchematicsTileGet(buf.readBlockPos());
    }

    @Override
    protected void handle() {
        TileBuilder tile = (TileBuilder)this.player.getWorld().getBlockEntity(this.pos);
        if (tile == null) {
            return;
        }
        Packets.send(this.player, new PacketGuiData(tile.writePartNBT(new NbtCompound())));
        Packets.send(this.player, new PacketGuiScrollList(new Vector<String>(SchematicController.Instance.list())));
        if (tile.hasSchematic()) {
            Packets.send(this.player, new PacketGuiData(tile.getSchematic().getNBTSmall()));
        }
    }
}

