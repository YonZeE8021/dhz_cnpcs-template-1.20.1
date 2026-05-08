/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screen.Screen
 */
package noppes.npcs.client.gui.questtypes;

import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.quests.QuestLocation;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiNpcQuestTypeLocation
extends GuiNPCInterface
implements ITextfieldListener {
    private Screen parent;
    private QuestLocation quest;

    public GuiNpcQuestTypeLocation(EntityNPCInterface npc, Quest q, Screen parent) {
        this.npc = npc;
        this.parent = parent;
        this.title = "Quest Location Setup";
        this.quest = (QuestLocation)q.questInterface;
        this.setBackground("menubg.png");
        this.imageWidth = 256;
        this.imageHeight = 216;
    }

    @Override
    public void init() {
        super.init();
        this.addLabel(new GuiLabel(0, "Fill in the name of your Location Quest Block", this.guiLeft + 4, this.guiTop + 50));
        this.addTextField(new GuiTextFieldNop(0, (Screen)this, this.guiLeft + 4, this.guiTop + 70, 180, 20, this.quest.location));
        this.addTextField(new GuiTextFieldNop(1, (Screen)this, this.guiLeft + 4, this.guiTop + 92, 180, 20, this.quest.location2));
        this.addTextField(new GuiTextFieldNop(2, (Screen)this, this.guiLeft + 4, this.guiTop + 114, 180, 20, this.quest.location3));
        this.addButton(new GuiButtonNop(this, 0, this.guiLeft + 150, this.guiTop + 190, 98, 20, "gui.back"));
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        if (guibutton.id == 0) {
            this.close();
        }
    }

    @Override
    public void save() {
    }

    @Override
    public void unFocused(GuiTextFieldNop textfield) {
        if (textfield.id == 0) {
            this.quest.location = textfield.getText();
        }
        if (textfield.id == 1) {
            this.quest.location2 = textfield.getText();
        }
        if (textfield.id == 2) {
            this.quest.location3 = textfield.getText();
        }
    }
}

