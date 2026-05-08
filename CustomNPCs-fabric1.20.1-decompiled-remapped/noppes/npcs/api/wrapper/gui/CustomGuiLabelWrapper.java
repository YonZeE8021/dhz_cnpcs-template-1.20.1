/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.NbtCompound;
import noppes.npcs.api.gui.ILabel;
import noppes.npcs.api.wrapper.gui.CustomGuiComponentWrapper;

public class CustomGuiLabelWrapper
extends CustomGuiComponentWrapper
implements ILabel {
    private String label = "";
    private int color = 0x404040;
    private float scale = 1.0f;
    private boolean centered = false;

    public CustomGuiLabelWrapper() {
    }

    public CustomGuiLabelWrapper(int id, String label, int x, int y, int width, int height) {
        this.setID(id);
        this.setText(label);
        this.setPos(x, y);
        this.setSize(width, height);
    }

    public CustomGuiLabelWrapper(int id, String label, int x, int y, int width, int height, int color) {
        this(id, label, x, y, width, height);
        this.setColor(color);
    }

    @Override
    public String getText() {
        return this.label;
    }

    @Override
    public ILabel setText(String label) {
        this.label = label;
        return this;
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public ILabel setColor(int color) {
        this.color = color;
        return this;
    }

    @Override
    public float getScale() {
        return this.scale;
    }

    @Override
    public ILabel setScale(float scale) {
        this.scale = scale;
        return this;
    }

    @Override
    public boolean getCentered() {
        return this.centered;
    }

    @Override
    public ILabel setCentered(boolean bo) {
        this.centered = bo;
        return this;
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public NbtCompound toNBT(NbtCompound compound) {
        super.toNBT(compound);
        compound.putString("label", this.label);
        compound.putInt("color", this.color);
        compound.putFloat("scale", this.scale);
        compound.putBoolean("centered", this.centered);
        return compound;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(NbtCompound compound) {
        super.fromNBT(compound);
        this.setText(compound.getString("label"));
        this.setColor(compound.getInt("color"));
        this.setScale(compound.getFloat("scale"));
        this.setCentered(compound.getBoolean("centered"));
        return this;
    }
}

