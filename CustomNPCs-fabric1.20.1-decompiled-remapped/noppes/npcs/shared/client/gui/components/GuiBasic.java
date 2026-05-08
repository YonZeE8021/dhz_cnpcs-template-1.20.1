/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.shared.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import java.net.URI;
import java.util.ArrayList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiMenuSideButton;
import noppes.npcs.shared.client.gui.components.GuiMenuTopButton;
import noppes.npcs.shared.client.gui.components.GuiSliderNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.components.GuiWrapper;
import noppes.npcs.shared.client.gui.listeners.IGui;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiBasic
extends Screen
implements IGuiInterface {
    public ClientPlayerEntity player;
    public boolean drawDefaultBackground = true;
    public String title;
    public Identifier background = null;
    public boolean closeOnEsc = true;
    public int guiLeft;
    public int guiTop;
    public int imageWidth;
    public int imageHeight;
    public float bgScale = 1.0f;
    public GuiWrapper wrapper = new GuiWrapper(this);

    public GuiBasic() {
        super((Text)Text.empty());
        this.player = MinecraftClient.getInstance().player;
        this.client = MinecraftClient.getInstance();
        this.title = "";
        this.imageWidth = 200;
        this.imageHeight = 222;
        this.client = MinecraftClient.getInstance();
        this.textRenderer = this.client.textRenderer;
    }

    public void setBackground(String texture) {
        this.background = new Identifier("customnpcs", "textures/gui/" + texture);
    }

    public Identifier getResource(String texture) {
        return new Identifier("customnpcs", "textures/gui/" + texture);
    }

    public void init() {
        super.init();
        this.setFocused(null);
        this.guiLeft = (this.width - this.imageWidth) / 2;
        this.guiTop = (this.height - this.imageHeight) / 2;
        this.drawables.clear();
        this.children().clear();
        this.wrapper.init(this.client, this.width, this.height);
    }

    @Override
    public GuiWrapper getWrapper() {
        return this.wrapper;
    }

    @Override
    public void initGui() {
        this.init();
    }

    public void tick() {
        this.wrapper.tick();
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrolled) {
        if (this.wrapper.mouseScrolled(mouseX, mouseY, scrolled)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrolled);
    }

    public boolean mouseClicked(double i, double j, int k) {
        if (this.wrapper.mouseClicked(i, j, k)) {
            return true;
        }
        return super.mouseClicked(i, j, k);
    }

    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (this.wrapper.mouseDragged(x, y, button, dx, dy)) {
            return true;
        }
        return super.mouseDragged(x, y, button, dx, dy);
    }

    public boolean mouseReleased(double x, double y, int button) {
        if (this.wrapper.mouseReleased(x, y, button)) {
            return true;
        }
        return super.mouseReleased(x, y, button);
    }

    public void setFocused(Element gui) {
        if (this.wrapper.subgui != null) {
            this.wrapper.subgui.setFocused(gui);
        } else {
            if (gui != null && !this.children().contains(gui)) {
                return;
            }
            this.wrapper.changeFocus(this.getFocused(), gui);
            super.setFocused(gui);
        }
    }

    public Element getFocused() {
        if (this.wrapper.subgui != null) {
            return this.wrapper.subgui.getFocused();
        }
        return super.getFocused();
    }

    @Override
    public void elementClicked() {
        if (this.wrapper.subgui != null && this.wrapper.subgui instanceof GuiBasic) {
            ((GuiBasic)this.wrapper.subgui).elementClicked();
        }
    }

    @Override
    public void subGuiClosed(Screen subgui) {
    }

    public boolean charTyped(char c, int i) {
        if (this.wrapper.charTyped(c, i)) {
            return true;
        }
        return super.charTyped(c, i);
    }

    public boolean keyPressed(int key, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (this.wrapper.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_)) {
            return true;
        }
        return super.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_);
    }

    public boolean isInventoryKey(int i) {
        return this.client.options.inventoryKey.boundKey.getCode() == i;
    }

    public boolean shouldCloseOnEsc() {
        return this.closeOnEsc;
    }

    public void close() {
        this.close();
    }

    public void close() {
        this.wrapper.close();
    }

    public void addButton(GuiButtonNop button) {
        this.wrapper.npcbuttons.put(button.id, button);
        super.addDrawableChild((Element)button);
    }

    public void addTopButton(GuiMenuTopButton button) {
        this.wrapper.topbuttons.put(button.id, button);
        super.addDrawableChild((Element)button);
    }

    public void addSideButton(GuiMenuSideButton button) {
        this.wrapper.sidebuttons.put(button.id, button);
        super.addDrawableChild((Element)button);
    }

    public GuiButtonNop getButton(int i) {
        return this.wrapper.npcbuttons.get(i);
    }

    public GuiMenuSideButton getSideButton(int i) {
        return this.wrapper.sidebuttons.get(i);
    }

    public GuiMenuTopButton getTopButton(int i) {
        return this.wrapper.topbuttons.get(i);
    }

    public void addTextField(GuiTextFieldNop tf) {
        this.wrapper.textfields.put(tf.id, tf);
    }

    public GuiTextFieldNop getTextField(int i) {
        return this.wrapper.textfields.get(i);
    }

    public void add(IGui gui) {
        this.wrapper.components.add(gui);
    }

    public IGui get(int id) {
        for (IGui comp : this.wrapper.components) {
            if (comp.getID() != id) continue;
            return comp;
        }
        return null;
    }

    public void addLabel(GuiLabel label) {
        this.wrapper.labels.put(label.id, label);
    }

    public GuiLabel getLabel(int i) {
        return this.wrapper.labels.get(i);
    }

    public void addSlider(GuiSliderNop slider) {
        this.wrapper.sliders.put(slider.id, slider);
        this.addDrawableChild((Element)slider);
    }

    public GuiSliderNop getSlider(int i) {
        return this.wrapper.sliders.get(i);
    }

    public void addScroll(GuiCustomScrollNop scroll) {
        scroll.init(this.client, scroll.width, scroll.height);
        this.wrapper.scrolls.put(scroll.id, scroll);
    }

    public GuiCustomScrollNop getScroll(int id) {
        return this.wrapper.scrolls.get(id);
    }

    @Override
    public void save() {
    }

    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        MatrixStack matrixStack = graphics.getMatrices();
        this.wrapper.mouseX = mouseX;
        this.wrapper.mouseY = mouseY;
        int x = mouseX;
        int y = mouseY;
        if (this.wrapper.subgui != null) {
            y = 0;
            x = 0;
        }
        if (this.drawDefaultBackground && this.wrapper.subgui == null) {
            this.renderBackground(graphics);
        }
        if (this.background != null) {
            matrixStack.push();
            matrixStack.translate((float)this.guiLeft, (float)this.guiTop, 0.0f);
            matrixStack.scale(this.bgScale, this.bgScale, this.bgScale);
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            RenderSystem.setShaderTexture((int)0, (Identifier)this.background);
            if (this.imageWidth > 256) {
                graphics.drawTexture(this.background, 0, 0, 0, 0, 250, this.imageHeight);
                graphics.drawTexture(this.background, 250, 0, 256 - (this.imageWidth - 250), 0, this.imageWidth - 250, this.imageHeight);
            } else {
                graphics.drawTexture(this.background, 0, 0, 0, 0, this.imageWidth, this.imageHeight);
            }
            matrixStack.pop();
        }
        graphics.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        for (GuiLabel label : new ArrayList<GuiLabel>(this.wrapper.labels.values())) {
            label.render(graphics, mouseX, mouseY, partialTicks);
        }
        for (GuiTextFieldNop tf : new ArrayList<GuiTextFieldNop>(this.wrapper.textfields.values())) {
            tf.renderButton(graphics, x, y, partialTicks);
        }
        for (GuiCustomScrollNop scroll : new ArrayList<GuiCustomScrollNop>(this.wrapper.scrolls.values())) {
            scroll.render(graphics, x, y, partialTicks);
        }
        for (IGui comp : new ArrayList<IGui>(this.wrapper.components)) {
            comp.render(graphics, x, y);
        }
        for (Screen gui : new ArrayList<Screen>(this.wrapper.extra.values())) {
            gui.render(graphics, x, y, partialTicks);
        }
        super.render(graphics, x, y, partialTicks);
        if (this.wrapper.subgui != null) {
            matrixStack.translate(0.0f, 0.0f, 60.0f);
            this.wrapper.subgui.render(graphics, mouseX, mouseY, partialTicks);
            matrixStack.translate(0.0f, 0.0f, -60.0f);
        }
    }

    public TextRenderer getFontRenderer() {
        return this.textRenderer;
    }

    public boolean shouldPause() {
        return false;
    }

    public void doubleClicked() {
    }

    public void setScreen(Screen gui) {
        this.client.setScreen(gui);
    }

    public void setSubGui(Screen gui) {
        this.wrapper.setSubgui(gui);
        this.init();
    }

    @Override
    public boolean hasSubGui() {
        return this.wrapper.subgui != null;
    }

    @Override
    public Screen getSubGui() {
        return this.wrapper.getSubGui();
    }

    public void drawNpc(DrawContext graphics, LivingEntity entity, int x, int y, float zoomed, int rotation) {
        this.wrapper.drawNpc(graphics, entity, x, y, zoomed, rotation, this.guiLeft, this.guiTop);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public void openLink(String link) {
        try {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
            oclass.getMethod("browse", URI.class).invoke(object, new URI(link));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    @Override
    public Screen getParent() {
        return this.wrapper.getParent();
    }
}

