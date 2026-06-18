package noppes.npcs.roles;

import net.minecraft.nbt.NbtCompound;

public class TraderVariableDef {
    public static final int RESET_NONE = 0;
    public static final int RESET_REAL = 1;
    public static final int RESET_GAME = 2;

    public String name = "";
    public int initialValue = 0;
    public int resetType = RESET_NONE;
    public int resetTime = 0;
    public int resetTimeUnit = TraderTimeUnit.DAY;
    public int maxValue = -1;
    public boolean playerVisible = false;

    public boolean isGlobal() {
        return this.name != null && this.name.startsWith("global.");
    }

    public String getStorageKey() {
        return this.isGlobal() ? this.name.substring("global.".length()) : this.name;
    }

    public NbtCompound writeNBT() {
        NbtCompound tag = new NbtCompound();
        tag.putString("Name", this.name);
        tag.putInt("Initial", this.initialValue);
        tag.putInt("ResetType", this.resetType);
        tag.putInt("ResetTime", this.resetTime);
        tag.putInt("ResetUnit", this.resetTimeUnit);
        tag.putInt("Max", this.maxValue);
        tag.putBoolean("Visible", this.playerVisible);
        return tag;
    }

    public void readNBT(NbtCompound tag) {
        this.name = tag.getString("Name");
        this.initialValue = tag.getInt("Initial");
        this.resetType = tag.getInt("ResetType");
        this.resetTime = tag.getInt("ResetTime");
        this.resetTimeUnit = tag.getInt("ResetUnit");
        this.maxValue = tag.getInt("Max");
        this.playerVisible = tag.getBoolean("Visible");
    }
}
