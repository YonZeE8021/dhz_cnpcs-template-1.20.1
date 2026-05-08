/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 */
package noppes.npcs.client.renderer.blocks;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import noppes.npcs.blocks.tiles.TileMailbox;
import noppes.npcs.client.model.blocks.ModelMailboxUS;
import noppes.npcs.client.model.blocks.ModelMailboxWow;

public class BlockMailboxRenderer<T extends TileMailbox>
implements BlockEntityRenderer<T> {
    private final ModelMailboxUS model = new ModelMailboxUS();
    private final ModelMailboxWow model2 = new ModelMailboxWow();
    private static final Identifier text1 = new Identifier("customnpcs", "textures/models/mailbox1.png");
    private static final Identifier text2 = new Identifier("customnpcs", "textures/models/mailbox2.png");
    private static final Identifier text3 = new Identifier("customnpcs", "textures/models/mailbox3.png");
    private static final RenderLayer type1 = RenderLayer.getEntityCutout((Identifier)text1);
    private static final RenderLayer type2 = RenderLayer.getEntityCutout((Identifier)text2);
    private static final RenderLayer type3 = RenderLayer.getEntityCutout((Identifier)text3);

    public BlockMailboxRenderer(BlockEntityRendererFactory.Context dispatcher) {
    }

    public void render(TileMailbox te, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider buffer, int light, int overlay) {
        int meta = 0;
        int type = te.getModel();
        matrixStack.push();
        matrixStack.translate(0.5f, 1.5f, 0.5f);
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)(90 * meta)));
        if (type == 0) {
            this.model.render(matrixStack, buffer.getBuffer(type1), light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
        } else if (type == 1) {
            this.model2.render(matrixStack, buffer.getBuffer(type2), light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
        } else if (type == 2) {
            this.model2.render(matrixStack, buffer.getBuffer(type3), light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        matrixStack.pop();
    }
}

