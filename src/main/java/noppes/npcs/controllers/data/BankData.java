/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.controllers.data;

import java.util.HashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerNPCBankInterface;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.util.CustomNPCsScheduler;

public class BankData {
    public HashMap<Integer, NpcMiscInventory> itemSlots = new HashMap();
    public HashMap<Integer, Boolean> upgradedSlots = new HashMap();
    public int unlockedSlots = 0;
    public int bankId = -1;

    public BankData() {
        for (int i = 0; i < 6; ++i) {
            this.itemSlots.put(i, new NpcMiscInventory(54));
            this.upgradedSlots.put(i, false);
        }
    }

    public void readNBT(NbtCompound nbttagcompound) {
        this.bankId = nbttagcompound.getInt("DataBankId");
        this.unlockedSlots = nbttagcompound.getInt("UnlockedSlots");
        this.itemSlots = this.getItemSlots(nbttagcompound.getList("BankInv", 10));
        this.upgradedSlots = NBTTags.getBooleanList(nbttagcompound.getList("UpdatedSlots", 10));
    }

    private HashMap<Integer, NpcMiscInventory> getItemSlots(NbtList tagList) {
        HashMap<Integer, NpcMiscInventory> list = new HashMap<Integer, NpcMiscInventory>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            int slot = nbttagcompound.getInt("Slot");
            NpcMiscInventory inv = new NpcMiscInventory(54);
            inv.setFromNBT(nbttagcompound.getCompound("BankItems"));
            list.put(slot, inv);
        }
        return list;
    }

    public void writeNBT(NbtCompound nbttagcompound) {
        nbttagcompound.putInt("DataBankId", this.bankId);
        nbttagcompound.putInt("UnlockedSlots", this.unlockedSlots);
        nbttagcompound.put("UpdatedSlots", (NbtElement)NBTTags.nbtBooleanList(this.upgradedSlots));
        nbttagcompound.put("BankInv", (NbtElement)this.nbtItemSlots(this.itemSlots));
    }

    private NbtList nbtItemSlots(HashMap<Integer, NpcMiscInventory> items) {
        NbtList list = new NbtList();
        for (int slot : items.keySet()) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putInt("Slot", slot);
            nbttagcompound.put("BankItems", (NbtElement)items.get(slot).getToNBT());
            list.add(nbttagcompound);
        }
        return list;
    }

    public boolean isUpgraded(Bank bank, int slot) {
        if (bank.isUpgraded(slot)) {
            return true;
        }
        return bank.canBeUpgraded(slot) && this.upgradedSlots.get(slot) != false;
    }

    public void openBankGui(ServerPlayerEntity player, EntityNPCInterface npc, int bankId, int slot) {
        Bank bank = BankController.getInstance().getBank(bankId);
        if (bank.getMaxSlots() <= slot) {
            return;
        }
        if (bank.startSlots > this.unlockedSlots) {
            this.unlockedSlots = bank.startSlots;
        }
        ItemStack currency = ItemStack.EMPTY;
        if (this.unlockedSlots <= slot) {
            currency = bank.currencyInventory.getStack(slot);
            NoppesUtilServer.openContainerGui(player, EnumGuiType.PlayerBankUnlock, buf -> {
                buf.writeInt(slot);
                buf.writeInt(bank.id);
            });
        } else if (this.isUpgraded(bank, slot)) {
            NoppesUtilServer.openContainerGui(player, EnumGuiType.PlayerBankLarge, buf -> {
                buf.writeInt(slot);
                buf.writeInt(bank.id);
            });
        } else if (bank.canBeUpgraded(slot)) {
            currency = bank.upgradeInventory.getStack(slot);
            NoppesUtilServer.openContainerGui(player, EnumGuiType.PlayerBankUprade, buf -> {
                buf.writeInt(slot);
                buf.writeInt(bank.id);
            });
        } else {
            NoppesUtilServer.openContainerGui(player, EnumGuiType.PlayerBankSmall, buf -> {
                buf.writeInt(slot);
                buf.writeInt(bank.id);
            });
        }
        ItemStack item = currency;
        CustomNPCsScheduler.runTack(() -> {
            NbtCompound compound = new NbtCompound();
            compound.putInt("MaxSlots", bank.getMaxSlots());
            compound.putInt("UnlockedSlots", this.unlockedSlots);
            if (item != null && !item.isEmpty()) {
                compound.put("Currency", (NbtElement)item.writeNbt(new NbtCompound()));
                ContainerNPCBankInterface container = this.getContainer((PlayerEntity)player);
                if (container != null) {
                    container.setCurrency(item);
                }
            }
            Packets.send(player, new PacketGuiData(compound));
        }, 300);
    }

    private ContainerNPCBankInterface getContainer(PlayerEntity player) {
        ScreenHandler con = player.currentScreenHandler;
        if (con == null || !(con instanceof ContainerNPCBankInterface)) {
            return null;
        }
        return (ContainerNPCBankInterface)con;
    }
}

