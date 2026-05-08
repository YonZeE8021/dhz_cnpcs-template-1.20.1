/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$class_4534
 *  com.mojang.blaze3d.platform.GlStateManager$class_4535
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayer$MultiPhaseParameters
 *  net.minecraft.text.Text
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.font.TextRenderer$TextLayerType
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.util.math.MatrixStack$Entry
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.RenderPhase$Cull
 *  net.minecraft.client.render.RenderPhase$DepthTest
 *  net.minecraft.client.render.RenderPhase$Lightmap
 *  net.minecraft.client.render.RenderPhase$Transparency
 *  net.minecraft.client.render.RenderPhase$ShaderProgram
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.client.render.GameRenderer
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 */
package noppes.npcs.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.text.StringVisitable;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.IChatMessages;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.entity.EntityNPCInterface;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

public class ChatMessages
implements IChatMessages {
    private static Map<String, ChatMessages> users = new Hashtable<String, ChatMessages>();
    protected static final RenderPhase.Transparency TRANSLUCENT_TRANSPARENCY = new RenderPhase.Transparency("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    private static final RenderPhase.ShaderProgram sharder = new RenderPhase.ShaderProgram(GameRenderer::getPositionColorLightmapProgram);
    protected static final RenderLayer type = RenderLayer.of((String)"chatbubble", (VertexFormat)VertexFormats.POSITION_COLOR_LIGHT, (VertexFormat.DrawMode)VertexFormat.DrawMode.QUADS, (int)256, (boolean)false, (boolean)false, (RenderLayer.MultiPhaseParameters)RenderLayer.MultiPhaseParameters.builder().cull(new RenderPhase.Cull(true)).lightmap(new RenderPhase.Lightmap(true)).program(sharder).build(true));
    protected static final RenderLayer typeDepth = RenderLayer.of((String)"chatbubbledepth", (VertexFormat)VertexFormats.POSITION_COLOR_LIGHT, (VertexFormat.DrawMode)VertexFormat.DrawMode.QUADS, (int)256, (boolean)false, (boolean)true, (RenderLayer.MultiPhaseParameters)RenderLayer.MultiPhaseParameters.builder().cull(new RenderPhase.Cull(true)).transparency(TRANSLUCENT_TRANSPARENCY).program(sharder).lightmap(new RenderPhase.Lightmap(true)).depthTest(new RenderPhase.DepthTest("always", 519)).build(false));
    private Map<Long, TextBlockClient> messages = new TreeMap<Long, TextBlockClient>();
    private int boxLength = 46;
    private float scale = 0.5f;
    private String lastMessage = "";
    private long lastMessageTime = 0L;

    @Override
    public void addMessage(String message, EntityNPCInterface npc) {
        if (!CustomNpcs.EnableChatBubbles) {
            return;
        }
        long time = System.currentTimeMillis();
        if (message.equals(this.lastMessage) && this.lastMessageTime + 1000L > time) {
            return;
        }
        TreeMap<Long, TextBlockClient> messages = new TreeMap<Long, TextBlockClient>(this.messages);
        messages.put(time, new TextBlockClient(message, this.boxLength * 4, true, new Object[]{MinecraftClient.getInstance().player, npc}));
        if (messages.size() > 3) {
            messages.remove(messages.keySet().iterator().next());
        }
        this.messages = messages;
        this.lastMessage = message;
        this.lastMessageTime = time;
    }

    @Override
    public void renderMessages(MatrixStack PoseStack, VertexConsumerProvider typeBuffer, float textscale, boolean inRange, int lightmapUV) {
        Map<Long, TextBlockClient> messages = this.getMessages();
        if (messages.isEmpty()) {
            return;
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShader(GameRenderer::getPositionColorLightmapProgram);
        if (inRange) {
            this.render(PoseStack, typeBuffer, typeBuffer.getBuffer(typeDepth), textscale, false, lightmapUV);
        }
        this.render(PoseStack, typeBuffer, typeBuffer.getBuffer(type), textscale, true, lightmapUV);
    }

    public void render(MatrixStack poseStack, VertexConsumerProvider typeBuffer, VertexConsumer ivertex, float textScale, boolean depth, int lightmapUV) {
        TextRenderer font = MinecraftClient.getInstance().textRenderer;
        float var14 = 0.02666667f;
        int size = 0;
        for (TextBlockClient block : this.messages.values()) {
            size += block.lines.size();
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        Objects.requireNonNull(font);
        int textYSize = (int)((float)(size * 9) * this.scale);
        poseStack.push();
        poseStack.translate(0.0f, (float)textYSize * var14, 0.0f);
        poseStack.scale(textScale, textScale, textScale);
        poseStack.multiply(mc.getEntityRenderDispatcher().getRotation());
        poseStack.scale(-var14, -var14, var14);
        int black = depth ? -16777216 : -16777216;
        int white = depth ? -1140850689 : 0x44FFFFFF;
        MatrixStack.Entry entry = poseStack.peek();
        Matrix4f matrix = entry.getPositionMatrix();
        this.drawRect(ivertex, matrix, lightmapUV, -this.boxLength - 2, -2.0f, this.boxLength + 2, textYSize + 1, white, 0.11f);
        this.drawRect(ivertex, matrix, lightmapUV, -this.boxLength - 1, -3.0f, this.boxLength + 1, -2.0f, black, 0.1f);
        this.drawRect(ivertex, matrix, lightmapUV, -this.boxLength - 1, textYSize + 2, -1.0f, textYSize + 1, black, 0.1f);
        this.drawRect(ivertex, matrix, lightmapUV, 3.0f, textYSize + 2, this.boxLength + 1, textYSize + 1, black, 0.1f);
        this.drawRect(ivertex, matrix, lightmapUV, -this.boxLength - 3, -1.0f, -this.boxLength - 2, textYSize, black, 0.1f);
        this.drawRect(ivertex, matrix, lightmapUV, this.boxLength + 3, -1.0f, this.boxLength + 2, textYSize, black, 0.1f);
        this.drawRect(ivertex, matrix, lightmapUV, -this.boxLength - 2, -2.0f, -this.boxLength - 1, -1.0f, black, 0.1f);
        this.drawRect(ivertex, matrix, lightmapUV, this.boxLength + 2, -2.0f, this.boxLength + 1, -1.0f, black, 0.1f);
        this.drawRect(ivertex, matrix, lightmapUV, -this.boxLength - 2, textYSize + 1, -this.boxLength - 1, textYSize, black, 0.1f);
        this.drawRect(ivertex, matrix, lightmapUV, this.boxLength + 2, textYSize + 1, this.boxLength + 1, textYSize, black, 0.1f);
        this.drawRect(ivertex, matrix, lightmapUV, 0.0f, textYSize + 1, 3.0f, textYSize + 4, white, 0.11f);
        this.drawRect(ivertex, matrix, lightmapUV, -1.0f, textYSize + 4, 1.0f, textYSize + 5, white, 0.11f);
        this.drawRect(ivertex, matrix, lightmapUV, -1.0f, textYSize + 1, 0.0f, textYSize + 4, black, 0.1f);
        this.drawRect(ivertex, matrix, lightmapUV, 3.0f, textYSize + 1, 4.0f, textYSize + 3, black, 0.1f);
        this.drawRect(ivertex, matrix, lightmapUV, 2.0f, textYSize + 3, 3.0f, textYSize + 4, black, 0.1f);
        this.drawRect(ivertex, matrix, lightmapUV, 1.0f, textYSize + 4, 2.0f, textYSize + 5, black, 0.1f);
        this.drawRect(ivertex, matrix, lightmapUV, -2.0f, textYSize + 4, -1.0f, textYSize + 5, black, 0.1f);
        this.drawRect(ivertex, matrix, lightmapUV, -2.0f, textYSize + 5, 1.0f, textYSize + 6, black, 0.1f);
        poseStack.scale(this.scale, this.scale, this.scale);
        int index = 0;
        for (TextBlockClient block : this.messages.values()) {
            for (Text chat : block.lines) {
                float f = -font.getWidth((StringVisitable)chat) / 2;
                Objects.requireNonNull(font);
                font.draw(chat, f, (float)(index * 9), black, false, matrix, typeBuffer, !depth ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, 0, lightmapUV);
                ++index;
            }
        }
        poseStack.pop();
    }

    public void drawRect(VertexConsumer ivertex, Matrix4f matrix, int lightmapUV, float x, float y, float x2, float y2, int color, float z) {
        float j1;
        if (x < x2) {
            j1 = x;
            x = x2;
            x2 = j1;
        }
        if (y < y2) {
            j1 = y;
            y = y2;
            y2 = j1;
        }
        float f1 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f2 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(color & 0xFF) / 255.0f;
        this.draw(ivertex, matrix, lightmapUV, x, y, z, f1, f2, f3);
        this.draw(ivertex, matrix, lightmapUV, x, y2, z, f1, f2, f3);
        this.draw(ivertex, matrix, lightmapUV, x2, y2, z, f1, f2, f3);
        this.draw(ivertex, matrix, lightmapUV, x2, y, z, f1, f2, f3);
    }

    private void draw(VertexConsumer ivertex, Matrix4f matrix, int lightmapUV, float x, float y, float z, float red, float green, float blue) {
        Vector4f v = new Vector4f(x, y, z, 1.0f);
        v.mul((Matrix4fc)matrix);
        ivertex.vertex((double)v.x(), (double)v.y(), (double)v.z()).color(red, green, blue, 1.0f).light(lightmapUV).next();
    }

    public static ChatMessages getChatMessages(String username) {
        if (users.containsKey(username)) {
            return users.get(username);
        }
        ChatMessages chat = new ChatMessages();
        users.put(username, chat);
        return chat;
    }

    private static boolean validPlayer(String username) {
        for (PlayerEntity player : MinecraftClient.getInstance().world.getPlayers()) {
            if (!username.equals(player.getName()) && !username.equals(player.getDisplayName().getString())) continue;
            return true;
        }
        return false;
    }

    private Map<Long, TextBlockClient> getMessages() {
        TreeMap<Long, TextBlockClient> messages = new TreeMap<Long, TextBlockClient>();
        long time = System.currentTimeMillis();
        for (Map.Entry<Long, TextBlockClient> entry : this.messages.entrySet()) {
            if (time > entry.getKey() + 10000L) continue;
            messages.put(entry.getKey(), entry.getValue());
        }
        this.messages = messages;
        return this.messages;
    }

    public boolean hasMessage() {
        return !this.messages.isEmpty();
    }
}

