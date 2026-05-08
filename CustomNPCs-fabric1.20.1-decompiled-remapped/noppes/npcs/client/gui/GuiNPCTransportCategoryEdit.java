/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.client.gui.screen.Screen
 */
package noppes.npcs.client.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTransportCategoriesGet;
import noppes.npcs.packets.server.SPacketTransportCategorySave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;

public class GuiNPCTransportCategoryEdit
extends GuiNPCInterface {
    private Screen parent;
    private String name;
    private int id;

    public GuiNPCTransportCategoryEdit(EntityNPCInterface npc, Screen parent, String name, int id) {
        super(npc);
        this.parent = parent;
        this.name = name;
        this.id = id;
        this.title = "Npc Transport Category";
    }

    @Override
    public void init() {
        super.init();
        this.addTextField(new GuiTextFieldNop(1, (Screen)this, this.width / 2 - 40, 100, 140, 20, this.name));
        this.addLabel(new GuiLabel(1, "Title:", this.width / 2 - 100 + 4, 105, 0xFFFFFF));
        this.addButton(new GuiButtonNop(this, 2, this.width / 2 - 100, 210, 98, 20, "gui.back"));
        this.addButton(new GuiButtonNop(this, 3, this.width / 2 + 2, 210, 98, 20, "Save"));
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        int id = guibutton.id;
        if (id == 2) {
            NoppesUtil.openGUI((PlayerEntity)this.player, this.parent);
            Packets.sendServer(new SPacketTransportCategoriesGet());
        }
        if (id == 3) {
            this.save();
            NoppesUtil.openGUI((PlayerEntity)this.player, this.parent);
            Packets.sendServer(new SPacketTransportCategoriesGet());
        }
    }

    @Override
    public void save() {
        String name = this.getTextField(1).getText();
        if (name.trim().isEmpty()) {
            return;
        }
        Packets.sendServer(new SPacketTransportCategorySave(this.id, name));
    }
}

