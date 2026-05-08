/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 */
package noppes.npcs.client.overlay;

import net.minecraft.item.ItemStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import noppes.npcs.api.overlay.IRenderItemOverlay;
import noppes.npcs.client.overlay.IOverlayRenderComponent;

public class OverlayRenderItemComponent
implements IOverlayRenderComponent {
    private final int x;
    private final int y;
    private final int id;
    private final ItemStack item;

    public OverlayRenderItemComponent(IRenderItemOverlay item) {
        this.x = item.getPosX();
        this.y = item.getPosY();
        this.id = item.getId();
        this.item = item.getItem().getMCItemStack();
    }

    @Override
    public void render(DrawContext graphics, int linkSide) {
        graphics.getMatrices().push();
        graphics.getMatrices().translate((double)this.x, (double)this.y, (double)this.id);
        graphics.getMatrices().scale(1.2f, 1.2f, 1.0f);
        int width = (int)((float)MinecraftClient.getInstance().getWindow().getScaledWidth() / 1.2f);
        int height = (int)((float)MinecraftClient.getInstance().getWindow().getScaledHeight() / 1.2f);
        this.renderItemOverlay(graphics, linkSide, this.item, this.x, this.y, width, height);
        graphics.getMatrices().pop();
    }

    public void renderItemOverlay(DrawContext graphics, int linkSide, ItemStack item, int x, int y, int width, int height) {
        int offsetX = width / 2 * ((linkSide - 1) % 3);
        int offsetY = height / 2 * ((linkSide - 1) / 3);
        graphics.drawItem(item, x + offsetX, y + offsetY);
        graphics.drawItemInSlot(MinecraftClient.getInstance().textRenderer, item, x + offsetX, y + offsetY);
    }
}

