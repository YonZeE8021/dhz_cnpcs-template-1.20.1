/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.item.ItemStack
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
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.containers.ContainerNPCFollowerHire;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketFollowerHire;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;

public class GuiNpcFollowerHire
extends GuiContainerNPCInterface<ContainerNPCFollowerHire> {
    private final Identifier resource = new Identifier("customnpcs", "textures/gui/followerhire.png");
    private ContainerNPCFollowerHire container;
    private RoleFollower role;

    public GuiNpcFollowerHire(ContainerNPCFollowerHire container, PlayerInventory inv, Text titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn);
        this.container = container;
        this.role = (RoleFollower)this.npc.role;
    }

    @Override
    public void init() {
        super.init();
        this.addButton(new GuiButtonNop(this, 5, this.guiLeft + 26, this.guiTop + 60, 50, 20, I18n.translate((String)"follower.hire", (Object[])new Object[0])));
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        if (guibutton.id == 5) {
            Packets.sendServer(new SPacketFollowerHire());
            this.close();
        }
    }

    @Override
    protected void drawForeground(DrawContext p_281635_, int p_282681_, int p_283686_) {
        super.drawForeground(p_281635_, p_282681_, p_283686_);
    }

    @Override
    protected void drawBackground(DrawContext graphics, float f, int i, int j) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)this.resource);
        int l = (this.width - this.backgroundWidth) / 2;
        int i1 = (this.height - this.backgroundHeight) / 2;
        graphics.drawTexture(this.resource, l, i1, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int index = 0;
        for (int slot = 0; slot < this.role.inventory.items.size(); ++slot) {
            ItemStack itemstack = (ItemStack)this.role.inventory.items.get(slot);
            if (NoppesUtilServer.IsItemStackNull(itemstack)) continue;
            int days = 1;
            if (this.role.rates.containsKey(slot)) {
                days = this.role.rates.get(slot);
            }
            int yOffset = index * 26;
            int x = this.guiLeft + 78;
            int y = this.guiTop + yOffset + 10;
            graphics.drawItem(itemstack, x + 11, y);
            graphics.drawItemInSlot(this.textRenderer, itemstack, x + 11, y);
            String daysS = days + " " + (days == 1 ? I18n.translate((String)"follower.day", (Object[])new Object[0]) : I18n.translate((String)"follower.days", (Object[])new Object[0]));
            graphics.drawTextWithShadow(this.textRenderer, " = " + daysS, x + 27, y + 4, CustomNpcResourceListener.DefaultTextColor);
            if (this.isPointWithinBounds(x - this.guiLeft + 11, y - this.guiTop, 16, 16, this.mouseX, this.mouseY)) {
                graphics.drawItemTooltip(this.textRenderer, itemstack, this.mouseX, this.mouseY);
            }
            ++index;
        }
    }

    @Override
    public void save() {
    }
}

