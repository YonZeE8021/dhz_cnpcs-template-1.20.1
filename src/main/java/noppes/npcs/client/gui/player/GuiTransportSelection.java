/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import java.util.Vector;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketPlayerTransport;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.listeners.IScrollData;
import noppes.npcs.shared.client.gui.listeners.ITopButtonListener;

public class GuiTransportSelection
extends GuiNPCInterface
implements ITopButtonListener,
IScrollData {
    private final Identifier resource = new Identifier("customnpcs", "textures/gui/smallbg.png");
    protected int imageWidth = 176;
    protected int guiLeft;
    protected int guiTop;
    private GuiCustomScrollNop scroll;

    public GuiTransportSelection(EntityNPCInterface npc) {
        super(npc);
        this.drawDefaultBackground = false;
        this.title = "";
    }

    @Override
    public void init() {
        super.init();
        this.guiLeft = (this.width - this.imageWidth) / 2;
        this.guiTop = (this.height - 222) / 2;
        String name = "";
        this.addLabel(new GuiLabel(0, name, this.guiLeft + (this.imageWidth - this.textRenderer.getWidth(name)) / 2, this.guiTop + 10));
        this.addButton(new GuiButtonNop(this, 0, this.guiLeft + 10, this.guiTop + 192, 156, 20, I18n.translate((String)"transporter.travel", (Object[])new Object[0])));
        if (this.scroll == null) {
            this.scroll = new GuiCustomScrollNop(this, 0);
        }
        this.scroll.setSize(156, 165);
        this.scroll.guiLeft = this.guiLeft + 10;
        this.scroll.guiTop = this.guiTop + 20;
        this.addScroll(this.scroll);
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)this.resource);
        graphics.drawTexture(this.resource, this.guiLeft, this.guiTop, 0, 0, 176, 222);
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        GuiButtonNop button = guibutton;
        String sel = this.scroll.getSelected();
        if (button.id == 0 && sel != null) {
            Packets.sendServer(new SPacketPlayerTransport(sel));
            this.close();
        }
    }

    @Override
    public boolean mouseClicked(double i, double j, int k) {
        super.mouseClicked(i, j, k);
        this.scroll.mouseClicked(i, j, k);
        return true;
    }

    @Override
    public void save() {
    }

    @Override
    public void setData(Vector<String> list, Map<String, Integer> data) {
        this.scroll.setList(list);
    }

    @Override
    public void setSelected(String selected) {
    }
}

