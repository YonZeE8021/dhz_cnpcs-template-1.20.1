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
import noppes.npcs.containers.ContainerNPCFollower;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketFollowerExtend;
import noppes.npcs.packets.server.SPacketFollowerState;
import noppes.npcs.packets.server.SPacketNpcRoleGet;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiNpcFollower
extends GuiContainerNPCInterface<ContainerNPCFollower>
implements IGuiData {
    private final Identifier resource = new Identifier("customnpcs", "textures/gui/follower.png");
    private RoleFollower role;

    public GuiNpcFollower(ContainerNPCFollower container, PlayerInventory inv, Text titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn);
        this.role = (RoleFollower)this.npc.role;
        Packets.sendServer(new SPacketNpcRoleGet());
    }

    @Override
    public void init() {
        super.init();
        this.drawables.clear();
        this.addButton(new GuiButtonNop((IGuiInterface)this, 4, this.guiLeft + 100, this.guiTop + 110, 50, 20, new String[]{I18n.translate((String)"follower.waiting", (Object[])new Object[0]), I18n.translate((String)"follower.following", (Object[])new Object[0])}, this.role.isFollowing ? 1 : 0));
        if (!this.role.infiniteDays) {
            this.addButton(new GuiButtonNop(this, 5, this.guiLeft + 8, this.guiTop + 30, 50, 20, I18n.translate((String)"follower.hire", (Object[])new Object[0])));
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        int id = guibutton.id;
        if (id == 4) {
            Packets.sendServer(new SPacketFollowerState());
        }
        if (id == 5) {
            Packets.sendServer(new SPacketFollowerExtend());
        }
    }

    @Override
    protected void drawForeground(DrawContext graphics, int x, int y) {
        graphics.drawTextWithShadow(this.textRenderer, I18n.translate((String)"follower.health", (Object[])new Object[0]) + ": " + this.npc.getHealth() + "/" + this.npc.getMaxHealth(), 62, 70, CustomNpcResourceListener.DefaultTextColor);
        if (!this.role.infiniteDays) {
            if (this.role.getDays() <= 1) {
                graphics.drawTextWithShadow(this.textRenderer, I18n.translate((String)"follower.daysleft", (Object[])new Object[0]) + ": " + I18n.translate((String)"follower.lastday", (Object[])new Object[0]), 62, 94, CustomNpcResourceListener.DefaultTextColor);
            } else {
                graphics.drawTextWithShadow(this.textRenderer, I18n.translate((String)"follower.daysleft", (Object[])new Object[0]) + ": " + (this.role.getDays() - 1), 62, 94, CustomNpcResourceListener.DefaultTextColor);
            }
        }
    }

    @Override
    protected void drawBackground(DrawContext graphics, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)this.resource);
        int l = this.guiLeft;
        int i1 = this.guiTop;
        graphics.drawTexture(this.resource, l, i1, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int index = 0;
        if (!this.role.infiniteDays) {
            for (int slot = 0; slot < this.role.inventory.items.size(); ++slot) {
                ItemStack itemstack = (ItemStack)this.role.inventory.items.get(slot);
                if (NoppesUtilServer.IsItemStackNull(itemstack)) continue;
                int days = 1;
                if (this.role.rates.containsKey(slot)) {
                    days = this.role.rates.get(slot);
                }
                int yOffset = index * 20;
                int i = this.guiLeft + 68;
                int j = this.guiTop + yOffset + 4;
                graphics.drawItem(itemstack, x + 11, y);
                graphics.drawItemInSlot(this.textRenderer, itemstack, x + 11, y);
                String daysS = days + " " + (days == 1 ? I18n.translate((String)"follower.day", (Object[])new Object[0]) : I18n.translate((String)"follower.days", (Object[])new Object[0]));
                graphics.drawTextWithShadow(this.textRenderer, " = " + daysS, i + 27, j + 4, CustomNpcResourceListener.DefaultTextColor);
                if (this.isPointWithinBounds(i - this.guiLeft + 11, j - this.guiTop, 16, 16, this.mouseX, this.mouseY)) {
                    graphics.drawItemTooltip(this.textRenderer, itemstack, this.mouseX, this.mouseY);
                }
                ++index;
            }
        }
        this.drawNpc(graphics, 33, 131);
    }

    @Override
    public void save() {
    }

    @Override
    public void setGuiData(NbtCompound compound) {
        this.npc.role.load(compound);
        this.init();
    }
}

