/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.containers.ContainerNPCTrader;
import noppes.npcs.roles.RoleTrader;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

public class GuiNPCTrader
extends GuiContainerNPCInterface<ContainerNPCTrader> {
    private final Identifier resource = new Identifier("customnpcs", "textures/gui/trader.png");
    private final Identifier slot = new Identifier("customnpcs", "textures/gui/slot.png");
    private RoleTrader role;
    private ContainerNPCTrader container;

    public GuiNPCTrader(ContainerNPCTrader container, PlayerInventory inv, Text titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn);
        this.container = container;
        this.role = (RoleTrader)this.npc.role;
        this.backgroundHeight = 224;
        this.backgroundWidth = 223;
        this.title = "role.trader";
    }

    @Override
    protected void drawBackground(DrawContext graphics, float partialTicks, int x, int y) {
        super.renderBackground(graphics);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)this.resource);
        graphics.drawTexture(this.resource, this.guiLeft, this.guiTop, 0, 0, this.backgroundWidth, this.backgroundHeight);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)this.slot);
        for (int slot = 0; slot < 18; ++slot) {
            int i = this.guiLeft + slot % 3 * 72 + 10;
            int j = this.guiTop + slot / 3 * 21 + 6;
            ItemStack item = (ItemStack)this.role.inventoryCurrency.items.get(slot);
            ItemStack item2 = (ItemStack)this.role.inventoryCurrency.items.get(slot + 18);
            if (NoppesUtilServer.IsItemStackNull(item)) {
                item = item2;
                item2 = ItemStack.EMPTY;
            }
            if (NoppesUtilPlayer.compareItems(item, item2, false, false)) {
                item = item.copy();
                item.setCount(item.getCount() + item2.getCount());
                item2 = ItemStack.EMPTY;
            }
            ItemStack sold = (ItemStack)this.role.inventorySold.items.get(slot);
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            RenderSystem.setShaderTexture((int)0, (Identifier)this.slot);
            graphics.drawTexture(this.slot, i + 42, j, 0, 0, 18, 18);
            if (NoppesUtilServer.IsItemStackNull(item) || NoppesUtilServer.IsItemStackNull(sold)) continue;
            if (!NoppesUtilServer.IsItemStackNull(item2)) {
                graphics.drawItem(item2, i, j + 1);
                graphics.drawItemInSlot(this.textRenderer, item2, i, j + 1);
            }
            graphics.drawItem(item, i + 18, j + 1);
            graphics.drawItemInSlot(this.textRenderer, item, i + 18, j + 1);
            graphics.drawTextWithShadow(this.textRenderer, "=", i + 36, j + 5, CustomNpcResourceListener.DefaultTextColor);
        }
    }

    @Override
    protected void drawForeground(DrawContext graphics, int x, int y) {
        for (int slot = 0; slot < 18; ++slot) {
            ItemStack sold;
            int i = slot % 3 * 72 + 10;
            int j = slot / 3 * 21 + 6;
            ItemStack item = (ItemStack)this.role.inventoryCurrency.items.get(slot);
            ItemStack item2 = (ItemStack)this.role.inventoryCurrency.items.get(slot + 18);
            if (NoppesUtilServer.IsItemStackNull(item)) {
                item = item2;
                item2 = ItemStack.EMPTY;
            }
            if (NoppesUtilPlayer.compareItems(item, item2, this.role.ignoreDamage, this.role.ignoreNBT)) {
                item = item.copy();
                item.setCount(item.getCount() + item2.getCount());
                item2 = ItemStack.EMPTY;
            }
            if (NoppesUtilServer.IsItemStackNull(sold = (ItemStack)this.role.inventorySold.items.get(slot))) continue;
            if (this.isPointWithinBounds(i + 43, j + 1, 16, 16, x, y)) {
                if (!this.container.canBuy(item, item2, (PlayerEntity)this.player)) {
                    graphics.getMatrices().translate(0.0f, 0.0f, 300.0f);
                    if (!item.isEmpty() && !NoppesUtilPlayer.compareItems((PlayerEntity)this.player, item, this.role.ignoreDamage, this.role.ignoreNBT)) {
                        graphics.fillGradient(i + 17, j, i + 35, j + 18, 0x70771010, 0x70771010);
                    }
                    if (!item2.isEmpty() && !NoppesUtilPlayer.compareItems((PlayerEntity)this.player, item2, this.role.ignoreDamage, this.role.ignoreNBT)) {
                        graphics.fillGradient(i - 1, j, i + 17, j + 18, 0x70771010, 0x70771010);
                    }
                    title = I18n.translate((String)"trader.insufficient", (Object[])new Object[0]);
                    graphics.drawTextWithShadow(this.textRenderer, title, (this.backgroundWidth - this.textRenderer.getWidth(title)) / 2, 131, 0xDD0000);
                    graphics.getMatrices().translate(0.0f, 0.0f, -300.0f);
                } else {
                    title = I18n.translate((String)"trader.sufficient", (Object[])new Object[0]);
                    graphics.drawTextWithShadow(this.textRenderer, title, (this.backgroundWidth - this.textRenderer.getWidth(title)) / 2, 131, 56576);
                }
            }
            if (this.isPointWithinBounds(i, j, 16, 16, x, y) && !NoppesUtilServer.IsItemStackNull(item2)) {
                graphics.drawItemTooltip(this.textRenderer, item2, x - this.guiLeft, y - this.guiTop);
            }
            if (!this.isPointWithinBounds(i + 18, j, 16, 16, x, y)) continue;
            graphics.drawItemTooltip(this.textRenderer, item, x - this.guiLeft, y - this.guiTop);
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
    }

    @Override
    public void save() {
    }
}

