package noppes.npcs.client.gui;

import java.util.ArrayList;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.roles.RoleTrader;
import noppes.npcs.roles.TraderTimeUnit;
import noppes.npcs.roles.TraderVariableDef;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class SubGuiNpcTraderVariables
extends GuiNPCInterface
implements ICustomScrollListener {
    private final RoleTrader role;
    private int index = 0;
    private TraderVariableDef editing;
    private GuiCustomScrollNop scroll;

    public SubGuiNpcTraderVariables(RoleTrader role) {
        this.role = role;
        this.setBackground("menubg.png");
        this.imageWidth = 420;
        this.imageHeight = 256;
        if (!this.role.variableDefs.isEmpty()) {
            this.index = 0;
            this.editing = this.role.variableDefs.get(0);
        }
    }

    @Override
    public void init() {
        super.init();
        if (this.editing == null) {
            this.editing = new TraderVariableDef();
        }
        if (this.scroll == null) {
            this.scroll = new GuiCustomScrollNop(this, 0);
            this.scroll.setSize(180, 170);
        }
        this.scroll.guiLeft = this.guiLeft + 4;
        this.scroll.guiTop = this.guiTop + 28;
        this.addScroll(this.scroll);
        this.refreshScrollList();
        this.addLabel(new GuiLabel(0, "trader.var.title", this.guiLeft + 4, this.guiTop + 4));
        this.addButton(new GuiButtonNop(this, 10, this.guiLeft + 190, this.guiTop + 4, 50, 20, "gui.add"));
        this.addButton(new GuiButtonNop(this, 11, this.guiLeft + 244, this.guiTop + 4, 50, 20, "gui.remove"));
        this.addLabel(new GuiLabel(1, "trader.var.name", this.guiLeft + 190, this.guiTop + 30));
        this.addTextField(new GuiTextFieldNop(0, (Screen)this, this.guiLeft + 190, this.guiTop + 40, 210, 20, this.editing.name));
        this.addLabel(new GuiLabel(2, "trader.var.initial", this.guiLeft + 190, this.guiTop + 64));
        this.addTextField(new GuiTextFieldNop(1, (Screen)this, this.guiLeft + 190, this.guiTop + 74, 70, 20, "" + this.editing.initialValue));
        this.getTextField(1).numbersOnly = true;
        this.addLabel(new GuiLabel(3, "trader.var.max", this.guiLeft + 266, this.guiTop + 64));
        this.addTextField(new GuiTextFieldNop(2, (Screen)this, this.guiLeft + 266, this.guiTop + 74, 70, 20, "" + this.editing.maxValue));
        this.getTextField(2).numbersOnly = true;
        this.addLabel(new GuiLabel(4, "trader.var.reset", this.guiLeft + 190, this.guiTop + 98));
        this.addButton(new GuiButtonNop((IGuiInterface)this, 0, this.guiLeft + 190, this.guiTop + 108, 210, 20, new String[]{"trader.var.reset.none", "trader.var.reset.real", "trader.var.reset.game"}, this.editing.resetType));
        this.addLabel(new GuiLabel(5, "trader.var.reset.time", this.guiLeft + 190, this.guiTop + 132));
        this.addTextField(new GuiTextFieldNop(3, (Screen)this, this.guiLeft + 190, this.guiTop + 142, 60, 20, "" + this.editing.resetTime));
        this.getTextField(3).numbersOnly = true;
        this.addButton(new GuiButtonNop((IGuiInterface)this, 1, this.guiLeft + 254, this.guiTop + 142, 80, 20, TraderTimeUnit.KEYS, this.editing.resetTimeUnit));
        this.addLabel(new GuiLabel(6, "trader.var.visible", this.guiLeft + 190, this.guiTop + 176));
        this.addButton(new GuiButtonYesNo((IGuiInterface)this, 2, this.guiLeft + 300, this.guiTop + 171, this.editing.playerVisible));
        this.lastNameInField = this.editing.name;
        this.updateResetFields();
    }

    private String lastNameInField = "";

    @Override
    public void tick() {
        super.tick();
        if (this.getTextField(0) == null) {
            return;
        }
        String current = this.getTextField(0).getText().trim();
        if (current.equals(this.lastNameInField)) {
            return;
        }
        this.lastNameInField = current;
        this.applyFieldsToEditing();
        this.refreshScrollList();
    }

    private void refreshScrollList() {
        ArrayList<String> names = new ArrayList<>();
        for (TraderVariableDef def : this.role.variableDefs) {
            if (def.name.isEmpty()) {
                names.add("(" + (def.isGlobal() ? "global" : "local") + ")");
                continue;
            }
            names.add(def.name);
        }
        this.scroll.setUnsortedList(names);
        if (this.index >= 0 && this.index < names.size()) {
            this.scroll.setSelectedIndex(this.index);
        }
    }

    private void updateResetFields() {
        boolean enabled = this.editing.resetType != TraderVariableDef.RESET_NONE;
        this.getTextField(3).enabled = enabled;
        this.getButton(1).active = enabled;
        this.getButton(1).shown = enabled;
        this.getLabel(5).enabled = enabled;
    }

    private void applyFieldsToEditing() {
        this.editing.name = this.getTextField(0).getText().trim();
        if (this.getTextField(1).isInteger()) {
            this.editing.initialValue = this.getTextField(1).getInteger();
        }
        if (this.getTextField(2).isInteger()) {
            this.editing.maxValue = this.getTextField(2).getInteger();
        }
        if (this.getTextField(3).isInteger()) {
            this.editing.resetTime = Math.max(0, this.getTextField(3).getInteger());
        }
    }

    private void loadEditingToFields() {
        this.getTextField(0).setText(this.editing.name);
        this.getTextField(1).setText("" + this.editing.initialValue);
        this.getTextField(2).setText("" + this.editing.maxValue);
        this.getTextField(3).setText("" + this.editing.resetTime);
        this.getButton(0).setDisplay(this.editing.resetType);
        this.getButton(1).setDisplay(this.editing.resetTimeUnit);
        ((GuiButtonYesNo)this.getButton(2)).setDisplay(this.editing.playerVisible ? 1 : 0);
        this.lastNameInField = this.editing.name;
        this.updateResetFields();
    }

    private void storeEditingAtIndex() {
        this.applyFieldsToEditing();
        if (this.index >= this.role.variableDefs.size()) {
            this.role.variableDefs.add(this.editing);
        } else if (this.index >= 0) {
            this.role.variableDefs.set(this.index, this.editing);
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        this.applyFieldsToEditing();
        if (guibutton.id == 0) {
            this.editing.resetType = guibutton.getValue();
            this.updateResetFields();
        }
        if (guibutton.id == 1) {
            this.editing.resetTimeUnit = guibutton.getValue();
        }
        if (guibutton.id == 2) {
            this.editing.playerVisible = ((GuiButtonYesNo)guibutton).getBoolean();
        }
        if (guibutton.id == 10) {
            this.storeEditingAtIndex();
            this.role.variableDefs.add(new TraderVariableDef());
            this.index = this.role.variableDefs.size() - 1;
            this.editing = this.role.variableDefs.get(this.index);
            this.loadEditingToFields();
            this.refreshScrollList();
        }
        if (guibutton.id == 11 && !this.role.variableDefs.isEmpty()) {
            this.role.variableDefs.remove(this.index);
            if (this.role.variableDefs.isEmpty()) {
                this.editing = new TraderVariableDef();
                this.index = 0;
            } else {
                this.index = Math.min(this.index, this.role.variableDefs.size() - 1);
                this.editing = this.role.variableDefs.get(this.index);
            }
            this.loadEditingToFields();
            this.refreshScrollList();
        }
    }

    @Override
    public void scrollClicked(double x, double y, int k, GuiCustomScrollNop scroll) {
        if (scroll.id != 0) {
            return;
        }
        this.storeEditingAtIndex();
        this.index = scroll.getSelectedIndex();
        if (this.index < 0 || this.index >= this.role.variableDefs.size()) {
            this.editing = new TraderVariableDef();
            return;
        }
        this.editing = this.role.variableDefs.get(this.index);
        this.loadEditingToFields();
        this.refreshScrollList();
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScrollNop scroll) {
    }

    @Override
    public void save() {
        this.storeEditingAtIndex();
    }
}
