package noppes.npcs.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.roles.RoleTrader;
import noppes.npcs.roles.TraderSlotLimit;
import noppes.npcs.roles.TraderTimeUnit;
import noppes.npcs.roles.TraderVariableOperation;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class SubGuiNpcTraderLimit
extends GuiNPCInterface {
    private final RoleTrader role;
    private final int slot;
    private TraderSlotLimit limit;

    public SubGuiNpcTraderLimit(RoleTrader role, int slot) {
        this.role = role;
        this.slot = slot;
        this.limit = role.getSlotLimit(slot);
        while (this.limit.variableOps.size() < 3) {
            this.limit.variableOps.add(new TraderVariableOperation());
        }
        this.setBackground("menubg.png");
        this.imageWidth = 280;
        this.imageHeight = 230;
    }

    @Override
    public void init() {
        super.init();
        this.addLabel(new GuiLabel(0, "trader.limit.slot", this.guiLeft + 4, this.guiTop + 4));
        this.addLabel(new GuiLabel(1, "trader.limit.type", this.guiLeft + 4, this.guiTop + 24));
        this.addButton(new GuiButtonNop((IGuiInterface)this, 0, this.guiLeft + 4, this.guiTop + 34, 250, 20, new String[]{"trader.limit.type.none", "trader.limit.type.once", "trader.limit.type.real", "trader.limit.type.game", "trader.limit.type.online"}, this.limit.playerLimitType));
        this.addLabel(new GuiLabel(2, "trader.limit.count", this.guiLeft + 4, this.guiTop + 58));
        this.addTextField(new GuiTextFieldNop(0, (Screen)this, this.guiLeft + 4, this.guiTop + 68, 60, 20, "" + this.limit.playerLimitCount));
        this.getTextField(0).numbersOnly = true;
        this.addLabel(new GuiLabel(3, "trader.limit.period", this.guiLeft + 70, this.guiTop + 58));
        this.addTextField(new GuiTextFieldNop(1, (Screen)this, this.guiLeft + 70, this.guiTop + 68, 50, 20, "" + this.limit.playerLimitTime));
        this.getTextField(1).numbersOnly = true;
        this.addButton(new GuiButtonNop((IGuiInterface)this, 1, this.guiLeft + 124, this.guiTop + 68, 80, 20, TraderTimeUnit.KEYS, this.limit.playerLimitTimeUnit));
        this.addLabel(new GuiLabel(4, "trader.var.ops", this.guiLeft + 4, this.guiTop + 94));
        this.addLabel(new GuiLabel(5, "trader.var.ops.name", this.guiLeft + 4, this.guiTop + 106));
        this.addLabel(new GuiLabel(6, "trader.var.ops.delta", this.guiLeft + 170, this.guiTop + 106));
        for (int i = 0; i < 3; ++i) {
            TraderVariableOperation op = this.limit.variableOps.get(i);
            this.addTextField(new GuiTextFieldNop(10 + i, (Screen)this, this.guiLeft + 4, this.guiTop + 116 + i * 22, 160, 20, op.variableName));
            this.addTextField(new GuiTextFieldNop(20 + i, (Screen)this, this.guiLeft + 170, this.guiTop + 116 + i * 22, 60, 20, op.delta == 0 ? "" : "" + op.delta));
            this.getTextField(20 + i).numbersOnly = true;
        }
        this.updateFieldState();
    }

    private void updateFieldState() {
        boolean none = this.limit.playerLimitType == TraderSlotLimit.PLAYER_NONE;
        boolean once = this.limit.playerLimitType == TraderSlotLimit.PLAYER_ONCE;
        boolean timed = this.limit.playerLimitType == TraderSlotLimit.PLAYER_REAL_TIME || this.limit.playerLimitType == TraderSlotLimit.PLAYER_GAME_TIME || this.limit.playerLimitType == TraderSlotLimit.PLAYER_ONLINE_TIME;
        this.getTextField(0).enabled = !none;
        this.getLabel(2).enabled = !none;
        this.getTextField(1).enabled = timed;
        this.getLabel(3).enabled = timed;
        this.getButton(1).active = timed;
        this.getButton(1).shown = timed;
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        if (guibutton.id == 0) {
            this.limit.playerLimitType = guibutton.getValue();
            this.updateFieldState();
        }
        if (guibutton.id == 1) {
            this.limit.playerLimitTimeUnit = guibutton.getValue();
        }
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        ItemStack sold = this.role.inventorySold.getStack(this.slot);
        if (!sold.isEmpty()) {
            graphics.drawItem(sold, this.guiLeft + 220, this.guiTop + 4);
        }
        graphics.drawTextWithShadow(this.textRenderer, I18n.translate("trader.limit.slot", this.slot + 1), this.guiLeft + 70, this.guiTop + 4, 0xFFFFFF);
    }

    @Override
    public void save() {
        if (this.getTextField(0).isInteger()) {
            this.limit.playerLimitCount = this.getTextField(0).getInteger();
        } else {
            this.limit.playerLimitCount = 0;
        }
        if (this.getTextField(1).isInteger()) {
            this.limit.playerLimitTime = Math.max(1, this.getTextField(1).getInteger());
        }
        this.limit.variableOps.clear();
        for (int i = 0; i < 3; ++i) {
            String name = this.getTextField(10 + i).getText().trim();
            if (name.isEmpty()) {
                continue;
            }
            TraderVariableOperation op = new TraderVariableOperation();
            op.variableName = name;
            if (this.getTextField(20 + i).isInteger()) {
                op.delta = this.getTextField(20 + i).getInteger();
            }
            if (op.delta != 0) {
                this.limit.variableOps.add(op);
            }
        }
        this.role.slotLimits[this.slot] = this.limit;
    }
}
