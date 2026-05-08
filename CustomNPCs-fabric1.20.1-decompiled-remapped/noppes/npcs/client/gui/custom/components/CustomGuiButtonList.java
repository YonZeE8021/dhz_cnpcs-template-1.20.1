/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.text.Text
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 *  net.minecraft.client.util.math.MatrixStack
 */
package noppes.npcs.client.gui.custom.components;

import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonListWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTexturedRectWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.components.CustomGuiButton;
import noppes.npcs.client.gui.custom.components.CustomGuiTexturedRect;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCustomGuiButtonList;

public class CustomGuiButtonList
extends CustomGuiButton {
    private CustomGuiTexturedRect left;
    private CustomGuiTexturedRect right;
    private CustomGuiTexturedRectWrapper leftWrapper;
    private CustomGuiTexturedRectWrapper rightWrapper;
    private boolean isRight = false;

    public CustomGuiButtonList(GuiCustom parent, CustomGuiButtonListWrapper component) {
        super(parent, component);
        this.onPress = button -> {
            CustomGuiButtonList list = (CustomGuiButtonList)button;
            component.setSelected(component.getSelected() + (list.isRight ? 1 : -1));
            list.setMessage((Text)Text.translatable((String)component.getLabel()));
            this.sendPacket();
            if (!component.disablePackets) {
                Packets.sendServer(new SPacketCustomGuiButtonList(component.getUniqueID(), list.isRight));
            } else {
                component.onPress(parent.guiWrapper);
            }
        };
    }

    private void sendPacket() {
        Packets.sendServer(new SPacketCustomGuiButtonList(this.component.getUniqueID(), this.isRight));
    }

    public CustomGuiButtonList(GuiCustom parent, CustomGuiButtonListWrapper component, ButtonWidget.PressAction onPress) {
        super(parent, component);
        this.component = component;
        this.onPress = onPress;
        this.init();
    }

    @Override
    public void init() {
        super.init();
        this.leftWrapper = ((CustomGuiButtonListWrapper)this.component).getLeftTexture();
        this.rightWrapper = ((CustomGuiButtonListWrapper)this.component).getRightTexture();
        this.left = new CustomGuiTexturedRect(this.parent, this.leftWrapper);
        this.right = new CustomGuiTexturedRect(this.parent, this.rightWrapper);
    }

    protected int getYImage(boolean p_93668_) {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (p_93668_) {
            i = 2;
        }
        return i;
    }

    @Override
    public void onRender(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        MatrixStack matrixStack = graphics.getMatrices();
        super.onRender(graphics, mouseX, mouseY, partialTicks);
        matrixStack.push();
        matrixStack.translate((float)this.getX(), (float)this.getY(), 10.0f);
        this.isRight = mouseX >= this.getX() + this.width / 2;
        this.left.textureY = this.leftWrapper.getTextureY() + this.getYImage(this.hovered && !this.isRight) * this.leftWrapper.getHeight();
        this.left.onRender(graphics, mouseX - this.getX(), mouseY - this.getY(), partialTicks);
        this.right.textureY = this.rightWrapper.getTextureY() + this.getYImage(this.hovered && this.isRight) * this.rightWrapper.getHeight();
        this.right.onRender(graphics, mouseX - this.getX(), mouseY - this.getY(), partialTicks);
        this.renderLabel(graphics);
        matrixStack.pop();
    }
}

