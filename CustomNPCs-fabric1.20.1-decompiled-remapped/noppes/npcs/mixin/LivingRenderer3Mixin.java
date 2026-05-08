/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package noppes.npcs.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={LivingEntityRenderer.class})
public interface LivingRenderer3Mixin<T extends LivingEntity, M extends EntityModel<T>>
extends FeatureRendererContext<T, M> {
    @Invoker
    public void callScale(T var1, MatrixStack var2, float var3);

    @Invoker
    public float callGetBob(T var1, float var2);
}

