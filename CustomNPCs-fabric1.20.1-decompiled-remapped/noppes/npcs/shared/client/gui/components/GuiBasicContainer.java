/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.item.ItemStack
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.client.network.ClientPlayerEntity
 */
package noppes.npcs.shared.client.gui.components;

import java.util.ArrayList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import noppes.npcs.client.gui.util.GuiTooltipUtils;
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

public class GuiBasicContainer<T extends ScreenHandler>
extends HandledScreen<T>
implements IGuiInterface {
    public boolean drawDefaultBackground = true;
    public int guiLeft;
    public int guiTop;
    public ClientPlayerEntity player;
    public GuiWrapper wrapper = new GuiWrapper((Screen)this);
    public String title;
    public boolean closeOnEsc = true;
    public int mouseX;
    public int mouseY;

    public GuiBasicContainer(T cont, PlayerInventory inv, Text titleIn) {
        super(cont, inv, titleIn);
        this.player = MinecraftClient.getInstance().player;
        this.title = "";
        this.client = MinecraftClient.getInstance();
        this.textRenderer = this.client.textRenderer;
    }

    public boolean shouldCloseOnEsc() {
        return this.closeOnEsc;
    }

    public void init() {
        super.init();
        this.setFocused(null);
        this.guiLeft = (this.width - this.backgroundWidth) / 2;
        this.guiTop = (this.height - this.backgroundHeight) / 2;
        this.drawables.clear();
        this.children().clear();
        this.wrapper.init(this.client, this.width, this.height);
    }

    public Identifier getResource(String texture) {
        return new Identifier("customnpcs", "textures/gui/" + texture);
    }

    public void handledScreenTick() {
        this.wrapper.tick();
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
        if (this.getFocused() != null && this.isDragging() && button == 0) {
            this.getFocused().mouseDragged(x, y, button, dx, dy);
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

    @Override
    public void elementClicked() {
        if (this.wrapper.subgui != null) {
            ((IGuiInterface)this.wrapper.subgui).elementClicked();
        }
    }

    @Override
    public void subGuiClosed(Screen subgui) {
    }

    @Override
    public GuiWrapper getWrapper() {
        return this.wrapper;
    }

    @Override
    public void initGui() {
        this.init();
    }

    public boolean isInventoryKey(int i) {
        return this.client.options.inventoryKey.boundKey.getCode() == i;
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

    public void buttonEvent(ButtonWidget guibutton) {
    }

    public void close() {
        this.save();
        this.player.closeHandledScreen();
        this.setScreen(null);
        this.client.mouse.lockCursor();
    }

    public void close() {
        this.close();
        GuiTextFieldNop.unfocus();
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

    protected void drawForeground(DrawContext p_281635_, int p_282681_, int p_283686_) {
    }

    protected void drawBackground(DrawContext graphics, float partialTicks, int x, int y) {
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
    }

    @Override
    public void save() {
    }

    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        this.wrapper.mouseX = mouseX;
        this.wrapper.mouseY = mouseY;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        ArrayList slots = new ArrayList(this.handler.slots);
        if (this.wrapper.subgui != null) {
            this.handler.slots.clear();
        }
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredTextWithShadow(this.getFontRenderer(), I18n.translate((String)this.title, (Object[])new Object[0]), this.width / 2, this.guiTop - 8, 0xFFFFFF);
        for (GuiLabel label : new ArrayList<GuiLabel>(this.wrapper.labels.values())) {
            label.render(graphics, mouseX, mouseY, partialTicks);
        }
        for (GuiTextFieldNop tf : new ArrayList<GuiTextFieldNop>(this.wrapper.textfields.values())) {
            tf.renderButton(graphics, mouseX, mouseY, partialTicks);
        }
        for (GuiCustomScrollNop scroll : new ArrayList<GuiCustomScrollNop>(this.wrapper.scrolls.values())) {
            scroll.render(graphics, mouseX, mouseY, partialTicks);
        }
        for (IGui comp : new ArrayList<IGui>(this.wrapper.components)) {
            comp.render(graphics, mouseX, mouseY);
            for (Screen gui : new ArrayList<Screen>(this.wrapper.extra.values())) {
                gui.render(graphics, mouseX, mouseY, partialTicks);
            }
        }
        if (this.wrapper.subgui != null) {
            this.handler.slots.addAll(slots);
            graphics.getMatrices().push();
            graphics.getMatrices().translate(0.0f, 0.0f, 100.0f);
            this.wrapper.subgui.render(graphics, mouseX, mouseY, partialTicks);
            graphics.getMatrices().pop();
        } else {
            this.drawMouseoverTooltip(graphics, mouseX, mouseY);
        }
    }

    public void drawMouseoverTooltip(DrawContext p_283594_, int p_282171_, int p_281909_) {
        if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
            ItemStack itemstack = this.focusedSlot.getStack();
            GuiTooltipUtils.renderTooltip(p_283594_, this.textRenderer, this.getTooltipFromItem(itemstack), itemstack.getTooltipData(), itemstack, p_282171_, p_281909_);
        }
    }

    public void renderBackground(DrawContext graphics) {
        if (this.drawDefaultBackground && this.wrapper.subgui == null) {
            super.renderBackground(graphics);
        }
    }

    public TextRenderer getFontRenderer() {
        return this.textRenderer;
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

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public Screen getParent() {
        return this.wrapper.getParent();
    }
}

