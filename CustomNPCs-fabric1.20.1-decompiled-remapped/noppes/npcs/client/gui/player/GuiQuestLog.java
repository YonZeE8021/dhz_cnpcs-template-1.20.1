/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.gui.Element
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.api.handler.data.IQuestObjective;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.gui.player.tabs.InventoryTabFactions;
import noppes.npcs.client.gui.player.tabs.InventoryTabQuests;
import noppes.npcs.client.gui.player.tabs.InventoryTabVanilla;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.shared.client.gui.components.GuiButtonNextPage;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiMenuSideButton;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.gui.listeners.ITopButtonListener;
import noppes.npcs.shared.common.util.NaturalOrderComparator;

public class GuiQuestLog
extends GuiNPCInterface
implements ITopButtonListener,
ICustomScrollListener {
    private final Identifier resource = new Identifier("customnpcs", "textures/gui/standardbg.png");
    public HashMap<String, List<Quest>> activeQuests = new HashMap();
    private HashMap<String, Quest> categoryQuests = new HashMap();
    public Quest selectedQuest = null;
    public Text selectedCategory = Text.empty();
    private PlayerEntity player;
    private GuiCustomScrollNop scroll;
    private HashMap<Integer, GuiMenuSideButton> sideButtons = new HashMap();
    private boolean noQuests = false;
    private final int maxLines = 10;
    private int currentPage = 0;
    private int maxPages = 1;
    TextBlockClient textblock = null;
    private MinecraftClient mc = MinecraftClient.getInstance();

    public GuiQuestLog(PlayerEntity player) {
        this.player = player;
        this.imageWidth = 280;
        this.imageHeight = 180;
        this.drawDefaultBackground = false;
    }

    @Override
    public void init() {
        super.init();
        for (Quest quest : PlayerQuestController.getActiveQuests(this.player)) {
            String category = quest.category.title;
            if (!this.activeQuests.containsKey(category)) {
                this.activeQuests.put(category, new ArrayList());
            }
            List<Quest> list = this.activeQuests.get(category);
            list.add(quest);
        }
        this.sideButtons.clear();
        this.guiTop += 10;
        this.addDrawableChild((Element)new InventoryTabVanilla().init(this));
        this.addDrawableChild((Element)new InventoryTabFactions().init(this));
        this.addDrawableChild((Element)new InventoryTabQuests().init(this));
        this.noQuests = false;
        if (this.activeQuests.isEmpty()) {
            this.noQuests = true;
            return;
        }
        ArrayList<String> categories = new ArrayList<String>();
        categories.addAll(this.activeQuests.keySet());
        Collections.sort(categories, new NaturalOrderComparator());
        int i = 0;
        for (String category : categories) {
            if (Objects.equals(this.selectedCategory, Text.empty())) {
                this.selectedCategory = Text.translatable((String)category);
            }
            this.sideButtons.put(i, new GuiMenuSideButton(this, i, this.guiLeft - 69, this.guiTop + 2 + i * 21, 70, 22, category));
            ++i;
        }
        this.sideButtons.get((Object)Integer.valueOf((int)categories.indexOf((Object)this.selectedCategory.getString()))).active = true;
        if (this.scroll == null) {
            this.scroll = new GuiCustomScrollNop(this, 0);
        }
        HashMap<String, Quest> categoryQuests = new HashMap<String, Quest>();
        for (Quest q : this.activeQuests.get(this.selectedCategory.getString())) {
            categoryQuests.put(q.title, q);
        }
        this.categoryQuests = categoryQuests;
        this.scroll.setList(new ArrayList<String>(categoryQuests.keySet()));
        this.scroll.setSize(134, 174);
        this.scroll.guiLeft = this.guiLeft + 5;
        this.scroll.guiTop = this.guiTop + 15;
        this.addScroll(this.scroll);
        this.addButton(new GuiButtonNextPage((IGuiInterface)this, 1, this.guiLeft + 286, this.guiTop + 114, true, b -> {
            ++this.currentPage;
            this.init();
        }));
        this.addButton(new GuiButtonNextPage((IGuiInterface)this, 2, this.guiLeft + 144, this.guiTop + 114, false, b -> {
            --this.currentPage;
            this.init();
        }));
        this.getButton((int)1).visible = this.selectedQuest != null && this.currentPage < this.maxPages - 1;
        this.getButton((int)2).visible = this.selectedQuest != null && this.currentPage > 0;
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        if (this.scroll != null) {
            this.scroll.visible = !this.noQuests;
        }
        MatrixStack matrixStack = graphics.getMatrices();
        this.renderBackground(graphics);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)this.resource);
        graphics.drawTexture(this.resource, this.guiLeft, this.guiTop, 0, 0, 252, 195);
        graphics.drawTexture(this.resource, this.guiLeft + 252, this.guiTop, 188, 0, 67, 195);
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (this.noQuests) {
            graphics.drawText(this.mc.textRenderer, (Text)Text.translatable((String)"quest.noquests"), this.guiLeft + 84, this.guiTop + 80, CustomNpcResourceListener.DefaultTextColor, false);
            return;
        }
        for (GuiMenuSideButton button : this.sideButtons.values().toArray(new GuiMenuSideButton[this.sideButtons.size()])) {
            button.render(graphics, mouseX, mouseY, partialTicks);
        }
        graphics.drawText(this.mc.textRenderer, this.selectedCategory, this.guiLeft + 5, this.guiTop + 5, CustomNpcResourceListener.DefaultTextColor, false);
        if (this.selectedQuest == null) {
            return;
        }
        this.drawProgress(graphics);
        this.drawQuestText(graphics);
        matrixStack.push();
        matrixStack.translate((float)(this.guiLeft + 148), (float)this.guiTop, 0.0f);
        matrixStack.scale(1.24f, 1.24f, 1.24f);
        MutableText title = Text.translatable((String)this.selectedQuest.title);
        graphics.drawText(this.mc.textRenderer, (Text)title, (130 - this.textRenderer.getWidth((StringVisitable)title)) / 2, 4, CustomNpcResourceListener.DefaultTextColor, false);
        matrixStack.pop();
        graphics.drawHorizontalLine(this.guiLeft + 142, this.guiLeft + 312, this.guiTop + 17, -16777216 + CustomNpcResourceListener.DefaultTextColor);
    }

    private void drawQuestText(DrawContext graphics) {
        if (this.textblock == null) {
            return;
        }
        int yoffset = this.guiTop + 5;
        for (int i = 0; i < 10; ++i) {
            int index = i + this.currentPage * 10;
            if (index >= this.textblock.lines.size()) continue;
            Text text = (Text)this.textblock.lines.get(index);
            Objects.requireNonNull(this.textRenderer);
            graphics.drawText(this.textRenderer, text, this.guiLeft + 142, this.guiTop + 20 + i * 9, CustomNpcResourceListener.DefaultTextColor, false);
        }
    }

    private void drawProgress(DrawContext graphics) {
        MutableText title = Text.translatable((String)"quest.objectives").append(":");
        graphics.drawText(this.mc.textRenderer, (Text)title, this.guiLeft + 142, this.guiTop + 130, CustomNpcResourceListener.DefaultTextColor, false);
        graphics.drawHorizontalLine(this.guiLeft + 142, this.guiLeft + 312, this.guiTop + 140, -16777216 + CustomNpcResourceListener.DefaultTextColor);
        int yoffset = this.guiTop + 144;
        for (IQuestObjective objective : this.selectedQuest.questInterface.getObjectives(this.player)) {
            graphics.drawText(this.mc.textRenderer, (Text)Text.literal((String)"- ").append(objective.getMCText()), this.guiLeft + 142, yoffset, CustomNpcResourceListener.DefaultTextColor, false);
            yoffset += 10;
        }
        graphics.drawHorizontalLine(this.guiLeft + 142, this.guiLeft + 312, this.guiTop + 178, -16777216 + CustomNpcResourceListener.DefaultTextColor);
        String complete = this.selectedQuest.getNpcName();
        if (complete != null && !complete.isEmpty()) {
            graphics.drawText(this.mc.textRenderer, (Text)Text.translatable((String)"quest.completewith", (Object[])new Object[]{complete}), this.guiLeft + 142, this.guiTop + 182, CustomNpcResourceListener.DefaultTextColor, false);
        }
    }

    @Override
    public boolean mouseClicked(double i, double j, int k) {
        super.mouseClicked(i, j, k);
        if (k == 0) {
            if (this.scroll != null) {
                this.scroll.mouseClicked(i, j, k);
            }
            for (GuiMenuSideButton button : new ArrayList<GuiMenuSideButton>(this.sideButtons.values())) {
                if (!button.mouseClicked(i, j, k)) continue;
                this.sideButtonPressed(button);
                return true;
            }
        }
        return false;
    }

    private void sideButtonPressed(GuiMenuSideButton button) {
        if (button.active) {
            return;
        }
        NoppesUtil.clickSound();
        this.selectedCategory = button.getMessage();
        this.selectedQuest = null;
        this.init();
    }

    @Override
    public void scrollClicked(double i, double j, int k, GuiCustomScrollNop scroll) {
        if (!scroll.hasSelected()) {
            return;
        }
        this.selectedQuest = this.categoryQuests.get(scroll.getSelected());
        this.textblock = new TextBlockClient(this.selectedQuest.getLogText(), 172, true, this.player);
        if (this.textblock.lines.size() > 10) {
            this.maxPages = MathHelper.ceil((float)(1.0f * (float)this.textblock.lines.size() / 10.0f));
        }
        this.currentPage = 0;
        this.init();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void save() {
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScrollNop scroll) {
    }
}

