/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry$DynamicItemRenderer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.block.BlockWithEntity
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.model.json.ModelTransformationMode
 *  net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher
 */
package noppes.npcs.client;

import java.util.HashMap;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import noppes.npcs.items.ItemNpcBlock;

public class CustomTileEntityItemStackRenderer
implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    private static CustomTileEntityItemStackRenderer i = null;
    private HashMap<Block, BlockEntity> data = new HashMap();

    private CustomTileEntityItemStackRenderer() {
    }

    public static CustomTileEntityItemStackRenderer instance() {
        if (i == null) {
            i = new CustomTileEntityItemStackRenderer();
        }
        return i;
    }

    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.getItem() instanceof ItemNpcBlock) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) {
                return;
            }
            BlockEntityRenderDispatcher dispatcher = client.getBlockEntityRenderDispatcher();
            if (dispatcher == null) {
                return;
            }
            ItemNpcBlock item = (ItemNpcBlock)stack.getItem();
            BlockEntity tile = this.data.get(item.block);
            if (tile == null) {
                tile = ((BlockWithEntity)item.block).createBlockEntity(BlockPos.ORIGIN, item.block.getDefaultState());
                this.data.put(item.block, tile);
            }
            dispatcher.renderEntity(tile, matrices, vertexConsumers, light, overlay);
        }
    }
}

