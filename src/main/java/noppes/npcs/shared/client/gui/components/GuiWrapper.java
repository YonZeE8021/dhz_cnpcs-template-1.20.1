/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.gui.screen.Screen
 */
package noppes.npcs.shared.client.gui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.gui.custom.components.CustomGuiEntityDisplay;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiMenuSideButton;
import noppes.npcs.shared.client.gui.components.GuiMenuTopButton;
import noppes.npcs.shared.client.gui.components.GuiSliderNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGui;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiWrapper {
    public Map<Integer, GuiButtonNop> npcbuttons = new ConcurrentHashMap<Integer, GuiButtonNop>();
    public Map<Integer, GuiMenuTopButton> topbuttons = new ConcurrentHashMap<Integer, GuiMenuTopButton>();
    public Map<Integer, GuiMenuSideButton> sidebuttons = new ConcurrentHashMap<Integer, GuiMenuSideButton>();
    public Map<Integer, GuiTextFieldNop> textfields = new ConcurrentHashMap<Integer, GuiTextFieldNop>();
    public Map<Integer, GuiLabel> labels = new ConcurrentHashMap<Integer, GuiLabel>();
    public Map<Integer, GuiCustomScrollNop> scrolls = new ConcurrentHashMap<Integer, GuiCustomScrollNop>();
    public Map<Integer, GuiSliderNop> sliders = new ConcurrentHashMap<Integer, GuiSliderNop>();
    public Map<Integer, Screen> extra = new ConcurrentHashMap<Integer, Screen>();
    public List<IGui> components = new ArrayList<IGui>();
    public Screen parent;
    public Screen gui;
    public Screen subgui;
    public int mouseX;
    public int mouseY;

    public GuiWrapper(Screen gui) {
        this.gui = gui;
    }

    public void init(MinecraftClient mc, int width, int height) {
        GuiTextFieldNop.unfocus();
        if (this.subgui != null) {
            this.subgui.init(mc, width, height);
        }
        this.npcbuttons = new ConcurrentHashMap<Integer, GuiButtonNop>();
        this.topbuttons = new ConcurrentHashMap<Integer, GuiMenuTopButton>();
        this.sidebuttons = new ConcurrentHashMap<Integer, GuiMenuSideButton>();
        this.textfields = new ConcurrentHashMap<Integer, GuiTextFieldNop>();
        this.labels = new ConcurrentHashMap<Integer, GuiLabel>();
        this.scrolls = new ConcurrentHashMap<Integer, GuiCustomScrollNop>();
        this.sliders = new ConcurrentHashMap<Integer, GuiSliderNop>();
        this.extra = new ConcurrentHashMap<Integer, Screen>();
        this.components = new ArrayList<IGui>();
    }

    public void tick() {
        if (this.subgui != null) {
            this.subgui.tick();
        } else {
            for (GuiTextFieldNop tf : new ArrayList<GuiTextFieldNop>(this.textfields.values())) {
                if (!tf.enabled) continue;
                tf.tick();
            }
            for (IGui comp : new ArrayList<IGui>(this.components)) {
                comp.tick();
            }
        }
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrolled) {
        if (this.subgui != null) {
            this.subgui.mouseScrolled(mouseX, mouseY, scrolled);
            return true;
        }
        for (IGui comp : new ArrayList<IGui>(this.components)) {
            if (!(comp instanceof Element) || !comp.isNarratable() || !((Element)comp).mouseScrolled(mouseX, mouseY, scrolled)) continue;
            return true;
        }
        for (GuiCustomScrollNop scroll : this.scrolls.values()) {
            if (!scroll.visible || !scroll.mouseScrolled(mouseX, mouseY, scrolled)) continue;
            return true;
        }
        return false;
    }

    public boolean mouseClicked(double i, double j, int k) {
        if (this.subgui != null) {
            this.subgui.mouseClicked(i, j, k);
            return true;
        }
        boolean clickedAnyTF = false;
        for (GuiTextFieldNop tf : new ArrayList<GuiTextFieldNop>(this.textfields.values())) {
            if (!tf.mouseClicked(i, j, k)) continue;
            clickedAnyTF = true;
        }
        if (clickedAnyTF) {
            return true;
        }
        for (IGui comp : new ArrayList<IGui>(this.components)) {
            if (!(comp instanceof Element) || !((Element)comp).mouseClicked(i, j, k)) continue;
            return true;
        }
        if (k == 0) {
            for (GuiCustomScrollNop scroll : new ArrayList<GuiCustomScrollNop>(this.scrolls.values())) {
                if (!scroll.mouseClicked(i, j, k)) continue;
                return true;
            }
        }
        return false;
    }

    public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (this.subgui != null) {
            this.subgui.mouseDragged(x, y, button, dx, dy);
            return true;
        }
        return false;
    }

    public boolean mouseReleased(double x, double y, int button) {
        if (this.subgui != null) {
            this.subgui.mouseReleased(x, y, button);
            return true;
        }
        return false;
    }

    public boolean charTyped(char c, int i) {
        if (this.subgui != null) {
            this.subgui.charTyped(c, i);
            return true;
        }
        for (GuiTextFieldNop tf : new ArrayList<GuiTextFieldNop>(this.textfields.values())) {
            tf.charTyped(c, i);
        }
        for (GuiCustomScrollNop scroll : new ArrayList<GuiCustomScrollNop>(this.scrolls.values())) {
            scroll.charTyped(c, i);
        }
        for (IGui comp : new ArrayList<IGui>(this.components)) {
            if (!(comp instanceof Element)) continue;
            ((Element)comp).charTyped(c, i);
        }
        return true;
    }

    public boolean keyPressed(int key, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (this.subgui != null) {
            this.subgui.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_);
            return true;
        }
        boolean active = GuiTextFieldNop.isAnyActive();
        for (IGui gui : this.components) {
            if (!gui.isNarratable()) continue;
            active = true;
            break;
        }
        if (this.gui.shouldCloseOnEsc() && (key == 256 || !active && MinecraftClient.getInstance().options.inventoryKey.boundKey.getCode() == key)) {
            this.gui.close();
            return true;
        }
        for (GuiTextFieldNop tf : this.textfields.values()) {
            tf.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_);
        }
        for (GuiCustomScrollNop scroll : new ArrayList<GuiCustomScrollNop>(this.scrolls.values())) {
            scroll.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_);
        }
        for (IGui comp : new ArrayList<IGui>(this.components)) {
            if (!(comp instanceof Element) || !((Element)comp).keyPressed(key, p_keyPressed_2_, p_keyPressed_3_)) continue;
            return true;
        }
        return active;
    }

    public void drawNpc(DrawContext graphics, LivingEntity entity, int x, int y, float zoomed, int rotation, int guiLeft, int guiTop) {
        CustomGuiEntityDisplay.drawEntity(graphics, (Entity)entity, x, y, zoomed, rotation, this.mouseX, this.mouseY, guiLeft, guiTop);
    }

    public void changeFocus(Element old, Element gui) {
        if (old instanceof GuiSliderNop && gui != old) {
            ((GuiSliderNop)old).onRelease(0.0, 0.0);
        }
    }

    public void setSubgui(Screen subgui) {
        this.gui.setFocused(null);
        this.subgui = subgui;
        subgui.init(MinecraftClient.getInstance(), this.gui.width, this.gui.height);
        if (subgui instanceof IGuiInterface) {
            ((IGuiInterface)subgui).getWrapper().parent = this.gui;
        }
    }

    public Screen getSubGui() {
        if (this.subgui instanceof IGuiInterface && ((IGuiInterface)this.subgui).hasSubGui()) {
            return ((IGuiInterface)this.subgui).getSubGui();
        }
        return this.subgui;
    }

    public Screen getParent() {
        if (this.parent != null) {
            return ((IGuiInterface)this.parent).getParent();
        }
        return this.gui;
    }

    public void close() {
        GuiTextFieldNop.unfocus();
        ((IGuiInterface)this.gui).save();
        if (this.parent != null) {
            if (this.parent instanceof IGuiInterface) {
                this.parent.setFocused(null);
                ((IGuiInterface)this.parent).getWrapper().subgui = null;
                ((IGuiInterface)this.parent).subGuiClosed(this.gui);
                ((IGuiInterface)this.parent).initGui();
            } else {
                this.gui.close();
            }
        } else {
            MinecraftClient minecraft = MinecraftClient.getInstance();
            minecraft.setScreen(this.gui);
            minecraft.mouse.lockCursor();
        }
    }
}

