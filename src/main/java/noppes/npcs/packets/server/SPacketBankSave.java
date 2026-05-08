/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.server.SPacketBankGet;
import noppes.npcs.packets.server.SPacketBanksGet;

public class SPacketBankSave
extends PacketServerBasic {
    private NbtCompound data;

    public SPacketBankSave(NbtCompound data) {
        this.data = data;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_BANK;
    }

    public static void encode(SPacketBankSave msg, PacketByteBuf buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketBankSave decode(PacketByteBuf buf) {
        return new SPacketBankSave(buf.readNbt());
    }

    @Override
    protected void handle() {
        Bank bank = new Bank();
        bank.readAdditionalSaveData(this.data);
        BankController.getInstance().saveBank(bank);
        SPacketBanksGet.sendBankDataAll(this.player);
        SPacketBankGet.sendBank(this.player, bank);
    }
}

