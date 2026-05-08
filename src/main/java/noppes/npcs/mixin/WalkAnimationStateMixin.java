/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LimbAnimator
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package noppes.npcs.mixin;

import net.minecraft.entity.LimbAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={LimbAnimator.class})
public interface WalkAnimationStateMixin {
    @Accessor("prevSpeed")
    public float getSpeedOld();

    @Accessor("prevSpeed")
    public void setSpeedOld(float var1);

    @Accessor("pos")
    public void setPosition(float var1);
}

