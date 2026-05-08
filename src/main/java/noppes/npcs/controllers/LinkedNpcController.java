/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NBTTags;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.NBTJsonUtil;

public class LinkedNpcController {
    public static LinkedNpcController Instance;
    public List<LinkedData> list = new ArrayList<LinkedData>();

    public LinkedNpcController() {
        Instance = this;
        this.load();
    }

    private void load() {
        try {
            this.loadNpcs();
        }
        catch (Exception e) {
            LogWriter.except(e);
        }
    }

    public File getDir() {
        File dir = new File(CustomNpcs.getLevelSaveDirectory(), "linkednpcs");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    private void loadNpcs() {
        LogWriter.info("Loading Linked Npcs");
        File dir = this.getDir();
        if (dir.exists()) {
            ArrayList<LinkedData> list = new ArrayList<LinkedData>();
            for (File file : dir.listFiles()) {
                if (!file.getName().endsWith(".json")) continue;
                try {
                    NbtCompound compound = NBTJsonUtil.LoadFile(file);
                    LinkedData linked = new LinkedData();
                    linked.setNBT(compound);
                    list.add(linked);
                }
                catch (Exception e) {
                    LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
                }
            }
            this.list = list;
        }
        LogWriter.info("Done loading Linked Npcs");
    }

    public void save() {
        for (LinkedData npc : this.list) {
            try {
                this.saveNpc(npc);
            }
            catch (IOException e) {
                LogWriter.except(e);
            }
        }
    }

    private void saveNpc(LinkedData npc) throws IOException {
        File file = new File(this.getDir(), npc.name + ".json_new");
        File file1 = new File(this.getDir(), npc.name + ".json");
        try {
            NBTJsonUtil.SaveFile(file, npc.getNBT());
            if (file1.exists()) {
                file1.delete();
            }
            file.renameTo(file1);
        }
        catch (NBTJsonUtil.JsonException e) {
            LogWriter.except(e);
        }
    }

    public void loadNpcData(EntityNPCInterface npc) {
        if (npc.linkedName.isEmpty()) {
            return;
        }
        LinkedData data = this.getData(npc.linkedName);
        if (data == null) {
            npc.linkedLast = 0L;
            npc.linkedName = "";
            npc.linkedData = null;
        } else {
            npc.linkedData = data;
            if (npc.getX() == 0.0 && npc.getY() == 0.0 && npc.getZ() == 0.0) {
                return;
            }
            npc.linkedLast = data.time;
            List<int[]> points = npc.ais.getMovingPath();
            NbtCompound compound = NBTTags.NBTMerge(this.readNpcData(npc), data.data);
            npc.display.readToNBT(compound);
            npc.stats.readToNBT(compound);
            npc.advanced.readToNBT(compound);
            npc.inventory.load(compound);
            if (compound.contains("ModelData")) {
                ((EntityCustomNpc)npc).modelData.load(compound.getCompound("ModelData"));
            }
            npc.ais.readToNBT(compound);
            npc.transform.readToNBT(compound);
            npc.ais.setMovingPath(points);
            npc.updateClient = true;
        }
    }

    private void cleanTags(NbtCompound compound) {
        compound.remove("MovingPathNew");
    }

    public LinkedData getData(String name) {
        for (LinkedData data : this.list) {
            if (!data.name.equalsIgnoreCase(name)) continue;
            return data;
        }
        return null;
    }

    private NbtCompound readNpcData(EntityNPCInterface npc) {
        NbtCompound compound = new NbtCompound();
        npc.display.save(compound);
        npc.inventory.save(compound);
        npc.stats.save(compound);
        npc.ais.save(compound);
        npc.advanced.save(compound);
        npc.transform.save(compound);
        compound.put("ModelData", (NbtElement)((EntityCustomNpc)npc).modelData.save());
        return compound;
    }

    public void saveNpcData(EntityNPCInterface npc) {
        NbtCompound compound = this.readNpcData(npc);
        this.cleanTags(compound);
        if (npc.linkedData.data.equals(compound)) {
            return;
        }
        npc.linkedData.data = compound;
        npc.linkedData.time = System.currentTimeMillis();
        this.save();
    }

    public void removeData(String name) {
        Iterator<LinkedData> ita = this.list.iterator();
        while (ita.hasNext()) {
            if (!ita.next().name.equalsIgnoreCase(name)) continue;
            ita.remove();
        }
        this.save();
    }

    public void addData(String name) {
        if (this.getData(name) != null || name.isEmpty()) {
            return;
        }
        LinkedData data = new LinkedData();
        data.name = name;
        this.list.add(data);
        this.save();
    }

    public static class LinkedData {
        public String name = "LinkedNpc";
        public long time = 0L;
        public NbtCompound data = new NbtCompound();

        public LinkedData() {
            this.time = System.currentTimeMillis();
        }

        public void setNBT(NbtCompound compound) {
            this.name = compound.getString("LinkedName");
            this.data = compound.getCompound("NPCData");
        }

        public NbtCompound getNBT() {
            NbtCompound compound = new NbtCompound();
            compound.putString("LinkedName", this.name);
            compound.put("NPCData", (NbtElement)this.data);
            return compound;
        }
    }
}

