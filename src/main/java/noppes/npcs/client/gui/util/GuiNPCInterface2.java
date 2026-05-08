/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.client.gui.util.GuiNpcMenu;
import noppes.npcs.entity.EntityNPCInterface;

public abstract class GuiNPCInterface2
extends GuiNPCInterface {
    private Identifier background = new Identifier("customnpcs:textures/gui/menubg.png");
    private GuiNpcMenu menu;

    public GuiNPCInterface2(EntityNPCInterface npc) {
        this(npc, -1);
    }

    public GuiNPCInterface2(EntityNPCInterface npc, int activeMenu) {
        super(npc);
        this.imageWidth = 420;
        this.imageHeight = 200;
        this.menu = new GuiNpcMenu(this, activeMenu, npc);
    }

    @Override
    public void init() {
        super.init();
        this.menu.initGui(this.guiLeft, this.guiTop, this.imageWidth);
    }

    @Override
    public boolean mouseClicked(double i, double j, int k) {
        if (!this.hasSubGui() && this.menu.mouseClicked(i, j, k)) {
            return true;
        }
        return super.mouseClicked(i, j, k);
    }

    @Override
    public abstract void save();

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.drawDefaultBackground) {
            this.renderBackground(graphics);
        }
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)this.background);
        graphics.drawTexture(this.background, this.guiLeft, this.guiTop, 0, 0, 200, 220);
        graphics.drawTexture(this.background, this.guiLeft + this.imageWidth - 230, this.guiTop, 26, 0, 230, 220);
        int x = mouseX;
        int y = mouseY;
        if (this.hasSubGui()) {
            y = 0;
            x = 0;
        }
        this.menu.drawElements(graphics, this.getFontRenderer(), x, y, this.client, partialTicks);
        boolean bo = this.drawDefaultBackground;
        this.drawDefaultBackground = false;
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.drawDefaultBackground = bo;
    }
}

