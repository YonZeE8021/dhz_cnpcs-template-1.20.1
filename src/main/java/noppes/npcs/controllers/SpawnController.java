/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.util.collection.Weighting
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.collection.Weighting;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.SpawnData;
import noppes.npcs.shared.common.util.LogWriter;

public class SpawnController {
    public HashMap<Identifier, ArrayList<SpawnData>> biomes = new HashMap();
    public ArrayList<SpawnData> data = new ArrayList();
    public Random random = Random.create();
    public static SpawnController instance;
    private int lastUsedID = 0;

    public SpawnController() {
        instance = this;
        this.loadData();
    }

    private void loadData() {
        File saveDir = CustomNpcs.getLevelSaveDirectory();
        if (saveDir == null) {
            return;
        }
        try {
            File file = new File(saveDir, "spawns.dat");
            if (file.exists()) {
                this.loadDataFile(file);
            }
        }
        catch (Exception e) {
            try {
                File file = new File(saveDir, "spawns.dat_old");
                if (file.exists()) {
                    this.loadDataFile(file);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private void loadDataFile(File file) throws IOException {
        DataInputStream var1 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
        this.loadData(var1);
        var1.close();
    }

    public void loadData(DataInputStream stream) throws IOException {
        ArrayList<SpawnData> data = new ArrayList<SpawnData>();
        NbtCompound nbttagcompound1 = NbtIo.read((DataInput)stream);
        this.lastUsedID = nbttagcompound1.getInt("lastID");
        NbtList nbtlist = nbttagcompound1.getList("NPCSpawnData", 10);
        if (nbtlist != null) {
            for (int i = 0; i < nbtlist.size(); ++i) {
                NbtCompound nbttagcompound = nbtlist.getCompound(i);
                SpawnData spawn = new SpawnData();
                spawn.readNBT(nbttagcompound);
                data.add(spawn);
            }
        }
        this.data = data;
        this.fillBiomeData();
    }

    public NbtCompound getNBT() {
        NbtList list = new NbtList();
        for (SpawnData spawn : this.data) {
            NbtCompound nbtfactions = new NbtCompound();
            spawn.writeNBT(nbtfactions);
            list.add(nbtfactions);
        }
        NbtCompound nbttagcompound = new NbtCompound();
        nbttagcompound.putInt("lastID", this.lastUsedID);
        nbttagcompound.put("NPCSpawnData", (NbtElement)list);
        return nbttagcompound;
    }

    public void saveData() {
        try {
            File saveDir = CustomNpcs.getLevelSaveDirectory();
            File file = new File(saveDir, "spawns.dat_new");
            File file1 = new File(saveDir, "spawns.dat_old");
            File file2 = new File(saveDir, "spawns.dat");
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

    public SpawnData getSpawnData(int id) {
        for (SpawnData spawn : this.data) {
            if (spawn.id != id) continue;
            return spawn;
        }
        return null;
    }

    public void saveSpawnData(SpawnData spawn) {
        SpawnData original;
        if (spawn.id < 0) {
            spawn.id = this.getUnusedId();
        }
        if ((original = this.getSpawnData(spawn.id)) == null) {
            this.data.add(spawn);
        } else {
            original.readNBT(spawn.writeNBT(new NbtCompound()));
        }
        this.fillBiomeData();
        this.saveData();
    }

    private void fillBiomeData() {
        HashMap<Identifier, ArrayList<SpawnData>> biomes = new HashMap<Identifier, ArrayList<SpawnData>>();
        for (SpawnData spawn : this.data) {
            for (Identifier s : spawn.biomes) {
                ArrayList<SpawnData> list = (ArrayList<SpawnData>)biomes.get(s);
                if (list == null) {
                    list = new ArrayList<SpawnData>();
                    biomes.put(s, list);
                }
                list.add(spawn);
            }
        }
        this.biomes = biomes;
    }

    public int getUnusedId() {
        ++this.lastUsedID;
        return this.lastUsedID;
    }

    public void removeSpawnData(int id) {
        ArrayList<SpawnData> data = new ArrayList<SpawnData>();
        for (SpawnData spawn : this.data) {
            if (spawn.id == id) continue;
            data.add(spawn);
        }
        this.data = data;
        this.fillBiomeData();
        this.saveData();
    }

    public List<SpawnData> getSpawnList(Identifier biome) {
        return this.biomes.get(biome);
    }

    public SpawnData getRandomSpawnData(Identifier biome) {
        List<SpawnData> list = this.getSpawnList(biome);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return Weighting.getRandom((Random)this.random, list).orElse(null);
    }

    public boolean hasSpawnList(Identifier biome) {
        return this.biomes.containsKey(biome) && !this.biomes.get(biome).isEmpty();
    }

    public Map<String, Integer> getScroll() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (SpawnData spawn : this.data) {
            map.put(spawn.name, spawn.id);
        }
        return map;
    }
}

