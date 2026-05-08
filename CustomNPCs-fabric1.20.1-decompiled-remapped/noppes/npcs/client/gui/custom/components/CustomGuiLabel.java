/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 */
package noppes.npcs.client.gui.custom.components;

import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiLabelWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;

public class CustomGuiLabel
extends ClickableWidget
implements IGuiComponent {
    private CustomGuiLabelWrapper component;
    private int id;
    private GuiCustom parent;

    public CustomGuiLabel(GuiCustom parent, CustomGuiLabelWrapper component) {
        super(component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight(), (Text)Text.translatable((String)component.getText()));
        this.component = component;
        this.parent = parent;
        this.init();
    }

    @Override
    public void init() {
        this.id = this.component.getID();
        this.setX(this.component.getPosX());
        this.setY(this.component.getPosY());
        this.setWidth(this.component.getWidth());
        this.height = this.component.getHeight();
        this.active = this.component.getEnabled() && this.component.getVisible();
        this.visible = this.component.getVisible();
        this.setMessage((Text)Text.translatable((String)this.component.getText()));
    }

    @Override
    public void onRender(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        boolean hovered;
        MatrixStack matrixStack = graphics.getMatrices();
        if (!this.active) {
            return;
        }
        matrixStack.push();
        matrixStack.translate(0.0f, 0.0f, (float)this.id);
        matrixStack.scale(this.component.getScale(), this.component.getScale(), 0.0f);
        boolean bl = hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        if (this.component.getCentered()) {
            graphics.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, this.getMessage(), (int)(((float)this.getX() + (float)(this.width - MinecraftClient.getInstance().textRenderer.getWidth((StringVisitable)this.getMessage())) / 2.0f) / this.component.getScale()), (int)((float)this.getY() / this.component.getScale()), this.component.getColor());
        } else {
            graphics.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, this.getMessage(), (int)((float)this.getX() / this.component.getScale()), (int)((float)this.getY() / this.component.getScale()), this.component.getColor());
        }
        if (hovered && this.component.hasHoverText()) {
            this.parent.hoverText = this.component.getHoverTextList();
        }
        matrixStack.pop();
    }

    @Override
    public int getID() {
        return this.id;
    }

    public void setText(String s) {
        this.setMessage((Text)Text.translatable((String)s));
    }

    @Override
    public ICustomGuiComponent component() {
        return this.component;
    }

    protected void renderButton(DrawContext p_282139_, int p_268034_, int p_268009_, float p_268085_) {
    }

    protected void appendClickableNarrations(NarrationMessageBuilder p_259858_) {
    }

    public void playDownSound(SoundManager p_93665_) {
    }

    public void setHeight(int height) {
        this.height = height;
    }
}

