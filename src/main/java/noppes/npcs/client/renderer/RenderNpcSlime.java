/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 */
package noppes.npcs.client.renderer;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRendererFactory;
import noppes.npcs.client.layer.LayerSlimeNpc;
import noppes.npcs.client.model.ModelNpcSlime;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.entity.EntityNpcSlime;

public class RenderNpcSlime<T extends EntityNpcSlime, M extends ModelNpcSlime<T>>
extends RenderNPCInterface<T, M> {
    private Model scaleAmount;

    public RenderNpcSlime(EntityRendererFactory.Context manager, M par1Model, Model limbSwingAmountModel, float par3) {
        super(manager, par1Model, par3);
        this.scaleAmount = limbSwingAmountModel;
        this.addFeature(new LayerSlimeNpc(this));
    }
}

