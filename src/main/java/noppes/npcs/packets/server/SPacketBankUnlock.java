/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.EventHooks;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.containers.ContainerNPCBankInterface;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.data.BankData;
import noppes.npcs.controllers.data.PlayerBankData;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketBankUnlock
extends PacketServerBasic {
    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    public static void encode(SPacketBankUnlock msg, PacketByteBuf buf) {
    }

    public static SPacketBankUnlock decode(PacketByteBuf buf) {
        return new SPacketBankUnlock();
    }

    @Override
    protected void handle() {
        if (this.npc.role.getType() != 3) {
            return;
        }
        ScreenHandler con = this.player.currentScreenHandler;
        if (con == null || !(con instanceof ContainerNPCBankInterface)) {
            return;
        }
        ContainerNPCBankInterface container = (ContainerNPCBankInterface)con;
        Bank bank = BankController.getInstance().getBank(container.bankid);
        ItemStack item = bank.currencyInventory.getStack(container.slot);
        if (item == null || item.isEmpty()) {
            return;
        }
        int price = item.getCount();
        ItemStack currency = container.currencyMatrix.getStack(0);
        if (currency == null || currency.isEmpty() || price > currency.getCount()) {
            return;
        }
        if (currency.getCount() - price == 0) {
            container.currencyMatrix.setStack(0, ItemStack.EMPTY);
        } else {
            currency = currency.split(price);
        }
        this.player.closeHandledScreen();
        PlayerBankData data = PlayerDataController.instance.getBankData((PlayerEntity)this.player, bank.id);
        BankData bankData = data.getBank(bank.id);
        if (bankData.unlockedSlots + 1 <= bank.maxSlots) {
            ++bankData.unlockedSlots;
        }
        RoleEvent.BankUnlockedEvent event = new RoleEvent.BankUnlockedEvent((PlayerEntity)this.player, this.npc.wrappedNPC, container.slot);
        EventHooks.onNPCRole(this.npc, event);
        bankData.openBankGui(this.player, this.npc, bank.id, container.slot);
    }
}

