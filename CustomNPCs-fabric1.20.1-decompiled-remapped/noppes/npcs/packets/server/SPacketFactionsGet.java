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
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketFactionsGet
extends PacketServerBasic {
    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketFactionsGet msg, PacketByteBuf buf) {
    }

    public static SPacketFactionsGet decode(PacketByteBuf buf) {
        return new SPacketFactionsGet();
    }

    @Override
    protected void handle() {
        SPacketFactionsGet.sendFactionDataAll(this.player);
    }

    public static void sendFactionDataAll(ServerPlayerEntity player) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (Faction faction : FactionController.instance.factions.values()) {
            map.put(faction.name, faction.id);
        }
        NoppesUtilServer.sendScrollData(player, map);
    }
}

