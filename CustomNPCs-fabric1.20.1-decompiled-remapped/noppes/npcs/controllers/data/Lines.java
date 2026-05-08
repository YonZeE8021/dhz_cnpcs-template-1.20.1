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
import java.util.Map;
import java.util.Random;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.controllers.data.Line;

public class Lines {
    private static final Random random = new Random();
    private int lastLine = -1;
    public HashMap<Integer, Line> lines = new HashMap();

    public NbtCompound save() {
        NbtCompound compound = new NbtCompound();
        NbtList nbttaglist = new NbtList();
        for (int slot : this.lines.keySet()) {
            Line line = this.lines.get(slot);
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putInt("Slot", slot);
            nbttagcompound.putString("Line", line.getText());
            nbttagcompound.putString("Song", line.getSound());
            nbttaglist.add((Object)nbttagcompound);
        }
        compound.put("Lines", (NbtElement)nbttaglist);
        return compound;
    }

    public void readNBT(NbtCompound compound) {
        NbtList nbttaglist = compound.getList("Lines", 10);
        HashMap<Integer, Line> map = new HashMap<Integer, Line>();
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NbtCompound nbttagcompound = nbttaglist.getCompound(i);
            Line line = new Line();
            line.setText(nbttagcompound.getString("Line"));
            line.setSound(nbttagcompound.getString("Song"));
            map.put(nbttagcompound.getInt("Slot"), line);
        }
        this.lines = map;
    }

    public Line getLine(boolean isRandom) {
        if (this.lines.isEmpty()) {
            return null;
        }
        if (isRandom) {
            int i = random.nextInt(this.lines.size());
            for (Map.Entry<Integer, Line> e : this.lines.entrySet()) {
                if (--i >= 0) continue;
                return e.getValue().copy();
            }
        }
        ++this.lastLine;
        while (true) {
            this.lastLine %= 8;
            Line line = this.lines.get(this.lastLine);
            if (line != null) {
                return line.copy();
            }
            ++this.lastLine;
        }
    }

    public boolean isEmpty() {
        return this.lines.isEmpty();
    }
}

