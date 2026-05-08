/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.registry.tag.DamageTypeTags
 */
package noppes.npcs.api.wrapper;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;

public class DamageSourceWrapper
implements IDamageSource {
    private DamageSource source;

    public DamageSourceWrapper(DamageSource source) {
        this.source = source;
    }

    @Override
    public String getType() {
        return this.source.getName();
    }

    @Override
    public boolean isUnblockable() {
        return this.source.isIn(DamageTypeTags.BYPASSES_ARMOR);
    }

    @Override
    public boolean isProjectile() {
        return this.source.isIn(DamageTypeTags.IS_PROJECTILE);
    }

    @Override
    public DamageSource getMCDamageSource() {
        return this.source;
    }

    @Override
    public IEntity getTrueSource() {
        return NpcAPI.Instance().getIEntity(this.source.getAttacker());
    }

    @Override
    public IEntity getImmediateSource() {
        return NpcAPI.Instance().getIEntity(this.source.getSource());
    }
}

