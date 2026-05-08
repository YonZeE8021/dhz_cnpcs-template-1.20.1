/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.text.Text
 */
package noppes.npcs.quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import noppes.npcs.NBTTags;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.quests.QuestInterface;

public class QuestManual
extends QuestInterface {
    public TreeMap<String, Integer> manuals = new TreeMap();

    @Override
    public void readAdditionalSaveData(NbtCompound compound) {
        this.manuals = new TreeMap<String, Integer>(NBTTags.getStringIntegerMap(compound.getList("QuestManual", 10)));
    }

    @Override
    public void addAdditionalSaveData(NbtCompound compound) {
        compound.put("QuestManual", (NbtElement)NBTTags.nbtStringIntegerMap(this.manuals));
    }

    @Override
    public boolean isCompleted(PlayerEntity player) {
        PlayerQuestData playerdata = PlayerData.get((PlayerEntity)player).questData;
        QuestData data = playerdata.activeQuests.get(this.questId);
        if (data == null) {
            return false;
        }
        HashMap<String, Integer> manual = this.getManual(data);
        if (manual.size() != this.manuals.size()) {
            return false;
        }
        for (String entity : manual.keySet()) {
            if (this.manuals.containsKey(entity) && this.manuals.get(entity) <= manual.get(entity)) continue;
            return false;
        }
        return true;
    }

    @Override
    public void handleComplete(PlayerEntity player) {
    }

    public HashMap<String, Integer> getManual(QuestData data) {
        return NBTTags.getStringIntegerMap(data.extraData.getList("Manual", 10));
    }

    public void setManual(QuestData data, HashMap<String, Integer> manual) {
        data.extraData.put("Manual", (NbtElement)NBTTags.nbtStringIntegerMap(manual));
    }

    @Override
    public IQuestObjective[] getObjectives(PlayerEntity player) {
        ArrayList<QuestManualObjective> list = new ArrayList<QuestManualObjective>();
        for (Map.Entry<String, Integer> entry : this.manuals.entrySet()) {
            list.add(new QuestManualObjective(player, entry.getKey(), entry.getValue()));
        }
        return list.toArray(new IQuestObjective[list.size()]);
    }

    class QuestManualObjective
    implements IQuestObjective {
        private final PlayerEntity player;
        private final String entity;
        private final int amount;

        public QuestManualObjective(PlayerEntity player, String entity, int amount) {
            this.player = player;
            this.entity = entity;
            this.amount = amount;
        }

        @Override
        public int getProgress() {
            PlayerData data = PlayerData.get(this.player);
            PlayerQuestData playerdata = data.questData;
            QuestData questdata = playerdata.activeQuests.get(QuestManual.this.questId);
            HashMap<String, Integer> manual = QuestManual.this.getManual(questdata);
            if (!manual.containsKey(this.entity)) {
                return 0;
            }
            return manual.get(this.entity);
        }

        @Override
        public void setProgress(int progress) {
            if (progress < 0 || progress > this.amount) {
                throw new CustomNPCsException("Progress has to be between 0 and " + this.amount, new Object[0]);
            }
            PlayerData data = PlayerData.get(this.player);
            PlayerQuestData playerdata = data.questData;
            QuestData questdata = playerdata.activeQuests.get(QuestManual.this.questId);
            HashMap<String, Integer> manual = QuestManual.this.getManual(questdata);
            if (manual.containsKey(this.entity) && manual.get(this.entity) == progress) {
                return;
            }
            manual.put(this.entity, progress);
            QuestManual.this.setManual(questdata, manual);
            data.questData.checkQuestCompletion(this.player, 5);
            data.updateClient = true;
        }

        @Override
        public int getMaxProgress() {
            return this.amount;
        }

        @Override
        public boolean isCompleted() {
            return this.getProgress() >= this.amount;
        }

        @Override
        public String getText() {
            return this.getMCText().getString();
        }

        @Override
        public Text getMCText() {
            return Text.translatable((String)this.entity).append(": " + this.getProgress() + "/" + this.getMaxProgress());
        }
    }
}

