/*
 * Decompiled with CFR 0.152.
 */
package noppes.npcs.client.gui.roles;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.text.Text;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.SubGuiNpcTraderLimit;
import noppes.npcs.client.gui.SubGuiNpcTraderVariables;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface2;
import noppes.npcs.containers.ContainerNPCTraderSetup;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcMarketSet;
import noppes.npcs.packets.server.SPacketNpcRoleSave;
import noppes.npcs.roles.RoleTrader;
import noppes.npcs.roles.TraderSlotLimit;
import noppes.npcs.util.TraderLimitHelper;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiNpcTraderSetup
extends GuiContainerNPCInterface2<ContainerNPCTraderSetup>
implements ITextfieldListener {
    private final Identifier slot = new Identifier("customnpcs", "textures/gui/slot.png");
    private RoleTrader role;

    public GuiNpcTraderSetup(ContainerNPCTraderSetup container, PlayerInventory inv, Text titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn);
        this.backgroundHeight = 220;
        this.menuYOffset = 10;
        this.role = container.role;
    }

    @Override
    public void init() {
        super.init();
        this.drawables.clear();
        this.setBackground("tradersetup.png");
        this.addLabel(new GuiLabel(0, "role.marketname", this.guiLeft + 214, this.guiTop + 150));
        this.addTextField(new GuiTextFieldNop(0, (Screen)this, this.guiLeft + 214, this.guiTop + 160, 180, 20, this.role.marketName));
        int panelX = 290;
        int panelBtnX = 340;
        this.addLabel(new GuiLabel(1, "gui.ignoreDamage", this.guiLeft + panelX, this.guiTop + 29));
        this.addButton(new GuiButtonYesNo((IGuiInterface)this, 1, this.guiLeft + panelBtnX, this.guiTop + 24, this.role.ignoreDamage));
        this.addLabel(new GuiLabel(2, "gui.ignoreNBT", this.guiLeft + panelX, this.guiTop + 51));
        this.addButton(new GuiButtonYesNo((IGuiInterface)this, 2, this.guiLeft + panelBtnX, this.guiTop + 46, this.role.ignoreNBT));
        this.addButton(new GuiButtonNop(this, 3, this.guiLeft + panelX, this.guiTop + 74, 78, 20, "trader.var.title"));
        int limitBtnH = 16;
        for (int slot = 0; slot < 18; ++slot) {
            int x = this.guiLeft + slot % 3 * 94 + 7;
            int y = this.guiTop + slot / 3 * 22 + 4;
            this.addButton(new GuiButtonNop(this, 100 + slot, x + 61, y + 1 + limitBtnH / 2, 16, limitBtnH, "trader.limit.short"));
        }
        this.updateLimitButtons();
    }

    private void updateLimitButtons() {
        for (int slot = 0; slot < 18; ++slot) {
            GuiButtonNop button = this.getButton(100 + slot);
            if (button == null) {
                continue;
            }
            TraderSlotLimit limit = this.role.getSlotLimit(slot);
            button.setMessage(Text.literal(TraderLimitHelper.getLimitShortLabel(limit)));
        }
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        if (!this.hasSubGui()) {
            this.updateLimitButtons();
        }
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        this.guiTop += 10;
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.guiTop -= 10;
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        if (guibutton.id == 1) {
            this.role.ignoreDamage = ((GuiButtonYesNo)guibutton).getBoolean();
        }
        if (guibutton.id == 2) {
            this.role.ignoreNBT = ((GuiButtonYesNo)guibutton).getBoolean();
        }
        if (guibutton.id == 3) {
            this.setSubGui(new SubGuiNpcTraderVariables(this.role));
        }
        if (guibutton.id >= 100 && guibutton.id < 118) {
            this.setSubGui(new SubGuiNpcTraderLimit(this.role, guibutton.id - 100));
        }
    }

    @Override
    protected void drawBackground(DrawContext graphics, float partialTicks, int xMouse, int yMouse) {
        super.drawBackground(graphics, partialTicks, xMouse, yMouse);
        for (int slot = 0; slot < 18; ++slot) {
            int x = this.guiLeft + slot % 3 * 94 + 7;
            int y = this.guiTop + slot / 3 * 22 + 4;
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            RenderSystem.setShaderTexture((int)0, (Identifier)this.slot);
            graphics.drawTexture(this.slot, x - 1, y, 0, 0, 18, 18);
            graphics.drawTexture(this.slot, x + 17, y, 0, 0, 18, 18);
            graphics.drawTextWithShadow(this.textRenderer, "=", x + 36, y + 5, CustomNpcResourceListener.DefaultTextColor);
            RenderSystem.setShader(GameRenderer::getPositionTexProgram);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            RenderSystem.setShaderTexture((int)0, (Identifier)this.slot);
            graphics.drawTexture(this.slot, x + 42, y, 0, 0, 18, 18);
        }
    }

    @Override
    public void save() {
        Packets.sendServer(new SPacketNpcMarketSet(this.role.marketName, true));
        Packets.sendServer(new SPacketNpcRoleSave(this.role.save(new NbtCompound())));
    }

    @Override
    public void unFocused(GuiTextFieldNop guiNpcTextField) {
        String name = guiNpcTextField.getText();
        if (!name.equalsIgnoreCase(this.role.marketName)) {
            this.role.marketName = name;
            Packets.sendServer(new SPacketNpcMarketSet(this.role.marketName, false));
        }
    }
}
