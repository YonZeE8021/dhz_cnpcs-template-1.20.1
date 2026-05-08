/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketRemoteMenuOpen
extends PacketServerBasic {
    private int entityId;

    public SPacketRemoteMenuOpen(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketRemoteMenuOpen msg, PacketByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static SPacketRemoteMenuOpen decode(PacketByteBuf buf) {
        return new SPacketRemoteMenuOpen(buf.readInt());
    }

    @Override
    protected void handle() {
        Entity entity = this.player.getWorld().getEntityById(this.entityId);
        if (entity == null || !(entity instanceof EntityNPCInterface)) {
            return;
        }
        NoppesUtilServer.sendOpenGui((PlayerEntity)this.player, EnumGuiType.MainMenuDisplay, (EntityNPCInterface)entity);
    }
}

