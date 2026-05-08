/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.data.DataTracker$Entry
 */
package noppes.npcs.client;

import java.util.List;
import net.minecraft.entity.data.DataTracker;

public interface ISynchedEntityData {
    public List<DataTracker.Entry<?>> getAll();
}

