/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 */
package noppes.npcs.client.gui.global;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.GuiNPCTransportCategoryEdit;
import noppes.npcs.client.gui.mainmenu.GuiNPCGlobalMainMenu;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTransportCategoriesGet;
import noppes.npcs.packets.server.SPacketTransportCategoryRemove;
import noppes.npcs.packets.server.SPacketTransportGet;
import noppes.npcs.packets.server.SPacketTransportRemove;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiStringSlotNop;
import noppes.npcs.shared.client.gui.listeners.IScrollData;

public class GuiNPCManageTransporters
extends GuiNPCInterface
implements IScrollData {
    private GuiStringSlotNop slot;
    private Map<String, Integer> data;
    private boolean selectCategory = true;

    public GuiNPCManageTransporters(EntityNPCInterface npc) {
        super(npc);
        Packets.sendServer(new SPacketTransportCategoriesGet());
        this.drawDefaultBackground = false;
        this.title = "";
        this.data = new HashMap<String, Integer>();
    }

    @Override
    public void init() {
        super.init();
        Vector<String> list = new Vector<String>();
        this.slot = new GuiStringSlotNop(list, this, false);
        this.addSelectableChild((Element)this.slot);
        this.addButton(new GuiButtonNop(this, 0, this.width / 2 - 100, this.height - 52, 65, 20, "gui.add"));
        this.addButton(new GuiButtonNop(this, 1, this.width / 2 - 33, this.height - 52, 65, 20, "selectServer.edit"));
        this.getButton(0).setEnabled(this.selectCategory);
        this.getButton(1).setEnabled(this.selectCategory);
        this.addButton(new GuiButtonNop(this, 3, this.width / 2 + 33, this.height - 52, 65, 20, "gui.remove"));
        this.addButton(new GuiButtonNop(this, 2, this.width / 2 - 100, this.height - 31, 98, 20, "gui.open"));
        this.getButton(2).setEnabled(this.selectCategory);
        this.addButton(new GuiButtonNop(this, 4, this.width / 2 + 2, this.height - 31, 98, 20, "gui.back"));
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        this.slot.render(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        int id = guibutton.id;
        if (id == 0 && this.selectCategory) {
            NoppesUtil.openGUI((PlayerEntity)this.player, new GuiNPCTransportCategoryEdit(this.npc, this, "", -1));
        }
        if (id == 1) {
            if (this.slot.getSelectedString() == null || this.slot.getSelectedString().isEmpty()) {
                return;
            }
            if (this.selectCategory) {
                NoppesUtil.openGUI((PlayerEntity)this.player, new GuiNPCTransportCategoryEdit(this.npc, this, this.slot.getSelectedString(), this.data.get(this.slot.getSelectedString())));
            }
        }
        if (id == 4) {
            if (this.selectCategory) {
                this.close();
                NoppesUtil.openGUI((PlayerEntity)this.player, new GuiNPCGlobalMainMenu(this.npc));
            } else {
                this.title = "";
                this.selectCategory = true;
                Packets.sendServer(new SPacketTransportCategoriesGet());
                this.init();
            }
        }
        if (id == 3) {
            if (this.slot.getSelectedString() == null || this.slot.getSelectedString().isEmpty()) {
                return;
            }
            this.save();
            if (this.selectCategory) {
                Packets.sendServer(new SPacketTransportCategoryRemove(this.data.get(this.slot.getSelectedString())));
            } else {
                Packets.sendServer(new SPacketTransportRemove(this.data.get(this.slot.getSelectedString())));
            }
            this.init();
        }
        if (id == 2) {
            this.doubleClicked();
        }
    }

    @Override
    public void doubleClicked() {
        if (this.slot.getSelectedString() == null || this.slot.getSelectedString().isEmpty()) {
            return;
        }
        if (this.selectCategory) {
            this.selectCategory = false;
            this.title = "";
            Packets.sendServer(new SPacketTransportGet(this.data.get(this.slot.getSelectedString())));
            this.init();
        }
    }

    @Override
    public void save() {
    }

    @Override
    public void setData(Vector<String> list, Map<String, Integer> data) {
        this.data = data;
        this.slot.setList(list);
    }

    @Override
    public void setSelected(String selected) {
    }
}

