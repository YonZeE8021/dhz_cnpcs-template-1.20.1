/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiButtonBiDirectional
extends GuiButtonNop {
    public static final Identifier resource = new Identifier("customnpcs", "textures/gui/arrowbuttons.png");
    private int color = 0xFFFFFF;
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

    public GuiButtonBiDirectional(IGuiInterface gui, int id, int x, int y, int width, int height, String[] arr, int current) {
        super(gui, id, x, y, width, height, arr, current);
    }

    public GuiButtonBiDirectional(IGuiInterface gui, int id, int x, int y, int width, int height, int current, String ... arr) {
        super(gui, id, x, y, width, height, arr, current);
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        boolean hover = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        boolean hoverL = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + 14 && mouseY < this.getY() + this.height;
        boolean hoverR = !hoverL && mouseX >= this.getX() + this.width - 14 && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        MinecraftClient mc = MinecraftClient.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)resource);
        graphics.drawTexture(resource, this.getX(), this.getY(), 0, hoverL ? 40 : 20, 11, 20);
        graphics.drawTexture(resource, this.getX() + this.width - 11, this.getY(), 11, hover && !hoverL || hoverR ? 40 : 20, 11, 20);
        int l = this.color;
        if (this.packedFGColor != 0) {
            l = this.packedFGColor;
        } else if (!this.active) {
            l = 0xA0A0A0;
        } else if (hover) {
            l = 0xFFFFA0;
        }
        Object text = "";
        float maxWidth = this.width - 36;
        String displayString = this.getMessage().getString();
        if ((float)mc.textRenderer.getWidth(displayString) > maxWidth) {
            char c;
            for (int h = 0; h < displayString.length() && !((float)mc.textRenderer.getWidth((String)(text = (String)text + (c = displayString.charAt(h)))) > maxWidth); ++h) {
            }
            text = (String)text + "...";
        } else {
            text = displayString;
        }
        if (hover) {
            text = "\u00a7n" + (String)text;
        }
        graphics.drawTextWithShadow(mc.textRenderer, (String)text, this.getX() + this.width / 2 - mc.textRenderer.getWidth((String)text) / 2, this.getY() + (this.height - 8) / 2, l);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int value = this.getValue();
        if (this.isMouseOver(mouseX, mouseY) && this.display != null && this.display.length != 0) {
            boolean hoverR;
            boolean hoverL = mouseX >= (double)this.getX() && mouseY >= (double)this.getY() && mouseX < (double)(this.getX() + 14) && mouseY < (double)(this.getY() + this.height);
            boolean bl = hoverR = !hoverL && mouseX >= (double)(this.getX() + 14) && mouseY >= (double)this.getY() && mouseX < (double)(this.getX() + this.width) && mouseY < (double)(this.getY() + this.height);
            if (hoverR) {
                value = (value + 1) % this.display.length;
            }
            if (hoverL) {
                if (value <= 0) {
                    value = this.display.length;
                }
                --value;
            }
            this.setDisplay(value);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClick(double x, double y) {
        if (this.gui.hasSubGui()) {
            return;
        }
        this.gui.buttonEvent(this);
    }
}

