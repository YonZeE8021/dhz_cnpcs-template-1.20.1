/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ItemEntity
 */
package noppes.npcs.api.entity;

import net.minecraft.entity.ItemEntity;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.item.IItemStack;

public interface IEntityItem<T extends ItemEntity>
extends IEntity<T> {
    public String getOwner();

    public void setOwner(String var1);

    public int getPickupDelay();

    public void setPickupDelay(int var1);

    @Override
    public long getAge();

    public void setAge(long var1);

    public IItemStack getItem();

    public void setItem(IItemStack var1);
}

