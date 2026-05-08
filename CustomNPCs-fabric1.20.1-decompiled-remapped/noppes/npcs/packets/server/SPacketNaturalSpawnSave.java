/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.controllers.data.SpawnData;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNaturalSpawnSave
extends PacketServerBasic {
    private NbtCompound data;

    public SPacketNaturalSpawnSave(NbtCompound data) {
        this.data = data;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_NATURALSPAWN;
    }

    public static void encode(SPacketNaturalSpawnSave msg, PacketByteBuf buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketNaturalSpawnSave decode(PacketByteBuf buf) {
        return new SPacketNaturalSpawnSave(buf.readNbt());
    }

    @Override
    protected void handle() {
        SpawnData sdata = new SpawnData();
        sdata.readNBT(this.data);
        SpawnController.instance.saveSpawnData(sdata);
        NoppesUtilServer.sendScrollData(this.player, SpawnController.instance.getScroll());
    }
}

