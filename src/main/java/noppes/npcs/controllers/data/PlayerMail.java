/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventories
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.collection.DefaultedList
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtString
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.controllers.data;

import java.util.ArrayList;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.data.IPlayerMail;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Quest;

public class PlayerMail
implements IPlayerMail,
Inventory {
    public String subject = "";
    public String sender = "";
    public NbtCompound message = new NbtCompound();
    public long time = 0L;
    public boolean beenRead = false;
    public int questId = -1;
    public DefaultedList<ItemStack> items = DefaultedList.ofSize((int)4, ItemStack.EMPTY);
    public long timePast;

    public void readNBT(NbtCompound compound) {
        this.subject = compound.getString("Subject");
        this.sender = compound.getString("Sender");
        this.time = compound.getLong("Time");
        this.beenRead = compound.getBoolean("BeenRead");
        this.message = compound.getCompound("Message");
        this.timePast = compound.getLong("TimePast");
        if (compound.contains("MailQuest")) {
            this.questId = compound.getInt("MailQuest");
        }
        this.items.clear();
        NbtList nbttaglist = compound.getList("MailItems", 10);
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NbtCompound nbttagcompound1 = nbttaglist.getCompound(i);
            int j = nbttagcompound1.getByte("Slot") & 0xFF;
            if (j < 0 || j >= this.items.size()) continue;
            this.items.set(j, ItemStack.fromNbt((NbtCompound)nbttagcompound1));
        }
    }

    public NbtCompound writeNBT() {
        NbtCompound compound = new NbtCompound();
        compound.putString("Subject", this.subject);
        compound.putString("Sender", this.sender);
        compound.putLong("Time", this.time);
        compound.putBoolean("BeenRead", this.beenRead);
        compound.put("Message", (NbtElement)this.message);
        compound.putLong("TimePast", System.currentTimeMillis() - this.time);
        compound.putInt("MailQuest", this.questId);
        if (this.hasQuest()) {
            compound.putString("MailQuestTitle", this.getQuest().title);
        }
        NbtList nbttaglist = new NbtList();
        for (int i = 0; i < this.items.size(); ++i) {
            if (((ItemStack)this.items.get(i)).isEmpty()) continue;
            NbtCompound nbttagcompound1 = new NbtCompound();
            nbttagcompound1.putByte("Slot", (byte)i);
            ((ItemStack)this.items.get(i)).writeNbt(nbttagcompound1);
            nbttaglist.add(nbttagcompound1);
        }
        compound.put("MailItems", (NbtElement)nbttaglist);
        return compound;
    }

    public boolean isValid() {
        return !this.subject.isEmpty() && !this.message.isEmpty() && !this.sender.isEmpty();
    }

    public boolean hasQuest() {
        return this.getQuest() != null;
    }

    @Override
    public Quest getQuest() {
        return QuestController.instance != null ? QuestController.instance.quests.get(this.questId) : null;
    }

    public int size() {
        return 4;
    }

    public int getMaxCountPerStack() {
        return 64;
    }

    public ItemStack getStack(int i) {
        return (ItemStack)this.items.get(i);
    }

    public ItemStack removeStack(int index, int count) {
        ItemStack itemstack = Inventories.splitStack(this.items, (int)index, (int)count);
        if (!itemstack.isEmpty()) {
            this.markDirty();
        }
        return itemstack;
    }

    public ItemStack removeStack(int var1) {
        return (ItemStack)this.items.set(var1, ItemStack.EMPTY);
    }

    public void setStack(int index, ItemStack stack) {
        this.items.set(index, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        this.markDirty();
    }

    public void markDirty() {
    }

    public boolean canPlayerUse(PlayerEntity var1) {
        return true;
    }

    public void onOpen(PlayerEntity player) {
    }

    public void onClose(PlayerEntity player) {
    }

    public boolean isValid(int var1, ItemStack var2) {
        return true;
    }

    public PlayerMail copy() {
        PlayerMail mail = new PlayerMail();
        mail.readNBT(this.writeNBT());
        return mail;
    }

    public boolean isEmpty() {
        for (int slot = 0; slot < this.size(); ++slot) {
            ItemStack item = this.getStack(slot);
            if (NoppesUtilServer.IsItemStackNull(item) || item.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public String getSender() {
        return this.sender;
    }

    @Override
    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String getSubject() {
        return this.subject;
    }

    @Override
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String[] getText() {
        ArrayList<String> list = new ArrayList<String>();
        NbtList pages = this.message.getList("pages", 8);
        for (int i = 0; i < pages.size(); ++i) {
            list.add(pages.getString(i));
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public void setText(String[] pages) {
        NbtList list = new NbtList();
        if (pages != null && pages.length > 0) {
            for (String page : pages) {
                list.add(NbtString.of((String)page));
            }
        }
        this.message.put("pages", (NbtElement)list);
    }

    @Override
    public void setQuest(int id) {
        this.questId = id;
    }

    @Override
    public IContainer getContainer() {
        return NpcAPI.Instance().getIContainer(this);
    }

    public void clear() {
    }
}

