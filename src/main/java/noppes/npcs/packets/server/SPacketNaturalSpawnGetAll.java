/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.network.PacketByteBuf;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNaturalSpawnGetAll
extends PacketServerBasic {
    public static void encode(SPacketNaturalSpawnGetAll msg, PacketByteBuf buf) {
    }

    public static SPacketNaturalSpawnGetAll decode(PacketByteBuf buf) {
        return new SPacketNaturalSpawnGetAll();
    }

    @Override
    protected void handle() {
        NoppesUtilServer.sendScrollData(this.player, SpawnController.instance.getScroll());
    }
}

