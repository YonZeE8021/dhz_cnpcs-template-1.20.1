/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.collection.Weight
 *  net.minecraft.util.collection.Weighted
 */
package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Weight;
import net.minecraft.util.collection.Weighted;
import noppes.npcs.NBTTags;
import noppes.npcs.controllers.data.CloneSpawnData;

public class SpawnData
implements Weighted {
    public List<Identifier> biomes = new ArrayList<Identifier>();
    public int id = -1;
    public String name = "";
    public Map<Integer, CloneSpawnData> data = new HashMap<Integer, CloneSpawnData>();
    public boolean liquid = false;
    public int type = 0;
    private Weight weight = Weight.of((int)10);

    public void readNBT(NbtCompound compound) {
        this.id = compound.getInt("SpawnId");
        this.name = compound.getString("SpawnName");
        this.setWeight(compound.getInt("SpawnWeight"));
        this.biomes = NBTTags.getResourceLocationList(compound.getList("SpawnBiomes", 10));
        this.data = CloneSpawnData.load(compound.getList("SpawnData", 10));
        this.type = compound.getInt("SpawnType");
    }

    public NbtCompound writeNBT(NbtCompound compound) {
        compound.putInt("SpawnId", this.id);
        compound.putString("SpawnName", this.name);
        compound.putInt("SpawnWeight", this.weight.getValue());
        compound.put("SpawnBiomes", (NbtElement)NBTTags.nbtResourceLocationList(this.biomes));
        compound.put("SpawnData", (NbtElement)CloneSpawnData.save(this.data));
        compound.putInt("SpawnType", this.type);
        return compound;
    }

    public void setWeight(int weight) {
        if (weight == 0) {
            weight = 1;
        }
        this.weight = Weight.of((int)weight);
    }

    public void setClone(int slot, int tab, String name) {
        this.data.put(slot, new CloneSpawnData(tab, name));
    }

    public NbtCompound getCompound(int slot) {
        CloneSpawnData sd = this.data.get(slot);
        if (sd == null) {
            return null;
        }
        return sd.getCompound();
    }

    public Weight getWeight() {
        return this.weight;
    }
}

