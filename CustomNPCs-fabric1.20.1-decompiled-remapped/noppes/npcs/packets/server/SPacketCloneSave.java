/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketCloneSave
extends PacketServerBasic {
    private String name;
    private int tab;

    public SPacketCloneSave(String name, int tab) {
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

    public static void encode(SPacketCloneSave msg, PacketByteBuf buf) {
        buf.writeString(msg.name);
        buf.writeInt(msg.tab);
    }

    public static SPacketCloneSave decode(PacketByteBuf buf) {
        return new SPacketCloneSave(buf.readString(Short.MAX_VALUE), buf.readInt());
    }

    @Override
    protected void handle() {
        PlayerData data = PlayerData.get((PlayerEntity)this.player);
        if (data.cloned == null) {
            return;
        }
        ServerCloneController.Instance.addClone(data.cloned, this.name, this.tab);
    }
}

