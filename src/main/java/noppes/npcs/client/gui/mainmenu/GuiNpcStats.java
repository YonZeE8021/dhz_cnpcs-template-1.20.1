/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Difficulty
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.text.Text
 *  net.minecraft.text.Style
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.client.gui.tooltip.Tooltip
 */
package noppes.npcs.client.gui.mainmenu;

import net.minecraft.world.Difficulty;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.client.gui.tooltip.Tooltip;
import noppes.npcs.client.gui.SubGuiNpcMeleeProperties;
import noppes.npcs.client.gui.SubGuiNpcProjectiles;
import noppes.npcs.client.gui.SubGuiNpcRangeProperties;
import noppes.npcs.client.gui.SubGuiNpcResistanceProperties;
import noppes.npcs.client.gui.SubGuiNpcRespawn;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataStats;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuGet;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiNpcStats
extends GuiNPCInterface2
implements ITextfieldListener,
IGuiData {
    private DataStats stats;

    public GuiNpcStats(EntityNPCInterface npc) {
        super(npc, 2);
        this.stats = npc.stats;
        Packets.sendServer(new SPacketMenuGet(EnumMenuType.STATS));
    }

    @Override
    public void init() {
        super.init();
        int y = this.guiTop + 10;
        this.addLabel(new GuiLabel(0, "stats.health", this.guiLeft + 4, y + 5, "guihint.npchealth"));
        this.addTextField(new GuiTextFieldNop(0, (Screen)this, this.guiLeft + 85, y, 50, 18, "" + this.stats.maxHealth));
        this.getTextField((int)0).numbersOnly = true;
        this.getTextField(0).setMinMaxDefault(0, Integer.MAX_VALUE, 20);
        this.addLabel(new GuiLabel(1, "stats.aggro", this.guiLeft + 140, y + 5, "guihint.npcaggrorange"));
        this.addTextField(new GuiTextFieldNop(1, (Screen)this, this.guiLeft + 220, y, 50, 18, "" + this.stats.aggroRange));
        this.getTextField((int)1).numbersOnly = true;
        this.getTextField(1).setMinMaxDefault(1, 512, 2);
        this.addLabel(new GuiLabel(34, "stats.creaturetype", this.guiLeft + 275, y + 5, "guihint.npccreaturetype"));
        this.addButton(new GuiButtonNop((IGuiInterface)this, 8, this.guiLeft + 355, y, 56, 20, new String[]{"stats.normal", "stats.undead", "stats.arthropod"}, this.stats.getCreatureType()));
        this.addButton(new GuiButtonNop(this, 0, this.guiLeft + 82, y += 22, 56, 20, "selectServer.edit"));
        this.addLabel(new GuiLabel(2, "stats.respawn", this.guiLeft + 4, y + 5));
        this.addButton(new GuiButtonNop(this, 2, this.guiLeft + 82, y += 22, 56, 20, "selectServer.edit"));
        GuiLabel meleePropsLabel = new GuiLabel(5, "stats.meleeproperties", this.guiLeft + 4, y + 5, "guihint.npcmeleeprops");
        if (this.client != null && this.client.world.getDifficulty() == Difficulty.PEACEFUL) {
            ((MutableText)meleePropsLabel.getMessage()).append((Text)Text.literal((String)"!").setStyle(Style.EMPTY.withColor(16515909).withBold(Boolean.valueOf(true))));
            meleePropsLabel.setTooltip(Tooltip.of((Text)Text.translatable((String)"guihint.npcmeleeprops").setStyle(Style.EMPTY.withColor(16762460)).append((Text)Text.translatable((String)"guihint.npcmeleeprops.peaceful").setStyle(Style.EMPTY.withColor(16515909)))));
            meleePropsLabel.setWidth(MinecraftClient.getInstance().textRenderer.getWidth((StringVisitable)meleePropsLabel.getMessage()));
        }
        this.addLabel(meleePropsLabel);
        this.addButton(new GuiButtonNop(this, 3, this.guiLeft + 82, y += 22, 56, 20, "selectServer.edit"));
        GuiLabel rangedPropsLabel = new GuiLabel(6, "stats.rangedproperties", this.guiLeft + 4, y + 5, "guihint.npcrangedprops");
        if (this.client != null && this.client.world.getDifficulty() == Difficulty.PEACEFUL) {
            ((MutableText)rangedPropsLabel.getMessage()).append((Text)Text.literal((String)"!").setStyle(Style.EMPTY.withColor(16515909).withBold(Boolean.valueOf(true))));
            rangedPropsLabel.setTooltip(Tooltip.of((Text)Text.translatable((String)"guihint.npcrangedprops").setStyle(Style.EMPTY.withColor(16762460)).append((Text)Text.translatable((String)"guihint.npcrangedprops.peaceful").setStyle(Style.EMPTY.withColor(16515909)))));
            rangedPropsLabel.setWidth(MinecraftClient.getInstance().textRenderer.getWidth((StringVisitable)rangedPropsLabel.getMessage()));
        }
        this.addLabel(rangedPropsLabel);
        this.addButton(new GuiButtonNop(this, 9, this.guiLeft + 217, y, 56, 20, "selectServer.edit"));
        this.addLabel(new GuiLabel(7, "stats.projectileproperties", this.guiLeft + 140, y + 5, "guihint.npcprojectiletype"));
        this.addButton(new GuiButtonNop(this, 15, this.guiLeft + 82, y += 34, 56, 20, "selectServer.edit"));
        this.addLabel(new GuiLabel(15, "effect.minecraft.resistance", this.guiLeft + 4, y + 5, "guihint.npcresistance"));
        this.addButton(new GuiButtonNop((IGuiInterface)this, 4, this.guiLeft + 82, y += 34, 56, 20, new String[]{"gui.no", "gui.yes"}, this.npc.isFireImmune() ? 1 : 0));
        this.addLabel(new GuiLabel(10, "stats.fireimmune", this.guiLeft + 4, y + 5, "guihint.npcimmunetofire"));
        this.addButton(new GuiButtonNop((IGuiInterface)this, 5, this.guiLeft + 217, y, 56, 20, new String[]{"gui.no", "gui.yes"}, this.stats.canDrown ? 1 : 0));
        this.addLabel(new GuiLabel(11, "stats.candrown", this.guiLeft + 140, y + 5, "guihint.npccandrown"));
        this.addTextField(new GuiTextFieldNop(14, (Screen)this, this.guiLeft + 355, y, 56, 20, "" + this.stats.healthRegen).setNumbersOnly());
        this.addLabel(new GuiLabel(14, "stats.regenhealth", this.guiLeft + 275, y + 5, "guihint.npchealthregen"));
        this.addTextField(new GuiTextFieldNop(16, (Screen)this, this.guiLeft + 355, y += 22, 56, 20, "" + this.stats.combatRegen).setNumbersOnly());
        this.addLabel(new GuiLabel(16, "stats.combatregen", this.guiLeft + 275, y + 5, "guihint.npccombatregen"));
        this.addButton(new GuiButtonNop((IGuiInterface)this, 6, this.guiLeft + 82, y, 56, 20, new String[]{"gui.no", "gui.yes"}, this.stats.burnInSun ? 1 : 0));
        this.addLabel(new GuiLabel(12, "stats.burninsun", this.guiLeft + 4, y + 5, "guihint.npcburnsinsun"));
        this.addButton(new GuiButtonNop((IGuiInterface)this, 7, this.guiLeft + 217, y, 56, 20, new String[]{"gui.no", "gui.yes"}, this.stats.noFallDamage ? 1 : 0));
        this.addLabel(new GuiLabel(13, "stats.nofalldamage", this.guiLeft + 140, y + 5, "guihint.npcnofalldamage"));
        this.addButton(new GuiButtonYesNo((IGuiInterface)this, 17, this.guiLeft + 82, y += 22, 56, 20, this.stats.potionImmune));
        this.addLabel(new GuiLabel(17, "stats.potionImmune", this.guiLeft + 4, y + 5, "guihint.npcpotionimmune"));
        this.addLabel(new GuiLabel(22, "ai.cobwebAffected", this.guiLeft + 140, y + 5, "guihint.npccobwebaffected"));
        this.addButton(new GuiButtonNop((IGuiInterface)this, 22, this.guiLeft + 217, y, 56, 20, new String[]{"gui.no", "gui.yes"}, this.stats.ignoreCobweb ? 0 : 1));
    }

    @Override
    public void unFocused(GuiTextFieldNop textfield) {
        if (textfield.id == 0) {
            this.stats.maxHealth = textfield.getInteger();
            this.npc.heal(this.stats.maxHealth);
        } else if (textfield.id == 1) {
            this.stats.aggroRange = textfield.getInteger();
        } else if (textfield.id == 14) {
            this.stats.healthRegen = textfield.getInteger();
        } else if (textfield.id == 16) {
            this.stats.combatRegen = textfield.getInteger();
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        GuiButtonNop button = guibutton;
        if (button.id == 0) {
            this.setSubGui(new SubGuiNpcRespawn(this.stats));
        } else if (button.id == 2) {
            this.setSubGui(new SubGuiNpcMeleeProperties(this.stats.melee));
        } else if (button.id == 3) {
            this.setSubGui(new SubGuiNpcRangeProperties(this.stats));
        } else if (button.id == 4) {
            this.npc.setImmuneToFire(button.getValue() == 1);
        } else if (button.id == 5) {
            this.stats.canDrown = button.getValue() == 1;
        } else if (button.id == 6) {
            this.stats.burnInSun = button.getValue() == 1;
        } else if (button.id == 7) {
            this.stats.noFallDamage = button.getValue() == 1;
        } else if (button.id == 8) {
            this.stats.setCreatureType(button.getValue());
        } else if (button.id == 9) {
            this.setSubGui(new SubGuiNpcProjectiles(this.stats.ranged));
        } else if (button.id == 15) {
            this.setSubGui(new SubGuiNpcResistanceProperties(this.stats.resistances));
        } else if (button.id == 17) {
            this.stats.potionImmune = ((GuiButtonYesNo)guibutton).getBoolean();
        } else if (button.id == 22) {
            this.stats.ignoreCobweb = button.getValue() == 0;
        }
    }

    @Override
    public void save() {
        Packets.sendServer(new SPacketMenuSave(EnumMenuType.STATS, this.stats.save(new NbtCompound())));
    }

    @Override
    public void setGuiData(NbtCompound compound) {
        this.stats.readToNBT(compound);
        this.init();
    }
}

