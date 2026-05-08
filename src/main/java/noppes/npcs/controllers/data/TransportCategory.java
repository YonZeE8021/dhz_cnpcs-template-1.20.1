/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.controllers.data;

import java.util.HashMap;
import java.util.Vector;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.controllers.data.TransportLocation;

public class TransportCategory {
    public int id = -1;
    public String title = "";
    public HashMap<Integer, TransportLocation> locations = new HashMap();

    public Vector<TransportLocation> getDefaultLocations() {
        Vector<TransportLocation> list = new Vector<TransportLocation>();
        for (TransportLocation loc : this.locations.values()) {
            if (!loc.isDefault()) continue;
            list.add(loc);
        }
        return list;
    }

    public void readNBT(NbtCompound compound) {
        this.id = compound.getInt("CategoryId");
        this.title = compound.getString("CategoryTitle");
        NbtList locs = compound.getList("CategoryLocations", 10);
        if (locs == null || locs.size() == 0) {
            return;
        }
        for (int ii = 0; ii < locs.size(); ++ii) {
            TransportLocation location = new TransportLocation();
            location.readNBT(locs.getCompound(ii));
            location.category = this;
            this.locations.put(location.id, location);
        }
    }

    public void writeNBT(NbtCompound compound) {
        compound.putInt("CategoryId", this.id);
        compound.putString("CategoryTitle", this.title);
        NbtList locs = new NbtList();
        for (TransportLocation location : this.locations.values()) {
            locs.add(location.writeNBT());
        }
        compound.put("CategoryLocations", (NbtElement)locs);
    }
}

