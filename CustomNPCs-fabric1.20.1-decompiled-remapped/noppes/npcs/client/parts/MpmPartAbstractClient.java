/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 */
package noppes.npcs.client.parts;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import noppes.npcs.client.parts.ModelPartWrapper;
import noppes.npcs.client.parts.MpmPart;
import noppes.npcs.client.parts.MpmPartData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.shared.common.util.NopVector3f;

public abstract class MpmPartAbstractClient
extends MpmPart {
    public NopVector3f pos = NopVector3f.ZERO;
    public NopVector3f rot = NopVector3f.ZERO;
    protected Map<String, ModelPartWrapper> defaultPose = new HashMap<String, ModelPartWrapper>();

    public void render(MpmPartData data, MatrixStack mStack, VertexConsumerProvider typeBuffer, int lightmapUV, LivingEntity player) {
        VertexConsumer c = typeBuffer.getBuffer(RenderLayer.getEntityTranslucent((Identifier)(data.usePlayerSkin ? ((EntityCustomNpc)player).textureLocation : data.getTexture())));
        this.render(data, mStack, c, lightmapUV, player);
    }

    public void render(MpmPartData data, MatrixStack mStack, VertexConsumer c, int lightmapUV, LivingEntity player) {
    }

    @Override
    public final ModelPartWrapper getPart(String name) {
        return this.defaultPose.get(name);
    }
}

