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
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketBanksGet
extends PacketServerBasic {
    public static void encode(SPacketBanksGet msg, PacketByteBuf buf) {
    }

    public static SPacketBanksGet decode(PacketByteBuf buf) {
        return new SPacketBanksGet();
    }

    @Override
    protected void handle() {
        SPacketBanksGet.sendBankDataAll(this.player);
    }

    public static void sendBankDataAll(ServerPlayerEntity player) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (Bank bank : BankController.getInstance().banks.values()) {
            map.put(bank.name, bank.id);
        }
        NoppesUtilServer.sendScrollData(player, map);
    }
}

