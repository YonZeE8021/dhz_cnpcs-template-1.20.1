/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtIo
 */
package noppes.npcs.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import noppes.npcs.CustomNpcs;

public class GlobalDataController {
    public static GlobalDataController instance;
    private int itemGiverId = 0;

    public GlobalDataController() {
        instance = this;
        this.load();
    }

    private void load() {
        File saveDir = CustomNpcs.getLevelSaveDirectory();
        try {
            File file = new File(saveDir, "global.dat");
            if (file.exists()) {
                this.loadData(file);
            }
        }
        catch (Exception e) {
            try {
                File file = new File(saveDir, "global.dat_old");
                if (file.exists()) {
                    this.loadData(file);
                }
            }
            catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    private void loadData(File file) throws Exception {
        NbtCompound nbttagcompound1 = NbtIo.readCompressed((InputStream)new FileInputStream(file));
        this.itemGiverId = nbttagcompound1.getInt("itemGiverId");
    }

    public void saveData() {
        try {
            File saveDir = CustomNpcs.getLevelSaveDirectory();
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putInt("itemGiverId", this.itemGiverId);
            File file = new File(saveDir, "global.dat_new");
            File file1 = new File(saveDir, "global.dat_old");
            File file2 = new File(saveDir, "global.dat");
            NbtIo.writeCompressed((NbtCompound)nbttagcompound, (OutputStream)new FileOutputStream(file));
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
            e.printStackTrace();
        }
    }

    public int incrementItemGiverId() {
        ++this.itemGiverId;
        this.saveData();
        return this.itemGiverId;
    }
}

