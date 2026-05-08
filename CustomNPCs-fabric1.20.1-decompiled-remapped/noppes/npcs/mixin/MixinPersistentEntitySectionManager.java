/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.entity.EntityLike
 *  net.minecraft.server.world.ServerEntityManager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package noppes.npcs.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.server.world.ServerEntityManager;
import noppes.npcs.CustomItems;
import noppes.npcs.EventHooks;
import noppes.npcs.items.ItemScripted;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ServerEntityManager.class})
public abstract class MixinPersistentEntitySectionManager<T extends EntityLike> {
    @Inject(method={"addEntity"}, at={@At(value="HEAD")}, cancellable=true)
    private void addEntity(T ent, boolean worldGenSpawned, CallbackInfoReturnable<Boolean> cir) {
        if (ent instanceof Entity) {
            Entity entity = (Entity)ent;
            if (entity.getWorld().isClient || !(entity instanceof ItemEntity)) {
                return;
            }
            ItemEntity entityItem = (ItemEntity)entity;
            ItemStack stack = entityItem.getStack();
            if (!stack.isEmpty() && stack.getItem() == CustomItems.scripted_item && EventHooks.onScriptItemSpawn(ItemScripted.GetWrapper(stack), entityItem)) {
                cir.setReturnValue((Object)false);
            }
        }
    }
}

