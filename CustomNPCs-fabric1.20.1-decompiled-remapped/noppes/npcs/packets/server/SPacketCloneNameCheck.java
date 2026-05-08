/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketCloneNameCheck
extends PacketServerBasic {
    private String name;
    private int tab;

    public SPacketCloneNameCheck(String name, int tab) {
        this.name = name;
        this.tab = tab;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return item.getItem() == CustomItems.cloner;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_CLONE;
    }

    public static void encode(SPacketCloneNameCheck msg, PacketByteBuf buf) {
        buf.writeString(msg.name);
        buf.writeInt(msg.tab);
    }

    public static SPacketCloneNameCheck decode(PacketByteBuf buf) {
        return new SPacketCloneNameCheck(buf.readString(Short.MAX_VALUE), buf.readInt());
    }

    @Override
    protected void handle() {
        boolean bo = ServerCloneController.Instance.getCloneData(null, this.name, this.tab) != null;
        NbtCompound compound = new NbtCompound();
        compound.putBoolean("NameExists", bo);
        Packets.send(this.player, new PacketGuiData(compound));
    }
}

