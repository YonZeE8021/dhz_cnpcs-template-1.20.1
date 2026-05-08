/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 */
package noppes.npcs.client.gui.player.tabs;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemConvertible;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.StringVisitable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.gui.player.GuiQuestLog;
import noppes.npcs.client.gui.player.tabs.AbstractTab;

public class InventoryTabQuests
extends AbstractTab {
    public Text displayString;

    public InventoryTabQuests() {
        super(2, 0, 0, new ItemStack((ItemConvertible)Items.BOOK));
        this.displayString = Text.translatable((String)"quest.quest").append(" (").append(ClientProxy.QuestLog.boundKey.getLocalizedText()).append(")");
    }

    @Override
    public void onTabClicked() {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.setScreen((Screen)new GuiQuestLog((PlayerEntity)mc.player));
    }

    @Override
    public boolean shouldAddToList() {
        return true;
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        boolean hovered;
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (!this.visible) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        boolean bl = hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        if (hovered) {
            graphics.getMatrices().translate((float)mouseX, (float)(this.getY() + 2), 0.0f);
            this.drawHoveringText(graphics, Arrays.asList(this.displayString), -mc.textRenderer.getWidth((StringVisitable)this.displayString), 0, mc.textRenderer);
            graphics.getMatrices().translate((float)(-mouseX), (float)(-(this.getY() + 2)), 0.0f);
        }
    }

    protected void appendClickableNarrations(NarrationMessageBuilder p_259858_) {
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

