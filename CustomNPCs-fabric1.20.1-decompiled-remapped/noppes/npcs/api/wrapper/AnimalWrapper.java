/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.passive.AnimalEntity
 */
package noppes.npcs.api.wrapper;

import net.minecraft.entity.passive.AnimalEntity;
import noppes.npcs.api.entity.IAnimal;
import noppes.npcs.api.wrapper.EntityLivingWrapper;

public class AnimalWrapper<T extends AnimalEntity>
extends EntityLivingWrapper<T>
implements IAnimal {
    public AnimalWrapper(T entity) {
        super(entity);
    }

    @Override
    public int getType() {
        return 4;
    }

    @Override
    public boolean typeOf(int type) {
        return type == 4 ? true : super.typeOf(type);
    }
}

