package noppes.npcs.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import noppes.npcs.CustomNpcs;

public class TraderGlobalVariableController {
    public static TraderGlobalVariableController instance;
    private final HashMap<String, Integer> values = new HashMap<>();
    private final HashMap<String, Long> realPeriod = new HashMap<>();
    private final HashMap<String, Long> gamePeriod = new HashMap<>();

    public TraderGlobalVariableController() {
        instance = this;
        this.load();
    }

    public int getValue(String key) {
        return this.values.getOrDefault(key, 0);
    }

    public void setValue(String key, int value) {
        this.values.put(key, value);
    }

    public long getRealPeriod(String key) {
        return this.realPeriod.getOrDefault(key, 0L);
    }

    public void setRealPeriod(String key, long marker) {
        this.realPeriod.put(key, marker);
    }

    public long getGamePeriod(String key) {
        return this.gamePeriod.getOrDefault(key, 0L);
    }

    public void setGamePeriod(String key, long marker) {
        this.gamePeriod.put(key, marker);
    }

    public void remove(String key) {
        this.values.remove(key);
        this.realPeriod.remove(key);
        this.gamePeriod.remove(key);
    }

    public NbtCompound writeNBT() {
        NbtCompound tag = new NbtCompound();
        NbtCompound vals = new NbtCompound();
        for (String key : this.values.keySet()) {
            vals.putInt(key, this.values.get(key));
        }
        tag.put("Values", vals);
        NbtCompound real = new NbtCompound();
        for (String key : this.realPeriod.keySet()) {
            real.putLong(key, this.realPeriod.get(key));
        }
        tag.put("RealPeriod", real);
        NbtCompound game = new NbtCompound();
        for (String key : this.gamePeriod.keySet()) {
            game.putLong(key, this.gamePeriod.get(key));
        }
        tag.put("GamePeriod", game);
        return tag;
    }

    public void readNBT(NbtCompound tag) {
        this.values.clear();
        this.realPeriod.clear();
        this.gamePeriod.clear();
        if (tag.contains("Values", 10)) {
            NbtCompound vals = tag.getCompound("Values");
            for (String key : vals.getKeys()) {
                this.values.put(key, vals.getInt(key));
            }
        }
        if (tag.contains("RealPeriod", 10)) {
            NbtCompound real = tag.getCompound("RealPeriod");
            for (String key : real.getKeys()) {
                this.realPeriod.put(key, real.getLong(key));
            }
        }
        if (tag.contains("GamePeriod", 10)) {
            NbtCompound game = tag.getCompound("GamePeriod");
            for (String key : game.getKeys()) {
                this.gamePeriod.put(key, game.getLong(key));
            }
        }
    }

    private void load() {
        File saveDir = CustomNpcs.getLevelSaveDirectory();
        if (saveDir == null) {
            return;
        }
        try {
            File file = new File(saveDir, "trader_globals.dat");
            if (file.exists()) {
                this.readNBT(NbtIo.readCompressed((InputStream)new FileInputStream(file)));
            }
        }
        catch (Exception ignored) {
        }
    }

    public void saveData() {
        File saveDir = CustomNpcs.getLevelSaveDirectory();
        if (saveDir == null) {
            return;
        }
        try {
            NbtCompound tag = this.writeNBT();
            File file = new File(saveDir, "trader_globals.dat_new");
            File old = new File(saveDir, "trader_globals.dat_old");
            File cur = new File(saveDir, "trader_globals.dat");
            NbtIo.writeCompressed((NbtCompound)tag, (OutputStream)new FileOutputStream(file));
            if (old.exists()) {
                old.delete();
            }
            if (cur.exists()) {
                cur.renameTo(old);
            }
            file.renameTo(cur);
            if (file.exists()) {
                file.delete();
            }
        }
        catch (Exception ignored) {
        }
    }
}
