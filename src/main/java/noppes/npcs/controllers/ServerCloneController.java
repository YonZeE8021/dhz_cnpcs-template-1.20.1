/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.world.World
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.text.Text
 */
package noppes.npcs.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.IWorld;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.handler.ICloneHandler;
import noppes.npcs.packets.server.SPacketToolMobSpawner;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.NBTJsonUtil;

public class ServerCloneController
implements ICloneHandler {
    public long lastLoaded = System.currentTimeMillis();
    public static ServerCloneController Instance;

    public ServerCloneController() {
        this.loadClones();
    }

    private void loadClones() {
        try {
            File dir = new File(this.getDir(), "..");
            File file = new File(dir, "clonednpcs.dat");
            if (file.exists()) {
                Map<Integer, Map<String, NbtCompound>> clones = this.loadOldClones(file);
                file.delete();
                file = new File(dir, "clonednpcs.dat_old");
                if (file.exists()) {
                    file.delete();
                }
                for (int tab : clones.keySet()) {
                    Map<String, NbtCompound> map = clones.get(tab);
                    for (String name : map.keySet()) {
                        this.saveClone(tab, name, map.get(name));
                    }
                }
            }
        }
        catch (Exception e) {
            LogWriter.except(e);
        }
    }

    public File getDir() {
        File dir = new File(CustomNpcs.getLevelSaveDirectory(), "clones");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    private Map<Integer, Map<String, NbtCompound>> loadOldClones(File file) throws Exception {
        HashMap<Integer, Map<String, NbtCompound>> clones = new HashMap<Integer, Map<String, NbtCompound>>();
        NbtCompound nbttagcompound1 = NbtIo.readCompressed((InputStream)new FileInputStream(file));
        NbtList list = nbttagcompound1.getList("Data", 10);
        if (list == null) {
            return clones;
        }
        for (int i = 0; i < list.size(); ++i) {
            HashMap<String, NbtCompound> tab;
            NbtCompound compound = list.getCompound(i);
            if (!compound.contains("ClonedTab")) {
                compound.putInt("ClonedTab", 1);
            }
            if ((tab = (HashMap<String, NbtCompound>)clones.get(compound.getInt("ClonedTab"))) == null) {
                tab = new HashMap<String, NbtCompound>();
                clones.put(compound.getInt("ClonedTab"), tab);
            }
            String name = compound.getString("ClonedName");
            int number = 1;
            while (tab.containsKey(name)) {
                name = String.format("%s%s", compound.getString("ClonedName"), ++number);
            }
            compound.remove("ClonedName");
            compound.remove("ClonedTab");
            compound.remove("ClonedDate");
            this.cleanTags(compound);
            tab.put(name, compound);
        }
        return clones;
    }

    public NbtCompound getCloneData(ServerCommandSource player, String name, int tab) {
        File file = new File(new File(this.getDir(), "" + tab), name + ".json");
        if (!file.exists()) {
            if (player != null) {
                player.sendError((Text)Text.literal((String)"Could not find clone file"));
            }
            return null;
        }
        try {
            return NBTJsonUtil.LoadFile(file);
        }
        catch (Exception e) {
            LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
            if (player != null) {
                player.sendError((Text)Text.literal((String)e.getMessage()));
            }
            return null;
        }
    }

    public void saveClone(int tab, String name, NbtCompound compound) {
        try {
            File dir = new File(this.getDir(), "" + tab);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String filename = name + ".json";
            File file = new File(dir, filename + "_new");
            File file2 = new File(dir, filename);
            NBTJsonUtil.SaveFile(file, compound);
            if (file2.exists()) {
                file2.delete();
            }
            file.renameTo(file2);
            this.lastLoaded = System.currentTimeMillis();
        }
        catch (Exception e) {
            LogWriter.except(e);
        }
    }

    public List<String> getClones(int tab) {
        ArrayList<String> list = new ArrayList<String>();
        File dir = new File(this.getDir(), "" + tab);
        if (!dir.exists() || !dir.isDirectory()) {
            return list;
        }
        for (String file : dir.list()) {
            if (!file.endsWith(".json")) continue;
            list.add(file.substring(0, file.length() - 5));
        }
        return list;
    }

    public boolean removeClone(String name, int tab) {
        File file = new File(new File(this.getDir(), "" + tab), name + ".json");
        if (!file.exists()) {
            return false;
        }
        file.delete();
        return true;
    }

    public String addClone(NbtCompound nbttagcompound, String name, int tab) {
        this.cleanTags(nbttagcompound);
        this.saveClone(tab, name, nbttagcompound);
        return name;
    }

    public void cleanTags(NbtCompound nbttagcompound) {
        NbtCompound adv;
        if (nbttagcompound.contains("ItemGiverId")) {
            nbttagcompound.putInt("ItemGiverId", 0);
        }
        if (nbttagcompound.contains("TransporterId")) {
            nbttagcompound.putInt("TransporterId", -1);
        }
        nbttagcompound.remove("StartPosNew");
        nbttagcompound.remove("StartPos");
        nbttagcompound.remove("MovingPathNew");
        nbttagcompound.remove("Pos");
        nbttagcompound.remove("Riding");
        nbttagcompound.remove("UUID");
        nbttagcompound.remove("UUIDMost");
        nbttagcompound.remove("UUIDLeast");
        if (!nbttagcompound.contains("ModRev")) {
            nbttagcompound.putInt("ModRev", 1);
        }
        if (nbttagcompound.contains("TransformRole")) {
            adv = nbttagcompound.getCompound("TransformRole");
            adv.putInt("TransporterId", -1);
            nbttagcompound.put("TransformRole", (NbtElement)adv);
        }
        if (nbttagcompound.contains("TransformJob")) {
            adv = nbttagcompound.getCompound("TransformJob");
            adv.putInt("ItemGiverId", 0);
            nbttagcompound.put("TransformJob", (NbtElement)adv);
        }
        if (nbttagcompound.contains("TransformAI")) {
            adv = nbttagcompound.getCompound("TransformAI");
            adv.remove("StartPosNew");
            adv.remove("StartPos");
            adv.remove("MovingPathNew");
            nbttagcompound.put("TransformAI", (NbtElement)adv);
        }
        if (nbttagcompound.contains("id")) {
            String id = nbttagcompound.getString("id");
            if (!CustomNpcs.FixUpdateFromPre_1_12) {
                id = id.replace("customnpcs.", "customnpcs:");
            }
            nbttagcompound.putString("id", id);
        }
    }

    @Override
    public IEntity spawn(double x, double y, double z, int tab, String name, IWorld level) {
        NbtCompound compound = this.getCloneData(null, name, tab);
        if (compound == null) {
            throw new CustomNPCsException("Unknown clone tab:" + tab + " name:" + name, new Object[0]);
        }
        Entity entity = SPacketToolMobSpawner.spawnClone(compound, x, y, z, (World)level.getMCLevel());
        if (entity == null) {
            return null;
        }
        return NpcAPI.Instance().getIEntity(entity);
    }

    @Override
    public IEntity get(int tab, String name, IWorld level) {
        NbtCompound compound = this.getCloneData(null, name, tab);
        if (compound == null) {
            throw new CustomNPCsException("Unknown clone tab:" + tab + " name:" + name, new Object[0]);
        }
        Instance.cleanTags(compound);
        Entity entity = EntityType.getEntityFromNbt((NbtCompound)compound, (World)level.getMCLevel()).orElse(null);
        if (entity == null) {
            return null;
        }
        return NpcAPI.Instance().getIEntity(entity);
    }

    @Override
    public void set(int tab, String name, IEntity entity) {
        NbtCompound compound = new NbtCompound();
        if (!entity.getMCEntity().saveSelfNbt(compound)) {
            throw new CustomNPCsException("Cannot save dead entities", new Object[0]);
        }
        this.cleanTags(compound);
        this.saveClone(tab, name, compound);
    }

    @Override
    public void remove(int tab, String name) {
        this.removeClone(name, tab);
    }

    public boolean hasClone(int tab, String name) {
        return this.getCloneData(null, name, tab) != null;
    }
}

