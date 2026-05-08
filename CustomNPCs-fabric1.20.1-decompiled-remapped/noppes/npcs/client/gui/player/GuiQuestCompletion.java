/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.TextBlockClient;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketQuestCompletionCheck;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.listeners.ITopButtonListener;

public class GuiQuestCompletion
extends GuiNPCInterface
implements ITopButtonListener {
    private IQuest quest;
    private final Identifier resource = new Identifier("customnpcs", "textures/gui/smallbg.png");

    public GuiQuestCompletion(IQuest quest) {
        this.imageWidth = 176;
        this.imageHeight = 222;
        this.quest = quest;
        this.drawDefaultBackground = false;
        this.title = "";
        this.closeOnEsc = false;
    }

    @Override
    public void init() {
        super.init();
        String questTitle = I18n.translate((String)this.quest.getName(), (Object[])new Object[0]);
        int left = (this.imageWidth - this.textRenderer.getWidth(questTitle)) / 2;
        this.addLabel(new GuiLabel(0, questTitle, this.guiLeft + left, this.guiTop + 4));
        this.addButton(new GuiButtonNop(this, 0, this.guiLeft + 38, this.guiTop + this.imageHeight - 24, 100, 20, I18n.translate((String)"quest.complete", (Object[])new Object[0])));
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)this.resource);
        graphics.drawTexture(this.resource, this.guiLeft, this.guiTop, 0, 0, this.imageWidth, this.imageHeight);
        graphics.drawHorizontalLine(this.guiLeft + 4, this.guiLeft + 170, this.guiTop + 13, -16777216 + CustomNpcResourceListener.DefaultTextColor);
        this.drawQuestText(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void drawQuestText(DrawContext graphics) {
        int xoffset = this.guiLeft + 4;
        TextBlockClient block = new TextBlockClient(this.quest.getCompleteText(), 172, true, this.player);
        int yoffset = this.guiTop + 20;
        for (int i = 0; i < block.lines.size(); ++i) {
            String text = ((Text)block.lines.get(i)).getString();
            Objects.requireNonNull(this.textRenderer);
            graphics.drawText(this.textRenderer, text, this.guiLeft + 4, this.guiTop + 16 + i * 9, CustomNpcResourceListener.DefaultTextColor, false);
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        if (guibutton.id == 0) {
            Packets.sendServer(new SPacketQuestCompletionCheck(this.quest.getId()));
            this.close();
        }
    }

    @Override
    public void save() {
    }
}

