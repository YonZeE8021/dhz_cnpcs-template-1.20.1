/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.item.ItemStack
 */
package noppes.npcs.api.wrapper;

import java.util.UUID;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntityItem;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.EntityWrapper;
import noppes.npcs.mixin.ItemEntityMixin;

public class EntityItemWrapper<T extends ItemEntity>
extends EntityWrapper<T>
implements IEntityItem {
    public EntityItemWrapper(T entity) {
        super(entity);
    }

    @Override
    public String getOwner() {
        if (((ItemEntity)this.entity).getOwner() == null) {
            return null;
        }
        return ((ItemEntity)this.entity).getOwner().toString();
    }

    @Override
    public void setOwner(String name) {
        ((ItemEntity)this.entity).setThrower(UUID.fromString(name));
    }

    @Override
    public int getPickupDelay() {
        return ((ItemEntityMixin)this.entity).pickupDelay();
    }

    @Override
    public void setPickupDelay(int delay) {
        ((ItemEntity)this.entity).setPickupDelay(delay);
    }

    @Override
    public int getType() {
        return 6;
    }

    @Override
    public long getAge() {
        return ((ItemEntity)this.entity).getItemAge();
    }

    @Override
    public void setAge(long age) {
        age = Math.max(Math.min(age, Integer.MAX_VALUE), Integer.MIN_VALUE);
        ((ItemEntityMixin)this.entity).setItemAge((int)age);
    }

    @Override
    public IItemStack getItem() {
        return NpcAPI.Instance().getIItemStack(((ItemEntity)this.entity).getStack());
    }

    @Override
    public void setItem(IItemStack item) {
        ItemStack stack = item == null ? ItemStack.EMPTY : item.getMCItemStack();
        ((ItemEntity)this.entity).setStack(stack);
    }
}

