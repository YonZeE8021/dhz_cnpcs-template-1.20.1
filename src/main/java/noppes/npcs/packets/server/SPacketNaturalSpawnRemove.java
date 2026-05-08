/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNaturalSpawnRemove
extends PacketServerBasic {
    private int id;

    public SPacketNaturalSpawnRemove(int id) {
        this.id = id;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_NATURALSPAWN;
    }

    public static void encode(SPacketNaturalSpawnRemove msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketNaturalSpawnRemove decode(PacketByteBuf buf) {
        return new SPacketNaturalSpawnRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        SpawnController.instance.removeSpawnData(this.id);
        NoppesUtilServer.sendScrollData(this.player, SpawnController.instance.getScroll());
    }
}

