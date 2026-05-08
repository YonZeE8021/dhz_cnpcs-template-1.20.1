/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.api.wrapper;

import net.minecraft.nbt.NbtCompound;
import noppes.npcs.api.overlay.ILabel;
import noppes.npcs.api.wrapper.OverlayComponentWrapper;

public class OverlayLabelWrapper
extends OverlayComponentWrapper
implements ILabel {
    private String text;
    private boolean isCenter = false;
    private float scale = 1.0f;

    public OverlayLabelWrapper(int id, int x, int y, String text) {
        super(id, x, y);
        this.text = text;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public ILabel setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public float getScale() {
        return this.scale;
    }

    @Override
    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public ILabel setCentered(boolean centered) {
        this.isCenter = centered;
        return this;
    }

    @Override
    public boolean isCentered() {
        return this.isCenter;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public void toNbt(NbtCompound compound) {
        super.toNbt(compound);
        compound.putString("text", this.text);
        compound.putFloat("scale", this.scale);
        if (this.isCenter) {
            compound.putBoolean("centered", true);
        }
    }

    @Override
    public void fromNbt(NbtCompound compound) {
        super.fromNbt(compound);
        this.text = compound.getString("text");
        this.scale = compound.getFloat("scale");
        this.isCenter = compound.getBoolean("centered");
    }
}

