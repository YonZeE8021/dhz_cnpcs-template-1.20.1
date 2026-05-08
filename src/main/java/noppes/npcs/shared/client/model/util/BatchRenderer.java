/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.util.Window
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.VertexFormat
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.ShaderProgram
 *  net.minecraft.client.render.GameRenderer
 *  org.joml.Matrix4f
 *  org.lwjgl.system.MemoryUtil
 */
package noppes.npcs.shared.client.model.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.util.Window;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.shared.common.util.NopVector2i;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

public class BatchRenderer {
    private static final FloatBuffer MATRIX_BUFFER = MemoryUtil.memAllocFloat((int)16);
    public static RenderLayer lastType = null;
    private static final BatchRenderer instance = new BatchRenderer();
    private final Map<RenderLayer, List<Batch>> queue = new LinkedHashMap<RenderLayer, List<Batch>>();

    public static BatchRenderer getInstance() {
        return instance;
    }

    public void add(RenderLayer renderType, Identifier resource, int id, VertexFormat format, Matrix4f matrix, int vertexCount, NopVector2i texPos, int light, int overlay, float red, float green, float blue, float alpha) {
        if (renderType == null) {
            renderType = lastType;
        }
        this.queue.computeIfAbsent(renderType, k -> new LinkedList()).add(new Batch(resource, id, format, matrix, vertexCount, texPos, light, overlay, red, green, blue, alpha));
    }

    public void draw() {
        this.queue.forEach((renderType, batches) -> {
            if (batches.isEmpty()) {
                return;
            }
            RenderSystem.assertOnRenderThread();
            renderType.startDrawing();
            RenderSystem.setShader(GameRenderer::getPositionTexColorNormalProgram);
            ShaderProgram shaderinstance = RenderSystem.getShader();
            for (Batch b : batches) {
                RenderSystem.setShaderTexture((int)0, (Identifier)b.resource);
                shaderinstance.colorModulator.set(new float[]{b.red, b.green, b.blue, b.alpha});
                if (shaderinstance.light0Direction != null) {
                    shaderinstance.light0Direction.set(b.light1);
                }
                if (shaderinstance.light1Direction != null) {
                    shaderinstance.light1Direction.set(b.light2);
                }
                shaderinstance.modelViewMat.set(b.matrix);
                if (shaderinstance.viewRotationMat != null) {
                    shaderinstance.viewRotationMat.set(RenderSystem.getInverseViewRotationMatrix());
                }
                if (shaderinstance.fogStart != null) {
                    shaderinstance.fogStart.set(RenderSystem.getShaderFogStart());
                }
                if (shaderinstance.fogEnd != null) {
                    shaderinstance.fogEnd.set(RenderSystem.getShaderFogEnd());
                }
                if (shaderinstance.fogColor != null) {
                    shaderinstance.fogColor.set(RenderSystem.getShaderFogColor());
                }
                if (shaderinstance.fogShape != null) {
                    shaderinstance.fogShape.set(RenderSystem.getShaderFogShape().getId());
                }
                if (shaderinstance.textureMat != null) {
                    shaderinstance.textureMat.set(BatchRenderer.createTranslateMatrix(b.texPos.x, b.texPos.y, 0.0f));
                }
                if (shaderinstance.gameTime != null) {
                    shaderinstance.gameTime.set(RenderSystem.getShaderGameTime());
                }
                if (shaderinstance.screenSize != null) {
                    Window window = MinecraftClient.getInstance().getWindow();
                    shaderinstance.screenSize.set((float)window.getFramebufferWidth(), (float)window.getFramebufferHeight());
                }
                RenderSystem.glBindBuffer((int)34962, () -> b.id);
                b.format.setupState();
                shaderinstance.bind();
                RenderSystem.drawElements((int)4, (int)0, (int)b.vertexCount);
                shaderinstance.unbind();
                b.format.clearState();
                RenderSystem.glBindBuffer((int)34962, () -> 0);
            }
            renderType.endDrawing();
        });
        this.queue.clear();
    }

    public static Matrix4f createTranslateMatrix(float p_27654_, float p_27655_, float p_27656_) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.m00(1.0f);
        matrix4f.m11(1.0f);
        matrix4f.m22(1.0f);
        matrix4f.m33(1.0f);
        matrix4f.m03(p_27654_);
        matrix4f.m13(p_27655_);
        matrix4f.m23(p_27656_);
        return matrix4f;
    }

    class Batch {
        final Matrix4f matrix;
        final int vertexCount;
        final Identifier resource;
        final int id;
        final VertexFormat format;
        final int light1;
        final int light2;
        final int overlay1;
        final int overlay2;
        final float red;
        final float green;
        final float blue;
        final float alpha;
        final NopVector2i texPos;

        public Batch(Identifier resource, int id, VertexFormat format, Matrix4f matrix, int vertexCount, NopVector2i texPos, int light, int overlay, float red, float green, float blue, float alpha) {
            this.resource = resource;
            this.id = id;
            this.format = format;
            this.matrix = matrix;
            this.vertexCount = vertexCount;
            this.texPos = texPos;
            this.light1 = light & 0xFFFF;
            this.light2 = light >> 16 & 0xFFFF;
            this.overlay1 = overlay & 0xFFFF;
            this.overlay2 = overlay >> 16 & 0xFFFF;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }
    }
}

