/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.api.handler.data.IQuestCategory;
import noppes.npcs.controllers.data.Quest;

public class QuestCategory
implements IQuestCategory {
    public HashMap<Integer, Quest> quests = new HashMap();
    public int id = -1;
    public String title = "";

    public void readNBT(NbtCompound nbttagcompound) {
        this.id = nbttagcompound.getInt("Slot");
        this.title = nbttagcompound.getString("Title");
        NbtList dialogsList = nbttagcompound.getList("Dialogs", 10);
        if (dialogsList != null) {
            for (int ii = 0; ii < dialogsList.size(); ++ii) {
                NbtCompound nbttagcompound2 = dialogsList.getCompound(ii);
                Quest quest = new Quest(this);
                quest.readNBT(nbttagcompound2);
                this.quests.put(quest.id, quest);
            }
        }
    }

    public NbtCompound writeNBT(NbtCompound nbttagcompound) {
        nbttagcompound.putInt("Slot", this.id);
        nbttagcompound.putString("Title", this.title);
        NbtList dialogs = new NbtList();
        for (int dialogId : this.quests.keySet()) {
            Quest quest = this.quests.get(dialogId);
            dialogs.add(quest.save(new NbtCompound()));
        }
        nbttagcompound.put("Dialogs", (NbtElement)dialogs);
        return nbttagcompound;
    }

    @Override
    public List<IQuest> quests() {
        return new ArrayList<IQuest>(this.quests.values());
    }

    @Override
    public String getName() {
        return this.title;
    }

    @Override
    public IQuest create() {
        return new Quest(this);
    }
}

