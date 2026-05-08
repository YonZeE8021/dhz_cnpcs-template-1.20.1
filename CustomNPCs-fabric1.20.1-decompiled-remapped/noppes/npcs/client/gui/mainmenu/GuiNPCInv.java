/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.mainmenu;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface2;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.containers.ContainerNPCInv;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuGet;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiNPCInv
extends GuiContainerNPCInterface2<ContainerNPCInv>
implements ITextfieldListener,
IGuiData {
    private HashMap<Integer, Float> chances = new HashMap();
    private ContainerNPCInv container;
    private Identifier slot;

    public GuiNPCInv(ContainerNPCInv container, PlayerInventory inv, Text titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn, 4);
        this.setBackground("npcinv.png");
        this.container = container;
        this.backgroundHeight = 200;
        this.slot = this.getResource("slot.png");
        Packets.sendServer(new SPacketMenuGet(EnumMenuType.INVENTORY));
    }

    @Override
    public void init() {
        super.init();
        this.addLabel(new GuiLabel(0, "inv.minExp", this.guiLeft + 118, this.guiTop + 18, "guihint.minXP"));
        this.addTextField(new GuiTextFieldNop(0, (Screen)this, this.guiLeft + 108, this.guiTop + 29, 60, 20, "" + this.npc.inventory.getExpMin()));
        this.getTextField((int)0).numbersOnly = true;
        this.getTextField(0).setMinMaxDefault(0, Short.MAX_VALUE, 0);
        this.addLabel(new GuiLabel(1, "inv.maxExp", this.guiLeft + 118, this.guiTop + 52, "guihint.maxXP"));
        this.addTextField(new GuiTextFieldNop(1, (Screen)this, this.guiLeft + 108, this.guiTop + 63, 60, 20, "" + this.npc.inventory.getExpMax()));
        this.getTextField((int)1).numbersOnly = true;
        this.getTextField(1).setMinMaxDefault(0, Short.MAX_VALUE, 0);
        this.addButton(new GuiButtonNop((IGuiInterface)this, 10, this.guiLeft + 88, this.guiTop + 88, 80, 20, new String[]{"stats.normal", "inv.auto"}, this.npc.inventory.lootMode));
        this.addLabel(new GuiLabel(2, "inv.npcInventory", this.guiLeft + 191, this.guiTop + 5));
        this.addLabel(new GuiLabel(3, "inv.inventory", this.guiLeft + 8, this.guiTop + 101));
        for (int i = 0; i < 9; ++i) {
            float chance = 100.0f;
            if (this.npc.inventory.dropchance.containsKey(i)) {
                chance = this.npc.inventory.dropchance.get(i).floatValue();
            }
            if (chance <= 0.0f || chance > 100.0f) {
                chance = 100.0f;
            }
            this.chances.put(i, Float.valueOf(chance));
            this.addLabel(new GuiLabel(5 + i, "inv.dropChance", this.guiLeft + 211, this.guiTop + 19 + i * 21));
            this.addTextField(new GuiTextFieldNop(2 + i, (Screen)this, this.guiLeft + 301, this.guiTop + 14 + i * 21, 60, 20, "" + chance).setFloatsOnly().setMinMaxDefault(0.0f, 100.0f, 100.0f));
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        if (guibutton.id == 10) {
            this.npc.inventory.lootMode = guibutton.getValue();
        }
    }

    @Override
    protected void drawBackground(DrawContext graphics, float partialTicks, int x, int y) {
        super.drawBackground(graphics, partialTicks, x, y);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)this.slot);
        for (int id = 4; id <= 6; ++id) {
            Slot slot = this.container.getSlot(id);
            if (!slot.hasStack()) continue;
            graphics.drawTexture(this.slot, this.guiLeft + slot.x - 1, this.guiTop + slot.y - 1, 0, 0, 18, 18);
        }
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        int showname = this.npc.display.getShowName();
        this.npc.display.setShowName(1);
        this.drawNpc(graphics, 50, 84);
        this.npc.display.setShowName(showname);
    }

    @Override
    public void save() {
        this.npc.inventory.dropchance = this.chances;
        this.npc.inventory.setExp(this.getTextField(0).getInteger(), this.getTextField(1).getInteger());
        Packets.sendServer(new SPacketMenuSave(EnumMenuType.INVENTORY, this.npc.inventory.save(new NbtCompound())));
    }

    @Override
    public void setGuiData(NbtCompound compound) {
        this.npc.inventory.load(compound);
        this.init();
    }

    @Override
    public void unFocused(GuiTextFieldNop textfield) {
        if (textfield.id >= 2) {
            this.chances.put(textfield.id - 2, Float.valueOf(textfield.getFloat()));
        }
    }
}

