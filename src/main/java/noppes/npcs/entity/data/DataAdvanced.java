/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.sound.SoundCategory
 */
package noppes.npcs.entity.data;

import java.util.HashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.sound.SoundCategory;
import noppes.npcs.api.entity.data.INPCAdvanced;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.FactionOptions;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.Lines;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketPlaySound;
import noppes.npcs.roles.JobBard;
import noppes.npcs.roles.JobBuilder;
import noppes.npcs.roles.JobChunkLoader;
import noppes.npcs.roles.JobConversation;
import noppes.npcs.roles.JobFarmer;
import noppes.npcs.roles.JobFollower;
import noppes.npcs.roles.JobGuard;
import noppes.npcs.roles.JobHealer;
import noppes.npcs.roles.JobInterface;
import noppes.npcs.roles.JobItemGiver;
import noppes.npcs.roles.JobPuppet;
import noppes.npcs.roles.JobSpawner;
import noppes.npcs.roles.RoleBank;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleDialog;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.roles.RoleInterface;
import noppes.npcs.roles.RolePostman;
import noppes.npcs.roles.RoleTrader;
import noppes.npcs.roles.RoleTransporter;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.util.ValueUtil;

public class DataAdvanced
implements INPCAdvanced {
    public Lines interactLines = new Lines();
    public Lines worldLines = new Lines();
    public Lines attackLines = new Lines();
    public Lines killedLines = new Lines();
    public Lines killLines = new Lines();
    public Lines npcInteractLines = new Lines();
    public boolean orderedLines = false;
    private String idleSound = "";
    private String angrySound = "";
    private String hurtSound = "minecraft:entity.player.hurt";
    private String deathSound = "minecraft:entity.player.hurt";
    private String stepSound = "";
    private EntityNPCInterface npc;
    public FactionOptions factions = new FactionOptions();
    public boolean attackOtherFactions = false;
    public boolean defendFaction = false;
    public boolean disablePitch = false;
    public DataScenes scenes;

    public DataAdvanced(EntityNPCInterface npc) {
        this.npc = npc;
        this.scenes = new DataScenes(npc);
    }

    public NbtCompound save(NbtCompound compound) {
        compound.put("NpcLines", (NbtElement)this.worldLines.save());
        compound.put("NpcKilledLines", (NbtElement)this.killedLines.save());
        compound.put("NpcInteractLines", (NbtElement)this.interactLines.save());
        compound.put("NpcAttackLines", (NbtElement)this.attackLines.save());
        compound.put("NpcKillLines", (NbtElement)this.killLines.save());
        compound.put("NpcInteractNPCLines", (NbtElement)this.npcInteractLines.save());
        compound.putBoolean("OrderedLines", this.orderedLines);
        compound.putString("NpcIdleSound", this.idleSound);
        compound.putString("NpcAngrySound", this.angrySound);
        compound.putString("NpcHurtSound", this.hurtSound);
        compound.putString("NpcDeathSound", this.deathSound);
        compound.putString("NpcStepSound", this.stepSound);
        compound.putInt("FactionID", this.npc.getFaction().id);
        compound.putBoolean("AttackOtherFactions", this.attackOtherFactions);
        compound.putBoolean("DefendFaction", this.defendFaction);
        compound.putBoolean("DisablePitch", this.disablePitch);
        compound.putInt("Role", this.npc.role.getType());
        compound.putInt("NpcJob", this.npc.job.getType());
        compound.put("FactionPoints", (NbtElement)this.factions.save(new NbtCompound()));
        compound.put("NPCDialogOptions", (NbtElement)this.nbtDialogs(this.npc.dialogs));
        compound.put("NpcScenes", (NbtElement)this.scenes.save(new NbtCompound()));
        return compound;
    }

    public void readToNBT(NbtCompound compound) {
        this.interactLines.readNBT(compound.getCompound("NpcInteractLines"));
        this.worldLines.readNBT(compound.getCompound("NpcLines"));
        this.attackLines.readNBT(compound.getCompound("NpcAttackLines"));
        this.killedLines.readNBT(compound.getCompound("NpcKilledLines"));
        this.killLines.readNBT(compound.getCompound("NpcKillLines"));
        this.npcInteractLines.readNBT(compound.getCompound("NpcInteractNPCLines"));
        this.orderedLines = compound.getBoolean("OrderedLines");
        this.idleSound = compound.getString("NpcIdleSound");
        this.angrySound = compound.getString("NpcAngrySound");
        this.hurtSound = compound.getString("NpcHurtSound");
        this.deathSound = compound.getString("NpcDeathSound");
        this.stepSound = compound.getString("NpcStepSound");
        this.npc.setFaction(compound.getInt("FactionID"));
        this.npc.faction = this.npc.getFaction();
        this.attackOtherFactions = compound.getBoolean("AttackOtherFactions");
        this.defendFaction = compound.getBoolean("DefendFaction");
        this.disablePitch = compound.getBoolean("DisablePitch");
        this.setRole(compound.getInt("Role"));
        this.setJob(compound.getInt("NpcJob"));
        this.factions.load(compound.getCompound("FactionPoints"));
        this.npc.dialogs = this.getDialogs(compound.getList("NPCDialogOptions", 10));
        this.scenes.load(compound.getCompound("NpcScenes"));
    }

    private HashMap<Integer, DialogOption> getDialogs(NbtList tagList) {
        HashMap<Integer, DialogOption> map = new HashMap<Integer, DialogOption>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            int slot = nbttagcompound.getInt("DialogSlot");
            DialogOption option = new DialogOption();
            option.readNBT(nbttagcompound.getCompound("NPCDialog"));
            option.optionType = 1;
            map.put(slot, option);
        }
        return map;
    }

    private NbtList nbtDialogs(HashMap<Integer, DialogOption> dialogs2) {
        NbtList nbttaglist = new NbtList();
        for (int slot : dialogs2.keySet()) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putInt("DialogSlot", slot);
            nbttagcompound.put("NPCDialog", (NbtElement)dialogs2.get(slot).writeNBT());
            nbttaglist.add(nbttagcompound);
        }
        return nbttaglist;
    }

    private Lines getLines(int type) {
        if (type == 0) {
            return this.interactLines;
        }
        if (type == 1) {
            return this.attackLines;
        }
        if (type == 2) {
            return this.worldLines;
        }
        if (type == 3) {
            return this.killedLines;
        }
        if (type == 4) {
            return this.killLines;
        }
        if (type == 5) {
            return this.npcInteractLines;
        }
        return null;
    }

    @Override
    public void setLine(int type, int slot, String text, String sound) {
        slot = ValueUtil.CorrectInt(slot, 0, 7);
        Lines lines = this.getLines(type);
        if (text == null || text.isEmpty()) {
            lines.lines.remove(slot);
        } else {
            Line line = lines.lines.get(slot);
            if (line == null) {
                line = new Line();
                lines.lines.put(slot, line);
            }
            line.setText(text);
            line.setSound(sound);
        }
    }

    @Override
    public String getLine(int type, int slot) {
        Line line = this.getLines((int)type).lines.get(slot);
        if (line == null) {
            return "";
        }
        return line.getText();
    }

    @Override
    public int getLineCount(int type) {
        return this.getLines((int)type).lines.size();
    }

    @Override
    public String getSound(int type) {
        String sound = null;
        if (type == 0) {
            sound = this.idleSound;
        } else if (type == 1) {
            sound = this.angrySound;
        } else if (type == 2) {
            sound = this.hurtSound;
        } else if (type == 3) {
            sound = this.deathSound;
        } else if (type == 4) {
            sound = this.stepSound;
        }
        if (sound != null && sound.isEmpty()) {
            return null;
        }
        return NoppesStringUtils.cleanResource(sound);
    }

    public void playSound(int type, float volume, float pitch) {
        String sound = this.getSound(type);
        if (sound == null) {
            return;
        }
        BlockPos pos = this.npc.getBlockPos();
        if (!this.npc.getWorld().isClient) {
            Packets.sendNearby(this.npc.getWorld(), pos, 16, new PacketPlaySound(sound, pos, volume, pitch));
        } else {
            MusicController.Instance.playSound(SoundCategory.VOICE, sound, pos, volume, pitch);
        }
    }

    @Override
    public void setSound(int type, String sound) {
        if (sound == null) {
            sound = "";
        }
        sound = NoppesStringUtils.cleanResource(sound);
        if (type == 0) {
            this.idleSound = sound;
        } else if (type == 1) {
            this.angrySound = sound;
        } else if (type == 2) {
            this.hurtSound = sound;
        } else if (type == 3) {
            this.deathSound = sound;
        } else if (type == 4) {
            this.stepSound = sound;
        }
    }

    public Line getInteractLine() {
        return this.interactLines.getLine(!this.orderedLines);
    }

    public Line getAttackLine() {
        return this.attackLines.getLine(!this.orderedLines);
    }

    public Line getKilledLine() {
        return this.killedLines.getLine(!this.orderedLines);
    }

    public Line getKillLine() {
        return this.killLines.getLine(!this.orderedLines);
    }

    public Line getLevelLine() {
        return this.worldLines.getLine(!this.orderedLines);
    }

    public Line getNPCInteractLine() {
        return this.npcInteractLines.getLine(!this.orderedLines);
    }

    public void setRole(int role) {
        if (8 <= role) {
            role -= 2;
        }
        if ((role %= 8) == 0) {
            this.npc.role = RoleInterface.NONE;
        } else if (role == 3 && !(this.npc.role instanceof RoleBank)) {
            this.npc.role = new RoleBank(this.npc);
        } else if (role == 2 && !(this.npc.role instanceof RoleFollower)) {
            this.npc.role = new RoleFollower(this.npc);
        } else if (role == 5 && !(this.npc.role instanceof RolePostman)) {
            this.npc.role = new RolePostman(this.npc);
        } else if (role == 1 && !(this.npc.role instanceof RoleTrader)) {
            this.npc.role = new RoleTrader(this.npc);
        } else if (role == 4 && !(this.npc.role instanceof RoleTransporter)) {
            this.npc.role = new RoleTransporter(this.npc);
        } else if (role == 6 && !(this.npc.role instanceof RoleCompanion)) {
            this.npc.role = new RoleCompanion(this.npc);
        } else if (role == 7 && !(this.npc.role instanceof RoleDialog)) {
            this.npc.role = new RoleDialog(this.npc);
        }
    }

    public void setJob(int job) {
        if (!this.npc.getWorld().isClient) {
            this.npc.job.reset();
        }
        if ((job %= 12) == 0) {
            this.npc.job = JobInterface.NONE;
        } else if (job == 1 && !(this.npc.job instanceof JobBard)) {
            this.npc.job = new JobBard(this.npc);
        } else if (job == 2 && !(this.npc.job instanceof JobHealer)) {
            this.npc.job = new JobHealer(this.npc);
        } else if (job == 3 && !(this.npc.job instanceof JobGuard)) {
            this.npc.job = new JobGuard(this.npc);
        } else if (job == 4 && !(this.npc.job instanceof JobItemGiver)) {
            this.npc.job = new JobItemGiver(this.npc);
        } else if (job == 5 && !(this.npc.job instanceof JobFollower)) {
            this.npc.job = new JobFollower(this.npc);
        } else if (job == 6 && !(this.npc.job instanceof JobSpawner)) {
            this.npc.job = new JobSpawner(this.npc);
        } else if (job == 7 && !(this.npc.job instanceof JobConversation)) {
            this.npc.job = new JobConversation(this.npc);
        } else if (job == 8 && !(this.npc.job instanceof JobChunkLoader)) {
            this.npc.job = new JobChunkLoader(this.npc);
        } else if (job == 9 && !(this.npc.job instanceof JobPuppet)) {
            this.npc.job = new JobPuppet(this.npc);
        } else if (job == 10 && !(this.npc.job instanceof JobBuilder)) {
            this.npc.job = new JobBuilder(this.npc);
        } else if (job == 11 && !(this.npc.job instanceof JobFarmer)) {
            this.npc.job = new JobFarmer(this.npc);
        }
    }

    public boolean hasLevelLines() {
        return !this.worldLines.isEmpty();
    }
}

