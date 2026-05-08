/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.entity.model.AnimalModel
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package noppes.npcs.mixin;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.AnimalModel;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.entity.EntityNPCInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={AnimalModel.class})
public class AgeableModelMixin<T extends EntityNPCInterface> {
    private boolean isCanceled = false;

    @Inject(at={@At(value="HEAD")}, method={"renderToBuffer"}, cancellable=true)
    private void renderToBuffer(MatrixStack stack, VertexConsumer builder, int light, int overlay, float r, float g, float b, float a, CallbackInfo callbackInfo) {
        if (!this.isCanceled && RenderNPCInterface.currentNpc != null && RenderNPCInterface.currentNpc.display.getTint() < 0xFFFFFF) {
            this.isCanceled = true;
            int color = RenderNPCInterface.currentNpc.display.getTint();
            r = (float)(color >> 16 & 0xFF) / 255.0f;
            g = (float)(color >> 8 & 0xFF) / 255.0f;
            b = (float)(color & 0xFF) / 255.0f;
            AnimalModel model = (AnimalModel)this;
            model.render(stack, builder, light, overlay, r, g, b, a);
            callbackInfo.cancel();
            return;
        }
        this.isCanceled = false;
    }
}

