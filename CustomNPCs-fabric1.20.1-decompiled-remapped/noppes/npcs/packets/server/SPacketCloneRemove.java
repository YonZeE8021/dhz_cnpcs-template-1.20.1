/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.server.SPacketCloneList;

public class SPacketCloneRemove
extends PacketServerBasic {
    private String name;
    private int tab;

    public SPacketCloneRemove(String name, int tab) {
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

    public static void encode(SPacketCloneRemove msg, PacketByteBuf buf) {
        buf.writeString(msg.name);
        buf.writeInt(msg.tab);
    }

    public static SPacketCloneRemove decode(PacketByteBuf buf) {
        return new SPacketCloneRemove(buf.readString(Short.MAX_VALUE), buf.readInt());
    }

    @Override
    protected void handle() {
        ServerCloneController.Instance.removeClone(this.name, this.tab);
        SPacketCloneList.sendList(this.player, this.tab);
    }
}

