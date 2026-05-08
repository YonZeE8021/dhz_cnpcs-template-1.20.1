/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.passive.VillagerEntity
 */
package noppes.npcs.api.wrapper;

import net.minecraft.entity.passive.VillagerEntity;
import noppes.npcs.api.entity.IVillager;
import noppes.npcs.api.wrapper.EntityLivingWrapper;

public class VillagerWrapper<T extends VillagerEntity>
extends EntityLivingWrapper<T>
implements IVillager {
    public VillagerWrapper(T entity) {
        super(entity);
    }

    public String getProfession() {
        return ((VillagerEntity)this.entity).getVillagerData().getProfession().toString();
    }

    public String VillagerType() {
        return ((VillagerEntity)this.entity).getVillagerData().getType().toString();
    }

    @Override
    public int getType() {
        return 9;
    }

    @Override
    public boolean typeOf(int type) {
        return type == 9 ? true : super.typeOf(type);
    }
}

