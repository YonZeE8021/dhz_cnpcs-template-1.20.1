/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.packets.server;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.containers.ContainerManageBanks;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketBankGet
extends PacketServerBasic {
    private int bank;

    public SPacketBankGet(int bank) {
        this.bank = bank;
    }

    public static void encode(SPacketBankGet msg, PacketByteBuf buf) {
        buf.writeInt(msg.bank);
    }

    public static SPacketBankGet decode(PacketByteBuf buf) {
        return new SPacketBankGet(buf.readInt());
    }

    @Override
    protected void handle() {
        SPacketBankGet.sendBank(this.player, BankController.getInstance().getBank(this.bank));
    }

    public static void sendBank(ServerPlayerEntity player, Bank bank) {
        NbtCompound compound = new NbtCompound();
        bank.addAdditionalSaveData(compound);
        Packets.send(player, new PacketGuiData(compound));
        if (player.currentScreenHandler instanceof ContainerManageBanks) {
            ((ContainerManageBanks)player.currentScreenHandler).setBank(bank);
        }
        player.currentScreenHandler.syncState();
    }
}

