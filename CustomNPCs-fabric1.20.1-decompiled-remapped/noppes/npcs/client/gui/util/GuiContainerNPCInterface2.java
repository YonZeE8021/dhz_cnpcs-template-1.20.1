/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.client.gui.util.GuiNpcMenu;
import noppes.npcs.entity.EntityNPCInterface;

public abstract class GuiContainerNPCInterface2<T extends ScreenHandler>
extends GuiContainerNPCInterface<T> {
    private Identifier background = new Identifier("customnpcs", "textures/gui/menubg.png");
    private final Identifier defaultBackground = new Identifier("customnpcs", "textures/gui/menubg.png");
    private GuiNpcMenu menu;
    public int menuYOffset = 0;

    public GuiContainerNPCInterface2(EntityNPCInterface npc, T cont, PlayerInventory inv, Text titleIn) {
        this(npc, cont, inv, titleIn, -1);
    }

    public GuiContainerNPCInterface2(EntityNPCInterface npc, T cont, PlayerInventory inv, Text titleIn, int activeMenu) {
        super(npc, cont, inv, titleIn);
        this.backgroundWidth = 420;
        this.menu = new GuiNpcMenu(this, activeMenu, npc);
        this.title = "";
    }

    public void setBackground(String texture) {
        this.background = new Identifier("customnpcs", "textures/gui/" + texture);
    }

    @Override
    public Identifier getResource(String texture) {
        return new Identifier("customnpcs", "textures/gui/" + texture);
    }

    @Override
    public void init() {
        super.init();
        this.menu.initGui(this.guiLeft, this.guiTop + this.menuYOffset, this.backgroundWidth);
    }

    @Override
    public boolean mouseClicked(double i, double j, int k) {
        if (!this.hasSubGui()) {
            this.menu.mouseClicked(i, j, k);
        }
        return super.mouseClicked(i, j, k);
    }

    public void delete() {
        this.npc.delete();
        this.setScreen(null);
        this.client.mouse.lockCursor();
    }

    @Override
    protected void drawBackground(DrawContext graphics, float partialTicks, int x, int y) {
        MatrixStack matrixStack = graphics.getMatrices();
        this.renderBackground(graphics);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)this.background);
        graphics.drawTexture(this.background, this.guiLeft, this.guiTop, 0, 0, 256, 256);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)this.defaultBackground);
        graphics.drawTexture(this.defaultBackground, this.guiLeft + this.backgroundWidth - 200, this.guiTop, 26, 0, 200, 220);
        this.menu.drawElements(graphics, this.textRenderer, x, y, this.client, partialTicks);
        super.drawBackground(graphics, partialTicks, x, y);
    }
}

