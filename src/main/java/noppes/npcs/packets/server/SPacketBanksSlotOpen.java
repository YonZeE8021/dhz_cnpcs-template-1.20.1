/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.BankData;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketBanksSlotOpen
extends PacketServerBasic {
    private final int slot;
    private final int bankId;

    public SPacketBanksSlotOpen(int slot, int bankId) {
        this.slot = slot;
        this.bankId = bankId;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    public static void encode(SPacketBanksSlotOpen msg, PacketByteBuf buf) {
        buf.writeInt(msg.slot);
        buf.writeInt(msg.bankId);
    }

    public static SPacketBanksSlotOpen decode(PacketByteBuf buf) {
        return new SPacketBanksSlotOpen(buf.readInt(), buf.readInt());
    }

    @Override
    protected void handle() {
        if (this.npc.role.getType() != 3) {
            return;
        }
        BankData data = PlayerDataController.instance.getBankData((PlayerEntity)this.player, this.bankId).getBankOrDefault(this.bankId);
        data.openBankGui(this.player, this.npc, this.bankId, this.slot);
    }
}

