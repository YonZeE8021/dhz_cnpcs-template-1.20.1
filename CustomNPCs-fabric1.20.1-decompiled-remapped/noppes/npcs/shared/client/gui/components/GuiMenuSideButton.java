/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiMenuSideButton
extends GuiButtonNop {
    public static final Identifier resource = new Identifier("customnpcs", "textures/gui/menusidebutton.png");
    public boolean active;

    public GuiMenuSideButton(IGuiInterface gui, int i, int j, int k, String s) {
        this(gui, i, j, k, 200, 20, s);
    }

    public GuiMenuSideButton(IGuiInterface gui, int i, int j, int k, int l, int i1, String s) {
        super(gui, i, j, k, l, i1, s);
    }

    @Override
    public void render(DrawContext graphics, int i, int j, float partialTicks) {
        if (!this.visible) {
            return;
        }
        MinecraftClient minecraft = MinecraftClient.getInstance();
        TextRenderer fontrenderer = minecraft.textRenderer;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)resource);
        int width = this.width + (this.active ? 2 : 0);
        boolean bl = this.hovered = i >= this.getX() && j >= this.getY() && i < this.getX() + width && j < this.getY() + this.height;
        int k = this.active ? 0 : (this.hovered ? 2 : 1);
        graphics.drawTexture(resource, this.getX(), this.getY(), 0, k * 22, width, this.height);
        Object text = "";
        float maxWidth = (float)width * 0.75f;
        String displayString = this.getMessage().getString();
        if ((float)fontrenderer.getWidth(displayString) > maxWidth) {
            char c;
            for (int h = 0; h < displayString.length() && !((float)fontrenderer.getWidth((String)text + (c = displayString.charAt(h))) > maxWidth); ++h) {
                text = (String)text + c;
            }
            text = (String)text + "...";
        } else {
            text = displayString;
        }
        if (this.active) {
            graphics.drawCenteredTextWithShadow(fontrenderer, (String)text, this.getX() + width / 2, this.getY() + (this.height - 8) / 2, 0xFFFFA0);
        } else if (this.hovered) {
            graphics.drawCenteredTextWithShadow(fontrenderer, (String)text, this.getX() + width / 2, this.getY() + (this.height - 8) / 2, 0xFFFFA0);
        } else {
            graphics.drawCenteredTextWithShadow(fontrenderer, (String)text, this.getX() + width / 2, this.getY() + (this.height - 8) / 2, 0xE0E0E0);
        }
    }

    public boolean mouseClicked(double i, double j, int button) {
        if (!this.active) {
            return super.mouseClicked(i, j, button);
        }
        return false;
    }
}

