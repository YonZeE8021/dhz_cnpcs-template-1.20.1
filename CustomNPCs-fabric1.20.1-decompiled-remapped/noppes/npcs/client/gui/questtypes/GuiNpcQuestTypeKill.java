/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.world.World
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.client.gui.questtypes;

import java.util.ArrayList;
import java.util.TreeMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.registry.Registries;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.quests.QuestKill;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiNpcQuestTypeKill
extends GuiNPCInterface
implements ITextfieldListener,
ICustomScrollListener {
    private Screen parent;
    private GuiCustomScrollNop scroll;
    private QuestKill quest;
    private GuiTextFieldNop lastActive;

    public GuiNpcQuestTypeKill(EntityNPCInterface npc, Quest q, Screen parent) {
        this.npc = npc;
        this.parent = parent;
        this.title = "Quest Kill Setup";
        this.quest = (QuestKill)q.questInterface;
        this.setBackground("menubg.png");
        this.imageWidth = 356;
        this.imageHeight = 216;
    }

    @Override
    public void init() {
        super.init();
        int i = 0;
        this.addLabel(new GuiLabel(0, "You can fill in npc or player names too", this.guiLeft + 4, this.guiTop + 50));
        for (String name : this.quest.targets.keySet()) {
            this.addTextField(new GuiTextFieldNop(i, (Screen)this, this.guiLeft + 4, this.guiTop + 70 + i * 22, 180, 20, name));
            this.addTextField(new GuiTextFieldNop(i + 3, (Screen)this, this.guiLeft + 186, this.guiTop + 70 + i * 22, 24, 20, String.valueOf(this.quest.targets.get(name))));
            this.getTextField((int)(i + 3)).numbersOnly = true;
            this.getTextField(i + 3).setMinMaxDefault(1, Integer.MAX_VALUE, 1);
            ++i;
        }
        while (i < 3) {
            this.addTextField(new GuiTextFieldNop(i, (Screen)this, this.guiLeft + 4, this.guiTop + 70 + i * 22, 180, 20, ""));
            this.addTextField(new GuiTextFieldNop(i + 3, (Screen)this, this.guiLeft + 186, this.guiTop + 70 + i * 22, 24, 20, "1"));
            this.getTextField((int)(i + 3)).numbersOnly = true;
            this.getTextField(i + 3).setMinMaxDefault(1, Integer.MAX_VALUE, 1);
            ++i;
        }
        ArrayList<String> list = new ArrayList<String>();
        for (EntityType<? extends Entity> ent : EntityUtil.getAllEntitiesClassesNoNpcs((World)this.client.world).keySet()) {
            list.add(Registries.ENTITY_TYPE.getId(ent).toString());
        }
        if (this.scroll == null) {
            this.scroll = new GuiCustomScrollNop(this, 0);
        }
        this.scroll.setList(list);
        this.scroll.setSize(130, 198);
        this.scroll.guiLeft = this.guiLeft + 220;
        this.scroll.guiTop = this.guiTop + 14;
        this.addScroll(this.scroll);
        this.addButton(new GuiButtonNop(this, 0, this.guiLeft + 4, this.guiTop + 140, 98, 20, "gui.back"));
        this.scroll.visible = false;
        this.lastActive = null;
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        if (guibutton.id == 0) {
            this.close();
        }
    }

    @Override
    public boolean mouseClicked(double i, double j, int k) {
        boolean bo = super.mouseClicked(i, j, k);
        if (GuiTextFieldNop.isAnyActive() && GuiTextFieldNop.getActive().id < 3) {
            this.scroll.visible = true;
            this.lastActive = GuiTextFieldNop.getActive();
        }
        return bo;
    }

    @Override
    public void save() {
    }

    @Override
    public void unFocused(GuiTextFieldNop guiNpcTextField) {
        this.saveTargets();
    }

    private void saveTargets() {
        TreeMap<String, Integer> map = new TreeMap<String, Integer>();
        for (int i = 0; i < 3; ++i) {
            String name = this.getTextField(i).getText();
            if (name.isEmpty()) continue;
            map.put(name, this.getTextField(i + 3).getInteger());
        }
        this.quest.targets = map;
    }

    @Override
    public void scrollClicked(double i, double j, int k, GuiCustomScrollNop guiCustomScroll) {
        if (this.lastActive != null) {
            this.lastActive.setText(guiCustomScroll.getSelected());
            this.saveTargets();
        }
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScrollNop scroll) {
    }
}

