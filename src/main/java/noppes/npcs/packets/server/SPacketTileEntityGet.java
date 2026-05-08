/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.block.entity.BlockEntity
 */
package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.block.entity.BlockEntity;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketTileEntityGet
extends PacketServerBasic {
    private BlockPos pos;

    public SPacketTileEntityGet(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketTileEntityGet msg, PacketByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static SPacketTileEntityGet decode(PacketByteBuf buf) {
        return new SPacketTileEntityGet(buf.readBlockPos());
    }

    @Override
    protected void handle() {
        BlockEntity tile = this.player.getWorld().getBlockEntity(this.pos);
        Packets.send(this.player, new PacketGuiData(tile.createNbtWithIdentifyingData()));
    }
}

