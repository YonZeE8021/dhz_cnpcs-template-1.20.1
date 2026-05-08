/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.util.math.MatrixStack
 */
package noppes.npcs.client.gui.custom.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.components.CustomGuiTexturedRect;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCustomGuiButton;

public class CustomGuiButton
extends ButtonWidget
implements IGuiComponent {
    protected GuiCustom parent;
    private CustomGuiTexturedRect background;
    public CustomGuiButtonWrapper component;
    protected boolean hovered;
    private int colour = 0xFFFFFF;
    protected ButtonWidget.PressAction onPress = button -> {
        if (!component.disablePackets) {
            Packets.sendServer(new SPacketCustomGuiButton(component.getUniqueID()));
        } else {
            component.onPress(parent.guiWrapper);
        }
    };
    public int id;

    public CustomGuiButton(GuiCustom parent, CustomGuiButtonWrapper component) {
        super(component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight(), (Text)Text.translatable((String)component.getLabel()), btn -> {}, null);
        this.parent = parent;
        this.component = component;
        this.init();
    }

    public void onPress() {
        this.onPress.onPress((ButtonWidget)this);
    }

    @Override
    public void init() {
        this.id = this.component.getID();
        this.setX(this.component.getPosX());
        this.setY(this.component.getPosY());
        this.setWidth(this.component.getWidth());
        this.height = this.component.getHeight();
        this.background = new CustomGuiTexturedRect(this.parent, this.component.getTextureRect());
        this.setMessage((Text)Text.translatable((String)this.component.getLabel()));
        this.active = this.component.getEnabled() && this.component.getVisible();
        this.visible = this.component.getVisible();
    }

    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        return false;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public void onRender(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        MatrixStack matrixStack = graphics.getMatrices();
        if (!this.visible) {
            return;
        }
        matrixStack.push();
        matrixStack.translate((float)this.getX(), (float)this.getY(), 0.0f);
        MinecraftClient mc = MinecraftClient.getInstance();
        this.hovered = this.isHovered(mouseX, mouseY);
        int i = 0;
        i = this.component.getTexture().equals("textures/gui/widgets.png") ? (!this.active ? 0 : (this.hovered ? 2 : 1)) : (this.hovered ? 1 : 0);
        this.background.textureY = this.component.getTextureY() + i * this.component.getTextureHoverOffset();
        this.background.onRender(graphics, mouseX - this.getX(), mouseY - this.getY(), partialTicks);
        this.renderLabel(graphics);
        if (!this.component.getDisplayItem().isEmpty()) {
            int xx = (int)(((float)this.width - 16.0f) / 2.0f);
            int yy = (int)(((float)this.height - 16.0f) / 2.0f) + 1;
            graphics.getMatrices().push();
            graphics.getMatrices().translate(0.0f, 0.0f, -90.0f);
            MatrixStack posestack = RenderSystem.getModelViewStack();
            posestack.push();
            posestack.translate((float)this.getX(), (float)this.getY(), -90.0f);
            RenderSystem.applyModelViewMatrix();
            graphics.drawItem(this.component.getDisplayItem().getMCItemStack(), xx, yy);
            graphics.drawItemInSlot(mc.textRenderer, this.component.getDisplayItem().getMCItemStack(), xx, yy);
            posestack.pop();
            graphics.getMatrices().pop();
            RenderSystem.applyModelViewMatrix();
        }
        if (this.hovered && this.component.hasHoverText()) {
            this.parent.hoverText = this.component.getHoverTextList();
        }
        matrixStack.pop();
    }

    public void renderLabel(DrawContext graphics) {
        if (!this.component.getLabel().isEmpty()) {
            int j = 0xE0E0E0;
            if (this.colour != 0) {
                j = this.colour;
            } else if (!this.active) {
                j = 0xA0A0A0;
            } else if (this.hovered) {
                j = 0xFFFFA0;
            }
            MinecraftClient mc = MinecraftClient.getInstance();
            graphics.getMatrices().translate(0.0f, 0.0f, (float)this.id);
            graphics.drawCenteredTextWithShadow(mc.textRenderer, this.getMessage(), this.width / 2, (this.height - 8) / 2, j);
        }
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
    }

    @Override
    public ICustomGuiComponent component() {
        return this.component;
    }

    public void setColour(int colour) {
        this.colour = colour;
    }

    protected int hoverState(boolean mouseOver) {
        int i = 0;
        if (mouseOver) {
            i = 1;
        }
        return i;
    }
}

