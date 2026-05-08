/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.NbtCompound;
import noppes.npcs.api.gui.IColoredLine;
import noppes.npcs.api.wrapper.gui.CustomGuiComponentWrapper;

public class CustomGuiColoredLineWrapper
extends CustomGuiComponentWrapper
implements IColoredLine {
    int xEnd;
    int yEnd;
    int color;
    float thickness;

    public CustomGuiColoredLineWrapper() {
    }

    public CustomGuiColoredLineWrapper(int id, int xStart, int yStart, int xEnd, int yEnd, int color, float thickness) {
        this.setID(id);
        this.setPos(xStart, yStart);
        this.setEnd(xEnd, yEnd);
        this.color = color;
        this.thickness = thickness;
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public IColoredLine setColor(int color) {
        this.color = color;
        return this;
    }

    @Override
    public int getXEnd() {
        return this.xEnd;
    }

    @Override
    public int getYEnd() {
        return this.yEnd;
    }

    @Override
    public IColoredLine setEnd(int x, int y) {
        this.xEnd = x;
        this.yEnd = y;
        return this;
    }

    @Override
    public float getThickness() {
        return this.thickness;
    }

    @Override
    public IColoredLine setThickness(float thickness) {
        this.thickness = thickness;
        return this;
    }

    @Override
    public int getType() {
        return 11;
    }

    @Override
    public NbtCompound toNBT(NbtCompound compound) {
        super.toNBT(compound);
        compound.putInt("xEnd", this.xEnd);
        compound.putInt("yEnd", this.yEnd);
        compound.putInt("color", this.color);
        compound.putFloat("thickness", this.thickness);
        return compound;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(NbtCompound compound) {
        super.fromNBT(compound);
        this.setColor(compound.getInt("color"));
        this.setThickness(compound.getFloat("thickness"));
        this.setEnd(compound.getInt("xEnd"), compound.getInt("yEnd"));
        return this;
    }
}

