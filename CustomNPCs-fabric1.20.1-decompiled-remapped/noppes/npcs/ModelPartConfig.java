/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs;

import net.minecraft.nbt.NbtCompound;

public class ModelPartConfig {
    public float scaleX = 1.0f;
    public float scaleY = 1.0f;
    public float scaleZ = 1.0f;
    public float transX = 0.0f;
    public float transY = 0.0f;
    public float transZ = 0.0f;
    public boolean notShared = false;

    public NbtCompound writeToNBT() {
        NbtCompound compound = new NbtCompound();
        compound.putFloat("ScaleX", this.scaleX);
        compound.putFloat("ScaleY", this.scaleY);
        compound.putFloat("ScaleZ", this.scaleZ);
        compound.putFloat("TransX", this.transX);
        compound.putFloat("TransY", this.transY);
        compound.putFloat("TransZ", this.transZ);
        compound.putBoolean("NotShared", this.notShared);
        return compound;
    }

    public void readFromNBT(NbtCompound compound) {
        this.scaleX = this.checkValue(compound.getFloat("ScaleX"), 0.0f, 2.0f);
        this.scaleY = this.checkValue(compound.getFloat("ScaleY"), 0.0f, 2.0f);
        this.scaleZ = this.checkValue(compound.getFloat("ScaleZ"), 0.0f, 2.0f);
        this.transX = this.checkValue(compound.getFloat("TransX"), -1.0f, 1.0f);
        this.transY = this.checkValue(compound.getFloat("TransY"), -1.0f, 1.0f);
        this.transZ = this.checkValue(compound.getFloat("TransZ"), -1.0f, 1.0f);
        this.notShared = compound.getBoolean("NotShared");
    }

    public String toString() {
        return "ScaleX: " + this.scaleX + " - ScaleY: " + this.scaleY + " - ScaleZ: " + this.scaleZ;
    }

    public void setScale(float x, float y, float z) {
        this.scaleX = x;
        this.scaleY = y;
        this.scaleZ = z;
    }

    public void setScale(float x, float y) {
        this.scaleZ = this.scaleX = x;
        this.scaleY = y;
    }

    public float checkValue(float given, float min, float max) {
        if (given < min) {
            return min;
        }
        if (given > max) {
            return max;
        }
        return given;
    }

    public void setTranslate(float transX, float transY, float transZ) {
        this.transX = transX;
        this.transY = transY;
        this.transZ = transZ;
    }

    public void copyValues(ModelPartConfig config) {
        this.scaleX = config.scaleX;
        this.scaleY = config.scaleY;
        this.scaleZ = config.scaleZ;
        this.transX = config.transX;
        this.transY = config.transY;
        this.transZ = config.transZ;
    }
}

