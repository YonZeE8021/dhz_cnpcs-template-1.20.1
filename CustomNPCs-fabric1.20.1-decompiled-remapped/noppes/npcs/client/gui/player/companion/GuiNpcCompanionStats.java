/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.block.Blocks
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.player.companion;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemConvertible;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCompanionOpenInv;
import noppes.npcs.packets.server.SPacketNpcRoleGet;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiBasicContainer;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiMenuTopIconButton;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.util.NoppesStringUtils;

public class GuiNpcCompanionStats
extends GuiNPCInterface
implements IGuiData {
    private RoleCompanion role;
    private boolean isEating = false;
    public static final Identifier GUI_ICONS_LOCATION = new Identifier("textures/gui/icons.png");

    public GuiNpcCompanionStats(EntityNPCInterface npc) {
        super(npc);
        this.role = (RoleCompanion)npc.role;
        this.setBackground("companion.png");
        this.imageWidth = 171;
        this.imageHeight = 166;
        Packets.sendServer(new SPacketNpcRoleGet());
    }

    @Override
    public void init() {
        super.init();
        int y = this.guiTop + 10;
        this.addLabel(new GuiLabel(0, NoppesStringUtils.translate("gui.name", ": ", this.npc.display.getName()), this.guiLeft + 4, y));
        this.addLabel(new GuiLabel(1, NoppesStringUtils.translate("companion.owner", ": ", this.role.ownerName), this.guiLeft + 4, y += 12));
        this.addLabel(new GuiLabel(2, NoppesStringUtils.translate("companion.age", ": ", this.role.ticksActive / 18000L + " (", this.role.stage.name, ")"), this.guiLeft + 4, y += 12));
        this.addLabel(new GuiLabel(3, NoppesStringUtils.translate("companion.strength", ": ", this.npc.stats.melee.getStrength()), this.guiLeft + 4, y += 12));
        this.addLabel(new GuiLabel(4, NoppesStringUtils.translate("companion.level", ": ", this.role.getTotalLevel()), this.guiLeft + 4, y += 12));
        this.addLabel(new GuiLabel(5, NoppesStringUtils.translate("job.name", ": ", "gui.none"), this.guiLeft + 4, y += 12));
        GuiNpcCompanionStats.addTopMenu(this.role, this, 1);
    }

    public static void addTopMenu(RoleCompanion role, Screen screen, int active) {
        GuiMenuTopIconButton button;
        IGuiInterface gui;
        if (screen instanceof GuiNPCInterface) {
            gui = (GuiNPCInterface)screen;
            button = new GuiMenuTopIconButton(gui, 1, ((GuiNPCInterface)gui).guiLeft + 4, ((GuiNPCInterface)gui).guiTop - 27, "menu.stats", new ItemStack((ItemConvertible)Items.field_8529));
            ((GuiBasic)gui).addTopButton(button);
            button = new GuiMenuTopIconButton(gui, 2, button, "companion.talent", new ItemStack((ItemConvertible)Items.field_8137));
            ((GuiBasic)gui).addTopButton(button);
            if (role.hasInv()) {
                button = new GuiMenuTopIconButton(gui, 3, button, "inv.inventory", new ItemStack((ItemConvertible)Blocks.field_10034));
                ((GuiBasic)gui).addTopButton(button);
            }
            if (role.companionJobInterface.getType() != EnumCompanionJobs.NONE) {
                ((GuiBasic)gui).addTopButton(new GuiMenuTopIconButton(gui, 4, button, "job.name", new ItemStack((ItemConvertible)Items.field_8179)));
            }
            ((GuiBasic)gui).getTopButton((int)active).active = true;
        }
        if (screen instanceof GuiContainerNPCInterface) {
            gui = (GuiContainerNPCInterface)screen;
            button = new GuiMenuTopIconButton(gui, 1, ((GuiContainerNPCInterface)gui).guiLeft + 4, ((GuiContainerNPCInterface)gui).guiTop - 27, "menu.stats", new ItemStack((ItemConvertible)Items.field_8529));
            ((GuiBasicContainer)gui).addTopButton(button);
            button = new GuiMenuTopIconButton(gui, 2, button, "companion.talent", new ItemStack((ItemConvertible)Items.field_8137));
            ((GuiBasicContainer)gui).addTopButton(button);
            if (role.hasInv()) {
                button = new GuiMenuTopIconButton(gui, 3, button, "inv.inventory", new ItemStack((ItemConvertible)Blocks.field_10034));
                ((GuiBasicContainer)gui).addTopButton(button);
            }
            if (role.companionJobInterface.getType() != EnumCompanionJobs.NONE) {
                ((GuiBasicContainer)gui).addTopButton(new GuiMenuTopIconButton(gui, 4, button, "job.name", new ItemStack((ItemConvertible)Items.field_8179)));
            }
            ((GuiBasicContainer)gui).getTopButton((int)active).active = true;
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        int id = guibutton.id;
        if (id == 2) {
            CustomNpcs.proxy.openGui(this.npc, EnumGuiType.CompanionTalent);
        }
        if (id == 3) {
            Packets.sendServer(new SPacketCompanionOpenInv());
        }
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (this.isEating && !this.role.isEating()) {
            Packets.sendServer(new SPacketNpcRoleGet());
        }
        this.isEating = this.role.isEating();
        super.drawNpc(graphics, 34, 150);
        int y = this.drawHealth(graphics, this.guiTop + 88);
    }

    private int drawHealth(DrawContext graphics, int y) {
        int x;
        int i;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)GUI_ICONS_LOCATION);
        int max = this.role.getTotalArmorValue();
        if (this.role.talents.containsKey((Object)EnumCompanionTalent.ARMOR) || max > 0) {
            for (int i2 = 0; i2 < 10; ++i2) {
                int x2 = this.guiLeft + 66 + i2 * 10;
                if (i2 * 2 + 1 < max) {
                    graphics.drawTexture(GUI_ICONS_LOCATION, x2, y, 34, 9, 9, 9);
                }
                if (i2 * 2 + 1 == max) {
                    graphics.drawTexture(GUI_ICONS_LOCATION, x2, y, 25, 9, 9, 9);
                }
                if (i2 * 2 + 1 <= max) continue;
                graphics.drawTexture(GUI_ICONS_LOCATION, x2, y, 16, 9, 9, 9);
            }
            y += 10;
        }
        max = MathHelper.ceil((float)this.npc.getMaxHealth());
        int k = (int)this.npc.getHealth();
        float scale = 1.0f;
        if (max > 40) {
            scale = (float)max / 40.0f;
            k = (int)((float)k / scale);
            max = 40;
        }
        for (i = 0; i < max; ++i) {
            x = this.guiLeft + 66 + i % 20 * 5;
            int offset = i / 20 * 10;
            graphics.drawTexture(GUI_ICONS_LOCATION, x, y + offset, 52 + i % 2 * 5, 9, i % 2 == 1 ? 4 : 5, 9);
            if (k <= i) continue;
            graphics.drawTexture(GUI_ICONS_LOCATION, x, y + offset, 52 + i % 2 * 5, 0, i % 2 == 1 ? 4 : 5, 9);
        }
        k = this.role.foodstats.getFoodLevel();
        y += 10;
        if (max > 20) {
            y += 10;
        }
        for (i = 0; i < 20; ++i) {
            x = this.guiLeft + 66 + i % 20 * 5;
            graphics.drawTexture(GUI_ICONS_LOCATION, x, y, 16 + i % 2 * 5, 27, i % 2 == 1 ? 4 : 5, 9);
            if (k <= i) continue;
            graphics.drawTexture(GUI_ICONS_LOCATION, x, y, 52 + i % 2 * 5, 27, i % 2 == 1 ? 4 : 5, 9);
        }
        return y;
    }

    @Override
    public void save() {
    }

    @Override
    public void setGuiData(NbtCompound compound) {
        this.role.load(compound);
    }
}

