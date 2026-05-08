/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.util.math.RotationAxis
 */
package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.math.RotationAxis;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiMenuTopButton
extends GuiButtonNop {
    public static final Identifier resource = new Identifier("customnpcs", "textures/gui/menutopbutton.png");
    protected int height;
    public boolean active = false;
    public boolean hover = false;
    public boolean rotated = false;

    public GuiMenuTopButton(IGuiInterface gui, int i, int j, int k, String s) {
        super(gui, i, j, k, s);
        this.width = MinecraftClient.getInstance().textRenderer.getWidth((StringVisitable)this.getMessage()) + 12;
        this.height = 20;
    }

    public GuiMenuTopButton(IGuiInterface gui, int i, GuiButtonNop parent, String s) {
        this(gui, i, parent.getX() + parent.getWidth(), parent.getY(), s);
    }

    @Override
    public void render(DrawContext graphics, int i, int j, float partialTicks) {
        if (!this.visible) {
            return;
        }
        MatrixStack matrixStack = graphics.getMatrices();
        MinecraftClient mc = MinecraftClient.getInstance();
        matrixStack.push();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)resource);
        int height = this.height - (this.active ? 0 : 2);
        boolean bl = this.hover = i >= this.getX() && j >= this.getY() && i < this.getX() + this.getWidth() && j < this.getY() + height;
        int k = this.active ? 0 : (this.hover ? 2 : 1);
        graphics.drawTexture(resource, this.getX(), this.getY(), 0, k * 20, this.getWidth() / 2, height);
        graphics.drawTexture(resource, this.getX() + this.getWidth() / 2, this.getY(), 200 - this.getWidth() / 2, k * 20, this.getWidth() / 2, height);
        TextRenderer fontrenderer = mc.textRenderer;
        if (this.rotated) {
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
        }
        if (this.active) {
            graphics.drawCenteredTextWithShadow(fontrenderer, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (height - 8) / 2, 0xFFFFA0);
        } else if (this.hover) {
            graphics.drawCenteredTextWithShadow(fontrenderer, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (height - 8) / 2, 0xFFFFA0);
        } else {
            graphics.drawCenteredTextWithShadow(fontrenderer, this.getMessage(), this.getX() + this.getWidth() / 2, this.getY() + (height - 8) / 2, 0xE0E0E0);
        }
        matrixStack.pop();
    }

    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        return false;
    }

    public boolean mouseReleased(double i, double j, int button) {
        return false;
    }

    public boolean mouseClicked(double i, double j, int button) {
        boolean bo;
        boolean bl = bo = !this.active && this.visible && this.hover;
        if (bo) {
            this.onClick(i, j);
        }
        return bo;
    }

    @Override
    public void onClick(double x, double y) {
        this.gui.buttonEvent(this);
    }
}

