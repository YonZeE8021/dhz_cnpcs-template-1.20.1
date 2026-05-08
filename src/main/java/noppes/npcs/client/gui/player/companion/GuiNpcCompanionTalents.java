/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.block.Blocks
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.player.companion;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionStats;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCompanionOpenInv;
import noppes.npcs.packets.server.SPacketCompanionTalentExp;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.util.NoppesStringUtils;

public class GuiNpcCompanionTalents
extends GuiNPCInterface {
    private RoleCompanion role;
    private Map<Integer, GuiTalent> talents = new HashMap<Integer, GuiTalent>();
    private GuiButtonNop selected;
    private long lastPressedTime = 0L;
    private long startPressedTime = 0L;

    public GuiNpcCompanionTalents(EntityNPCInterface npc) {
        super(npc);
        this.role = (RoleCompanion)npc.role;
        this.setBackground("companion_empty.png");
        this.imageWidth = 171;
        this.imageHeight = 166;
    }

    @Override
    public void init() {
        super.init();
        this.talents = new HashMap<Integer, GuiTalent>();
        int y = this.guiTop + 12;
        this.addLabel(new GuiLabel(0, NoppesStringUtils.translate("quest.exp", ": "), this.guiLeft + 4, this.guiTop + 10));
        GuiNpcCompanionStats.addTopMenu(this.role, this, 2);
        int i = 0;
        for (EnumCompanionTalent e : this.role.talents.keySet()) {
            this.addTalent(i++, e);
        }
    }

    private void addTalent(int i, EnumCompanionTalent talent) {
        int y = this.guiTop + 28 + i / 2 * 26;
        int x = this.guiLeft + 4 + i % 2 * 84;
        GuiTalent gui = new GuiTalent(this.role, talent, x, y);
        gui.init(this.client, this.width, this.height);
        this.talents.put(i, gui);
        if (this.role.getTalentLevel(talent) < 5) {
            this.addButton(new GuiButtonNop(this, i + 10, x + 26, y, 14, 14, "+"));
            y += 8;
        }
        this.addLabel(new GuiLabel(i, String.valueOf(this.role.talents.get(talent)) + "/" + this.role.getNextLevel(talent), x + 26, y + 8));
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        int id = guibutton.id;
        if (id == 1) {
            CustomNpcs.proxy.openGui(this.npc, EnumGuiType.Companion);
        }
        if (id == 3) {
            Packets.sendServer(new SPacketCompanionOpenInv());
        }
        if (id >= 10) {
            this.selected = guibutton;
            this.lastPressedTime = this.startPressedTime = this.client.world.getTimeOfDay();
            this.addExperience(1);
        }
    }

    private void addExperience(int exp) {
        EnumCompanionTalent talent = this.talents.get(Integer.valueOf((int)(this.selected.id - 10))).talent;
        if (!this.role.canAddExp(-exp) && this.role.currentExp <= 0) {
            return;
        }
        if (exp > this.role.currentExp) {
            exp = this.role.currentExp;
        }
        Packets.sendServer(new SPacketCompanionTalentExp(talent, exp));
        this.role.talents.put(talent, this.role.talents.get(talent) + exp);
        this.role.addExp(-exp);
        this.getLabel(this.selected.id - 10).setMessage((Text)Text.literal((String)(String.valueOf(this.role.talents.get(talent)) + "/" + this.role.getNextLevel(talent))));
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (this.selected != null && this.client.world.getTimeOfDay() - this.startPressedTime > 4L && this.lastPressedTime < this.client.world.getTimeOfDay() && this.client.world.getTimeOfDay() % 4L == 0L) {
            if (this.selected.mouseClicked(mouseX, mouseY, 0)) {
                this.lastPressedTime = this.client.world.getTimeOfDay();
                if (this.lastPressedTime - this.startPressedTime < 20L) {
                    this.addExperience(1);
                } else if (this.lastPressedTime - this.startPressedTime < 40L) {
                    this.addExperience(2);
                } else if (this.lastPressedTime - this.startPressedTime < 60L) {
                    this.addExperience(4);
                } else if (this.lastPressedTime - this.startPressedTime < 90L) {
                    this.addExperience(8);
                } else if (this.lastPressedTime - this.startPressedTime < 140L) {
                    this.addExperience(14);
                } else {
                    this.addExperience(28);
                }
            } else {
                this.lastPressedTime = 0L;
                this.selected = null;
            }
        }
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)GuiNpcCompanionStats.GUI_ICONS_LOCATION);
        graphics.drawTexture(GuiNpcCompanionStats.GUI_ICONS_LOCATION, this.guiLeft + 4, this.guiTop + 20, 10, 64, 162, 5);
        if (this.role.currentExp > 0) {
            float v = 1.0f * (float)this.role.currentExp / (float)this.role.getMaxExp();
            if (v > 1.0f) {
                v = 1.0f;
            }
            graphics.drawTexture(GuiNpcCompanionStats.GUI_ICONS_LOCATION, this.guiLeft + 4, this.guiTop + 20, 10, 69, (int)(v * 162.0f), 5);
        }
        String s = this.role.currentExp + "\\" + this.role.getMaxExp();
        graphics.drawTextWithShadow(this.client.textRenderer, s, this.guiLeft + this.imageWidth / 2 - this.client.textRenderer.getWidth(s) / 2, this.guiTop + 10, CustomNpcResourceListener.DefaultTextColor);
        for (GuiTalent talent : this.talents.values()) {
            talent.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void save() {
    }

    public static class GuiTalent
    extends Screen {
        private EnumCompanionTalent talent;
        private int x;
        private int y;
        private RoleCompanion role;
        private static final Identifier resource = new Identifier("customnpcs:textures/gui/talent.png");

        public GuiTalent(RoleCompanion role, EnumCompanionTalent talent, int x, int y) {
            super((Text)Text.empty());
            this.talent = talent;
            this.x = x;
            this.y = y;
            this.role = role;
        }

        public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
            MinecraftClient mc = MinecraftClient.getInstance();
            MatrixStack matrixStack = graphics.getMatrices();
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            RenderSystem.setShaderTexture((int)0, (Identifier)resource);
            ItemStack item = this.talent.item;
            if (item.getItem() == null) {
                item = new ItemStack((ItemConvertible)Blocks.DIRT);
            }
            matrixStack.push();
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            RenderSystem.enableBlend();
            boolean hover = this.x < mouseX && this.x + 24 > mouseX && this.y < mouseY && this.y + 24 > mouseY;
            graphics.drawTexture(resource, this.x, this.y, 0, hover ? 24 : 0, 24, 24);
            graphics.getMatrices().push();
            graphics.getMatrices().translate(0.0f, 0.0f, 100.0f);
            graphics.drawItem(item, this.x + 4, this.y + 4);
            graphics.drawItemInSlot(mc.textRenderer, item, this.x + 4, this.y + 4);
            matrixStack.translate(0.0f, 0.0f, 200.0f);
            graphics.drawCenteredTextWithShadow(mc.textRenderer, "" + this.role.getTalentLevel(this.talent), this.x + 20, this.y + 16, 0xFFFFFF);
            graphics.getMatrices().pop();
            matrixStack.pop();
        }
    }
}

