/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.world.entity.SectionedEntityCache
 *  net.minecraft.server.world.ServerEntityManager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package noppes.npcs.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.world.entity.SectionedEntityCache;
import net.minecraft.server.world.ServerEntityManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ServerEntityManager.class})
public interface PersistentEntitySectionManagerMixin<T extends Entity> {
    @Accessor("cache")
    public SectionedEntityCache<T> sectionStorage();
}

