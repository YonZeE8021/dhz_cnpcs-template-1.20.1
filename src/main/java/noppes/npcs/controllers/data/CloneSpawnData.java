/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 */
package noppes.npcs.controllers.data;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import noppes.npcs.controllers.ServerCloneController;

public class CloneSpawnData {
    public int tab;
    public String name;
    private long lastLoaded;
    private NbtCompound compound;

    public CloneSpawnData(int tab, String name) {
        this.name = name;
        this.tab = tab;
    }

    public NbtCompound getCompound() {
        if (this.lastLoaded < ServerCloneController.Instance.lastLoaded) {
            this.compound = ServerCloneController.Instance.getCloneData(null, this.name, this.tab);
            this.lastLoaded = ServerCloneController.Instance.lastLoaded;
        }
        return this.compound;
    }

    public static Map<Integer, CloneSpawnData> load(NbtList list) {
        HashMap<Integer, CloneSpawnData> data = new HashMap<Integer, CloneSpawnData>();
        for (int i = 0; i < list.size(); ++i) {
            NbtCompound c = list.getCompound(i);
            int tab = c.getInt("tab");
            String name = c.getString("name");
            if (ServerCloneController.Instance != null && !ServerCloneController.Instance.hasClone(tab, name)) continue;
            data.put(c.getInt("slot"), new CloneSpawnData(tab, name));
        }
        return data;
    }

    public static NbtList save(Map<Integer, CloneSpawnData> data) {
        NbtList list = new NbtList();
        for (Map.Entry<Integer, CloneSpawnData> entry : data.entrySet()) {
            if (ServerCloneController.Instance != null && !ServerCloneController.Instance.hasClone(entry.getValue().tab, entry.getValue().name)) continue;
            NbtCompound c = new NbtCompound();
            c.putInt("slot", entry.getKey().intValue());
            c.putInt("tab", entry.getValue().tab);
            c.putString("name", entry.getValue().name);
            list.add(c);
        }
        return list;
    }
}

