/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.text.Text
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 */
package noppes.npcs.client.gui.roles;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.gui.player.companion.GuiNpcCompanionTalents;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.constants.EnumCompanionStage;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcRoleCompanionUpdate;
import noppes.npcs.packets.server.SPacketNpcRoleSave;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiSliderNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.gui.listeners.ISliderListener;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiNpcCompanion
extends GuiNPCInterface2
implements ITextfieldListener,
ISliderListener {
    private RoleCompanion role;
    private List<GuiNpcCompanionTalents.GuiTalent> talents = new ArrayList<GuiNpcCompanionTalents.GuiTalent>();

    public GuiNpcCompanion(EntityNPCInterface npc) {
        super(npc);
        this.role = (RoleCompanion)npc.role;
    }

    @Override
    public void init() {
        super.init();
        this.talents = new ArrayList<GuiNpcCompanionTalents.GuiTalent>();
        int y = this.guiTop + 4;
        this.addButton(new GuiButtonNop((IGuiInterface)this, 0, this.guiLeft + 70, y, 90, 20, new String[]{EnumCompanionStage.BABY.name, EnumCompanionStage.CHILD.name, EnumCompanionStage.TEEN.name, EnumCompanionStage.ADULT.name, EnumCompanionStage.FULLGROWN.name}, this.role.stage.ordinal()));
        this.addLabel(new GuiLabel(0, "companion.stage", this.guiLeft + 4, y + 5));
        this.addButton(new GuiButtonNop(this, 1, this.guiLeft + 162, y, 90, 20, "gui.update"));
        this.addButton(new GuiButtonNop((IGuiInterface)this, 2, this.guiLeft + 70, y += 22, 90, 20, new String[]{"gui.no", "gui.yes"}, this.role.canAge ? 1 : 0));
        this.addLabel(new GuiLabel(2, "companion.age", this.guiLeft + 4, y + 5));
        if (this.role.canAge) {
            this.addTextField(new GuiTextFieldNop(2, (Screen)this, this.guiLeft + 162, y, 140, 20, "" + this.role.ticksActive));
            this.getTextField((int)2).numbersOnly = true;
            this.getTextField(2).setMinMaxDefault(0, Integer.MAX_VALUE, 0);
        }
        this.talents.add(new GuiNpcCompanionTalents.GuiTalent(this.role, EnumCompanionTalent.INVENTORY, this.guiLeft + 4, y += 26));
        this.addSlider(new GuiSliderNop(this, 10, this.guiLeft + 30, y + 2, 100, 20, (float)this.role.getExp(EnumCompanionTalent.INVENTORY) / 5000.0f));
        this.talents.add(new GuiNpcCompanionTalents.GuiTalent(this.role, EnumCompanionTalent.ARMOR, this.guiLeft + 4, y += 26));
        this.addSlider(new GuiSliderNop(this, 11, this.guiLeft + 30, y + 2, 100, 20, (float)this.role.getExp(EnumCompanionTalent.ARMOR) / 5000.0f));
        this.talents.add(new GuiNpcCompanionTalents.GuiTalent(this.role, EnumCompanionTalent.SWORD, this.guiLeft + 4, y += 26));
        this.addSlider(new GuiSliderNop(this, 12, this.guiLeft + 30, y + 2, 100, 20, (float)this.role.getExp(EnumCompanionTalent.SWORD) / 5000.0f));
        for (GuiNpcCompanionTalents.GuiTalent gui : this.talents) {
            gui.init(this.client, this.width, this.height);
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        GuiButtonNop button;
        if (guibutton.id == 0) {
            button = guibutton;
            this.role.matureTo(EnumCompanionStage.values()[button.getValue()]);
            if (this.role.canAge) {
                this.role.ticksActive = this.role.stage.matureAge;
            }
            this.init();
        }
        if (guibutton.id == 1) {
            Packets.sendServer(new SPacketNpcRoleCompanionUpdate(this.role.stage));
        }
        if (guibutton.id == 2) {
            button = guibutton;
            this.role.canAge = button.getValue() == 1;
            this.init();
        }
    }

    @Override
    public void unFocused(GuiTextFieldNop textfield) {
        if (textfield.id == 2) {
            this.role.ticksActive = textfield.getInteger();
        }
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        for (GuiNpcCompanionTalents.GuiTalent talent : new ArrayList<GuiNpcCompanionTalents.GuiTalent>(this.talents)) {
            talent.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void elementClicked() {
    }

    @Override
    public void save() {
        Packets.sendServer(new SPacketNpcRoleSave(this.role.save(new NbtCompound())));
    }

    @Override
    public void mouseDragged(GuiSliderNop slider) {
        if (slider.sliderValue <= 0.0f) {
            slider.setMessage((Text)Text.translatable((String)"gui.disabled"));
            this.role.talents.remove(EnumCompanionTalent.values()[slider.id - 10]);
        } else {
            slider.setMessage((Text)Text.translatable((String)((int)(slider.sliderValue * 50.0f) * 100 + " exp")));
            this.role.setExp(EnumCompanionTalent.values()[slider.id - 10], (int)(slider.sliderValue * 50.0f) * 100);
        }
    }

    @Override
    public void mousePressed(GuiSliderNop slider) {
    }

    @Override
    public void mouseReleased(GuiSliderNop slider) {
    }
}

