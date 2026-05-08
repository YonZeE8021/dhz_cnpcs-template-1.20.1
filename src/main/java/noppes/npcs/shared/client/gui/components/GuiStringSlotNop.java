/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.EntryListWidget$Entry
 *  net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
 *  net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget$Entry
 */
package noppes.npcs.shared.client.gui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.common.util.NaturalOrderComparator;

public class GuiStringSlotNop<E extends AlwaysSelectedEntryListWidget.Entry<E>>
extends AlwaysSelectedEntryListWidget {
    public HashSet<String> selectedList;
    private boolean multiSelect;
    private GuiBasic parent;

    public GuiStringSlotNop(Collection<String> list, GuiBasic parent, boolean multiSelect) {
        super(MinecraftClient.getInstance(), parent.width, parent.height, 32, parent.height - 64, 9 + 3);
        Objects.requireNonNull(parent.getFontRenderer());
        this.selectedList = new HashSet();
        this.parent = parent;
        this.multiSelect = multiSelect;
        if (list != null) {
            this.setList(list);
        }
    }

    public void setList(Collection<String> l) {
        this.clearEntries();
        ArrayList<String> list = new ArrayList<String>(l);
        Collections.sort(list, new NaturalOrderComparator());
        for (String s : list) {
            this.addEntry((EntryListWidget.Entry)new ListEntry(s));
        }
        this.setSelected((EntryListWidget.Entry)((ListEntry)null));
    }

    public void setColoredList(Map<String, Integer> m) {
        this.clearEntries();
        ArrayList<String> list = new ArrayList<String>(m.keySet());
        Collections.sort(list, new NaturalOrderComparator());
        for (String s : list) {
            this.addEntry((EntryListWidget.Entry)new ListEntry(s, m.get(s)));
        }
        this.setSelected((EntryListWidget.Entry)((ListEntry)null));
    }

    public void setSelected(String s) {
        if (s == null) {
            this.setSelected((EntryListWidget.Entry)((ListEntry)null));
        } else {
            for (Object e : this.children()) {
                if (!((ListEntry)(e)).data.equals(s)) continue;
                this.setSelected((EntryListWidget.Entry)((ListEntry)(e)));
            }
        }
    }

    public String getSelectedString() {
        if (this.getSelectedOrNull() == null) {
            return null;
        }
        return ((ListEntry)this.getSelectedOrNull()).data;
    }

    protected boolean isSelectedEntry(int i) {
        if (!this.multiSelect) {
            return super.isSelectedEntry(i);
        }
        return this.selectedList.contains(((ListEntry)this.getEntry((int)i)).data);
    }

    protected void renderBackground(DrawContext graphics) {
        this.parent.renderBackground(graphics);
    }

    public void clear() {
        this.clearEntries();
    }

    public class ListEntry
    extends AlwaysSelectedEntryListWidget.Entry {
        public final String data;
        public final int color;
        private long prevTime = 0L;

        public ListEntry(String data) {
            this.data = data;
            this.color = 0xFFFFFF;
        }

        public ListEntry(String data, int color) {
            this.data = data;
            this.color = color;
        }

        public void render(DrawContext graphics, int index, int rowTop, int rowBottom, int width, int height, int mouseX, int mouseY, boolean mouseOver, float partialTicks) {
            graphics.drawTextWithShadow(GuiStringSlotNop.this.parent.getFontRenderer(), this.data, rowBottom, rowTop, this.color);
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            long time = System.currentTimeMillis();
            ListEntry s = (ListEntry)GuiStringSlotNop.this.getSelectedOrNull();
            if (s == this && time - this.prevTime < 400L) {
                GuiStringSlotNop.this.parent.doubleClicked();
            }
            this.prevTime = time;
            GuiStringSlotNop.this.setSelected((EntryListWidget.Entry)this);
            if (GuiStringSlotNop.this.selectedList.contains(this.data)) {
                GuiStringSlotNop.this.selectedList.remove(this.data);
            } else {
                GuiStringSlotNop.this.selectedList.add(this.data);
            }
            GuiStringSlotNop.this.parent.elementClicked();
            return true;
        }

        public Text getNarration() {
            return Text.literal((String)this.data);
        }
    }
}

