/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.player;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.containers.ContainerNPCBankInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketBankUnlock;
import noppes.npcs.packets.server.SPacketBankUpgrade;
import noppes.npcs.packets.server.SPacketBanksSlotOpen;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class GuiNPCBankChest
extends GuiContainerNPCInterface<ContainerNPCBankInterface>
implements IGuiData {
    private final Identifier resource = new Identifier("customnpcs", "textures/gui/bankchest.png");
    private ContainerNPCBankInterface container;
    private int availableSlots = 0;
    private int maxSlots = 1;
    private int unlockedSlots = 1;
    private ItemStack currency;

    public GuiNPCBankChest(ContainerNPCBankInterface container, PlayerInventory inv, Text titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn);
        this.container = container;
        this.title = "";
        this.backgroundHeight = 235;
    }

    @Override
    public void init() {
        super.init();
        this.availableSlots = 0;
        if (this.maxSlots > 1) {
            for (int i = 0; i < this.maxSlots; ++i) {
                GuiButtonNop button = new GuiButtonNop(this, i, this.guiLeft - 50, this.guiTop + 10 + i * 24, 50, 20, I18n.translate((String)"gui.tab", (Object[])new Object[0]) + " " + (i + 1));
                if (i > this.unlockedSlots) {
                    button.setEnabled(false);
                }
                this.addButton(button);
                ++this.availableSlots;
            }
            if (this.availableSlots == 1) {
                this.drawables.clear();
            }
        }
        if (!this.container.isAvailable()) {
            this.addButton(new GuiButtonNop(this, 8, this.guiLeft + 48, this.guiTop + 48, 80, 20, I18n.translate((String)"bank.unlock", (Object[])new Object[0])));
        } else if (this.container.canBeUpgraded()) {
            this.addButton(new GuiButtonNop(this, 9, this.guiLeft + 48, this.guiTop + 48, 80, 20, I18n.translate((String)"bank.upgrade", (Object[])new Object[0])));
        }
        if (this.maxSlots > 1) {
            this.getButton((int)this.container.slot).visible = false;
            this.getButton(this.container.slot).setEnabled(false);
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        int id = guibutton.id;
        if (id < 6) {
            Packets.sendServer(new SPacketBanksSlotOpen(id, this.container.bankid));
        }
        if (id == 8) {
            Packets.sendServer(new SPacketBankUnlock());
        }
        if (id == 9) {
            Packets.sendServer(new SPacketBankUpgrade());
        }
    }

    @Override
    protected void drawBackground(DrawContext graphics, float partialTicks, int x, int y) {
        super.drawBackground(graphics, partialTicks, x, y);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)this.resource);
        int l = (this.width - this.backgroundWidth) / 2;
        int i1 = (this.height - this.backgroundHeight) / 2;
        graphics.drawTexture(this.resource, l, i1, 0, 0, this.backgroundWidth, 6);
        if (!this.container.isAvailable()) {
            graphics.drawTexture(this.resource, l, i1 + 6, 0, 6, this.backgroundWidth, 64);
            graphics.drawTexture(this.resource, l, i1 + 70, 0, 124, this.backgroundWidth, 98);
            int i = this.guiLeft + 30;
            int j = this.guiTop + 8;
            graphics.drawTextWithShadow(this.textRenderer, I18n.translate((String)"bank.unlockCosts", (Object[])new Object[0]) + ":", i, j + 4, CustomNpcResourceListener.DefaultTextColor);
            this.drawItem(graphics, i + 90, j, this.currency, x, y);
        } else if (this.container.isUpgraded()) {
            graphics.drawTexture(this.resource, l, i1 + 60, 0, 60, this.backgroundWidth, 162);
            graphics.drawTexture(this.resource, l, i1 + 6, 0, 60, this.backgroundWidth, 64);
        } else if (this.container.canBeUpgraded()) {
            graphics.drawTexture(this.resource, l, i1 + 6, 0, 6, this.backgroundWidth, 216);
            int i = this.guiLeft + 30;
            int j = this.guiTop + 8;
            graphics.drawTextWithShadow(this.textRenderer, I18n.translate((String)"bank.upgradeCosts", (Object[])new Object[0]) + ":", i, j + 4, CustomNpcResourceListener.DefaultTextColor);
            this.drawItem(graphics, i + 90, j, this.currency, x, y);
        } else {
            graphics.drawTexture(this.resource, l, i1 + 6, 0, 60, this.backgroundWidth, 162);
        }
        if (this.maxSlots > 1) {
            for (int ii = 0; ii < this.maxSlots && this.availableSlots != ii; ++ii) {
                graphics.drawTextWithShadow(this.textRenderer, "Tab " + (ii + 1), this.guiLeft - 40, this.guiTop + 16 + ii * 24, 0xFFFFFF);
            }
        }
    }

    private void drawItem(DrawContext graphics, int x, int y, ItemStack item, int mouseX, int mouseY) {
        if (NoppesUtilServer.IsItemStackNull(item)) {
            return;
        }
        graphics.drawItem(item, x, y);
        graphics.drawItemInSlot(this.textRenderer, item, x, y);
        if (this.isPointWithinBounds(x - this.guiLeft, y - this.guiTop, 16, 16, mouseX, mouseY)) {
            graphics.drawItemTooltip(this.textRenderer, item, mouseX, mouseY);
        }
    }

    @Override
    public void save() {
    }

    @Override
    public void setGuiData(NbtCompound compound) {
        this.maxSlots = compound.getInt("MaxSlots");
        this.unlockedSlots = compound.getInt("UnlockedSlots");
        this.currency = compound.contains("Currency") ? ItemStack.fromNbt((NbtCompound)compound.getCompound("Currency")) : ItemStack.EMPTY;
        if (this.container.currency != null) {
            this.container.currency.item = this.currency;
        }
        this.init();
    }
}

