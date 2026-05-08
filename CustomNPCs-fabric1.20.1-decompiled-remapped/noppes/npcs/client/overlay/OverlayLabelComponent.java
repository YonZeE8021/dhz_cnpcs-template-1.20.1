/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 */
package noppes.npcs.client.overlay;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import noppes.npcs.api.overlay.ILabel;
import noppes.npcs.client.overlay.IOverlayRenderComponent;

public class OverlayLabelComponent
implements IOverlayRenderComponent {
    private final String text;
    private final int x;
    private final int y;
    private final int id;
    private final float scale;

    public OverlayLabelComponent(ILabel label) {
        String[] split;
        String text = label.getText();
        this.x = label.getPosX();
        this.y = label.getPosY();
        this.id = label.getId();
        this.scale = label.getScale();
        StringBuilder stringBuilder = new StringBuilder();
        String[] values = split = text.split("&t");
        for (String s : split) {
            if (I18n.hasTranslation((String)s)) {
                stringBuilder.append(I18n.translate((String)s, (Object[])new Object[0]));
                continue;
            }
            stringBuilder.append(s);
        }
        this.text = stringBuilder.toString();
    }

    @Override
    public void render(DrawContext graphics, int linkSide) {
        graphics.getMatrices().push();
        graphics.getMatrices().translate((double)this.x, (double)this.y, (double)this.id);
        graphics.getMatrices().scale(this.scale, this.scale, this.scale);
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
        this.renderString(graphics, this.text, this.x, this.y, linkSide, width, height);
        graphics.getMatrices().pop();
    }

    public void renderString(DrawContext graphics, String text, int x, int y, int linkSide, int width, int height) {
        int offsetX = width / 2 * ((linkSide - 1) % 3);
        int offsetY = height / 2 * ((linkSide - 1) / 3);
        graphics.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, text, x + offsetX, y + offsetY, 0xFFFFFF);
    }
}

