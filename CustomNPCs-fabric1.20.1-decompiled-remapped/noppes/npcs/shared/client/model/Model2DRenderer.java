/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.gl.VertexBuffer
 *  net.minecraft.client.gl.VertexBuffer$Usage
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.resource.Resource
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Matrix3f
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 */
package noppes.npcs.shared.client.model;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.math.RotationAxis;
import noppes.npcs.shared.client.model.NopModelPart;
import noppes.npcs.shared.client.model.util.BatchRenderer;
import noppes.npcs.shared.client.model.util.CustomRenderStates;
import noppes.npcs.shared.client.model.util.Polygon;
import noppes.npcs.shared.client.model.util.Vertex;
import noppes.npcs.shared.client.util.ImageDownloadAlt;
import noppes.npcs.shared.client.util.ResourceDownloader;
import noppes.npcs.shared.common.util.NopVector2i;
import noppes.npcs.shared.common.util.NopVector3f;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Model2DRenderer
extends NopModelPart {
    private final float x1;
    private final float x2;
    private final float y1;
    private final float y2;
    private final int width;
    private final int height;
    private final NopVector2i texPos;
    private float rotationOffsetX;
    private float rotationOffsetY;
    private float rotationOffsetZ;
    public static Identifier textureOverride = null;
    private Identifier location;
    private float scaleX = 1.0f;
    private float scaleY = 1.0f;
    private float thickness = 1.0f;
    private final Map<Identifier, Polygon[]> compiled = new HashMap<Identifier, Polygon[]>();
    VertexBuffer cache;

    public Model2DRenderer(int texWidth, int texHeight, int x, int y, int width, int height, Identifier location) {
        super(texWidth, texHeight, 0, 0);
        this.width = width;
        this.height = height;
        this.texPos = new NopVector2i(x, y);
        this.setTexSize(texWidth, texHeight);
        this.location = location;
        this.x1 = (float)x / (float)texWidth;
        this.y1 = (float)y / (float)texHeight;
        this.x2 = ((float)x + (float)width) / (float)texWidth;
        this.y2 = ((float)y + (float)height) / (float)texHeight;
        this.init(location);
    }

    public Polygon[] init(Identifier location) {
        float f9;
        float f8;
        float f7;
        int k;
        BufferedImage image;
        Polygon[] polygons;
        block35: {
            polygons = this.compiled.get(location);
            if (polygons != null || location == null || location.toString().isEmpty()) {
                return polygons;
            }
            if (ResourceDownloader.contains(location)) {
                return null;
            }
            image = null;
            Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(location).orElse(null);
            if (resource != null) {
                try {
                    image = ImageIO.read(resource.getInputStream());
                }
                catch (Exception e) {
                    AbstractTexture text = MinecraftClient.getInstance().getTextureManager().getOrDefault(location, null);
                    if (text == null || !(text instanceof ImageDownloadAlt)) break block35;
                    try (FileInputStream input2 = new FileInputStream(((ImageDownloadAlt)text).cacheFile);){
                        image = ImageIO.read(input2);
                    }
                    catch (Exception input2) {
                        // empty catch block
                    }
                }
            }
        }
        int scaleW = 1;
        int scaleH = 1;
        if (image != null) {
            scaleW = Math.max(1, (int)((float)image.getWidth() / this.xTexSize));
            scaleH = Math.max(1, (int)((float)image.getHeight() / this.yTexSize));
        }
        int width = this.width * scaleW;
        int height = this.height * scaleH;
        NopVector2i texPos = this.texPos.mul(scaleW, scaleH);
        polygons = new Polygon[6];
        polygons[0] = new Polygon(new Vector3f(0.0f, 0.0f, 1.0f), new Vertex(0.0f, 0.0f, 0.0f, this.x1, this.y2), new Vertex(1.0f, 0.0f, 0.0f, this.x2, this.y2), new Vertex(1.0f, 1.0f, 0.0f, this.x2, this.y1), new Vertex(0.0f, 1.0f, 0.0f, this.x1, this.y1));
        polygons[1] = new Polygon(new Vector3f(0.0f, 0.0f, -1.0f), new Vertex(0.0f, 1.0f, -0.0625f, this.x1, this.y1), new Vertex(1.0f, 1.0f, -0.0625f, this.x2, this.y1), new Vertex(1.0f, 0.0f, -0.0625f, this.x2, this.y2), new Vertex(0.0f, 0.0f, -0.0625f, this.x1, this.y2));
        ArrayList<Vertex> list = new ArrayList<Vertex>();
        ArrayList<Vertex> list2 = new ArrayList<Vertex>();
        for (k = 0; k < width; ++k) {
            f7 = (float)k / (float)width;
            f8 = this.x1 + (this.x2 - this.x1) * f7 - 0.5f * (this.x1 - this.x2) / (float)width;
            f9 = f7 + 1.0f / (float)width;
            boolean left = false;
            boolean right = false;
            if (image == null) {
                left = true;
                right = true;
            } else {
                try {
                    for (int n = 0; n < height; ++n) {
                        if ((image.getRGB(texPos.x + k, texPos.y + n) >> 24 & 0xFF) < 128) continue;
                        if (k + 1 < width && (image.getRGB(texPos.x + k + 1, texPos.y + n) >> 24 & 0xFF) < 128) {
                            right = true;
                        } else if (k + 1 == width) {
                            right = true;
                        }
                        if (k > 0 && (image.getRGB(texPos.x + k - 1, texPos.y + n) >> 24 & 0xFF) < 128) {
                            left = true;
                            continue;
                        }
                        if (k != 0) continue;
                        left = true;
                    }
                }
                catch (Exception n) {
                    // empty catch block
                }
            }
            if (left) {
                list.add(new Vertex(f7, 0.0f, -0.0625f, f8, this.y2));
                list.add(new Vertex(f7, 0.0f, 0.0f, f8, this.y2));
                list.add(new Vertex(f7, 1.0f, 0.0f, f8, this.y1));
                list.add(new Vertex(f7, 1.0f, -0.0625f, f8, this.y1));
            }
            if (!right) continue;
            list2.add(new Vertex(f9, 1.0f, -0.0625f, f8, this.y1));
            list2.add(new Vertex(f9, 1.0f, 0.0f, f8, this.y1));
            list2.add(new Vertex(f9, 0.0f, 0.0f, f8, this.y2));
            list2.add(new Vertex(f9, 0.0f, -0.0625f, f8, this.y2));
        }
        polygons[2] = new Polygon(new Vector3f(-1.0f, 0.0f, 0.0f), list.toArray(new Vertex[0]));
        polygons[3] = new Polygon(new Vector3f(1.0f, 0.0f, 0.0f), list2.toArray(new Vertex[0]));
        list = new ArrayList();
        list2 = new ArrayList();
        for (k = 0; k < height; ++k) {
            f7 = (float)k / (float)height;
            f8 = this.y2 + (this.y1 - this.y2) * f7 - 0.5f * (this.y2 - this.y1) / (float)height;
            f9 = f7 + 1.0f / (float)height;
            boolean top = false;
            boolean bottom = false;
            if (image == null) {
                top = true;
                bottom = true;
            } else {
                try {
                    for (int n = 0; n < width; ++n) {
                        int m = height - k - 1;
                        if ((image.getRGB(texPos.x + n, texPos.y + m) >> 24 & 0xFF) < 128) continue;
                        if (m > 0 && (image.getRGB(texPos.x + n, texPos.y + m - 1) >> 24 & 0xFF) < 128) {
                            top = true;
                        } else if (m == 0) {
                            top = true;
                        }
                        if (m + 1 < height && (image.getRGB(texPos.x + n, texPos.y + m + 1) >> 24 & 0xFF) < 128) {
                            bottom = true;
                            continue;
                        }
                        if (m + 1 != height) continue;
                        bottom = true;
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            if (bottom) {
                list2.add(new Vertex(1.0f, f7, 0.0f, this.x2, f8));
                list2.add(new Vertex(0.0f, f7, 0.0f, this.x1, f8));
                list2.add(new Vertex(0.0f, f7, -0.0625f, this.x1, f8));
                list2.add(new Vertex(1.0f, f7, -0.0625f, this.x2, f8));
            }
            if (!top) continue;
            list.add(new Vertex(0.0f, f9, 0.0f, this.x1, f8));
            list.add(new Vertex(1.0f, f9, 0.0f, this.x2, f8));
            list.add(new Vertex(1.0f, f9, -0.0625f, this.x2, f8));
            list.add(new Vertex(0.0f, f9, -0.0625f, this.x1, f8));
        }
        polygons[4] = new Polygon(new Vector3f(0.0f, 1.0f, 0.0f), list.toArray(new Vertex[0]));
        polygons[5] = new Polygon(new Vector3f(0.0f, -1.0f, 0.0f), list2.toArray(new Vertex[0]));
        this.compiled.put(location, polygons);
        return polygons;
    }

    @Override
    public void render(MatrixStack mstack, VertexConsumer builder, int light, int overlay, float red, float green, float blue, float alpha) {
        this.render(textureOverride != null ? textureOverride : this.location, mstack, builder, light, overlay, red, green, blue, alpha);
    }

    public void render(Identifier location, MatrixStack mstack, VertexConsumer builder, int light, int overlay, float red, float green, float blue, float alpha) {
        if (!this.visible || location == null || location.toString().isEmpty()) {
            return;
        }
        mstack.push();
        this.translateAndRotate(mstack);
        float f = 0.0625f;
        mstack.translate(this.rotationOffsetX * f, this.rotationOffsetY * f, this.rotationOffsetZ * f);
        mstack.scale(this.scaleX * (float)this.width / (float)this.height, this.scaleY, this.thickness);
        mstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
        if (this.mirror) {
            mstack.translate(0.0f, 0.0f, -1.0f * f);
            mstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        }
        this.renderModel(location, mstack.peek().getNormalMatrix(), mstack.peek().getPositionMatrix(), builder, light, overlay, red, green, blue, alpha);
        mstack.pop();
    }

    public void render(Identifier resource, MatrixStack mstack, int light, int overlay, float red, float green, float blue, float alpha) {
        if (!this.visible || resource == null) {
            return;
        }
        MinecraftClient.getInstance().getTextureManager().bindTexture(resource);
        RenderLayer rType = CustomRenderStates.entityCutout(resource);
        RenderSystem.setShader(() -> CustomRenderStates.posTexNormalShader);
        RenderSystem.setShaderTexture((int)0, (Identifier)resource);
        RenderSystem.setTextureMatrix((Matrix4f)BatchRenderer.createTranslateMatrix(this.texPos.x, this.texPos.y, 0.0f));
        if (this.cache == null) {
            this.cache = new VertexBuffer(VertexBuffer.Usage.field_44793);
            MatrixStack mmstack = new MatrixStack();
            mmstack.push();
            Tessellator t = Tessellator.getInstance();
            BufferBuilder bufferbuilder = t.getBuffer();
            bufferbuilder.begin(VertexFormat.DrawMode.field_27379, CustomRenderStates.POS_TEX_NORMAL);
            this.renderModel(resource, mmstack.peek().getNormalMatrix(), mmstack.peek().getPositionMatrix(), (VertexConsumer)bufferbuilder, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
            this.cache.upload(bufferbuilder.end());
            mmstack.pop();
        }
        mstack.push();
        this.translateAndRotate(mstack);
        float f = 0.0625f;
        mstack.translate(this.rotationOffsetX * f, this.rotationOffsetY * f, this.rotationOffsetZ * f);
        mstack.scale(this.scaleX * (float)this.width / (float)this.height, this.scaleY, this.thickness);
        mstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0f));
        if (this.mirror) {
            mstack.translate(0.0f, 0.0f, -1.0f * f);
            mstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        }
        MatrixStack.Entry entry = mstack.peek();
        Matrix4f matrix = entry.getPositionMatrix();
        this.cache.draw(matrix, new Matrix4f(), RenderSystem.getShader());
        mstack.pop();
    }

    public void renderModel(Identifier resource, Matrix3f matrix3f, Matrix4f matrix4f, VertexConsumer builder, int light, int overlay, float red, float green, float blue, float alpha) {
        Polygon[] polygons = this.init(resource);
        if (polygons == null) {
            return;
        }
        for (int i = 0; i < polygons.length; ++i) {
            Polygon p = polygons[i];
            Vector3f vector3f = new Vector3f(p.normal.x, p.normal.y, p.normal.z);
            vector3f.mul((Matrix3fc)matrix3f);
            float nX = vector3f.x();
            float nY = vector3f.y();
            float nZ = vector3f.z();
            for (int j = 0; j < p.vertexes.length; ++j) {
                Vertex vec = p.vertexes[j];
                Vector4f vector4f = new Vector4f(vec.pos.x, vec.pos.y, vec.pos.z, 1.0f);
                vector4f.mul((Matrix4fc)matrix4f);
                builder.vertex((double)vector4f.x(), (double)vector4f.y(), (double)vector4f.z());
                builder.color(red, green, blue, alpha);
                builder.texture(vec.texCoords.x, vec.texCoords.y);
                builder.overlay(overlay);
                builder.light(light);
                builder.normal(nX, nY, nZ);
                builder.next();
            }
        }
    }

    private void addVertex(VertexConsumer builder, Matrix4f matrix, float x, float y, float z, float red, float green, float blue, float alpha, float texU, float texV, int overlayUV, int lightmapUV, float normalX, float normalY, float normalZ) {
        Vector4f v = new Vector4f(x, y, z, 1.0f);
        v.mul((Matrix4fc)matrix);
        builder.vertex((double)v.x(), (double)v.y(), (double)v.z());
        builder.color(red, green, blue, alpha);
        builder.texture(texU, texV);
        builder.overlay(overlayUV);
        builder.light(lightmapUV);
        builder.normal(normalX, normalY, normalZ);
        builder.next();
    }

    public Model2DRenderer setRotationOffset(float x, float y, float z) {
        this.rotationOffsetX = x;
        this.rotationOffsetY = y;
        this.rotationOffsetZ = z;
        return this;
    }

    public Model2DRenderer setRotationOffset(NopVector3f scale) {
        this.rotationOffsetX = scale.x;
        this.rotationOffsetY = scale.y;
        this.rotationOffsetZ = scale.z;
        return this;
    }

    public void setScale(float scale) {
        this.scaleX = scale;
        this.scaleY = scale;
    }

    public void setScale(float x, float y) {
        this.scaleX = x;
        this.scaleY = y;
    }

    public Model2DRenderer setScale(NopVector3f scale) {
        this.scaleX = scale.x;
        this.scaleY = scale.y;
        this.thickness = scale.z;
        return this;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }
}

