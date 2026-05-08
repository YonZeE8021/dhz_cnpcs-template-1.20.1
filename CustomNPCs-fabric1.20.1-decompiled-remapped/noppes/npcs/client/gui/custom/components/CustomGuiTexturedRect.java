/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.text.Text
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferBuilder$BuiltBuffer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.render.GameRenderer
 *  org.joml.Matrix4f
 */
package noppes.npcs.client.gui.custom.components;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiTexturedRectWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import org.joml.Matrix4f;

public class CustomGuiTexturedRect
extends ClickableWidget
implements IGuiComponent {
    private CustomGuiTexturedRectWrapper component = null;
    GuiCustom parent;
    Identifier texture;
    public int id;
    public int x;
    public int y;
    public int width;
    public int height;
    public int textureX;
    public int textureY;
    float scale = 1.0f;
    List<Text> hoverText;
    public boolean hasRepeatingTexture = false;
    public int texRepWidth;
    public int texRepHeight;
    public int texRepBorderSize = 0;

    public CustomGuiTexturedRect(GuiCustom parent, CustomGuiTexturedRectWrapper component) {
        super(component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight(), (Text)Text.empty());
        this.component = component;
        this.parent = parent;
        this.init();
    }

    @Override
    public void init() {
        this.id = this.component.getID();
        this.texture = new Identifier(this.component.getTexture());
        this.x = this.component.getPosX();
        this.y = this.component.getPosY();
        this.width = this.component.getWidth();
        this.height = this.component.getHeight();
        this.textureX = this.component.getTextureX();
        this.textureY = this.component.getTextureY();
        this.scale = this.component.getScale();
        this.hasRepeatingTexture = this.component.hasRepeatingTexture;
        this.texRepWidth = this.component.texRepWidth;
        this.texRepHeight = this.component.texRepHeight;
        this.texRepBorderSize = this.component.texRepBorderSize;
        if (this.component.hasHoverText()) {
            this.hoverText = this.component.getHoverTextList();
        }
    }

    public CustomGuiTexturedRect setRep(int texRepWidth, int texRepHeight, int texRepBorderSize) {
        this.texRepWidth = texRepWidth;
        this.texRepHeight = texRepHeight;
        this.texRepBorderSize = texRepBorderSize;
        this.hasRepeatingTexture = true;
        return this;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public void onRender(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.component.getTexture().isEmpty() || !this.component.getVisible()) {
            return;
        }
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        graphics.getMatrices().push();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)this.texture);
        Matrix4f m = graphics.getMatrices().peek().getPositionMatrix();
        if (!this.hasRepeatingTexture) {
            this.draw(m, this.x, this.y, this.textureX, this.textureY, this.width, this.height);
        } else {
            if (this.texRepBorderSize > 0) {
                this.draw(m, this.x, this.y, this.textureX, this.textureY, this.texRepBorderSize, this.texRepBorderSize);
                this.draw(m, this.x + this.width - this.texRepBorderSize, this.y, this.textureX + this.texRepWidth - this.texRepBorderSize, this.textureY, this.texRepBorderSize, this.texRepBorderSize);
                this.draw(m, this.x, this.y + this.height - this.texRepBorderSize, this.textureX, this.textureY + this.texRepHeight - this.texRepBorderSize, this.texRepBorderSize, this.texRepBorderSize);
                this.draw(m, this.x + this.width - this.texRepBorderSize, this.y + this.height - this.texRepBorderSize, this.textureX + this.texRepWidth - this.texRepBorderSize, this.textureY + this.texRepHeight - this.texRepBorderSize, this.texRepBorderSize, this.texRepBorderSize);
            }
            float w = (float)this.width - (float)this.texRepBorderSize * 2.0f;
            float h = (float)this.height - (float)this.texRepBorderSize * 2.0f;
            float tw = (float)this.texRepWidth - (float)this.texRepBorderSize * 2.0f;
            float th = (float)this.texRepHeight - (float)this.texRepBorderSize * 2.0f;
            float mx = w / tw;
            float my = h / th;
            int i = 0;
            while ((float)i < my) {
                float dh = th * Math.min(1.0f, my - (float)i);
                this.draw(m, this.x, (float)(this.y + this.texRepBorderSize) + th * (float)i, this.textureX, this.textureY + this.texRepBorderSize, this.texRepBorderSize, dh);
                this.draw(m, this.x + this.width - this.texRepBorderSize, (float)(this.y + this.texRepBorderSize) + th * (float)i, this.textureX + this.texRepWidth - this.texRepBorderSize, this.textureY + this.texRepBorderSize, this.texRepBorderSize, dh);
                int j = 0;
                while ((float)j < mx) {
                    float dw = tw * Math.min(1.0f, mx - (float)j);
                    this.draw(m, (float)(this.x + this.texRepBorderSize) + tw * (float)j, this.y, this.textureX + this.texRepBorderSize, this.textureY, dw, this.texRepBorderSize);
                    this.draw(m, (float)(this.x + this.texRepBorderSize) + tw * (float)j, this.y + this.height - this.texRepBorderSize, this.textureX + this.texRepBorderSize, this.textureY + this.texRepHeight - this.texRepBorderSize, dw, this.texRepBorderSize);
                    this.draw(m, (float)(this.x + this.texRepBorderSize) + tw * (float)j, (float)(this.y + this.texRepBorderSize) + th * (float)i, this.textureX + this.texRepBorderSize, this.textureY + this.texRepBorderSize, dw, dh);
                    ++j;
                }
                ++i;
            }
        }
        if (hovered && this.hoverText != null && this.hoverText.size() > 0) {
            this.parent.hoverText = this.hoverText;
        }
        graphics.getMatrices().pop();
    }

    private void draw(Matrix4f m, float x, float y, float texX, float texY, float width, float height) {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(VertexFormat.DrawMode.field_27382, VertexFormats.POSITION_TEXTURE);
        int blitLevel = Math.max(0, this.id);
        bufferbuilder.vertex(m, x, y + height * this.scale, (float)blitLevel).texture(texX * 0.00390625f, (texY + height) * 0.00390625f).next();
        bufferbuilder.vertex(m, x + width * this.scale, y + height * this.scale, (float)blitLevel).texture((texX + width) * 0.00390625f, (texY + height) * 0.00390625f).next();
        bufferbuilder.vertex(m, x + width * this.scale, y, (float)blitLevel).texture((texX + width) * 0.00390625f, texY * 0.00390625f).next();
        bufferbuilder.vertex(m, x, y, (float)blitLevel).texture(texX * 0.00390625f, texY * 0.00390625f).next();
        BufferRenderer.drawWithGlobalProgram((BufferBuilder.BuiltBuffer)bufferbuilder.end());
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }

    @Override
    public ICustomGuiComponent component() {
        return this.component;
    }

    public void playDownSound(SoundManager p_93665_) {
    }

    protected void renderButton(DrawContext p_282139_, int p_268034_, int p_268009_, float p_268085_) {
    }

    protected void appendClickableNarrations(NarrationMessageBuilder p_259858_) {
    }
}

