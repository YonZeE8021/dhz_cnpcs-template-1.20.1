/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketPlayerMailGet
extends PacketServerBasic {
    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketPlayerMailGet msg, PacketByteBuf buf) {
    }

    public static SPacketPlayerMailGet decode(PacketByteBuf buf) {
        return new SPacketPlayerMailGet();
    }

    @Override
    protected void handle() {
        PlayerMailData data = PlayerData.get((PlayerEntity)this.player).mailData;
        Packets.send(this.player, new PacketGuiData(data.saveNBTData(new NbtCompound())));
    }
}

