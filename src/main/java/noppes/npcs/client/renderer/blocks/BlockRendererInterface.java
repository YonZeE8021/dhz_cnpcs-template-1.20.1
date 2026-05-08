/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 */
package noppes.npcs.client.renderer.blocks;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;

public abstract class BlockRendererInterface<T extends BlockEntity>
implements BlockEntityRenderer<T> {
    public static float[][] colorTable = new float[][]{{1.0f, 1.0f, 1.0f}, {0.95f, 0.7f, 0.2f}, {0.9f, 0.5f, 0.85f}, {0.6f, 0.7f, 0.95f}, {0.9f, 0.9f, 0.2f}, {0.5f, 0.8f, 0.1f}, {0.95f, 0.7f, 0.8f}, {0.3f, 0.3f, 0.3f}, {0.6f, 0.6f, 0.6f}, {0.3f, 0.6f, 0.7f}, {0.7f, 0.4f, 0.9f}, {0.2f, 0.4f, 0.8f}, {0.5f, 0.4f, 0.3f}, {0.4f, 0.5f, 0.2f}, {0.8f, 0.3f, 0.3f}, {0.1f, 0.1f, 0.1f}};

    public BlockRendererInterface(BlockEntityRendererFactory.Context dispatcher) {
    }

    public boolean playerTooFar(BlockEntity tile) {
        double d8;
        double d7;
        MinecraftClient mc = MinecraftClient.getInstance();
        double d6 = mc.getCameraEntity().getX() - (double)tile.getPos().getX();
        return d6 * d6 + (d7 = mc.getCameraEntity().getY() - (double)tile.getPos().getY()) * d7 + (d8 = mc.getCameraEntity().getZ() - (double)tile.getPos().getZ()) * d8 > (double)(this.specialRenderDistance() * this.specialRenderDistance());
    }

    public int specialRenderDistance() {
        return 20;
    }
}

