/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.shared.client.gui.listeners.ISliderListener;

public class GuiSliderNop
extends ClickableWidget {
    private ISliderListener listener;
    public int id;
    public float sliderValue = 1.0f;
    public float startValue = 1.0f;
    public static final int UNSET_FG_COLOR = -1;
    protected int packedFGColor = -1;

    public int getFGColor() {
        if (this.packedFGColor != -1) {
            return this.packedFGColor;
        }
        return this.active ? 0xFFFFFF : 0xA0A0A0;
    }

    public void setFGColor(int color) {
        this.packedFGColor = color;
    }

    public void clearFGColor() {
        this.packedFGColor = -1;
    }

    public GuiSliderNop(Screen parent, int id, int xPos, int yPos, String displayString, float sliderValue) {
        super(xPos, yPos, 150, 20, (Text)Text.translatable((String)displayString));
        this.id = id;
        this.sliderValue = sliderValue;
        this.startValue = sliderValue;
        this.listener = (ISliderListener)parent;
    }

    public GuiSliderNop(Screen parent, int id, int xPos, int yPos, float sliderValue) {
        this(parent, id, xPos, yPos, "", sliderValue);
        this.listener.mouseDragged(this);
    }

    public GuiSliderNop(Screen parent, int id, int xPos, int yPos, int width, int height, float sliderValue) {
        this(parent, id, xPos, yPos, "", sliderValue);
        this.width = width;
        this.height = height;
        this.listener.mouseDragged(this);
    }

    public void setString(String str) {
        this.setMessage((Text)Text.translatable((String)str));
    }

    private void setSliderValue(float value) {
        if ((value = MathHelper.clamp((float)value, (float)0.0f, (float)1.0f)) == this.sliderValue) {
            return;
        }
        this.sliderValue = value;
        this.listener.mouseDragged(this);
    }

    protected void renderButton(DrawContext p_93676_, int p_93677_, int p_93678_, float p_93679_) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        TextRenderer font = minecraft.textRenderer;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture((int)0, (Identifier)WIDGETS_TEXTURE);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)this.alpha);
        int i = 0;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        p_93676_.drawTexture(WIDGETS_TEXTURE, this.getX(), this.getY(), 0, 46 + i * 20, this.width / 2, this.height);
        p_93676_.drawTexture(WIDGETS_TEXTURE, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        this.renderBg(p_93676_, minecraft, p_93677_, p_93678_);
        int j = this.getFGColor();
        p_93676_.drawCenteredTextWithShadow(font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, j | MathHelper.ceil((float)(this.alpha * 255.0f)) << 24);
    }

    public void onClick(double x, double y) {
        if (!this.visible || !this.active) {
            return;
        }
        this.setSliderValue((float)(x - (double)(this.getX() + 4)) / (float)(this.width - 8));
        super.onClick(x, y);
    }

    protected void onDrag(double x, double y, double p_onDrag_5_, double p_onDrag_7_) {
        this.setSliderValue((float)(x - (double)(this.getX() + 4)) / (float)(this.width - 8));
        super.onDrag(x, y, p_onDrag_5_, p_onDrag_7_);
    }

    protected void appendClickableNarrations(NarrationMessageBuilder p_259858_) {
    }

    public void onRelease(double x, double y) {
        if (this.sliderValue == this.startValue) {
            return;
        }
        super.playDownSound(MinecraftClient.getInstance().getSoundManager());
        this.listener.mouseReleased(this);
        this.startValue = this.sliderValue;
    }

    public void renderBg(DrawContext graphics, MinecraftClient mc, int p_146119_2_, int p_146119_3_) {
        if (!this.visible) {
            return;
        }
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)WIDGETS_TEXTURE);
        int lvt_4_1_ = (this.hovered ? 2 : 1) * 20;
        graphics.drawTexture(WIDGETS_TEXTURE, this.getX() + (int)((double)this.sliderValue * (double)(this.width - 8)), this.getY(), 0, 46 + lvt_4_1_, 4, 20);
        graphics.drawTexture(WIDGETS_TEXTURE, this.getX() + (int)((double)this.sliderValue * (double)(this.width - 8)) + 4, this.getY(), 196, 46 + lvt_4_1_, 4, 20);
    }
}

