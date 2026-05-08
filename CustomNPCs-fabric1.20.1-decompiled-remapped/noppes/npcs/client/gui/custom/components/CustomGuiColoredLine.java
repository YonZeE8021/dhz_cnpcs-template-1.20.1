/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.text.Text
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 */
package noppes.npcs.client.gui.custom.components;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiColoredLineWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;

public class CustomGuiColoredLine
extends ClickableWidget
implements IGuiComponent {
    private GuiCustom parent;
    public CustomGuiColoredLineWrapper component;
    public int id;

    public CustomGuiColoredLine(GuiCustom parent, CustomGuiColoredLineWrapper component) {
        super(component.getPosX(), component.getPosY(), component.getXEnd() - component.getPosX(), component.getYEnd() - component.getPosY(), (Text)Text.empty());
        this.component = component;
        this.parent = parent;
        this.init();
    }

    @Override
    public void init() {
        this.id = this.component.getID();
        this.setX(this.component.getPosX());
        this.setY(this.component.getPosY());
        this.setWidth(this.component.getXEnd() - this.component.getPosX());
        this.height = this.component.getYEnd() - this.component.getPosY();
        this.active = true;
        this.visible = true;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public void onRender(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            int color = this.component.getColor();
            int r = color >> 24 & 0xFF;
            int g = color >> 16 & 0xFF;
            int b = color >> 8 & 0xFF;
            int a = color & 0xFF;
            double dx = this.component.getXEnd() - this.getX();
            double dy = this.component.getYEnd() - this.getY();
            double length = Math.sqrt(dx * dx + dy * dy);
            double nx = -dy / length * (double)this.component.getThickness() / 2.0;
            double ny = dx / length * (double)this.component.getThickness() / 2.0;
            VertexConsumer builder = graphics.getVertexConsumers().getBuffer(RenderLayer.getGui());
            builder.vertex((double)this.component.getXEnd() + nx, (double)this.component.getYEnd() + ny, (double)this.id).color(r, g, b, a).next();
            builder.vertex((double)this.component.getXEnd() - nx, (double)this.component.getYEnd() - ny, (double)this.id).color(r, g, b, a).next();
            builder.vertex((double)this.getX() - nx, (double)this.getY() - ny, (double)this.id).color(r, g, b, a).next();
            builder.vertex((double)this.getX() + nx, (double)this.getY() + ny, (double)this.id).color(r, g, b, a).next();
            graphics.draw();
        }
    }

    protected int getYImage(boolean p_getYImage_1_) {
        return 0;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
        return true;
    }

    public static CustomGuiColoredLine fromComponent(GuiCustom parent, CustomGuiColoredLineWrapper component) {
        CustomGuiColoredLine line = new CustomGuiColoredLine(parent, component);
        return line;
    }

    @Override
    public ICustomGuiComponent component() {
        return this.component;
    }

    protected void renderButton(DrawContext p_282139_, int p_268034_, int p_268009_, float p_268085_) {
    }

    public void appendClickableNarrations(NarrationMessageBuilder p_169152_) {
    }
}

