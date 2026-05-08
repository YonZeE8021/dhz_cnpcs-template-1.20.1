/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.client.gui.screen.Screen
 */
package noppes.npcs.client.gui;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.entity.data.DataDisplay;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpRandomNameSet;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonBiDirectional;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiNpcName
extends GuiBasic
implements ITextfieldListener,
IGuiData {
    private DataDisplay display;

    public SubGuiNpcName(DataDisplay display) {
        this.display = display;
        this.setBackground("menubg.png");
        this.imageWidth = 256;
        this.imageHeight = 216;
    }

    @Override
    public void init() {
        super.init();
        int y = this.guiTop + 4;
        this.addButton(new GuiButtonNop(this, 66, this.guiLeft + this.imageWidth - 24, y, 20, 20, "X"));
        this.addTextField(new GuiTextFieldNop(0, (Screen)this, this.guiLeft + 4, y += 50, 226, 20, this.display.getName()));
        this.addButton(new GuiButtonBiDirectional((IGuiInterface)this, 1, this.guiLeft + 4, y += 22, 200, 20, new String[]{"markov.roman.name", "markov.japanese.name", "markov.slavic.name", "markov.welsh.name", "markov.sami.name", "markov.oldNorse.name", "markov.ancientGreek.name", "markov.aztec.name", "markov.classicCNPCs.name", "markov.spanish.name"}, this.display.getMarkovGeneratorId()));
        this.addButton(new GuiButtonBiDirectional((IGuiInterface)this, 2, this.guiLeft + 64, y += 22, 120, 20, new String[]{"markov.gender.either", "markov.gender.male", "markov.gender.female"}, this.display.getMarkovGender()));
        this.addLabel(new GuiLabel(2, "markov.gender.name", this.guiLeft + 5, y + 5));
        this.addButton(new GuiButtonNop(this, 3, this.guiLeft + 4, y += 42, 70, 20, "markov.generate"));
    }

    @Override
    public void unFocused(GuiTextFieldNop textfield) {
        if (textfield.id == 0) {
            if (!textfield.isEmpty()) {
                this.display.setName(textfield.getText());
            } else {
                textfield.setText(this.display.getName());
            }
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        GuiButtonNop button = guibutton;
        if (button.id == 1) {
            this.display.setMarkovGeneratorId(button.getValue());
        }
        if (button.id == 2) {
            this.display.setMarkovGender(button.getValue());
        }
        if (button.id == 3) {
            Packets.sendServer(new SPacketNpRandomNameSet(this.display.getMarkovGeneratorId(), this.display.getMarkovGender()));
        }
        if (button.id == 66) {
            this.close();
        }
    }

    @Override
    public void setGuiData(NbtCompound compound) {
        this.display.readToNBT(compound);
        this.init();
    }
}

