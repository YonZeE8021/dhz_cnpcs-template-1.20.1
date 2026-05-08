/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.block.Blocks
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.client.render.GameRenderer
 *  net.minecraft.client.render.item.ItemRenderer
 */
package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.StringVisitable;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiMenuTopButton;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiMenuTopIconButton
extends GuiMenuTopButton {
    private static final Identifier resource = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    protected static ItemRenderer itemRenderer;
    private ItemStack item;

    public GuiMenuTopIconButton(IGuiInterface gui, int i, int x, int y, String s, ItemStack item) {
        super(gui, i, x, y, s);
        this.width = 28;
        this.height = 28;
        this.item = item;
        itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    public GuiMenuTopIconButton(IGuiInterface gui, int i, GuiButtonNop parent, String s, ItemStack item) {
        super(gui, i, parent, s);
        this.width = 28;
        this.height = 28;
        this.item = item;
        itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        if (this.item.isEmpty()) {
            this.item = new ItemStack((ItemConvertible)Blocks.DIRT);
        }
        this.hover = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.getWidth() && mouseY < this.getY() + this.height;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (this.hover) {
            this.drawHoveringText(graphics, Arrays.asList(this.getMessage()), mouseX, mouseY, MinecraftClient.getInstance().textRenderer);
        }
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)resource);
        graphics.getMatrices().push();
        graphics.drawTexture(resource, this.getX(), this.getY() + (this.active ? 2 : 0), 0, this.active ? 32 : 0, 28, 28);
        graphics.getMatrices().translate(0.0f, 0.0f, 100.0f);
        graphics.drawItem(this.item, this.getX() + 6, this.getY() + 10);
        graphics.drawItemInSlot(mc.textRenderer, this.item, this.getX() + 6, this.getY() + 10);
        graphics.getMatrices().pop();
    }

    protected void drawHoveringText(DrawContext graphics, List<Text> list, int x, int y, TextRenderer font) {
        if (list.isEmpty()) {
            return;
        }
        RenderSystem.disableDepthTest();
        int k = 0;
        for (Text o : list) {
            int l = font.getWidth((StringVisitable)o);
            if (l <= k) continue;
            k = l;
        }
        int j2 = x;
        int k2 = y;
        int i1 = 8;
        if (list.size() > 1) {
            i1 += 2 + (list.size() - 1) * 10;
        }
        graphics.getMatrices().push();
        graphics.getMatrices().translate(0.0f, 0.0f, 300.0f);
        int j1 = -267386864;
        graphics.fillGradient(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
        graphics.fillGradient(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
        graphics.fillGradient(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
        graphics.fillGradient(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
        graphics.fillGradient(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
        int k1 = 0x505000FF;
        int l1 = (k1 & 0xFEFEFE) >> 1 | k1 & 0xFF000000;
        graphics.fillGradient(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
        graphics.fillGradient(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
        graphics.fillGradient(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
        graphics.fillGradient(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);
        for (int i2 = 0; i2 < list.size(); ++i2) {
            Text s1 = list.get(i2);
            graphics.drawTextWithShadow(font, s1, j2, k2, -1);
            if (i2 == 0) {
                k2 += 2;
            }
            k2 += 10;
        }
        graphics.getMatrices().pop();
        RenderSystem.enableDepthTest();
    }
}

