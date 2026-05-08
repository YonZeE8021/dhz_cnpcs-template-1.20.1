/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  net.minecraft.client.util.math.MatrixStack
 */
package noppes.npcs.client.gui.custom.components;

import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCustomGuiFocusUpdate;
import noppes.npcs.packets.server.SPacketCustomGuiTextUpdate;

public class CustomGuiTextField
extends TextFieldWidget
implements IGuiComponent {
    private static CustomGuiTextField focused = null;
    private GuiCustom parent;
    private CustomGuiTextFieldWrapper component;
    public int id;

    public CustomGuiTextField(GuiCustom parent, CustomGuiTextFieldWrapper component) {
        super(MinecraftClient.getInstance().textRenderer, component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight(), (Text)Text.translatable((String)component.getText()));
        this.setMaxLength(500);
        this.component = component;
        this.parent = parent;
        this.init();
    }

    @Override
    public void init() {
        this.id = this.component.getID();
        this.setX(this.component.getPosX());
        this.setY(this.component.getPosY());
        this.width = this.component.getWidth();
        this.height = this.component.getHeight();
        this.setEditableColor(this.component.getColor());
        if (this.component.getText() != null) {
            this.setText(this.component.getText());
        }
        this.active = this.component.getEnabled() && this.component.getVisible();
        this.visible = this.component.getVisible();
        this.setFocused(this.component.getFocused());
    }

    @Override
    public ICustomGuiComponent component() {
        return this.component;
    }

    @Override
    public void onRender(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        MatrixStack matrixStack = graphics.getMatrices();
        matrixStack.push();
        matrixStack.translate(0.0f, 0.0f, (float)this.id);
        boolean hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        this.renderButton(graphics, mouseX, mouseY, partialTicks);
        if (hovered && this.component.hasHoverText()) {
            this.parent.hoverText = this.component.getHoverTextList();
        }
        matrixStack.pop();
    }

    @Override
    public int getID() {
        return this.id;
    }

    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        String text = this.getText();
        boolean bo = super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
        this.component.setText(this.getText());
        if (!this.getText().equals(this.component.getText())) {
            this.setText(this.component.getText());
        }
        if (!text.equals(this.getText())) {
            if (!this.component.disablePackets) {
                Packets.sendServer(new SPacketCustomGuiTextUpdate(this.component.getUniqueID(), this.getText()));
            } else {
                this.component.onChange(null);
            }
        }
        return bo;
    }

    public boolean mouseClicked(double i, double j, int k) {
        boolean flag = i >= (double)this.getX() && i < (double)(this.getX() + this.width) && j >= (double)this.getY() && j < (double)(this.getY() + this.height);
        this.setFocused(flag);
        return super.mouseClicked(i, j, k);
    }

    private boolean isValidChar(char c) {
        if (this.component.getCharacterType() == 1) {
            return Character.isDigit(c);
        }
        if (this.component.getCharacterType() == 2) {
            return Character.isDigit(c) || Character.toLowerCase(c) >= 'a' && Character.toLowerCase(c) <= 'f';
        }
        if (this.component.getCharacterType() == 3) {
            return Character.isDigit(c) || c == '.' && !this.getText().contains(".") || c == '-' && this.getCursor() == 0;
        }
        return true;
    }

    public boolean charTyped(char c, int i) {
        if (!this.isValidChar(c)) {
            return false;
        }
        String text = this.getText();
        boolean bo = super.charTyped(c, i);
        if (!text.equals(this.getText())) {
            this.component.setText(this.getText());
            if (!this.component.disablePackets) {
                Packets.sendServer(new SPacketCustomGuiTextUpdate(this.component.getUniqueID(), this.getText()));
            } else {
                this.component.onChange(null);
            }
        }
        return bo;
    }

    public void setFocused(boolean bo) {
        if (this.isFocused() == bo) {
            return;
        }
        super.setFocused(bo);
        if (this.component.getFocused() != bo) {
            if (!(this.component.getText().isEmpty() || this.component.getCharacterType() != 1 && this.component.getCharacterType() != 2)) {
                this.component.setInteger(this.component.getInteger());
                this.setText(this.component.getText());
                if (!this.component.disablePackets) {
                    Packets.sendServer(new SPacketCustomGuiTextUpdate(this.component.getUniqueID(), this.component.getText()));
                }
                this.component.onChange(null);
            }
            this.component.setFocused(bo);
            if (!this.component.disablePackets) {
                Packets.sendServer(new SPacketCustomGuiFocusUpdate(this.component.getUniqueID(), bo));
            } else {
                this.component.onFocusLost(null);
            }
        }
        if (this.isFocused() && focused != this) {
            if (focused != null) {
                focused.setFocused(false);
            }
            focused = this;
        }
        if (!this.isFocused() && focused != this) {
            focused = null;
        }
    }

    public void playDownSound(SoundManager p_93665_) {
    }
}

