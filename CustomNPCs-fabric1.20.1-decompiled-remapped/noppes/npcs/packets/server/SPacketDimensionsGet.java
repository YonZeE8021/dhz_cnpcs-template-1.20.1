/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.registry.RegistryKey
 */
package noppes.npcs.packets.server;

import java.util.HashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketDimensionsGet
extends PacketServerBasic {
    @Override
    public boolean toolAllowed(ItemStack item) {
        return item.getItem() == CustomItems.teleporter;
    }

    public static void encode(SPacketDimensionsGet msg, PacketByteBuf buf) {
    }

    public static SPacketDimensionsGet decode(PacketByteBuf buf) {
        return new SPacketDimensionsGet();
    }

    @Override
    protected void handle() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (RegistryKey key : CustomNpcs.Server.getWorldRegistryKeys()) {
            map.put(key.getValue().toString(), 0);
        }
        NoppesUtilServer.sendScrollData(this.player, map);
    }
}

