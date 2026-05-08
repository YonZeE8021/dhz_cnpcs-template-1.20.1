/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.player.GuiRecipes;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.containers.ContainerCarpentryBench;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

public class GuiNpcCarpentryBench
extends GuiContainerNPCInterface<ContainerCarpentryBench> {
    private final Identifier resource = new Identifier("customnpcs", "textures/gui/carpentry.png");
    private ContainerCarpentryBench container;
    private GuiButtonNop button;

    public GuiNpcCarpentryBench(ContainerCarpentryBench container, PlayerInventory inv, Text titleIn) {
        super(null, container, inv, titleIn);
        this.container = container;
        this.title = "";
        this.backgroundHeight = 180;
    }

    @Override
    public void init() {
        super.init();
        this.button = new GuiButtonNop(this, 0, this.guiLeft + 158, this.guiTop + 4, 12, 20, "...");
        this.addButton(this.button);
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        this.setScreen(new GuiRecipes());
    }

    @Override
    protected void drawBackground(DrawContext graphics, float partialTicks, int x, int y) {
        this.button.active = RecipeController.instance != null && !RecipeController.instance.anvilRecipes.isEmpty();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)this.resource);
        int l = (this.width - this.backgroundWidth) / 2;
        int i1 = (this.height - this.backgroundHeight) / 2;
        String title = I18n.translate((String)"block.customnpcs.npccarpentybench", (Object[])new Object[0]);
        graphics.drawTexture(this.resource, l, i1, 0, 0, this.backgroundWidth, this.backgroundHeight);
        graphics.drawText(this.textRenderer, title, this.guiLeft + 4, this.guiTop + 4, CustomNpcResourceListener.DefaultTextColor, false);
        graphics.drawText(this.textRenderer, I18n.translate((String)"container.inventory", (Object[])new Object[0]), this.guiLeft + 4, this.guiTop + 87, CustomNpcResourceListener.DefaultTextColor, false);
    }

    @Override
    public void save() {
    }
}

