/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 */
package noppes.npcs.client.gui.custom.components;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiEntityDisplayWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiItemRendererWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.components.CustomGuiEntityDisplay;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;

public class CustomGuiItemRenderer
extends ClickableWidget
implements IGuiComponent {
    private GuiCustom parent;
    public CustomGuiItemRendererWrapper component;
    private ItemStack stack;
    public int id;
    MinecraftClient minecraft;

    public CustomGuiItemRenderer(GuiCustom parent, CustomGuiItemRendererWrapper component) {
        super(component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight(), (Text)Text.empty());
        this.component = component;
        this.parent = parent;
        this.minecraft = MinecraftClient.getInstance();
        this.init();
    }

    @Override
    public void init() {
        this.id = this.component.getID();
        this.setX(this.component.getPosX());
        this.setY(this.component.getPosY());
        this.setWidth(this.component.getWidth());
        this.height = this.component.getHeight();
        this.stack = this.component.hasStack() ? this.component.getStack().getMCItemStack() : ItemStack.EMPTY;
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
            boolean hovered;
            if (!NoppesUtilServer.IsItemStackNull(this.stack)) {
                double scale = this.component.getScale();
                graphics.getMatrices().push();
                graphics.getMatrices().scale((float)scale, (float)scale, 1.0f);
                graphics.getMatrices().translate(0.0f, 0.0f, (float)this.id);
                graphics.drawItem(this.stack, (int)((double)this.getX() / scale), (int)((double)this.getY() / scale));
                graphics.drawItemInSlot(this.minecraft.textRenderer, this.stack, (int)((double)this.getX() / scale), (int)((double)this.getY() / scale));
                graphics.getMatrices().pop();
            }
            boolean bl = hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
            if (hovered && this.component.hasHoverText()) {
                this.parent.hoverText = this.component.getHoverTextList();
            }
        }
    }

    protected int getYImage(boolean p_getYImage_1_) {
        return 0;
    }

    protected void renderButton(DrawContext p_282139_, int p_268034_, int p_268009_, float p_268085_) {
    }

    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
        return true;
    }

    protected void appendClickableNarrations(NarrationMessageBuilder p_259858_) {
    }

    public static CustomGuiEntityDisplay fromComponent(GuiCustom parent, CustomGuiEntityDisplayWrapper component) {
        CustomGuiEntityDisplay btn = new CustomGuiEntityDisplay(parent, component);
        return btn;
    }

    @Override
    public ICustomGuiComponent component() {
        return this.component;
    }
}

