/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.questtypes;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.global.GuiNPCManageQuest;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.containers.ContainerNpcQuestTypeItem;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.quests.QuestItem;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiNpcQuestTypeItem
extends GuiContainerNPCInterface<ContainerNpcQuestTypeItem>
implements ITextfieldListener {
    private Quest quest;
    private static final Identifier field_110422_t = new Identifier("customnpcs", "textures/gui/followersetup.png");

    public GuiNpcQuestTypeItem(ContainerNpcQuestTypeItem container, PlayerInventory inv, Text titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn);
        this.quest = NoppesUtilServer.getEditingQuest((PlayerEntity)this.player);
        this.title = "";
        this.backgroundHeight = 202;
        this.closeOnEsc = false;
    }

    @Override
    public void init() {
        super.init();
        this.addLabel(new GuiLabel(0, "quest.takeitems", this.guiLeft + 4, this.guiTop + 8));
        this.addButton(new GuiButtonNop((IGuiInterface)this, 0, this.guiLeft + 90, this.guiTop + 3, 60, 20, new String[]{"gui.yes", "gui.no"}, ((QuestItem)this.quest.questInterface).leaveItems ? 1 : 0));
        this.addLabel(new GuiLabel(1, "gui.ignoreDamage", this.guiLeft + 4, this.guiTop + 29));
        this.addButton(new GuiButtonYesNo((IGuiInterface)this, 1, this.guiLeft + 90, this.guiTop + 24, 50, 20, ((QuestItem)this.quest.questInterface).ignoreDamage));
        this.addLabel(new GuiLabel(2, "gui.ignoreNBT", this.guiLeft + 62, this.guiTop + 51));
        this.addButton(new GuiButtonYesNo((IGuiInterface)this, 2, this.guiLeft + 120, this.guiTop + 46, 50, 20, ((QuestItem)this.quest.questInterface).ignoreNBT));
        this.addButton(new GuiButtonNop(this, 5, this.guiLeft, this.guiTop + this.backgroundHeight, 98, 20, "gui.back"));
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        if (guibutton.id == 0) {
            boolean bl = ((QuestItem)this.quest.questInterface).leaveItems = guibutton.getValue() == 1;
        }
        if (guibutton.id == 1) {
            ((QuestItem)this.quest.questInterface).ignoreDamage = ((GuiButtonYesNo)guibutton).getBoolean();
        }
        if (guibutton.id == 2) {
            ((QuestItem)this.quest.questInterface).ignoreNBT = ((GuiButtonYesNo)guibutton).getBoolean();
        }
        if (guibutton.id == 5) {
            NoppesUtil.openGUI((PlayerEntity)this.player, GuiNPCManageQuest.Instance);
        }
    }

    @Override
    protected void drawBackground(DrawContext graphics, float partialTicks, int x, int y) {
        super.renderBackground(graphics);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)field_110422_t);
        int l = (this.width - this.backgroundWidth) / 2;
        int i1 = (this.height - this.backgroundHeight) / 2;
        graphics.drawTexture(field_110422_t, l, i1, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    public void save() {
    }

    @Override
    public void unFocused(GuiTextFieldNop textfield) {
        this.quest.rewardExp = textfield.getInteger();
    }
}

