/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketMenuClose
extends PacketServerBasic {
    @Override
    public boolean requiresNpc() {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketMenuClose msg, PacketByteBuf buf) {
    }

    public static SPacketMenuClose decode(PacketByteBuf buf) {
        return new SPacketMenuClose();
    }

    @Override
    protected void handle() {
        this.npc.reset();
        if (this.npc.linkedData != null) {
            LinkedNpcController.Instance.saveNpcData(this.npc);
        }
        NoppesUtilServer.setEditingNpc((PlayerEntity)this.player, null);
    }
}

