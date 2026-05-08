/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.NBTTags;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataAI;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuGet;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class GuiNpcPather
extends GuiNPCInterface
implements IGuiData {
    private GuiCustomScrollNop scroll;
    private HashMap<String, Integer> data = new HashMap();
    private DataAI ai;

    public GuiNpcPather(EntityNPCInterface npc) {
        this.drawDefaultBackground = false;
        this.imageWidth = 176;
        this.title = "Npc Pather";
        this.setBackground("smallbg.png");
        this.ai = npc.ais;
        Packets.sendServer(new SPacketMenuGet(EnumMenuType.MOVING_PATH));
    }

    @Override
    public void init() {
        super.init();
        this.scroll = new GuiCustomScrollNop(this, 0);
        this.scroll.setSize(160, 164);
        ArrayList<String> list = new ArrayList<String>();
        for (int[] arr : this.ai.getMovingPath()) {
            list.add("x:" + arr[0] + " y:" + arr[1] + " z:" + arr[2]);
        }
        this.scroll.setUnsortedList(list);
        this.scroll.guiLeft = this.guiLeft + 7;
        this.scroll.guiTop = this.guiTop + 12;
        this.addScroll(this.scroll);
        this.addButton(new GuiButtonNop(this, 0, this.guiLeft + 6, this.guiTop + 178, 52, 20, "gui.down"));
        this.addButton(new GuiButtonNop(this, 1, this.guiLeft + 62, this.guiTop + 178, 52, 20, "gui.up"));
        this.addButton(new GuiButtonNop(this, 2, this.guiLeft + 118, this.guiTop + 178, 52, 20, "selectServer.delete"));
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        int[] b;
        int[] a;
        int selected;
        List<int[]> list;
        if (!this.scroll.hasSelected()) {
            return;
        }
        int id = guibutton.id;
        if (id == 0) {
            list = this.ai.getMovingPath();
            selected = this.scroll.getSelectedIndex();
            if (list.size() <= selected + 1) {
                return;
            }
            a = list.get(selected);
            b = list.get(selected + 1);
            list.set(selected, b);
            list.set(selected + 1, a);
            this.ai.setMovingPath(list);
            this.init();
            this.scroll.setSelectedIndex(selected + 1);
        }
        if (id == 1) {
            if (this.scroll.getSelectedIndex() - 1 < 0) {
                return;
            }
            list = this.ai.getMovingPath();
            selected = this.scroll.getSelectedIndex();
            a = list.get(selected);
            b = list.get(selected - 1);
            list.set(selected, b);
            list.set(selected - 1, a);
            this.ai.setMovingPath(list);
            this.init();
            this.scroll.setSelectedIndex(selected - 1);
        }
        if (id == 2) {
            list = this.ai.getMovingPath();
            if (list.size() <= 1) {
                return;
            }
            list.remove(this.scroll.getSelectedIndex());
            this.ai.setMovingPath(list);
            this.init();
        }
    }

    @Override
    public boolean mouseClicked(double i, double j, int k) {
        this.scroll.mouseClicked(i, j, k);
        return super.mouseClicked(i, j, k);
    }

    @Override
    public void save() {
        NbtCompound compound = new NbtCompound();
        compound.put("MovingPathNew", (NbtElement)NBTTags.nbtIntegerArraySet(this.ai.getMovingPath()));
        Packets.sendServer(new SPacketMenuSave(EnumMenuType.MOVING_PATH, compound));
    }

    @Override
    public void setGuiData(NbtCompound compound) {
        this.ai.readToNBT(compound);
        this.init();
    }
}

