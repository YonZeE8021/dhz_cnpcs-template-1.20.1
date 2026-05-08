/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.text.Text
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.controllers.data;

import java.util.HashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.EventHooks;
import noppes.npcs.constants.EnumQuestCompletion;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketAchievement;
import noppes.npcs.packets.client.PacketChat;
import noppes.npcs.quests.QuestInterface;

public class PlayerQuestData {
    public HashMap<Integer, QuestData> activeQuests = new HashMap();
    public HashMap<Integer, Long> finishedQuests = new HashMap();

    public void loadNBTData(NbtCompound mainCompound) {
        NbtList list2;
        if (mainCompound == null) {
            return;
        }
        NbtCompound compound = mainCompound.getCompound("QuestData");
        NbtList list = compound.getList("CompletedQuests", 10);
        if (list != null) {
            HashMap<Integer, Long> finishedQuests = new HashMap<Integer, Long>();
            for (int i = 0; i < list.size(); ++i) {
                NbtCompound nbttagcompound = list.getCompound(i);
                finishedQuests.put(nbttagcompound.getInt("Quest"), nbttagcompound.getLong("Date"));
            }
            this.finishedQuests = finishedQuests;
        }
        if ((list2 = compound.getList("ActiveQuests", 10)) != null) {
            HashMap<Integer, QuestData> activeQuests = new HashMap<Integer, QuestData>();
            for (int i = 0; i < list2.size(); ++i) {
                NbtCompound nbttagcompound = list2.getCompound(i);
                int id = nbttagcompound.getInt("Quest");
                Quest quest = QuestController.instance.quests.get(id);
                if (quest == null) continue;
                QuestData data = new QuestData(quest);
                data.readAdditionalSaveData(nbttagcompound);
                activeQuests.put(id, data);
            }
            this.activeQuests = activeQuests;
        }
    }

    public void saveNBTData(NbtCompound maincompound) {
        NbtCompound compound = new NbtCompound();
        NbtList list = new NbtList();
        for (int quest : this.finishedQuests.keySet()) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putInt("Quest", quest);
            nbttagcompound.putLong("Date", this.finishedQuests.get(quest).longValue());
            list.add((Object)nbttagcompound);
        }
        compound.put("CompletedQuests", (NbtElement)list);
        NbtList list2 = new NbtList();
        for (int quest : this.activeQuests.keySet()) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putInt("Quest", quest);
            this.activeQuests.get(quest).addAdditionalSaveData(nbttagcompound);
            list2.add((Object)nbttagcompound);
        }
        compound.put("ActiveQuests", (NbtElement)list2);
        maincompound.put("QuestData", (NbtElement)compound);
    }

    public QuestData getQuestCompletion(PlayerEntity player, EntityNPCInterface npc) {
        for (QuestData data : this.activeQuests.values()) {
            Quest quest = data.quest;
            if (quest == null || quest.completion != EnumQuestCompletion.Npc || !quest.completerNpc.equals(npc.getName().getString()) || !quest.questInterface.isCompleted(player)) continue;
            return data;
        }
        return null;
    }

    public boolean checkQuestCompletion(PlayerEntity player, int type) {
        boolean bo = false;
        for (QuestData data : this.activeQuests.values()) {
            if (data.quest.type != type && type >= 0) continue;
            QuestInterface inter = data.quest.questInterface;
            if (inter.isCompleted(player)) {
                if (data.isCompleted) continue;
                if (!data.quest.complete(player, data)) {
                    Packets.send((ServerPlayerEntity)player, new PacketAchievement((Text)Text.translatable((String)"quest.completed"), (Text)Text.translatable((String)data.quest.title), 2));
                    Packets.send((ServerPlayerEntity)player, new PacketChat((Text)Text.translatable((String)"quest.completed").append(": ").append((Text)Text.translatable((String)data.quest.title))));
                }
                data.isCompleted = true;
                bo = true;
                EventHooks.onQuestFinished(PlayerData.get((PlayerEntity)player).scriptData, data.quest);
                continue;
            }
            data.isCompleted = false;
        }
        return bo;
    }
}

