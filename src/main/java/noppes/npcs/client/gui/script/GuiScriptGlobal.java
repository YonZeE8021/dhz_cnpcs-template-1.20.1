/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.script;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.client.gui.script.GuiScriptForge;
import noppes.npcs.client.gui.script.GuiScriptPlayers;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

public class GuiScriptGlobal
extends GuiNPCInterface {
    private final Identifier resource = new Identifier("customnpcs", "textures/gui/smallbg.png");

    public GuiScriptGlobal() {
        this.imageWidth = 176;
        this.imageHeight = 222;
        this.drawDefaultBackground = false;
        this.title = "";
    }

    @Override
    public void init() {
        super.init();
        this.addButton(new GuiButtonNop(this, 0, this.guiLeft + 38, this.guiTop + 20, 100, 20, "Players"));
        this.addButton(new GuiButtonNop(this, 1, this.guiLeft + 38, this.guiTop + 50, 100, 20, "Forge (BROKEN)"));
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        MatrixStack matrixStack = graphics.getMatrices();
        this.renderBackground(graphics);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)this.resource);
        graphics.drawTexture(this.resource, this.guiLeft, this.guiTop, 0, 0, this.imageWidth, this.imageHeight);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        if (guibutton.id == 0) {
            this.setScreen(new GuiScriptPlayers());
        }
        if (guibutton.id == 1) {
            this.setScreen(new GuiScriptForge());
        }
    }

    @Override
    public void save() {
    }
}

