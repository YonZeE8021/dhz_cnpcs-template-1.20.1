/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.packets.server;

import java.util.HashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportCategory;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketTransportGet
extends PacketServerBasic {
    private int id;

    public SPacketTransportGet(int id) {
        this.id = id;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketTransportGet msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketTransportGet decode(PacketByteBuf buf) {
        return new SPacketTransportGet(buf.readInt());
    }

    @Override
    protected void handle() {
        SPacketTransportGet.sendTransportData(this.player, this.id);
    }

    public static void sendTransportData(ServerPlayerEntity player, int categoryid) {
        TransportCategory category = TransportController.getInstance().categories.get(categoryid);
        if (category == null) {
            return;
        }
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (TransportLocation transport : category.locations.values()) {
            map.put(transport.name, transport.id);
        }
        NoppesUtilServer.sendScrollData(player, map);
    }
}

