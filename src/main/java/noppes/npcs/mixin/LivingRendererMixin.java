/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package noppes.npcs.mixin;

import com.google.common.collect.Lists;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.entity.EntityCustomNpc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LivingEntityRenderer.class})
public class LivingRendererMixin<T extends EntityCustomNpc, M extends BipedEntityModel<T>> {
    @Inject(at={@At(value="HEAD")}, method={"addLayer"})
    private void spawnOriginalMobs(FeatureRenderer<T, M> layer, CallbackInfoReturnable<Boolean> cir) {
        LivingEntityRenderer<?, ?> renderer = (LivingEntityRenderer<?, ?>)(Object)this;
        if (renderer instanceof RenderCustomNpc) {
            if (((RenderCustomNpc)renderer).npclayers == null) {
                ((RenderCustomNpc)renderer).npclayers = Lists.newArrayList();
            }
            ((RenderCustomNpc)renderer).npclayers.add(layer);
        }
    }
}

