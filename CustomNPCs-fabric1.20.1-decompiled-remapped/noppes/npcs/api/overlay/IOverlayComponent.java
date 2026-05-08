/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.api.overlay;

import net.minecraft.nbt.NbtCompound;

public interface IOverlayComponent {
    public int getId();

    public int getPosX();

    public int getPosY();

    public IOverlayComponent setPos(int var1, int var2);

    public int getType();

    public void toNbt(NbtCompound var1);

    public void fromNbt(NbtCompound var1);
}

