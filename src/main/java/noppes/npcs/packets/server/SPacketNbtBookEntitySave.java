/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNbtBookEntitySave
extends PacketServerBasic {
    private int id;
    private NbtCompound data;

    public SPacketNbtBookEntitySave(int id, NbtCompound data) {
        this.id = id;
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

    public static void encode(SPacketNbtBookEntitySave msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
        buf.writeNbt(msg.data);
    }

    public static SPacketNbtBookEntitySave decode(PacketByteBuf buf) {
        return new SPacketNbtBookEntitySave(buf.readInt(), buf.readNbt());
    }

    @Override
    protected void handle() {
        Entity entity = this.player.getWorld().getEntityById(this.id);
        if (entity != null) {
            entity.readNbt(this.data);
        }
    }
}

