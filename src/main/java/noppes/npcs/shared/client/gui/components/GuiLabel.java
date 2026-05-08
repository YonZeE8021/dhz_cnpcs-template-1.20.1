/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.text.Text
 *  net.minecraft.text.Style
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.Element
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.tooltip.Tooltip
 */
package noppes.npcs.shared.client.gui.components;

import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.Element;
import net.minecraft.text.StringVisitable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import noppes.npcs.client.CustomNpcResourceListener;

public class GuiLabel
extends ClickableWidget
implements Element {
    public int id;
    private boolean centered = false;
    public boolean enabled = true;
    private boolean labelBgEnabled;
    private int textColor;
    private int backColor;
    private int ulColor;
    private int brColor;
    private int border;

    public GuiLabel(int id, Text label, int color, int x, int y, int width, int height) {
        super(x, y, width, height, label);
        this.id = id;
        this.textColor = color;
        this.width = MinecraftClient.getInstance().textRenderer.getWidth((StringVisitable)this.getMessage());
    }

    public GuiLabel(int id, String s, int x, int y) {
        this(id, (Text)Text.translatable((String)s), CustomNpcResourceListener.DefaultTextColor, x, y, 40, 0);
    }

    public GuiLabel(int id, String s, int x, int y, String tooltip) {
        this(id, (Text)Text.translatable((String)s), CustomNpcResourceListener.DefaultTextColor, x, y, 40, 10);
        this.setTooltip(Tooltip.of((Text)Text.translatable((String)tooltip).setStyle(Style.EMPTY.withColor(16762460))));
    }

    public GuiLabel(int id, String s, int x, int y, int color) {
        this(id, (Text)Text.translatable((String)s), color, x, y, 40, 0);
    }

    public GuiLabel(int id, String s, int x, int y, int width, int height) {
        this(id, (Text)Text.translatable((String)s), CustomNpcResourceListener.DefaultTextColor, x, y, width, height);
        this.centered = true;
    }

    public GuiLabel(int id, String s, int x, int y, int color, int width, int height) {
        this(id, (Text)Text.translatable((String)s), color, x, y, width, height);
        this.centered = true;
    }

    public void setColor(int color) {
        this.textColor = color;
    }

    public void setCentered(boolean bo) {
        this.centered = bo;
    }

    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.enabled) {
            this.drawBox(graphics);
            int i = this.getY() + this.height / 2 + this.border / 2;
            if (this.centered) {
                graphics.drawText(MinecraftClient.getInstance().textRenderer, this.getMessage(), (int)((float)this.getX() + (float)(this.width - MinecraftClient.getInstance().textRenderer.getWidth((StringVisitable)this.getMessage())) / 2.0f), this.getY(), this.textColor, false);
            } else {
                graphics.drawText(MinecraftClient.getInstance().textRenderer, this.getMessage(), this.getX(), this.getY(), this.textColor, false);
            }
            super.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    protected void renderButton(DrawContext p_282139_, int p_268034_, int p_268009_, float p_268085_) {
    }

    protected void appendClickableNarrations(NarrationMessageBuilder p_259858_) {
    }

    protected void drawBox(DrawContext graphics) {
        if (this.labelBgEnabled) {
            int i = this.width + this.border * 2;
            int j = this.height + this.border * 2;
            int k = this.getX() - this.border;
            int l = this.getY() - this.border;
            graphics.fill(k, l, k + i, l + j, this.backColor);
            graphics.drawHorizontalLine(k, k + i, l, this.ulColor);
            graphics.drawHorizontalLine(k, k + i, l + j, this.brColor);
            graphics.drawHorizontalLine(k, l, l + j, this.ulColor);
            graphics.drawHorizontalLine(k + i, l, l + j, this.brColor);
        }
    }
}

