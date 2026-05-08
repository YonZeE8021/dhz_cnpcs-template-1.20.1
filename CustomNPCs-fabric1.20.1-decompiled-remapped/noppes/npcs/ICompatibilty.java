/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs;

import net.minecraft.nbt.NbtCompound;

public interface ICompatibilty {
    public int getVersion();

    public void setVersion(int var1);

    public NbtCompound save(NbtCompound var1);
}

