/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.text.Text
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.ButtonWidget
 *  net.minecraft.client.gui.widget.ButtonWidget$PressAction
 */
package noppes.npcs.shared.client.gui.components;

import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiButtonNop
extends ButtonWidget {
    public boolean shown = true;
    public IGuiInterface gui;
    protected String[] display;
    private int displayValue = 0;
    public int id;
    protected static final ButtonWidget.PressAction clicked = button -> {
        GuiButtonNop b = (GuiButtonNop)button;
        b.gui.buttonEvent(b);
    };

    public GuiButtonNop(IGuiInterface gui, int i, int j, int k, String s) {
        super(j, k, 200, 20, (Text)Text.translatable((String)s), clicked, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.id = i;
        this.gui = gui;
    }

    public GuiButtonNop(IGuiInterface gui, int i, int j, int k, String[] display, int val) {
        this(gui, i, j, k, display[val]);
        this.display = display;
        this.displayValue = val;
    }

    public GuiButtonNop(IGuiInterface gui, int i, int j, int k, int l, int m, String string) {
        super(j, k, l, m, (Text)Text.translatable((String)string), clicked, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.id = i;
        this.gui = gui;
    }

    public GuiButtonNop(IGuiInterface gui, int i, int j, int k, int l, int m, String string, ButtonWidget.PressAction clicked) {
        super(j, k, l, m, (Text)Text.translatable((String)string), clicked, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.id = i;
        this.gui = gui;
    }

    public GuiButtonNop(IGuiInterface gui, int i, int j, int k, int l, int m, String string, boolean enabled) {
        this(gui, i, j, k, l, m, string);
        this.active = enabled;
    }

    public GuiButtonNop(IGuiInterface gui, int i, int j, int k, int l, int m, String[] display, int val) {
        this(gui, i, j, k, l, m, val, display);
    }

    public GuiButtonNop(IGuiInterface gui, int i, int j, int k, int l, int m, int val, String ... display) {
        this(gui, i, j, k, l, m, display.length == 0 ? "" : display[val % display.length]);
        this.display = display;
        this.displayValue = display.length == 0 ? 0 : val % display.length;
    }

    public GuiButtonNop(IGuiInterface gui, int i, int j, int k, int l, int m, ButtonWidget.PressAction clicked, int val, String ... display) {
        this(gui, i, j, k, l, m, display.length == 0 ? "" : display[val % display.length], clicked);
        this.display = display;
        this.displayValue = display.length == 0 ? 0 : val % display.length;
    }

    public void setDisplayText(String text) {
        this.setMessage((Text)Text.translatable((String)text));
    }

    public int getValue() {
        return this.displayValue;
    }

    public void clicked() {
    }

    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        if (!this.shown) {
            return;
        }
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    public void onClick(double x, double y) {
        if (this.gui.hasSubGui()) {
            return;
        }
        if (this.display != null) {
            this.setDisplay((this.displayValue + 1) % this.display.length);
        }
        super.onPress();
    }

    public void setDisplay(int value) {
        this.displayValue = value;
        this.setDisplayText(this.display[value]);
    }

    public void setEnabled(boolean bo) {
        this.active = bo;
    }
}

