/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture
 *  com.mojang.authlib.minecraft.MinecraftProfileTexture$Type
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.client.util.DefaultSkinHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.font.TextRenderer$TextLayerType
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.util.Uuids
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  org.joml.Matrix4f
 */
package noppes.npcs.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Uuids;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.MatrixStackMixin;
import noppes.npcs.shared.client.util.ImageDownloadAlt;
import noppes.npcs.shared.client.util.ResourceDownloader;
import noppes.npcs.shared.common.util.LogWriter;
import org.joml.Matrix4f;

public class RenderNPCInterface<T extends EntityNPCInterface, M extends EntityModel<T>>
extends LivingEntityRenderer<T, M> {
    public static int LastTextureTick;
    public static EntityNPCInterface currentNpc;

    public RenderNPCInterface(EntityRendererFactory.Context manager, M model, float f) {
        super(manager, model, f);
    }

    public void renderNameTag(T npc, Text text, MatrixStack matrixStack, VertexConsumerProvider buffer, int light) {
        if (npc == null || !this.hasLabel(npc) || this.dispatcher == null) {
            return;
        }
        double d0 = this.dispatcher.getSquaredDistanceToCamera(npc);
        if (d0 > 512.0) {
            return;
        }
        matrixStack.push();
        Vec3d renderOffset = this.getPositionOffset(npc, 0.0f);
        matrixStack.translate(-renderOffset.getX(), -renderOffset.getY(), -renderOffset.getZ());
        if (((EntityNPCInterface)(npc)).messages != null) {
            float height = ((EntityNPCInterface)(npc)).baseSize.height / 5.0f * (float)((EntityNPCInterface)(npc)).display.getSize();
            float offset = npc.getHeight() * (1.2f + (!((EntityNPCInterface)(npc)).display.showName() ? 0.0f : (((EntityNPCInterface)(npc)).display.getTitle().isEmpty() ? 0.15f : 0.25f)));
            matrixStack.translate(0.0f, offset, 0.0f);
            ((EntityNPCInterface)(npc)).messages.renderMessages(matrixStack, buffer, 0.666667f * height, ((EntityNPCInterface)(npc)).isInRange(this.dispatcher.camera.getFocusedEntity(), 4.0), light);
            matrixStack.translate(0.0f, -offset, 0.0f);
        }
        if (((EntityNPCInterface)(npc)).display.showName()) {
            this.renderLivingLabel(npc, matrixStack, buffer, light);
        }
        matrixStack.pop();
    }

    protected void renderLivingLabel(T npc, MatrixStack matrixStack, VertexConsumerProvider buffer, int light) {
        float scale = ((EntityNPCInterface)(npc)).baseSize.height / 5.0f * (float)((EntityNPCInterface)(npc)).display.getSize();
        float height = npc.getHeight() - 0.06f * scale;
        matrixStack.push();
        TextRenderer fontrenderer = this.getTextRenderer();
        float f2 = 0.01666667f * scale;
        matrixStack.translate(0.0f, height, 0.0f);
        matrixStack.multiply(this.dispatcher.getRotation());
        int color = ((EntityNPCInterface)(npc)).getFaction().color;
        matrixStack.translate(0.0f, scale / 6.5f * 2.0f, 0.0f);
        float f1 = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25f);
        int j = (int)(f1 * 255.0f) << 24;
        matrixStack.scale(-f2, -f2, f2);
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        float y = 0.0f;
        boolean nearby = ((EntityNPCInterface)(npc)).isInRange(this.dispatcher.camera.getFocusedEntity(), 8.0);
        if (!((EntityNPCInterface)(npc)).display.getTitle().isEmpty() && nearby) {
            MutableText title = Text.literal((String)"<").append((Text)Text.translatable((String)((EntityNPCInterface)(npc)).display.getTitle())).append(">");
            float f3 = 0.6f;
            matrixStack.translate(0.0f, 4.0f, 0.0f);
            matrixStack.scale(f3, f3, f3);
            fontrenderer.draw((Text)title, (float)(-fontrenderer.getWidth((StringVisitable)title) / 2), 0.0f, color, false, matrix4f, buffer, TextRenderer.TextLayerType.NORMAL, j, light);
            matrixStack.scale(1.0f / f3, 1.0f / f3, 1.0f / f3);
            y = -10.0f;
        }
        Text name = ((EntityNPCInterface)(npc)).getName();
        fontrenderer.draw(name, (float)(-fontrenderer.getWidth((StringVisitable)name) / 2), y, color, false, matrix4f, buffer, nearby ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, j, light);
        if (nearby) {
            fontrenderer.draw(name, (float)(-fontrenderer.getWidth((StringVisitable)name) / 2), y, color, false, matrix4f, buffer, TextRenderer.TextLayerType.NORMAL, 0, light);
        }
        matrixStack.pop();
    }

    protected void renderColor(EntityNPCInterface npc) {
        if (npc.hurtTime <= 0 && npc.deathTime <= 0) {
            float red = (float)(npc.display.getTint() >> 16 & 0xFF) / 255.0f;
            float green = (float)(npc.display.getTint() >> 8 & 0xFF) / 255.0f;
            float blue = (float)(npc.display.getTint() & 0xFF) / 255.0f;
            RenderSystem.setShaderColor((float)red, (float)green, (float)blue, (float)1.0f);
        }
    }

    protected void setupRotations(T npc, MatrixStack matrixScale, float f, float f1, float f2) {
        if (((EntityNPCInterface)(npc)).isAlive() && ((EntityNPCInterface)(npc)).isSleeping()) {
            matrixScale.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)((EntityNPCInterface)(npc)).ais.orientation));
            matrixScale.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.getLyingAngle(npc)));
            matrixScale.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0f));
        } else if (((EntityNPCInterface)(npc)).isAlive() && ((EntityNPCInterface)(npc)).currentAnimation == 7) {
            matrixScale.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0f - f1));
            float scale = (float)((EntityCustomNpc)(npc)).display.getSize() / 5.0f;
            matrixScale.translate(-scale + ((EntityCustomNpc)(npc)).modelData.getLegsY() * scale, 0.14f, 0.0f);
            matrixScale.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(270.0f));
            matrixScale.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0f));
        } else {
            super.setupTransforms(npc, matrixScale, f, f1, f2);
        }
    }

    protected void scale(T npc, MatrixStack matrixScale, float f) {
        this.renderColor((EntityNPCInterface)(npc));
        int size = ((EntityNPCInterface)(npc)).display.getSize();
        matrixScale.scale(((EntityNPCInterface)(npc)).scaleX / 5.0f * (float)size, ((EntityNPCInterface)(npc)).scaleY / 5.0f * (float)size, ((EntityNPCInterface)(npc)).scaleZ / 5.0f * (float)size);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void render(T npc, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider buffer, int packedLight) {
        if (((EntityNPCInterface)(npc)).isKilled()) {
            this.shadowRadius = 0.0f;
        }
        if (((EntityNPCInterface)(npc)).isKilled() && ((EntityNPCInterface)(npc)).stats.hideKilledBody && ((EntityNPCInterface)(npc)).deathTime > 20) {
            return;
        }
        float xOffset = 0.0f;
        float yOffset = ((EntityNPCInterface)(npc)).currentAnimation == 0 ? ((EntityNPCInterface)(npc)).ais.bodyOffsetY / 10.0f - 0.5f : 0.0f;
        float zOffset = 0.0f;
        if (((EntityNPCInterface)(npc)).isAlive()) {
            if (((EntityNPCInterface)(npc)).isSleeping()) {
                xOffset = (float)(-Math.cos(Math.toRadians(180 - ((EntityNPCInterface)(npc)).ais.orientation)));
                zOffset = (float)(-Math.sin(Math.toRadians(((EntityNPCInterface)(npc)).ais.orientation)));
                yOffset += 0.14f;
            } else if (((EntityNPCInterface)(npc)).currentAnimation == 1 || npc.hasVehicle()) {
                yOffset -= 0.5f - ((EntityCustomNpc)(npc)).modelData.getLegsY() * 0.8f;
            }
        }
        xOffset = xOffset / 5.0f * (float)((EntityNPCInterface)(npc)).display.getSize();
        yOffset = yOffset / 5.0f * (float)((EntityNPCInterface)(npc)).display.getSize();
        zOffset = zOffset / 5.0f * (float)((EntityNPCInterface)(npc)).display.getSize();
        if (((EntityNPCInterface)(npc)).display.getBossbar() != 1 && (((EntityNPCInterface)(npc)).display.getBossbar() != 2 || !((EntityNPCInterface)(npc)).isAttacking()) || ((EntityNPCInterface)(npc)).isKilled() || ((EntityNPCInterface)(npc)).deathTime > 20 || ((EntityNPCInterface)(npc)).canNpcSee((Entity)MinecraftClient.getInstance().player)) {
            // empty if block
        }
        if (((EntityNPCInterface)(npc)).ais.getStandingType() == 3 && !((EntityNPCInterface)(npc)).isWalking() && !((EntityNPCInterface)(npc)).isInteracting()) {
            ((EntityNPCInterface)(npc)).prevBodyYaw = ((EntityNPCInterface)(npc)).bodyYaw = (float)((EntityNPCInterface)(npc)).ais.orientation;
        }
        this.shadowRadius = npc.getWidth() * 0.8f;
        int stackSize = ((MatrixStackMixin)matrixStack).getStack().size();
        try {
            currentNpc = npc;
            super.render(npc, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        }
        catch (Throwable e) {
            while (((MatrixStackMixin)matrixStack).getStack().size() > stackSize) {
                matrixStack.pop();
            }
            LogWriter.except(e);
        }
        finally {
            currentNpc = null;
        }
    }

    protected float getBob(T npc, float limbSwingAmount) {
        if (((EntityNPCInterface)(npc)).isKilled() || !((EntityNPCInterface)(npc)).display.getHasLivingAnimation()) {
            return 0.0f;
        }
        return super.getAnimationProgress(npc, limbSwingAmount);
    }

    @Override
    public Identifier getTexture(T npc) {
        return this.getTextureLocation(npc);
    }

    public Identifier getTextureLocation(T npc) {
        if (((EntityNPCInterface)(npc)).textureLocation == null) {
            if (((EntityNPCInterface)(npc)).display.skinType == 0) {
                ((EntityNPCInterface)(npc)).textureLocation = new Identifier(((EntityNPCInterface)(npc)).display.getSkinTexture());
            } else {
                if (LastTextureTick < 5) {
                    return DefaultSkinHelper.getTexture();
                }
                if (((EntityNPCInterface)(npc)).display.skinType == 1 && ((EntityNPCInterface)(npc)).display.playerProfile != null) {
                    MinecraftClient minecraft = MinecraftClient.getInstance();
                    Map map = minecraft.getSkinProvider().getTextures(((EntityNPCInterface)(npc)).display.playerProfile);
                    ((EntityNPCInterface)(npc)).textureLocation = map.containsKey(MinecraftProfileTexture.Type.SKIN) ? minecraft.getSkinProvider().loadSkin((MinecraftProfileTexture)map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN) : DefaultSkinHelper.getTexture((UUID)Uuids.getUuidFromProfile((GameProfile)((EntityNPCInterface)(npc)).display.playerProfile));
                } else if (((EntityNPCInterface)(npc)).display.skinType == 2 && !((EntityNPCInterface)(npc)).display.getSkinUrl().isEmpty()) {
                    try {
                        boolean fixSkin = npc instanceof EntityCustomNpc && ((EntityCustomNpc)(npc)).modelData.getEntity((EntityNPCInterface)(npc)) == null;
                        File file = ResourceDownloader.getUrlFile(((EntityNPCInterface)(npc)).display.getSkinUrl(), fixSkin);
                        ((EntityNPCInterface)(npc)).textureLocation = ResourceDownloader.getUrlResourceLocation(((EntityNPCInterface)(npc)).display.getSkinUrl(), fixSkin);
                        this.loadSkin(file, ((EntityNPCInterface)(npc)).textureLocation, ((EntityNPCInterface)(npc)).display.getSkinUrl(), fixSkin);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        if (((EntityNPCInterface)(npc)).textureLocation == null) {
            return DefaultSkinHelper.getTexture();
        }
        return ((EntityNPCInterface)(npc)).textureLocation;
    }

    private void loadSkin(File file, Identifier resource, String par1Str, boolean fix64) {
        TextureManager texturemanager = MinecraftClient.getInstance().getTextureManager();
        AbstractTexture object = texturemanager.getOrDefault(resource, null);
        if (object == null) {
            ResourceDownloader.load(new ImageDownloadAlt(file, par1Str, resource, DefaultSkinHelper.getTexture(), fix64, () -> {}));
        }
    }
}

