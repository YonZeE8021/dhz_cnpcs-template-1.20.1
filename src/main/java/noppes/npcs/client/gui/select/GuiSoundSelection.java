/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.sound.SoundCategory
 */
package noppes.npcs.client.gui.select;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.util.NoppesStringUtils;

public class GuiSoundSelection
extends GuiBasic
implements ICustomScrollListener {
    private GuiCustomScrollNop scrollCategories;
    private GuiCustomScrollNop scrollQuests;
    private String selectedDomain;
    public Identifier selectedResource;
    private HashMap<String, List<String>> domains = new HashMap();

    public GuiSoundSelection(String sound) {
        this.drawDefaultBackground = false;
        this.title = "";
        this.setBackground("menubg.png");
        this.imageWidth = 366;
        this.imageHeight = 226;
        SoundManager handler = MinecraftClient.getInstance().getSoundManager();
        Collection<Identifier> set = handler.getKeys();
        for (Identifier location : set) {
            List<String> list = this.domains.get(location.getNamespace());
            if (list == null) {
                list = new ArrayList<String>();
                this.domains.put(location.getNamespace(), list);
            }
            list.add(location.getPath());
            this.domains.put(location.getNamespace(), list);
        }
        if (sound != null && !sound.isEmpty()) {
            this.selectedResource = new Identifier(sound);
            this.selectedDomain = this.selectedResource.getNamespace();
            if (!this.domains.containsKey(this.selectedDomain)) {
                this.selectedDomain = null;
            }
        }
    }

    @Override
    public void init() {
        super.init();
        this.addButton(new GuiButtonNop(this, 2, this.guiLeft + this.imageWidth - 26, this.guiTop + 4, 20, 20, "X"));
        this.addButton(new GuiButtonNop((IGuiInterface)this, 1, this.guiLeft + 125, this.guiTop + 212, 70, 20, "gui.play", this.selectedResource != null));
        this.addButton(new GuiButtonNop((IGuiInterface)this, 3, this.guiLeft + 195, this.guiTop + 212, 70, 20, "gui.copy", this.selectedResource != null));
        if (this.scrollCategories == null) {
            this.scrollCategories = new GuiCustomScrollNop(this, 0);
            this.scrollCategories.setSize(90, 200);
        }
        this.scrollCategories.setList(Lists.newArrayList(this.domains.keySet()));
        if (this.selectedDomain != null) {
            this.scrollCategories.setSelected(this.selectedDomain);
        }
        this.scrollCategories.guiLeft = this.guiLeft + 4;
        this.scrollCategories.guiTop = this.guiTop + 14;
        this.addScroll(this.scrollCategories);
        if (this.scrollQuests == null) {
            this.scrollQuests = new GuiCustomScrollNop(this, 1);
            this.scrollQuests.setSize(250, 200);
        }
        if (this.selectedDomain != null) {
            this.scrollQuests.setList(this.domains.get(this.selectedDomain));
        }
        if (this.selectedResource != null) {
            this.scrollQuests.setSelected(this.selectedResource.getPath());
        }
        this.scrollQuests.guiLeft = this.guiLeft + 95;
        this.scrollQuests.guiTop = this.guiTop + 14;
        this.addScroll(this.scrollQuests);
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        if (guibutton.id == 1) {
            MusicController.Instance.stopMusic();
            BlockPos pos = this.player.getBlockPos();
            MusicController.Instance.playSound(SoundCategory.NEUTRAL, this.selectedResource.toString(), pos, 1.0f, 1.0f);
        }
        if (guibutton.id == 3) {
            NoppesStringUtils.setClipboardContents(this.selectedResource.toString());
        }
        if (guibutton.id == 2) {
            this.close();
        }
    }

    @Override
    public void scrollClicked(double i, double j, int k, GuiCustomScrollNop scroll) {
        if (scroll.id == 0) {
            this.selectedDomain = scroll.getSelected();
            this.selectedResource = null;
            this.scrollQuests.clearSelection();
        }
        if (scroll.id == 1) {
            this.selectedResource = new Identifier(this.selectedDomain, scroll.getSelected());
        }
        this.init();
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScrollNop scroll) {
        if (this.selectedResource == null) {
            return;
        }
        this.close();
    }
}

