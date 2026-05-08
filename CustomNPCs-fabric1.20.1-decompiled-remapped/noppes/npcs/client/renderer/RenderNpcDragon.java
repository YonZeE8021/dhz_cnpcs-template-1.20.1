/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.model.EntityModel
 */
package noppes.npcs.client.renderer;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.entity.EntityNPCInterface;

public class RenderNpcDragon<T extends EntityNPCInterface, M extends EntityModel<T>>
extends RenderNPCInterface<T, M> {
    public RenderNpcDragon(EntityRendererFactory.Context manager, M model, float f) {
        super(manager, model, f);
    }

    @Override
    protected void scale(T npc, MatrixStack matrixScale, float f) {
        matrixScale.translate(0.0f, 0.0f, 0.120000005f * (float)((EntityNPCInterface)((Object)npc)).display.getSize());
        super.scale(npc, matrixScale, f);
    }
}

