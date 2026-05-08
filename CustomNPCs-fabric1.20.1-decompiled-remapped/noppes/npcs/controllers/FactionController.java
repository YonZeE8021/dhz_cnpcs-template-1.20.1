/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.controllers;

import java.io.BufferedInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.handler.IFactionHandler;
import noppes.npcs.api.handler.data.IFaction;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncRemove;
import noppes.npcs.packets.client.PacketSyncUpdate;
import noppes.npcs.shared.common.util.LogWriter;

public class FactionController
implements IFactionHandler {
    public HashMap<Integer, Faction> factionsSync = new HashMap();
    public HashMap<Integer, Faction> factions = new HashMap();
    public static FactionController instance = new FactionController();
    private int lastUsedID = 0;

    public FactionController() {
        instance = this;
        this.factions.put(0, new Faction(0, "Friendly", 56576, 2000));
        this.factions.put(1, new Faction(1, "Neutral", 15916288, 1000));
        this.factions.put(2, new Faction(2, "Aggressive", 0xDD0000, 0));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void load() {
        this.factions = new HashMap();
        this.lastUsedID = 0;
        try {
            File saveDir = CustomNpcs.getLevelSaveDirectory();
            if (saveDir == null) {
                return;
            }
            try {
                File file = new File(saveDir, "factions.dat");
                if (file.exists()) {
                    this.loadFactionsFile(file);
                }
            }
            catch (Exception e) {
                try {
                    File file = new File(saveDir, "factions.dat_old");
                    if (file.exists()) {
                        this.loadFactionsFile(file);
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        finally {
            EventHooks.onGlobalFactionsLoaded(this);
            if (this.factions.isEmpty()) {
                this.factions.put(0, new Faction(0, "Friendly", 56576, 2000));
                this.factions.put(1, new Faction(1, "Neutral", 15916288, 1000));
                this.factions.put(2, new Faction(2, "Aggressive", 0xDD0000, 0));
            }
        }
    }

    private void loadFactionsFile(File file) throws IOException {
        DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
        this.loadFactions(var1);
        var1.close();
    }

    public void loadFactions(DataInputStream stream) throws IOException {
        HashMap<Integer, Faction> factions = new HashMap<Integer, Faction>();
        NbtCompound nbttagcompound1 = NbtIo.read((DataInput)stream);
        this.lastUsedID = nbttagcompound1.getInt("lastID");
        NbtList list = nbttagcompound1.getList("NPCFactions", 10);
        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                NbtCompound nbttagcompound = list.getCompound(i);
                Faction faction = new Faction();
                faction.readNBT(nbttagcompound);
                factions.put(faction.id, faction);
            }
        }
        this.factions = factions;
    }

    public NbtCompound getNBT() {
        NbtList list = new NbtList();
        for (int slot : this.factions.keySet()) {
            Faction faction = this.factions.get(slot);
            NbtCompound nbtfactions = new NbtCompound();
            faction.writeNBT(nbtfactions);
            list.add((Object)nbtfactions);
        }
        NbtCompound nbttagcompound = new NbtCompound();
        nbttagcompound.putInt("lastID", this.lastUsedID);
        nbttagcompound.put("NPCFactions", (NbtElement)list);
        return nbttagcompound;
    }

    public void saveFactions() {
        try {
            File saveDir = CustomNpcs.getLevelSaveDirectory();
            File file = new File(saveDir, "factions.dat_new");
            File file1 = new File(saveDir, "factions.dat_old");
            File file2 = new File(saveDir, "factions.dat");
            NbtIo.writeCompressed((NbtCompound)this.getNBT(), (OutputStream)new FileOutputStream(file));
            if (file1.exists()) {
                file1.delete();
            }
            file2.renameTo(file1);
            if (file2.exists()) {
                file2.delete();
            }
            file.renameTo(file2);
            if (file.exists()) {
                file.delete();
            }
        }
        catch (Exception e) {
            LogWriter.except(e);
        }
    }

    public Faction getFaction(int faction) {
        return this.factions.get(faction);
    }

    public void saveFaction(Faction faction) {
        if (faction.id < 0) {
            faction.id = this.getUnusedId();
            while (this.hasName(faction.name)) {
                faction.name = faction.name + "_";
            }
        } else {
            Faction existing = this.factions.get(faction.id);
            if (existing != null && !existing.name.equals(faction.name)) {
                while (this.hasName(faction.name)) {
                    faction.name = faction.name + "_";
                }
            }
        }
        this.factions.remove(faction.id);
        this.factions.put(faction.id, faction);
        Packets.sendAll(new PacketSyncUpdate(faction.id, 1, faction.writeNBT(new NbtCompound())));
        this.saveFactions();
    }

    public int getUnusedId() {
        if (this.lastUsedID == 0) {
            for (int catid : this.factions.keySet()) {
                if (catid <= this.lastUsedID) continue;
                this.lastUsedID = catid;
            }
        }
        ++this.lastUsedID;
        return this.lastUsedID;
    }

    @Override
    public IFaction delete(int id) {
        if (id < 0 || this.factions.size() <= 1) {
            return null;
        }
        Faction faction = this.factions.remove(id);
        if (faction == null) {
            return null;
        }
        this.saveFactions();
        faction.id = -1;
        Packets.sendAll(new PacketSyncRemove(id, 1));
        return faction;
    }

    public int getFirstFactionId() {
        return this.factions.keySet().iterator().next();
    }

    public Faction getFirstFaction() {
        return this.factions.values().iterator().next();
    }

    public boolean hasName(String newName) {
        if (newName.trim().isEmpty()) {
            return true;
        }
        for (Faction faction : this.factions.values()) {
            if (!faction.name.equals(newName)) continue;
            return true;
        }
        return false;
    }

    public Faction getFactionFromName(String factioname) {
        for (Map.Entry<Integer, Faction> entryfaction : this.factions.entrySet()) {
            if (!entryfaction.getValue().name.equalsIgnoreCase(factioname)) continue;
            return entryfaction.getValue();
        }
        return null;
    }

    public String[] getNames() {
        String[] names = new String[this.factions.size()];
        int i = 0;
        for (Faction faction : this.factions.values()) {
            names[i] = faction.name.toLowerCase();
            ++i;
        }
        return names;
    }

    @Override
    public List<IFaction> list() {
        return new ArrayList<IFaction>(this.factions.values());
    }

    @Override
    public IFaction create(String name, int color) {
        Faction faction = new Faction();
        while (this.hasName((String)name)) {
            name = (String)name + "_";
        }
        faction.name = name;
        faction.color = color;
        this.saveFaction(faction);
        return faction;
    }

    @Override
    public IFaction get(int id) {
        return this.factions.get(id);
    }
}

