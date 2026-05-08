/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.blaze3d.platform.GlStateManager$class_4534
 *  com.mojang.blaze3d.platform.GlStateManager$class_4535
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Util
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayer$MultiPhaseParameters
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormatElement
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.render.RenderPhase
 *  net.minecraft.client.render.RenderPhase$Texture
 *  net.minecraft.client.render.RenderPhase$Transparency
 *  net.minecraft.client.render.RenderPhase$TextureBase
 *  net.minecraft.client.render.RenderPhase$ShaderProgram
 *  net.minecraft.client.gl.ShaderProgram
 *  net.minecraft.client.render.GameRenderer
 *  org.joml.Vector4f
 */
package noppes.npcs.shared.client.model.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.function.Function;
import net.minecraft.util.Util;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import org.joml.Vector4f;

public class CustomRenderStates
extends RenderPhase {
    public static final Vector4f WHITE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    public static VertexFormat POS_COL_TEX_LIGHT_FADE_NORMAL;
    public static VertexFormat POS_COL_TEX_NORMAL;
    public static final VertexFormat POS_TEX_NORMAL;
    protected static final RenderPhase.Transparency ADDITIVE_TRANSPARENCY;
    protected static final RenderPhase.Transparency SUBTRACTIVE_TRANSPARENCY;
    private static final RenderLayer[] OBJ_RENDER_TYPES;
    public static final RenderLayer OBJ_OUTLINE_RENDER_TYPE;
    protected static final RenderPhase.ShaderProgram RENDERTYPE_ENTITY_CUTOUT_SHADER;
    public static ShaderProgram posTexNormalShader;
    private static final Function<Identifier, RenderLayer> ENTITY_CUTOUT;

    public CustomRenderStates(String p_i225973_1_, Runnable p_i225973_2_, Runnable p_i225973_3_) {
        super(p_i225973_1_, p_i225973_2_, p_i225973_3_);
    }

    public static RenderLayer getObjVBORenderType(int blending, boolean glow) {
        return OBJ_RENDER_TYPES[blending << 1 | (glow ? 1 : 0)];
    }

    public static RenderLayer entityCutout(Identifier p_110444_) {
        return ENTITY_CUTOUT.apply(p_110444_);
    }

    public static RenderLayer getObjRenderType(Identifier texture, int blending, boolean glow) {
        if (POS_COL_TEX_LIGHT_FADE_NORMAL == null) {
            HashMap<String, VertexFormatElement> vertexFormatValues = new HashMap<String, VertexFormatElement>();
            vertexFormatValues.put("Position", VertexFormats.POSITION_ELEMENT);
            vertexFormatValues.put("Color", VertexFormats.COLOR_ELEMENT);
            vertexFormatValues.put("UV0", VertexFormats.TEXTURE_ELEMENT);
            vertexFormatValues.put("UV1", VertexFormats.OVERLAY_ELEMENT);
            vertexFormatValues.put("UV2", VertexFormats.LIGHT_ELEMENT);
            vertexFormatValues.put("Normal", VertexFormats.NORMAL_ELEMENT);
            vertexFormatValues.put("Padding", VertexFormats.PADDING_ELEMENT);
            POS_COL_TEX_LIGHT_FADE_NORMAL = new VertexFormat(ImmutableMap.copyOf(vertexFormatValues));
        }
        RenderPhase.Transparency TransparencyStateShard = TRANSLUCENT_TRANSPARENCY;
        if (blending == BLEND.ADD.getValue()) {
            TransparencyStateShard = ADDITIVE_TRANSPARENCY;
        } else if (blending == BLEND.SUB.getValue()) {
            TransparencyStateShard = SUBTRACTIVE_TRANSPARENCY;
        }
        RenderLayer.MultiPhaseParameters renderTypeState = RenderLayer.MultiPhaseParameters.builder().texture((RenderPhase.TextureBase)new RenderPhase.Texture(texture, false, false)).transparency(TransparencyStateShard).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
        return RenderLayer.of((String)"lm_obj_translucent_no_cull", (VertexFormat)POS_COL_TEX_LIGHT_FADE_NORMAL, (VertexFormat.DrawMode)VertexFormat.DrawMode.field_27379, (int)256, (boolean)true, (boolean)false, (RenderLayer.MultiPhaseParameters)renderTypeState);
    }

    public static RenderLayer getObjColorOnlyRenderType(Identifier texture, int blending, boolean glow) {
        if (POS_COL_TEX_LIGHT_FADE_NORMAL == null) {
            HashMap<String, VertexFormatElement> vertexFormatValues = new HashMap<String, VertexFormatElement>();
            vertexFormatValues.put("Position", VertexFormats.POSITION_ELEMENT);
            vertexFormatValues.put("Color", VertexFormats.COLOR_ELEMENT);
            vertexFormatValues.put("Normal", VertexFormats.NORMAL_ELEMENT);
            vertexFormatValues.put("Padding", VertexFormats.PADDING_ELEMENT);
            POS_COL_TEX_LIGHT_FADE_NORMAL = new VertexFormat(ImmutableMap.copyOf(vertexFormatValues));
        }
        RenderPhase.Transparency TransparencyStateShard = TRANSLUCENT_TRANSPARENCY;
        if (blending == BLEND.ADD.getValue()) {
            TransparencyStateShard = ADDITIVE_TRANSPARENCY;
        } else if (blending == BLEND.SUB.getValue()) {
            TransparencyStateShard = SUBTRACTIVE_TRANSPARENCY;
        }
        RenderLayer.MultiPhaseParameters renderTypeState = RenderLayer.MultiPhaseParameters.builder().texture((RenderPhase.TextureBase)new RenderPhase.Texture(texture, false, false)).transparency(TransparencyStateShard).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
        return RenderLayer.of((String)"lm_obj_translucent_no_cull", (VertexFormat)POS_COL_TEX_LIGHT_FADE_NORMAL, (VertexFormat.DrawMode)VertexFormat.DrawMode.field_27379, (int)256, (boolean)true, (boolean)false, (RenderLayer.MultiPhaseParameters)renderTypeState);
    }

    public static RenderLayer getObjOutlineRenderType(Identifier texture) {
        if (POS_COL_TEX_LIGHT_FADE_NORMAL == null) {
            HashMap<String, VertexFormatElement> vertexFormatValues = new HashMap<String, VertexFormatElement>();
            vertexFormatValues.put("Position", VertexFormats.POSITION_ELEMENT);
            vertexFormatValues.put("Color", VertexFormats.COLOR_ELEMENT);
            vertexFormatValues.put("UV0", VertexFormats.TEXTURE_ELEMENT);
            vertexFormatValues.put("UV1", VertexFormats.OVERLAY_ELEMENT);
            vertexFormatValues.put("UV2", VertexFormats.LIGHT_ELEMENT);
            vertexFormatValues.put("Normal", VertexFormats.NORMAL_ELEMENT);
            vertexFormatValues.put("Padding", VertexFormats.PADDING_ELEMENT);
            POS_COL_TEX_LIGHT_FADE_NORMAL = new VertexFormat(ImmutableMap.copyOf(vertexFormatValues));
        }
        RenderLayer.MultiPhaseParameters renderTypeState = RenderLayer.MultiPhaseParameters.builder().texture((RenderPhase.TextureBase)new RenderPhase.Texture(texture, false, false)).cull(DISABLE_CULLING).depthTest(ALWAYS_DEPTH_TEST).target(OUTLINE_TARGET).build(false);
        return RenderLayer.of((String)"lm_obj_outline_no_cull", (VertexFormat)POS_COL_TEX_LIGHT_FADE_NORMAL, (VertexFormat.DrawMode)VertexFormat.DrawMode.field_27379, (int)256, (boolean)true, (boolean)false, (RenderLayer.MultiPhaseParameters)renderTypeState);
    }

    public static RenderLayer getSpriteRenderType(Identifier texture) {
        if (POS_COL_TEX_NORMAL == null) {
            HashMap<String, VertexFormatElement> vertexFormatValues = new HashMap<String, VertexFormatElement>();
            vertexFormatValues.put("Position", VertexFormats.POSITION_ELEMENT);
            vertexFormatValues.put("Color", VertexFormats.COLOR_ELEMENT);
            vertexFormatValues.put("UV0", VertexFormats.TEXTURE_ELEMENT);
            vertexFormatValues.put("Normal", VertexFormats.NORMAL_ELEMENT);
            vertexFormatValues.put("Padding", VertexFormats.PADDING_ELEMENT);
            POS_COL_TEX_NORMAL = new VertexFormat(ImmutableMap.copyOf(vertexFormatValues));
        }
        RenderLayer.MultiPhaseParameters renderTypeState = RenderLayer.MultiPhaseParameters.builder().texture((RenderPhase.TextureBase)new RenderPhase.Texture(texture, false, false)).build(true);
        return RenderLayer.of((String)"lm_sprite", (VertexFormat)POS_COL_TEX_NORMAL, (VertexFormat.DrawMode)VertexFormat.DrawMode.field_27382, (int)256, (boolean)true, (boolean)false, (RenderLayer.MultiPhaseParameters)renderTypeState);
    }

    static {
        POS_TEX_NORMAL = new VertexFormat(ImmutableMap.of((Object)"Position", (Object)VertexFormats.POSITION_ELEMENT, (Object)"UV0", (Object)VertexFormats.TEXTURE_ELEMENT, (Object)"Normal", (Object)VertexFormats.NORMAL_ELEMENT, (Object)"Padding", (Object)VertexFormats.PADDING_ELEMENT));
        ADDITIVE_TRANSPARENCY = new RenderPhase.Transparency("lm_additive_transparency", () -> {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc((GlStateManager.class_4535)GlStateManager.class_4535.SRC_ALPHA, (GlStateManager.class_4534)GlStateManager.class_4534.ONE);
        }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        });
        SUBTRACTIVE_TRANSPARENCY = new RenderPhase.Transparency("lm_subtractive_transparency", () -> {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc((GlStateManager.class_4535)GlStateManager.class_4535.DST_COLOR, (GlStateManager.class_4534)GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
        }, () -> {
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        });
        OBJ_RENDER_TYPES = new RenderLayer[BLEND.values().length * 2];
        for (BLEND blend : BLEND.values()) {
            for (int glow = 0; glow < 2; ++glow) {
                CustomRenderStates.OBJ_RENDER_TYPES[blend.id * 2 + glow] = RenderLayer.of((String)("lm_obj_" + blend.toString() + (glow == 1 ? "_glow" : "")), (VertexFormat)POS_TEX_NORMAL, (VertexFormat.DrawMode)VertexFormat.DrawMode.field_27379, (int)256, (boolean)true, (boolean)false, (RenderLayer.MultiPhaseParameters)RenderLayer.MultiPhaseParameters.builder().transparency(blend == BLEND.ADD ? ADDITIVE_TRANSPARENCY : (blend == BLEND.SUB ? SUBTRACTIVE_TRANSPARENCY : TRANSLUCENT_TRANSPARENCY)).cull(DISABLE_CULLING).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(false));
            }
        }
        OBJ_OUTLINE_RENDER_TYPE = RenderLayer.of((String)"lm_obj_outline_no_cull", (VertexFormat)POS_TEX_NORMAL, (VertexFormat.DrawMode)VertexFormat.DrawMode.field_27379, (int)256, (boolean)true, (boolean)false, (RenderLayer.MultiPhaseParameters)RenderLayer.MultiPhaseParameters.builder().depthTest(ALWAYS_DEPTH_TEST).cull(DISABLE_CULLING).target(OUTLINE_TARGET).build(false));
        RENDERTYPE_ENTITY_CUTOUT_SHADER = new RenderPhase.ShaderProgram(GameRenderer::getRenderTypeEntityCutoutProgram);
        posTexNormalShader = null;
        ENTITY_CUTOUT = Util.memoize(p_173202_ -> {
            RenderLayer.MultiPhaseParameters rendertype$compositestate = RenderLayer.MultiPhaseParameters.builder().program(new RenderPhase.ShaderProgram(() -> posTexNormalShader)).texture((RenderPhase.TextureBase)new RenderPhase.Texture(p_173202_, false, false)).transparency(NO_TRANSPARENCY).lightmap(ENABLE_LIGHTMAP).overlay(ENABLE_OVERLAY_COLOR).build(true);
            return RenderLayer.of((String)"nop_entity_cutout", (VertexFormat)POS_TEX_NORMAL, (VertexFormat.DrawMode)VertexFormat.DrawMode.field_27379, (int)256, (boolean)true, (boolean)false, (RenderLayer.MultiPhaseParameters)rendertype$compositestate);
        });
    }

    public static enum BLEND {
        NORMAL(0),
        ADD(1),
        SUB(2);

        public final int id;

        private BLEND(int value) {
            this.id = value;
        }

        public int getValue() {
            return this.id;
        }
    }
}

