/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.model.BakedModel
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.item.ItemModels
 *  net.minecraft.registry.Registries
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package noppes.npcs.mixin;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.registry.Registries;
import noppes.npcs.CustomItems;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.api.wrapper.WrapperNpcAPI;
import noppes.npcs.items.ItemScripted;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ItemModels.class})
public class ItemModelMesherMixin {
    @Inject(at={@At(value="HEAD")}, method={"getModel"}, cancellable=true)
    public void getModel(ItemStack item, CallbackInfoReturnable<BakedModel> cir) {
        if (item.getItem() == CustomItems.scripted_item) {
            BakedModel model;
            ItemScriptedWrapper si = (ItemScriptedWrapper)WrapperNpcAPI.Instance().getIItemStack(item);
            if (si == null) {
                return;
            }
            Item i = null;
            if (si.texture != null) {
                i = Registries.ITEM.get(si.texture);
            }
            if (i == null) {
                i = CustomItems.scripted_item;
            }
            if ((model = MinecraftClient.getInstance().getItemRenderer().getModels().getModel(i)) != null) {
                cir.setReturnValue(model);
                cir.cancel();
            }
        }
    }
}

