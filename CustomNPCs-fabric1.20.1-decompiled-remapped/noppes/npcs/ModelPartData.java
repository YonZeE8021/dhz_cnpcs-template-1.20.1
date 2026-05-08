/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.util.Identifier
 */
package noppes.npcs;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class ModelPartData {
    private static Map<String, Identifier> resources = new HashMap<String, Identifier>();
    public int color = 0xFFFFFF;
    public int colorPattern = 0xFFFFFF;
    public byte type = 0;
    public byte pattern = 0;
    public boolean playerTexture = false;
    public String name;
    private Identifier location;

    public ModelPartData(String name) {
        this.name = name;
    }

    public NbtCompound save() {
        NbtCompound compound = new NbtCompound();
        compound.putByte("Type", this.type);
        compound.putInt("Color", this.color);
        compound.putBoolean("PlayerTexture", this.playerTexture);
        compound.putByte("Pattern", this.pattern);
        return compound;
    }

    public void load(NbtCompound compound) {
        if (!compound.contains("Type")) {
            this.type = (byte)-1;
            return;
        }
        this.type = compound.getByte("Type");
        this.color = compound.getInt("Color");
        this.playerTexture = compound.getBoolean("PlayerTexture");
        this.pattern = compound.getByte("Pattern");
        this.location = null;
    }

    public Identifier getResource() {
        if (this.location != null) {
            return this.location;
        }
        String texture = this.name + "/" + this.type;
        this.location = resources.get(texture);
        if (this.location != null) {
            return this.location;
        }
        this.location = new Identifier("moreplayermodels:textures/" + texture + ".png");
        resources.put(texture, this.location);
        return this.location;
    }

    public void setType(int type) {
        this.type = (byte)type;
        this.location = null;
    }

    public String toString() {
        return "Color: " + this.color + " Type: " + this.type;
    }

    public String getColor() {
        Object str = Integer.toHexString(this.color);
        while (((String)str).length() < 6) {
            str = "0" + (String)str;
        }
        return str;
    }
}

