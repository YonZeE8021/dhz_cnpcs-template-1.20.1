/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.server.SPacketBankGet;
import noppes.npcs.packets.server.SPacketBanksGet;

public class SPacketBankRemove
extends PacketServerBasic {
    private int bank;

    public SPacketBankRemove(int bank) {
        this.bank = bank;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_BANK;
    }

    public static void encode(SPacketBankRemove msg, PacketByteBuf buf) {
        buf.writeInt(msg.bank);
    }

    public static SPacketBankRemove decode(PacketByteBuf buf) {
        return new SPacketBankRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        BankController.getInstance().removeBank(this.bank);
        SPacketBanksGet.sendBankDataAll(this.player);
        SPacketBankGet.sendBank(this.player, new Bank());
    }
}

