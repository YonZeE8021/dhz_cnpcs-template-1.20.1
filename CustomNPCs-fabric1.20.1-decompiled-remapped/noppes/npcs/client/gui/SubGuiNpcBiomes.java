/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.registry.Registry
 *  net.minecraft.util.Identifier
 *  net.minecraft.registry.RegistryKeys
 */
package noppes.npcs.client.gui;

import java.util.ArrayList;
import java.util.stream.Collectors;
import net.minecraft.world.biome.Biome;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKeys;
import noppes.npcs.controllers.data.SpawnData;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;

public class SubGuiNpcBiomes
extends GuiBasic {
    private SpawnData data;
    private GuiCustomScrollNop scroll1;
    private GuiCustomScrollNop scroll2;

    public SubGuiNpcBiomes(SpawnData data) {
        this.data = data;
        this.setBackground("menubg.png");
        this.imageWidth = 346;
        this.imageHeight = 216;
    }

    @Override
    public void init() {
        super.init();
        if (this.scroll1 == null) {
            this.scroll1 = new GuiCustomScrollNop(this, 0);
            this.scroll1.setSize(140, 180);
        }
        this.scroll1.guiLeft = this.guiLeft + 4;
        this.scroll1.guiTop = this.guiTop + 14;
        this.addScroll(this.scroll1);
        this.addLabel(new GuiLabel(1, "spawning.availableBiomes", this.guiLeft + 4, this.guiTop + 4));
        if (this.scroll2 == null) {
            this.scroll2 = new GuiCustomScrollNop(this, 1);
            this.scroll2.setSize(140, 180);
        }
        this.scroll2.guiLeft = this.guiLeft + 200;
        this.scroll2.guiTop = this.guiTop + 14;
        this.addScroll(this.scroll2);
        this.addLabel(new GuiLabel(2, "spawning.spawningBiomes", this.guiLeft + 200, this.guiTop + 4));
        ArrayList<String> biomes = new ArrayList<String>();
        Registry biomeRegistry = this.player.getWorld().getRegistryManager().get(RegistryKeys.BIOME);
        for (Biome base : biomeRegistry) {
            if (base == null || biomeRegistry.getId((Object)base) == null || this.data.biomes.contains(biomeRegistry.getId((Object)base).toString())) continue;
            biomes.add(biomeRegistry.getId((Object)base).toString());
        }
        this.scroll1.setList(biomes);
        this.scroll2.setList(this.data.biomes.stream().map(Object::toString).collect(Collectors.toList()));
        this.addButton(new GuiButtonNop(this, 1, this.guiLeft + 145, this.guiTop + 40, 55, 20, ">"));
        this.addButton(new GuiButtonNop(this, 2, this.guiLeft + 145, this.guiTop + 62, 55, 20, "<"));
        this.addButton(new GuiButtonNop(this, 3, this.guiLeft + 145, this.guiTop + 90, 55, 20, ">>"));
        this.addButton(new GuiButtonNop(this, 4, this.guiLeft + 145, this.guiTop + 112, 55, 20, "<<"));
        this.addButton(new GuiButtonNop(this, 66, this.guiLeft + 260, this.guiTop + 194, 60, 20, "gui.done"));
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        GuiButtonNop button = guibutton;
        if (button.id == 1 && this.scroll1.hasSelected()) {
            this.data.biomes.add(new Identifier(this.scroll1.getSelected()));
            this.scroll1.clearSelection();
            this.scroll2.clearSelection();
            this.init();
        }
        if (button.id == 2 && this.scroll2.hasSelected()) {
            this.data.biomes.remove(new Identifier(this.scroll2.getSelected()));
            this.scroll2.clearSelection();
            this.init();
        }
        if (button.id == 3) {
            this.data.biomes.clear();
            Registry biomeRegistry = this.player.getWorld().getRegistryManager().get(RegistryKeys.BIOME);
            for (Biome base : biomeRegistry) {
                if (base == null) continue;
                this.data.biomes.add(biomeRegistry.getId((Object)base));
            }
            this.scroll1.clearSelection();
            this.scroll2.clearSelection();
            this.init();
        }
        if (button.id == 4) {
            this.data.biomes.clear();
            this.scroll1.clearSelection();
            this.scroll2.clearSelection();
            this.init();
        }
        if (button.id == 66) {
            this.close();
        }
    }
}

