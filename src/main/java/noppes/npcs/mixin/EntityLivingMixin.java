/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package noppes.npcs.mixin;

import net.minecraft.util.Hand;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.items.ItemTeleporter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LivingEntity.class})
public abstract class EntityLivingMixin {
    @Shadow
    public abstract ItemStack getStackInHand(Hand var1);

    @Inject(at={@At(value="HEAD")}, method={"writeCustomDataToNbt"})
    private void addAdditionalSaveData(NbtCompound compound, CallbackInfo callbackInfo) {
        LivingEntity e = (LivingEntity)(Object)this;
        if (!e.getWorld().isClient()) {
            MarkData.get(e).save();
        }
    }

    @Inject(method={"swingHand"}, at={@At(value="HEAD")}, cancellable=true)
    public void swing(Hand hand, CallbackInfo ci) {
        ItemStack stack = this.getStackInHand(hand);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemTeleporter && ItemTeleporter.onEntitySwing(stack, (LivingEntity)(Object)this)) {
            ci.cancel();
        }
    }
}

