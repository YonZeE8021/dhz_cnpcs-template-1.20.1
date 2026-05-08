/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.custom.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiSliderWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.components.CustomGuiTextField;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCustomGuiSliderUpdate;

public class CustomGuiSlider
extends ClickableWidget
implements IGuiComponent {
    private GuiCustom parent;
    private CustomGuiSliderWrapper component;
    private CustomGuiTextFieldWrapper tfcomponent;
    private CustomGuiTextField textfield;
    private float sliderValue;
    private float startValue;
    private long lastClickedTime = 0L;
    private float total;
    public int id;
    private boolean disablePackets = false;
    public static final int UNSET_FG_COLOR = -1;
    protected int packedFGColor = -1;

    public int getFGColor() {
        if (this.packedFGColor != -1) {
            return this.packedFGColor;
        }
        return this.active ? 0xFFFFFF : 0xA0A0A0;
    }

    public void setFGColor(int color) {
        this.packedFGColor = color;
    }

    public void clearFGColor() {
        this.packedFGColor = -1;
    }

    public CustomGuiSlider(GuiCustom parent, CustomGuiSliderWrapper component) {
        super(component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight(), (Text)Text.translatable((String)component.getFormat(), (Object[])new Object[]{Float.valueOf(component.getValue())}));
        this.component = component;
        this.parent = parent;
        this.tfcomponent = new CustomGuiTextFieldWrapper(this.id, this.getX(), this.getY(), this.width, this.height).setCharacterType(3);
        this.init();
    }

    @Override
    public void init() {
        this.id = this.component.getID();
        this.setX(this.component.getPosX());
        this.setY(this.component.getPosY());
        this.setWidth(this.component.getWidth());
        this.height = this.component.getHeight();
        this.total = this.component.getMax() - this.component.getMin();
        this.startValue = this.sliderValue = (this.component.getValue() - this.component.getMin()) / this.total;
        this.tfcomponent.setID(this.id);
        this.tfcomponent.setPos(this.component.getPosX(), this.component.getPosY());
        this.tfcomponent.setSize(this.component.getWidth(), this.component.getHeight());
        this.active = this.component.getEnabled() && this.component.getVisible();
        this.visible = this.component.getVisible();
        this.setMessage((Text)Text.translatable((String)this.component.getFormat(), (Object[])new Object[]{Float.valueOf(this.component.getValue())}));
    }

    public CustomGuiSlider disablePackets() {
        this.disablePackets = true;
        return this;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public void onRender(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        boolean hovered;
        MatrixStack matrixStack = graphics.getMatrices();
        if (!this.visible) {
            return;
        }
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (this.textfield != null) {
            this.textfield.onRender(graphics, mouseX, mouseY, partialTicks);
            if (!this.textfield.isFocused()) {
                this.closeTextfield();
            }
        }
        boolean bl = hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        if (hovered && this.component.hasHoverText()) {
            this.parent.hoverText = this.component.getHoverTextList();
        }
    }

    private void setSliderValue(float value) {
        if ((value = MathHelper.clamp((float)value, (float)0.0f, (float)1.0f)) == this.sliderValue) {
            return;
        }
        this.sliderValue = value;
        this.component.setValue(value * this.total + this.component.getMin());
        this.setMessage((Text)Text.translatable((String)this.component.getFormat(), (Object[])new Object[]{Float.valueOf(this.component.getValue())}));
        if (!this.disablePackets) {
            Packets.sendServer(new SPacketCustomGuiSliderUpdate(this.component.getUniqueID(), this.component.getValue()));
        } else {
            this.component.onChange(null);
        }
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.textfield != null && (keyCode == 257 || keyCode == 335)) {
            this.closeTextfield();
        }
        if (this.textfield != null) {
            return this.textfield.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void closeTextfield() {
        this.setSliderValue((this.tfcomponent.getFloat() + this.component.getMin()) / this.total);
        this.textfield = null;
    }

    public boolean charTyped(char c, int i) {
        if (this.textfield != null) {
            return this.textfield.charTyped(c, i);
        }
        return super.charTyped(c, i);
    }

    protected void renderButton(DrawContext p_93676_, int p_93677_, int p_93678_, float p_93679_) {
        MinecraftClient minecraft = MinecraftClient.getInstance();
        TextRenderer font = minecraft.textRenderer;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture((int)0, (Identifier)WIDGETS_TEXTURE);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)this.alpha);
        int i = 0;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        p_93676_.drawTexture(WIDGETS_TEXTURE, this.getX(), this.getY(), 0, 46 + i * 20, this.width / 2, this.height);
        p_93676_.drawTexture(WIDGETS_TEXTURE, this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        this.renderBg(p_93676_, minecraft, p_93677_, p_93678_);
        int j = this.getFGColor();
        p_93676_.drawCenteredTextWithShadow(font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, j | MathHelper.ceil((float)(this.alpha * 255.0f)) << 24);
    }

    public void onClick(double x, double y) {
        if (!this.visible || !this.active) {
            return;
        }
        long time = System.currentTimeMillis();
        if (time - this.lastClickedTime < 500L) {
            this.tfcomponent.setText("" + this.component.getValue());
            this.textfield = new CustomGuiTextField(this.parent, this.tfcomponent);
            this.textfield.setFocused(true);
        } else if (this.textfield != null) {
            this.textfield.mouseClicked(x, y, 0);
            return;
        }
        this.lastClickedTime = time;
        this.setSliderValue((float)(x - (double)(this.getX() + 4)) / (float)(this.width - 8));
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (this.textfield != null) {
            return this.textfield.mouseClicked(mouseX, mouseY, mouseButton);
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
        this.setSliderValue((float)(mouseX - (double)(this.getX() + 4)) / (float)(this.width - 8));
        return true;
    }

    protected void appendClickableNarrations(NarrationMessageBuilder p_259858_) {
    }

    public void tick() {
        if (this.textfield != null) {
            this.textfield.tick();
        }
    }

    public void onRelease(double x, double y) {
        if (this.sliderValue == this.startValue) {
            return;
        }
        super.playDownSound(MinecraftClient.getInstance().getSoundManager());
        this.startValue = this.sliderValue;
    }

    public void renderBg(DrawContext graphics, MinecraftClient mc, int p_146119_2_, int p_146119_3_) {
        if (!this.visible) {
            return;
        }
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)WIDGETS_TEXTURE);
        int lvt_4_1_ = (this.hovered ? 2 : 1) * 20;
        graphics.getMatrices().push();
        graphics.getMatrices().translate(0.0f, 0.0f, 10.0f);
        graphics.drawTexture(WIDGETS_TEXTURE, this.getX() + (int)((double)this.sliderValue * (double)(this.width - 8)), this.getY(), 0, 46 + lvt_4_1_, 4, this.height / 2);
        graphics.drawTexture(WIDGETS_TEXTURE, this.getX() + (int)((double)this.sliderValue * (double)(this.width - 8)), this.getY() + this.height / 2, 0, 46 + lvt_4_1_ + 20 - this.height / 2, 4, this.height / 2);
        graphics.drawTexture(WIDGETS_TEXTURE, this.getX() + (int)((double)this.sliderValue * (double)(this.width - 8)) + 4, this.getY(), 196, 46 + lvt_4_1_, 4, this.height / 2);
        graphics.drawTexture(WIDGETS_TEXTURE, this.getX() + (int)((double)this.sliderValue * (double)(this.width - 8)) + 4, this.getY() + this.height / 2, 196, 46 + lvt_4_1_ + 20 - this.height / 2, 4, this.height / 2);
        graphics.getMatrices().pop();
    }

    @Override
    public ICustomGuiComponent component() {
        return this.component;
    }
}

