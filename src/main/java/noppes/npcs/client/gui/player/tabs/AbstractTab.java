/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.PressableWidget
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.render.item.ItemRenderer
 */
package noppes.npcs.client.gui.player.tabs;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.item.ItemRenderer;

public abstract class AbstractTab
extends PressableWidget {
    public int id;
    Identifier texture = new Identifier("customnpcs", "textures/gui/tabs.png");
    ItemStack renderStack;

    public AbstractTab(int id, int posX, int posY, ItemStack renderStack) {
        super(posX, posY, 28, 32, (Text)Text.translatable((String)""));
        this.renderStack = renderStack;
        this.id = id;
    }

    public AbstractTab init(Screen s) {
        int guiLeft = (s.width - 176) / 2;
        int guiTop = (s.height - 166) / 2;
        this.setX(guiLeft + this.id * 28);
        this.setY(guiTop - 28);
        return this;
    }

    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            MinecraftClient mc = MinecraftClient.getInstance();
            int yTexPos = this.active ? 3 : 32;
            int ySize = this.active ? 25 : 32;
            int xOffset = this.id == 2 ? 0 : 1;
            int yPos = this.getY() + (this.active ? 3 : 0);
            ItemRenderer itemRender = mc.getItemRenderer();
            RenderSystem.setShaderTexture((int)0, (Identifier)this.texture);
            graphics.drawTexture(this.texture, this.getX(), yPos, xOffset * 28, yTexPos, 28, ySize);
            graphics.getMatrices().push();
            graphics.getMatrices().translate(0.0f, 0.0f, 30.0f);
            graphics.drawItem(this.renderStack, this.getX() + 6, this.getY() + 8);
            graphics.drawItemInSlot(mc.textRenderer, this.renderStack, this.getX() + 6, this.getY() + 8, null);
            graphics.getMatrices().pop();
        }
    }

    public void onClick(double mouseX, double mouseY) {
        this.onTabClicked();
    }

    public void onPress() {
    }

    public abstract void onTabClicked();

    public abstract boolean shouldAddToList();
}

