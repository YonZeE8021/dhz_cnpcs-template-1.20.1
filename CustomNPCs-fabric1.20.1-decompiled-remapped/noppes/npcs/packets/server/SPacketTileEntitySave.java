/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketTileEntitySave
extends PacketServerBasic {
    private NbtCompound data;

    public SPacketTileEntitySave(NbtCompound data) {
        this.data = data;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return item.getItem() == CustomItems.wand || item.getItem() == CustomBlocks.border_item || item.getItem() == CustomBlocks.copy_item || item.getItem() == CustomBlocks.redstone_item || item.getItem() == CustomBlocks.scripted_item || item.getItem() == CustomBlocks.waypoint_item;
    }

    public static void encode(SPacketTileEntitySave msg, PacketByteBuf buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketTileEntitySave decode(PacketByteBuf buf) {
        return new SPacketTileEntitySave(buf.readNbt());
    }

    @Override
    protected void handle() {
        SPacketTileEntitySave.saveTileEntity(this.player, this.data);
    }

    public static BlockEntity saveTileEntity(ServerPlayerEntity player, NbtCompound compound) {
        int x = compound.getInt("x");
        int y = compound.getInt("y");
        int z = compound.getInt("z");
        BlockEntity tile = player.getWorld().getBlockEntity(new BlockPos(x, y, z));
        if (tile != null) {
            tile.readNbt(compound);
        }
        return tile;
    }
}

