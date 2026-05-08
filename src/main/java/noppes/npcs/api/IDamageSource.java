/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.damage.DamageSource
 */
package noppes.npcs.api;

import net.minecraft.entity.damage.DamageSource;
import noppes.npcs.api.entity.IEntity;

public interface IDamageSource {
    public String getType();

    public boolean isUnblockable();

    public boolean isProjectile();

    public IEntity getTrueSource();

    public IEntity getImmediateSource();

    public DamageSource getMCDamageSource();
}

