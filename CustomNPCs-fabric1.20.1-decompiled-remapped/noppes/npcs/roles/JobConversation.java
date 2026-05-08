/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobInterface;

public class JobConversation
extends JobInterface {
    public Availability availability = new Availability();
    private ArrayList<String> names = new ArrayList();
    private HashMap<String, EntityNPCInterface> npcs = new HashMap();
    public HashMap<Integer, ConversationLine> lines = new HashMap();
    public int quest = -1;
    public String questTitle = "";
    public int generalDelay = 400;
    public int ticks = 100;
    public int range = 20;
    private ConversationLine nextLine;
    private boolean hasStarted = false;
    private int startedTicks = 20;
    public int mode = 0;

    public JobConversation(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public NbtCompound save(NbtCompound compound) {
        compound.put("ConversationAvailability", (NbtElement)this.availability.save(new NbtCompound()));
        compound.putInt("ConversationQuest", this.quest);
        compound.putInt("ConversationDelay", this.generalDelay);
        compound.putInt("ConversationRange", this.range);
        compound.putInt("ConversationMode", this.mode);
        NbtList nbttaglist = new NbtList();
        for (int slot : this.lines.keySet()) {
            ConversationLine line = this.lines.get(slot);
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putInt("Slot", slot);
            line.addAdditionalSaveData(nbttagcompound);
            nbttaglist.add((Object)nbttagcompound);
        }
        compound.put("ConversationLines", (NbtElement)nbttaglist);
        if (this.hasQuest()) {
            compound.putString("ConversationQuestTitle", this.getQuest().title);
        }
        return compound;
    }

    @Override
    public void load(NbtCompound compound) {
        this.names.clear();
        this.availability.load(compound.getCompound("ConversationAvailability"));
        this.quest = compound.getInt("ConversationQuest");
        this.generalDelay = compound.getInt("ConversationDelay");
        this.questTitle = compound.getString("ConversationQuestTitle");
        this.range = compound.getInt("ConversationRange");
        this.mode = compound.getInt("ConversationMode");
        NbtList nbttaglist = compound.getList("ConversationLines", 10);
        HashMap<Integer, ConversationLine> map = new HashMap<Integer, ConversationLine>();
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NbtCompound nbttagcompound = nbttaglist.getCompound(i);
            ConversationLine line = new ConversationLine();
            line.readAdditionalSaveData(nbttagcompound);
            if (!line.npc.isEmpty() && !this.names.contains(line.npc.toLowerCase())) {
                this.names.add(line.npc.toLowerCase());
            }
            map.put(nbttagcompound.getInt("Slot"), line);
        }
        this.lines = map;
        this.ticks = this.generalDelay;
    }

    public boolean hasQuest() {
        return this.getQuest() != null;
    }

    public Quest getQuest() {
        if (this.npc.isClientSide()) {
            return null;
        }
        return QuestController.instance.quests.get(this.quest);
    }

    @Override
    public void aiUpdateTask() {
        --this.ticks;
        if (this.ticks > 0 || this.nextLine == null) {
            return;
        }
        this.say(this.nextLine);
        boolean seenNext = false;
        ConversationLine compare = this.nextLine;
        this.nextLine = null;
        for (ConversationLine line : this.lines.values()) {
            if (line.isEmpty()) continue;
            if (seenNext) {
                this.nextLine = line;
                break;
            }
            if (line != compare) continue;
            seenNext = true;
        }
        if (this.nextLine != null) {
            this.ticks = this.nextLine.delay;
        } else if (this.hasQuest()) {
            List inRange = this.npc.getWorld().getNonSpectatingEntities(PlayerEntity.class, this.npc.getBoundingBox().expand((double)this.range, (double)this.range, (double)this.range));
            for (PlayerEntity player : inRange) {
                if (!this.availability.isAvailable(player)) continue;
                PlayerQuestController.addActiveQuest(this.getQuest(), player);
            }
        }
    }

    @Override
    public boolean aiShouldExecute() {
        if (this.lines.isEmpty() || this.npc.isKilled() || this.npc.isAttacking() || !this.shouldRun()) {
            return false;
        }
        if (!this.hasStarted && this.mode == 1) {
            if (this.startedTicks-- > 0) {
                return false;
            }
            this.startedTicks = 10;
            if (this.npc.getWorld().getNonSpectatingEntities(PlayerEntity.class, this.npc.getBoundingBox().expand((double)this.range, (double)this.range, (double)this.range)).isEmpty()) {
                return false;
            }
        }
        for (ConversationLine line : this.lines.values()) {
            if (line == null || line.isEmpty()) continue;
            this.nextLine = line;
            break;
        }
        return this.nextLine != null;
    }

    private boolean shouldRun() {
        boolean bo;
        --this.ticks;
        if (this.ticks > 0) {
            return false;
        }
        this.npcs.clear();
        List list = this.npc.getWorld().getNonSpectatingEntities(EntityNPCInterface.class, this.npc.getBoundingBox().expand(10.0, 10.0, 10.0));
        for (EntityNPCInterface npc : list) {
            String name = npc.getName().getString().toLowerCase();
            if (npc.isKilled() || npc.isAttacking() || !this.names.contains(name)) continue;
            this.npcs.put(name, npc);
        }
        boolean bl = bo = this.names.size() == this.npcs.size();
        if (!bo) {
            this.ticks = 20;
        }
        return bo;
    }

    @Override
    public boolean aiContinueExecute() {
        for (EntityNPCInterface npc : this.npcs.values()) {
            if (!npc.isKilled() && !npc.isAttacking()) continue;
            return false;
        }
        return this.nextLine != null;
    }

    @Override
    public void stop() {
        this.nextLine = null;
        this.ticks = this.generalDelay;
        this.hasStarted = false;
    }

    @Override
    public void aiStartExecuting() {
        this.startedTicks = 20;
        this.hasStarted = true;
    }

    private void say(ConversationLine line) {
        List inRange = this.npc.getWorld().getNonSpectatingEntities(PlayerEntity.class, this.npc.getBoundingBox().expand((double)this.range, (double)this.range, (double)this.range));
        EntityNPCInterface npc = this.npcs.get(line.npc.toLowerCase());
        if (npc == null) {
            return;
        }
        for (PlayerEntity player : inRange) {
            if (!this.availability.isAvailable(player)) continue;
            npc.say(player, line);
        }
    }

    @Override
    public void reset() {
        this.hasStarted = false;
        this.stop();
        this.ticks = 60;
    }

    @Override
    public void killed() {
        this.reset();
    }

    public ConversationLine getLine(int slot) {
        if (this.lines.containsKey(slot)) {
            return this.lines.get(slot);
        }
        ConversationLine line = new ConversationLine();
        this.lines.put(slot, line);
        return line;
    }

    @Override
    public int getType() {
        return 7;
    }

    public class ConversationLine
    extends Line {
        public String npc = "";
        public int delay = 40;

        public void addAdditionalSaveData(NbtCompound compound) {
            compound.putString("Line", this.text);
            compound.putString("Npc", this.npc);
            compound.putString("Sound", this.sound);
            compound.putInt("Delay", this.delay);
        }

        public void readAdditionalSaveData(NbtCompound compound) {
            this.text = compound.getString("Line");
            this.npc = compound.getString("Npc");
            this.sound = compound.getString("Sound");
            this.delay = compound.getInt("Delay");
        }

        public boolean isEmpty() {
            return this.npc.isEmpty() || this.text.isEmpty();
        }
    }
}

