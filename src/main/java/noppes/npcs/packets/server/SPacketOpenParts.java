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
import noppes.npcs.api.gui.ModelMenu;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketOpenParts
extends PacketServerBasic {
    @Override
    public boolean requiresNpc() {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketOpenParts msg, PacketByteBuf buf) {
    }

    public static SPacketOpenParts decode(PacketByteBuf buf) {
        return new SPacketOpenParts();
    }

    @Override
    protected void handle() {
        ModelMenu.open((PlayerEntity)this.player, (EntityCustomNpc)this.npc);
    }
}

