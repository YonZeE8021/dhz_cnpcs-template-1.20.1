/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.packets.server;

import java.util.HashMap;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportCategory;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketTransportCategoriesGet
extends PacketServerBasic {
    public static void encode(SPacketTransportCategoriesGet msg, PacketByteBuf buf) {
    }

    public static SPacketTransportCategoriesGet decode(PacketByteBuf buf) {
        return new SPacketTransportCategoriesGet();
    }

    @Override
    protected void handle() {
        SPacketTransportCategoriesGet.sendTransportCategoryData(this.player);
    }

    public static void sendTransportCategoryData(ServerPlayerEntity player) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (TransportCategory category : TransportController.getInstance().categories.values()) {
            map.put(category.title, category.id);
        }
        NoppesUtilServer.sendScrollData(player, map);
    }
}

