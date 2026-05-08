/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.model.BakedModel
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.SwordItem
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.model.json.Transformation
 *  net.minecraft.client.render.model.json.ModelTransformationMode
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 */
package noppes.npcs.client.layer;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.client.layer.LayerInterface;

public class LayerBackItem
extends LayerInterface {
    public LayerBackItem(LivingEntityRenderer render) {
        super(render);
    }

    @Override
    public void render(MatrixStack mStack, VertexConsumerProvider typeBuffer, int lightmapUV, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        ItemStack itemstack = ItemStackWrapper.MCItem(this.npc.inventory.getRightHand());
        if (NoppesUtilServer.IsItemStackNull(itemstack) || this.npc.isAttacking()) {
            return;
        }
        Item item = itemstack.getItem();
        if (item instanceof BlockItem) {
            return;
        }
        mStack.push();
        this.base.body.rotate(mStack);
        mStack.translate(0.0f, 0.36f, 0.14f);
        mStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
        if (item instanceof SwordItem) {
            mStack.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(180.0f));
        }
        BakedModel model = minecraft.getItemRenderer().getModels().getModel(itemstack);
        Transformation p_175034_1_ = model.getTransformation().thirdPersonRightHand;
        mStack.scale(p_175034_1_.scale.x(), p_175034_1_.scale.y(), p_175034_1_.scale.z());
        minecraft.getItemRenderer().renderItem((LivingEntity)this.npc, itemstack, ModelTransformationMode.NONE, false, mStack, typeBuffer, this.npc.getWorld(), lightmapUV, LivingEntityRenderer.getOverlay((LivingEntity)this.npc, (float)0.0f), 0);
        mStack.pop();
    }

    @Override
    public void rotate(MatrixStack matrixStack, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    }
}

