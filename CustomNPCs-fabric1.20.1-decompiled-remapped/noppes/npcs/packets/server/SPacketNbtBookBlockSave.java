/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.block.entity.BlockEntity
 */
package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.block.entity.BlockEntity;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNbtBookBlockSave
extends PacketServerBasic {
    private BlockPos pos;
    private NbtCompound data;

    public SPacketNbtBookBlockSave(BlockPos pos, NbtCompound data) {
        this.pos = pos;
        this.data = data;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return item.getItem() == CustomItems.nbt_book;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.TOOL_NBTBOOK;
    }

    public static void encode(SPacketNbtBookBlockSave msg, PacketByteBuf buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeNbt(msg.data);
    }

    public static SPacketNbtBookBlockSave decode(PacketByteBuf buf) {
        return new SPacketNbtBookBlockSave(buf.readBlockPos(), buf.readNbt());
    }

    @Override
    protected void handle() {
        BlockEntity tile = this.player.getWorld().getBlockEntity(this.pos);
        if (tile != null) {
            tile.readNbt(this.data);
            tile.markDirty();
        }
    }
}

