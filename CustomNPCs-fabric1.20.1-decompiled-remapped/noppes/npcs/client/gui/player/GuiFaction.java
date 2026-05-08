/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Element
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.player.tabs.InventoryTabFactions;
import noppes.npcs.client.gui.player.tabs.InventoryTabQuests;
import noppes.npcs.client.gui.player.tabs.InventoryTabVanilla;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerFactionData;
import noppes.npcs.shared.client.gui.components.GuiButtonNextPage;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiFaction
extends GuiNPCInterface {
    private ArrayList<Faction> playerFactions = new ArrayList();
    private PlayerFactionData data;
    private int page = 0;
    private int pages = 1;
    private GuiButtonNextPage buttonNextPage;
    private GuiButtonNextPage buttonPreviousPage;
    private Identifier indicator;

    public GuiFaction() {
        this.imageWidth = 200;
        this.imageHeight = 195;
        this.drawDefaultBackground = false;
        this.title = "";
        this.indicator = this.getResource("standardbg.png");
    }

    @Override
    public void init() {
        super.init();
        this.data = PlayerData.get((PlayerEntity)this.player).factionData;
        this.playerFactions = new ArrayList();
        for (int id : this.data.factionData.keySet()) {
            Faction faction = FactionController.instance.getFaction(id);
            if (faction == null || faction.hideFaction) continue;
            this.playerFactions.add(faction);
        }
        this.pages = (this.playerFactions.size() - 1) / 5;
        ++this.pages;
        this.page = 1;
        this.guiLeft = (this.width - this.imageWidth) / 2;
        this.guiTop += 12;
        this.addDrawableChild((Element)new InventoryTabVanilla().init(this));
        this.addDrawableChild((Element)new InventoryTabFactions().init(this));
        this.addDrawableChild((Element)new InventoryTabQuests().init(this));
        this.buttonNextPage = new GuiButtonNextPage((IGuiInterface)this, 1, this.guiLeft + this.imageWidth - 43, this.guiTop + 180, true, button -> {
            ++this.page;
            this.updateButtons();
        });
        this.addButton(this.buttonNextPage);
        this.buttonPreviousPage = new GuiButtonNextPage((IGuiInterface)this, 2, this.guiLeft + 20, this.guiTop + 180, false, button -> {
            --this.page;
            this.updateButtons();
        });
        this.addButton(this.buttonPreviousPage);
        this.updateButtons();
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)this.indicator);
        graphics.drawTexture(this.indicator, this.guiLeft, this.guiTop + 8, 0, 0, this.imageWidth, this.imageHeight);
        graphics.drawTexture(this.indicator, this.guiLeft + 4, this.guiTop + 8, 56, 0, 200, this.imageHeight);
        if (this.playerFactions.isEmpty()) {
            MutableText noFaction = Text.translatable((String)"faction.nostanding");
            TextRenderer font = MinecraftClient.getInstance().textRenderer;
            graphics.drawTextWithShadow(font, (Text)noFaction, this.guiLeft + (this.imageWidth - font.getWidth((StringVisitable)noFaction)) / 2, this.guiTop + 80, CustomNpcResourceListener.DefaultTextColor);
        } else {
            this.renderScreen(graphics);
        }
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void renderScreen(DrawContext graphics) {
        int size = 5;
        if (this.playerFactions.size() % 5 != 0 && this.page == this.pages) {
            size = this.playerFactions.size() % 5;
        }
        for (int id = 0; id < size; ++id) {
            graphics.drawHorizontalLine(this.guiLeft + 2, this.guiLeft + this.imageWidth, this.guiTop + 14 + id * 30, -16777216 + CustomNpcResourceListener.DefaultTextColor);
            Faction faction = this.playerFactions.get((this.page - 1) * 5 + id);
            MutableText name = Text.translatable((String)faction.name);
            int current = this.data.factionData.get(faction.id);
            String points = " : " + current;
            MutableText standing = Text.translatable((String)"faction.friendly");
            int color = 65280;
            if (current < faction.neutralPoints) {
                standing = Text.translatable((String)"faction.unfriendly");
                color = 0xFF0000;
                points = points + "/" + faction.neutralPoints;
            } else if (current < faction.friendlyPoints) {
                standing = Text.translatable((String)"faction.neutral");
                color = 0xF2FF00;
                points = points + "/" + faction.friendlyPoints;
            } else {
                points = points + "/-";
            }
            graphics.drawTextWithShadow(this.textRenderer, (Text)name, this.guiLeft + (this.imageWidth - this.textRenderer.getWidth((StringVisitable)name)) / 2, this.guiTop + 19 + id * 30, faction.color);
            graphics.drawTextWithShadow(this.textRenderer, (Text)standing, this.width / 2 - this.textRenderer.getWidth((StringVisitable)standing) - 1, this.guiTop + 33 + id * 30, color);
            graphics.drawTextWithShadow(this.textRenderer, points, this.width / 2, this.guiTop + 33 + id * 30, CustomNpcResourceListener.DefaultTextColor);
        }
        graphics.drawHorizontalLine(this.guiLeft + 2, this.guiLeft + this.imageWidth, this.guiTop + 14 + size * 30, -16777216 + CustomNpcResourceListener.DefaultTextColor);
        if (this.pages > 1) {
            String s = this.page + "/" + this.pages;
            graphics.drawTextWithShadow(this.textRenderer, s, this.guiLeft + (this.imageWidth - this.textRenderer.getWidth(s)) / 2, this.guiTop + 203, CustomNpcResourceListener.DefaultTextColor);
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        if (!(guibutton instanceof GuiButtonNextPage)) {
            return;
        }
        int id = guibutton.id;
        if (id == 1) {
            ++this.page;
        }
        if (id == 2) {
            --this.page;
        }
        this.updateButtons();
    }

    private void updateButtons() {
        this.buttonNextPage.visible = this.page < this.pages;
        this.buttonPreviousPage.visible = this.page > 1;
    }

    @Override
    public void save() {
    }
}

