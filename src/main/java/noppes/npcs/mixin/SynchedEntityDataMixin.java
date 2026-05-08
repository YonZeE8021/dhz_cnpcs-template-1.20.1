/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.minecraft.entity.data.DataTracker
 *  net.minecraft.entity.data.DataTracker$Entry
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package noppes.npcs.mixin;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.minecraft.entity.data.DataTracker;
import noppes.npcs.client.ISynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={DataTracker.class})
public class SynchedEntityDataMixin
implements ISynchedEntityData {
    @Shadow
    private final Int2ObjectMap<DataTracker.Entry<?>> entries = new Int2ObjectOpenHashMap();
    @Shadow
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public List<DataTracker.Entry<?>> getAll() {
        ArrayList list = null;
        this.lock.readLock().lock();
        for (DataTracker.Entry dataitem : this.entries.values()) {
            if (list == null) {
                list = Lists.newArrayList();
            }
            list.add(new DataTracker.Entry(dataitem.getData(), dataitem.toSerialized()));
        }
        this.lock.readLock().unlock();
        return list;
    }
}

