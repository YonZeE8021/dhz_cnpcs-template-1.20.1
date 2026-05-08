/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.text.StringVisitable
 */
package noppes.npcs.client.gui;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.StringVisitable;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiNpcFactionPoints
extends GuiBasic
implements ITextfieldListener {
    private Faction faction;

    public SubGuiNpcFactionPoints(Faction faction) {
        this.faction = faction;
        this.setBackground("menubg.png");
        this.imageWidth = 256;
        this.imageHeight = 216;
    }

    @Override
    public void init() {
        super.init();
        this.addLabel(new GuiLabel(2, "faction.default", this.guiLeft + 4, this.guiTop + 33));
        this.addTextField(new GuiTextFieldNop(2, (Screen)this, this.guiLeft + 8 + this.textRenderer.getWidth((StringVisitable)this.getLabel(2).getMessage()), this.guiTop + 28, 70, 20, "" + this.faction.defaultPoints));
        this.getTextField(2).setMaxLength(6);
        this.getTextField((int)2).numbersOnly = true;
        String title = I18n.translate((String)"faction.unfriendly", (Object[])new Object[0]) + "<->" + I18n.translate((String)"faction.neutral", (Object[])new Object[0]);
        this.addLabel(new GuiLabel(3, title, this.guiLeft + 4, this.guiTop + 80));
        this.addTextField(new GuiTextFieldNop(3, (Screen)this, this.guiLeft + 8 + this.textRenderer.getWidth(title), this.guiTop + 75, 70, 20, "" + this.faction.neutralPoints));
        title = I18n.translate((String)"faction.neutral", (Object[])new Object[0]) + "<->" + I18n.translate((String)"faction.friendly", (Object[])new Object[0]);
        this.addLabel(new GuiLabel(4, title, this.guiLeft + 4, this.guiTop + 105));
        this.addTextField(new GuiTextFieldNop(4, (Screen)this, this.guiLeft + 8 + this.textRenderer.getWidth(title), this.guiTop + 100, 70, 20, "" + this.faction.friendlyPoints));
        this.getTextField((int)3).numbersOnly = true;
        this.getTextField((int)4).numbersOnly = true;
        if (this.getTextField(3).getX() > this.getTextField(4).getX()) {
            this.getTextField(4).setX(this.getTextField(3).getX());
        } else {
            this.getTextField(3).setX(this.getTextField(4).getX());
        }
        this.addButton(new GuiButtonNop(this, 66, this.guiLeft + 20, this.guiTop + 192, 90, 20, "gui.done"));
    }

    @Override
    public void unFocused(GuiTextFieldNop textfield) {
        if (textfield.id == 2) {
            this.faction.defaultPoints = textfield.getInteger();
        } else if (textfield.id == 3) {
            this.faction.neutralPoints = textfield.getInteger();
        } else if (textfield.id == 4) {
            this.faction.friendlyPoints = textfield.getInteger();
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        int id = guibutton.id;
        if (id == 66) {
            this.close();
        }
    }
}

