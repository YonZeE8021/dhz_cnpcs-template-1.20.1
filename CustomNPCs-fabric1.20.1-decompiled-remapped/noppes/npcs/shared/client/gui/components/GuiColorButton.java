/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package noppes.npcs.shared.client.gui.components;

import net.minecraft.client.gui.DrawContext;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.components.CustomGuiButton;

public class GuiColorButton
extends CustomGuiButton {
    public int color;

    public GuiColorButton(GuiCustom parent, CustomGuiButtonWrapper component, int color) {
        super(parent, component);
        this.color = color;
    }

    @Override
    public void onRender(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        graphics.fill(this.getX(), this.getY(), this.getX() + 50, this.getY() + 20, -16777216 + this.color);
    }
}

