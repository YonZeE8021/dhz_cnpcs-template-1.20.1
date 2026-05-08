/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.text.Text
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 */
package noppes.npcs.client.gui.global;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface2;
import noppes.npcs.containers.ContainerManageBanks;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketBankGet;
import noppes.npcs.packets.server.SPacketBankRemove;
import noppes.npcs.packets.server.SPacketBankSave;
import noppes.npcs.packets.server.SPacketBanksGet;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiNPCManageBanks
extends GuiContainerNPCInterface2<ContainerManageBanks>
implements IScrollData,
ICustomScrollListener,
ITextfieldListener,
IGuiData {
    private GuiCustomScrollNop scroll;
    private Map<String, Integer> data = new HashMap<String, Integer>();
    private ContainerManageBanks container;
    private Bank bank = new Bank();
    private String selected = null;

    public GuiNPCManageBanks(ContainerManageBanks container, PlayerInventory inv, Text titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn);
        this.container = container;
        this.drawDefaultBackground = false;
        this.setBackground("npcbanksetup.png");
        this.backgroundHeight = 200;
        Packets.sendServer(new SPacketBanksGet());
    }

    @Override
    public void init() {
        super.init();
        this.addButton(new GuiButtonNop(this, 6, this.guiLeft + 340, this.guiTop + 10, 45, 20, "gui.add"));
        this.addButton(new GuiButtonNop(this, 7, this.guiLeft + 340, this.guiTop + 32, 45, 20, "gui.remove"));
        if (this.scroll == null) {
            this.scroll = new GuiCustomScrollNop((Screen)this, 0);
            this.scroll.setSize(160, 180);
        }
        this.scroll.guiLeft = this.guiLeft + 174;
        this.scroll.guiTop = this.guiTop + 8;
        this.addScroll(this.scroll);
        for (int i = 0; i < 6; ++i) {
            int x = this.guiLeft + 6;
            int y = this.guiTop + 36 + i * 22;
            this.addButton(new GuiButtonNop((IGuiInterface)this, i, x + 50, y, 80, 20, new String[]{"bank.canUpgrade", "bank.cantUpgrade", "bank.upgraded"}, 0));
            this.getButton(i).setEnabled(false);
        }
        this.addTextField(new GuiTextFieldNop(0, (Screen)this, this.guiLeft + 8, this.guiTop + 8, 160, 16, ""));
        this.getTextField(0).setMaxLength(20);
        this.addTextField(new GuiTextFieldNop(1, (Screen)this, this.guiLeft + 10, this.guiTop + 80, 16, 16, ""));
        this.getTextField((int)1).numbersOnly = true;
        this.getTextField(1).setMaxLength(1);
        this.addTextField(new GuiTextFieldNop(2, (Screen)this, this.guiLeft + 10, this.guiTop + 110, 16, 16, ""));
        this.getTextField((int)2).numbersOnly = true;
        this.getTextField(2).setMaxLength(1);
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        GuiButtonNop button = guibutton;
        if (button.id == 6) {
            this.save();
            this.scroll.clear();
            Object name = "New";
            while (this.data.containsKey(name)) {
                name = (String)name + "_";
            }
            Bank bank = new Bank();
            bank.name = name;
            NbtCompound compound = new NbtCompound();
            bank.addAdditionalSaveData(compound);
            Packets.sendServer(new SPacketBankSave(compound));
        } else if (button.id == 7) {
            if (this.data.containsKey(this.scroll.getSelected())) {
                Packets.sendServer(new SPacketBankRemove(this.data.get(this.selected)));
            }
        } else if (button.id >= 0 && button.id < 6) {
            this.bank.slotTypes.put(button.id, button.getValue());
        }
    }

    @Override
    protected void drawForeground(DrawContext graphics, int par1, int limbSwingAmount) {
        graphics.drawText(this.textRenderer, I18n.translate((String)"bank.tabCost", (Object[])new Object[0]), 23, 28, CustomNpcResourceListener.DefaultTextColor, false);
        graphics.drawText(this.textRenderer, I18n.translate((String)"bank.upgCost", (Object[])new Object[0]), 123, 28, CustomNpcResourceListener.DefaultTextColor, false);
        graphics.drawText(this.textRenderer, I18n.translate((String)"gui.start", (Object[])new Object[0]), 6, 70, CustomNpcResourceListener.DefaultTextColor, false);
        graphics.drawText(this.textRenderer, I18n.translate((String)"gui.max", (Object[])new Object[0]), 9, 100, CustomNpcResourceListener.DefaultTextColor, false);
    }

    @Override
    public void setGuiData(NbtCompound compound) {
        Bank bank = new Bank();
        bank.readAdditionalSaveData(compound);
        this.bank = bank;
        if (bank.id == -1) {
            this.getTextField(0).setText("");
            this.getTextField(1).setText("");
            this.getTextField(2).setText("");
            for (int i = 0; i < 6; ++i) {
                this.getButton(i).setDisplay(0);
                this.getButton(i).setEnabled(false);
            }
        } else {
            this.getTextField(0).setText(bank.name);
            this.getTextField(1).setText(Integer.toString(bank.startSlots));
            this.getTextField(2).setText(Integer.toString(bank.maxSlots));
            for (int i = 0; i < 6; ++i) {
                int type = 0;
                if (bank.slotTypes.containsKey(i)) {
                    type = bank.slotTypes.get(i);
                }
                this.getButton(i).setDisplay(type);
                this.getButton(i).setEnabled(true);
            }
        }
        this.setSelected(bank.name);
    }

    @Override
    public void setData(Vector<String> list, Map<String, Integer> data) {
        String name = this.scroll.getSelected();
        this.data = data;
        this.scroll.setList(list);
        if (name != null) {
            this.scroll.setSelected(name);
        }
    }

    @Override
    public void setSelected(String selected) {
        this.selected = selected;
        this.scroll.setSelected(selected);
    }

    @Override
    public void scrollClicked(double i, double j, int k, GuiCustomScrollNop guiCustomScroll) {
        if (guiCustomScroll.id == 0) {
            this.save();
            this.selected = this.scroll.getSelected();
            Packets.sendServer(new SPacketBankGet(this.data.get(this.selected)));
        }
    }

    @Override
    public void save() {
        if (this.selected != null && this.data.containsKey(this.selected) && this.bank != null) {
            NbtCompound compound = new NbtCompound();
            this.bank.currencyInventory = this.container.bank.currencyInventory;
            this.bank.upgradeInventory = this.container.bank.upgradeInventory;
            this.bank.addAdditionalSaveData(compound);
            Packets.sendServer(new SPacketBankSave(compound));
        }
    }

    @Override
    public void unFocused(GuiTextFieldNop guiNpcTextField) {
        if (this.bank.id != -1) {
            if (guiNpcTextField.id == 0) {
                String name = guiNpcTextField.getText();
                if (!name.isEmpty() && !this.data.containsKey(name)) {
                    String old = this.bank.name;
                    this.data.remove(this.bank.name);
                    this.bank.name = name;
                    this.data.put(this.bank.name, this.bank.id);
                    this.selected = name;
                    this.scroll.replace(old, this.bank.name);
                }
            } else if (guiNpcTextField.id == 1 || guiNpcTextField.id == 2) {
                int num = 1;
                if (!guiNpcTextField.isEmpty()) {
                    num = guiNpcTextField.getInteger();
                }
                if (num > 6) {
                    num = 6;
                }
                if (num < 0) {
                    num = 0;
                }
                if (guiNpcTextField.id == 1) {
                    this.bank.startSlots = num;
                } else if (guiNpcTextField.id == 2) {
                    this.bank.maxSlots = num;
                }
                if (this.bank.startSlots > this.bank.maxSlots) {
                    this.bank.maxSlots = this.bank.startSlots;
                }
                guiNpcTextField.setText(Integer.toString(num));
            }
        }
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScrollNop scroll) {
    }
}

