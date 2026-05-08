/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.passive.TameableEntity
 */
package noppes.npcs.api.entity;

import net.minecraft.entity.passive.TameableEntity;
import noppes.npcs.api.entity.IAnimal;

public interface IPixelmon<T extends TameableEntity>
extends IAnimal<T> {
    public Object getPokemonData();
}

