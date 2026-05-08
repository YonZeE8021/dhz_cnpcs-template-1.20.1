/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.screen.v1.ScreenEvents$AfterInit
 *  net.minecraft.client.render.model.BakedModel
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.gl.VertexBuffer
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.VertexConsumerProvider$Immediate
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.render.WorldRenderer
 *  net.minecraft.client.render.block.BlockRenderManager
 */
package noppes.npcs.client;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import noppes.npcs.CustomNpcs;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.client.gui.player.tabs.InventoryTabFactions;
import noppes.npcs.client.gui.player.tabs.InventoryTabQuests;
import noppes.npcs.client.gui.player.tabs.InventoryTabVanilla;
import noppes.npcs.client.renderer.MarkRenderer;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.schematics.SchematicWrapper;
import noppes.npcs.shared.common.util.LogWriter;

public class ClientEventHandler
implements ScreenEvents.AfterInit {
    private VertexBuffer cache = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void onRenderTick(MatrixStack matrixStack, BlockPos rpos, BlockEntity te) {
        VertexConsumerProvider.Immediate buffer = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (rpos == null || rpos == BlockPos.ORIGIN || rpos.getSquaredDistance((Vec3i)player.getBlockPos()) > 1000000.0) {
            return;
        }
        TileBuilder tile = (TileBuilder)te;
        SchematicWrapper schem = tile.getSchematic();
        if (schem == null) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(1.0f, (float)tile.yOffest, 1.0f);
        if (!TileBuilder.Compiled) {
            BlockRenderManager dispatcher = MinecraftClient.getInstance().getBlockRenderManager();
            try {
                for (int i = 0; i < schem.size && i < 25000; ++i) {
                    BlockState state = schem.schema.getBlockState(i);
                    if (state.getRenderType() == BlockRenderType.INVISIBLE || state.getRenderType() != BlockRenderType.MODEL) continue;
                    int posX = i % schem.schema.getWidth();
                    int posZ = (i - posX) / schem.schema.getWidth() % schem.schema.getLength();
                    int posY = ((i - posX) / schem.schema.getWidth() - posZ) / schem.schema.getLength();
                    BlockPos pos = schem.rotatePos(posX, posY, posZ, tile.rotation);
                    matrixStack.push();
                    matrixStack.translate((float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
                    state = schem.rotationState(state, tile.rotation);
                    try {
                        BakedModel ibakedmodel = dispatcher.getModel(state);
                        BufferBuilder builder = (BufferBuilder)buffer.getBuffer(RenderLayers.getEntityBlockLayer((BlockState)state, (boolean)false));
                        dispatcher.getModelRenderer().render(matrixStack.peek(), (VertexConsumer)builder, state, ibakedmodel, 1.0f, 1.0f, 1.0f, 0xF000F0, OverlayTexture.DEFAULT_UV);
                        continue;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    finally {
                        matrixStack.pop();
                    }
                }
            }
            catch (Exception e) {
                LogWriter.error("Error preview builder block", e);
            }
        }
        if (tile.rotation % 2 == 0) {
            ClientEventHandler.drawSelectionBox(matrixStack, (VertexConsumerProvider)buffer, new BlockPos((int)schem.schema.getWidth(), (int)schem.schema.getHeight(), (int)schem.schema.getLength()));
        } else {
            ClientEventHandler.drawSelectionBox(matrixStack, (VertexConsumerProvider)buffer, new BlockPos((int)schem.schema.getLength(), (int)schem.schema.getHeight(), (int)schem.schema.getWidth()));
        }
        matrixStack.pop();
    }

    public static void post(LivingEntity entity, float entityYaw, float partialTicks, MatrixStack poseStack, VertexConsumerProvider buffer, int packedLight) {
        MarkData data = MarkData.get(entity);
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        for (MarkData.Mark m : data.marks) {
            if (m.getType() == 0 || !m.availability.isAvailable((PlayerEntity)player)) continue;
            MarkRenderer.render(entity, poseStack, buffer, packedLight, m);
            break;
        }
    }

    public static void drawSelectionBox(MatrixStack matrixStack, VertexConsumerProvider buffer, BlockPos pos) {
        matrixStack.push();
        Box bb = new Box(BlockPos.ORIGIN, pos);
        matrixStack.translate(0.001f, 0.001f, 0.001f);
        WorldRenderer.drawBox((MatrixStack)matrixStack, (VertexConsumer)buffer.getBuffer(RenderLayer.getLines()), (Box)bb, (float)1.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        matrixStack.pop();
    }

    public void afterInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
        if (screen instanceof InventoryScreen && CustomNpcs.InventoryGuiEnabled) {
            screen.addDrawableChild(new InventoryTabVanilla().init(screen));
            screen.addDrawableChild(new InventoryTabFactions().init(screen));
            screen.addDrawableChild(new InventoryTabQuests().init(screen));
        }
    }
}

