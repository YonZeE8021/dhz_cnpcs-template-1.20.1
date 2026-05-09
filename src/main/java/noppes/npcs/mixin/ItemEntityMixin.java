/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ItemEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package noppes.npcs.mixin;

import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ItemEntity.class})
public interface ItemEntityMixin {
    @Accessor(value="pickupDelay")
    public int pickupDelay();

    /** 勿命名为 age，intermediary 下会误绑到 Entity.age（field_6012），仅启动器崩。 */
    @Accessor("itemAge")
    void setItemAge(int ticks);
}

