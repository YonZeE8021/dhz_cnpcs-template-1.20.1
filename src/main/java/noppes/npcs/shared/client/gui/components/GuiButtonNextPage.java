/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiButtonNextPage
extends GuiButtonNop {
    private final boolean isLeftButton;
    private static final String __OBFID = "CL_00000745";
    private static final Identifier field_110405_a = new Identifier("textures/gui/book.png");

    public GuiButtonNextPage(IGuiInterface gui, int id, int x, int y, boolean par4, ButtonWidget.PressAction press) {
        super(gui, id, x, y, 23, 13, "", press);
        this.isLeftButton = par4;
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            boolean flag = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            RenderSystem.setShaderTexture((int)0, (Identifier)field_110405_a);
            int k = 0;
            int l = 192;
            if (flag) {
                k += 23;
            }
            if (!this.isLeftButton) {
                l += 13;
            }
            graphics.drawTexture(field_110405_a, this.getX(), this.getY(), k, l, 23, 13);
        }
    }
}

