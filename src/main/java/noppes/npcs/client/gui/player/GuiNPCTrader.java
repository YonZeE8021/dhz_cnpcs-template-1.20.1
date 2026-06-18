/*
 * Decompiled with CFR 0.152.
 */
package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.ClientTraderLimitCache;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.containers.ContainerNPCTrader;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTraderLimitRefresh;
import noppes.npcs.roles.RoleTrader;
import noppes.npcs.roles.TraderSlotLimit;
import noppes.npcs.util.TraderLimitHelper;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

public class GuiNPCTrader
extends GuiContainerNPCInterface<ContainerNPCTrader> {
    private final Identifier resource = new Identifier("customnpcs", "textures/gui/trader.png");
    private final Identifier slot = new Identifier("customnpcs", "textures/gui/slot.png");
    private RoleTrader role;
    private ContainerNPCTrader container;
    private int refreshTick;
    private static final int STATUS_TEXT_Y = 130;

    public GuiNPCTrader(ContainerNPCTrader container, PlayerInventory inv, Text titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn);
        this.container = container;
        this.role = (RoleTrader)this.npc.role;
        this.backgroundHeight = 224;
        this.backgroundWidth = 223;
        this.title = "role.trader";
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        if (++this.refreshTick >= 20) {
            this.refreshTick = 0;
            Packets.sendServer(new SPacketTraderLimitRefresh());
        }
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
        List<Text> tooltip = null;
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
            boolean limitBlocked = ClientTraderLimitCache.tradeBlocked[slot];
            if (this.isPointWithinBounds(i + 43, j + 1, 16, 16, x, y)) {
                if (limitBlocked) {
                    graphics.getMatrices().translate(0.0f, 0.0f, 300.0f);
                    graphics.fillGradient(i + 43, j + 1, i + 59, j + 17, 0x70888888, 0x70888888);
                    String blockedMsg = this.getBlockedMessage(slot);
                    int blockedColor = this.getBlockedColor(slot);
                    graphics.drawTextWithShadow(this.textRenderer, blockedMsg, (this.backgroundWidth - this.textRenderer.getWidth(blockedMsg)) / 2, STATUS_TEXT_Y, blockedColor);
                    graphics.getMatrices().translate(0.0f, 0.0f, -300.0f);
                } else if (!this.container.canBuy(item, item2, (PlayerEntity)this.player)) {
                    graphics.getMatrices().translate(0.0f, 0.0f, 300.0f);
                    if (!item.isEmpty() && !NoppesUtilPlayer.compareItems((PlayerEntity)this.player, item, this.role.ignoreDamage, this.role.ignoreNBT)) {
                        graphics.fillGradient(i + 17, j, i + 35, j + 18, 0x70771010, 0x70771010);
                    }
                    if (!item2.isEmpty() && !NoppesUtilPlayer.compareItems((PlayerEntity)this.player, item2, this.role.ignoreDamage, this.role.ignoreNBT)) {
                        graphics.fillGradient(i - 1, j, i + 17, j + 18, 0x70771010, 0x70771010);
                    }
                    title = I18n.translate("trader.insufficient", new Object[0]);
                    graphics.drawTextWithShadow(this.textRenderer, title, (this.backgroundWidth - this.textRenderer.getWidth(title)) / 2, STATUS_TEXT_Y, 0xDD0000);
                    graphics.getMatrices().translate(0.0f, 0.0f, -300.0f);
                } else {
                    title = I18n.translate("trader.sufficient", new Object[0]);
                    graphics.drawTextWithShadow(this.textRenderer, title, (this.backgroundWidth - this.textRenderer.getWidth(title)) / 2, STATUS_TEXT_Y, 56576);
                }
                tooltip = this.buildLimitTooltip(slot);
            }
            if (this.isPointWithinBounds(i, j, 16, 16, x, y) && !NoppesUtilServer.IsItemStackNull(item2)) {
                graphics.drawItemTooltip(this.textRenderer, item2, x - this.guiLeft, y - this.guiTop);
            }
            if (!this.isPointWithinBounds(i + 18, j, 16, 16, x, y)) continue;
            graphics.drawItemTooltip(this.textRenderer, item, x - this.guiLeft, y - this.guiTop);
        }
        if (tooltip != null && !tooltip.isEmpty()) {
            graphics.drawTooltip(this.textRenderer, tooltip, x - this.guiLeft, y - this.guiTop - tooltip.size() * 10 - 8);
        }
    }

    private String getBlockedMessage(int slot) {
        int reason = ClientTraderLimitCache.blockReason[slot];
        if (reason == TraderLimitHelper.REASON_VARIABLE) {
            return I18n.translate("trader.out_of_stock", new Object[0]);
        }
        if (reason == TraderLimitHelper.REASON_STOCK_FULL) {
            return I18n.translate("trader.stock_full", new Object[0]);
        }
        return I18n.translate("trader.limit.reached", new Object[0]);
    }

    private int getBlockedColor(int slot) {
        int reason = ClientTraderLimitCache.blockReason[slot];
        if (reason == TraderLimitHelper.REASON_VARIABLE) {
            return 0xDD4444;
        }
        if (reason == TraderLimitHelper.REASON_STOCK_FULL) {
            return 0xDD6644;
        }
        return 0xDD8800;
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
    }

    @Override
    public void save() {
    }

    private List<Text> buildLimitTooltip(int slot) {
        ArrayList<Text> lines = new ArrayList<>();
        TraderSlotLimit limit = this.role.getSlotLimit(slot);
        int playerRemaining = ClientTraderLimitCache.playerRemaining[slot];
        int playerMax = ClientTraderLimitCache.playerMax[slot];
        boolean hasLimitSection = limit.hasPlayerLimit() && playerRemaining >= 0 && playerMax >= 0;
        boolean hasStockSection = !ClientTraderLimitCache.slotVariables[slot].isEmpty();
        if (hasLimitSection) {
            lines.add(Text.literal(I18n.translate("trader.tooltip.limit.header", new Object[0])).formatted(Formatting.GOLD));
            lines.add(Text.literal("  " + I18n.translate("trader.limit.remaining", playerRemaining, playerMax)).formatted(Formatting.WHITE));
            String limitRefresh = this.formatRefreshTime(ClientTraderLimitCache.getEffectiveLimitRefresh(slot), ClientTraderLimitCache.limitRefreshType[slot]);
            if (limitRefresh != null) {
                lines.add(Text.literal("  " + I18n.translate("trader.limit.refresh", limitRefresh)).formatted(Formatting.GRAY));
            }
        }
        if (hasStockSection) {
            if (hasLimitSection) {
                lines.add(Text.empty());
            }
            lines.add(Text.literal(I18n.translate("trader.tooltip.stock.header", new Object[0])).formatted(Formatting.GOLD));
            for (ClientTraderLimitCache.VariableTip tip : ClientTraderLimitCache.slotVariables[slot]) {
                String stockText = tip.maxValue >= 0 ? I18n.translate("trader.var.stock.capped", tip.name, tip.value, tip.maxValue) : I18n.translate("trader.var.stock", tip.name, tip.value);
                MutableText stockLine = Text.literal("  " + stockText);
                if (tip.value <= 0) {
                    stockLine.formatted(Formatting.RED);
                } else if (tip.maxValue >= 0 && tip.value >= tip.maxValue) {
                    stockLine.formatted(Formatting.GRAY);
                } else {
                    stockLine.formatted(Formatting.YELLOW);
                }
                lines.add(stockLine);
                String restock = this.formatRefreshTime(ClientTraderLimitCache.getEffectiveRefreshRemaining(tip.restockRemaining, tip.restockType), tip.restockType);
                if (restock != null) {
                    lines.add(Text.literal("    " + I18n.translate("trader.var.restock", restock)).formatted(Formatting.GRAY));
                }
            }
        }
        return lines;
    }

    private String formatRefreshTime(long remaining, int type) {
        if (remaining < 0L || type == TraderLimitHelper.REFRESH_NONE) {
            return null;
        }
        if (type == TraderLimitHelper.REFRESH_REAL_MS) {
            long totalSec = (remaining + 999L) / 1000L;
            long hours = totalSec / 3600L;
            long minutes = totalSec % 3600L / 60L;
            long seconds = totalSec % 60L;
            if (hours > 0L) {
                return I18n.translate("trader.time.hms", hours, minutes, seconds);
            }
            if (minutes > 0L) {
                return I18n.translate("trader.time.ms", minutes, seconds);
            }
            return I18n.translate("trader.time.s", seconds);
        }
        long days = remaining / 24000L;
        long hours = remaining % 24000L / 1000L;
        long minutes = remaining % 1000L / 20L / 60L;
        if (days > 0L) {
            return I18n.translate("trader.time.game.dhm", days, hours, minutes);
        }
        if (hours > 0L) {
            return I18n.translate("trader.time.game.hm", hours, minutes);
        }
        if (minutes > 0L) {
            return I18n.translate("trader.time.game.m", minutes);
        }
        return I18n.translate("trader.time.game.soon", new Object[0]);
    }
}
