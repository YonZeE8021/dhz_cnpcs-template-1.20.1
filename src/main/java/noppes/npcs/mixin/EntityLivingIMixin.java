/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package noppes.npcs.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={LivingEntity.class})
public interface EntityLivingIMixin {
    @Accessor(value="jumping")
    public boolean jumping();

    @Accessor("itemUseTimeLeft")
    public void useItemRemaining(int var1);

    @Accessor("lookDirection")
    public float animStep();

    @Accessor("lookDirection")
    public void animStep(float var1);

    @Accessor("prevLookDirection")
    public float animStepO();

    @Accessor("prevLookDirection")
    public void animStepO(float var1);

    @Accessor("leaningPitch")
    public float swimAmount();

    @Accessor("leaningPitch")
    public void swimAmount(float var1);

    @Accessor("lastLeaningPitch")
    public float swimAmountO();

    @Accessor("lastLeaningPitch")
    public void swimAmountO(float var1);

    @Accessor("playerHitTimer")
    public int lastHurtByPlayerTime();

    @Accessor("playerHitTimer")
    public void lastHurtByPlayerTime(int var1);
}

