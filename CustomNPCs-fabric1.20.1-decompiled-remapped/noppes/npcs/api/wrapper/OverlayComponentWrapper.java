/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.api.wrapper;

import net.minecraft.nbt.NbtCompound;
import noppes.npcs.api.overlay.IOverlayComponent;

public abstract class OverlayComponentWrapper
implements IOverlayComponent {
    private int id;
    private int x;
    private int y;

    public OverlayComponentWrapper(int id, int x, int y) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int getPosX() {
        return this.x;
    }

    @Override
    public int getPosY() {
        return this.y;
    }

    @Override
    public IOverlayComponent setPos(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    @Override
    public void toNbt(NbtCompound compound) {
        compound.putInt("id", this.id);
        compound.putIntArray("pos", new int[]{this.x, this.y});
        compound.putInt("type", this.getType());
    }

    @Override
    public void fromNbt(NbtCompound compound) {
        int[] pos = compound.getIntArray("pos");
        this.x = pos[0];
        this.y = pos[1];
        this.id = compound.getInt("id");
    }
}

