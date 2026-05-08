/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.text.MutableText
 */
package noppes.npcs.client.gui.roles;

import java.util.HashMap;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.util.GuiContainerNPCInterface2;
import noppes.npcs.containers.ContainerNPCFollowerSetup;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcRoleSave;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

public class GuiNpcFollowerSetup
extends GuiContainerNPCInterface2<ContainerNPCFollowerSetup> {
    private RoleFollower role;
    private static final Identifier field_110422_t = new Identifier("textures/gui/followersetup.png");

    public GuiNpcFollowerSetup(ContainerNPCFollowerSetup container, PlayerInventory inv, Text titleIn) {
        super(NoppesUtil.getLastNpc(), container, inv, titleIn);
        this.backgroundHeight = 200;
        this.role = (RoleFollower)this.npc.role;
        this.setBackground("followersetup.png");
    }

    @Override
    public void init() {
        int i;
        super.init();
        for (i = 0; i < 3; ++i) {
            int x = this.guiLeft + 66;
            int y = this.guiTop + 37;
            GuiTextFieldNop tf = new GuiTextFieldNop(i, (Screen)this, x, y += i * 25, 24, 20, "1");
            tf.numbersOnly = true;
            tf.setMinMaxDefault(1, Integer.MAX_VALUE, 1);
            this.addTextField(tf);
        }
        i = 0;
        for (int day : this.role.rates.values()) {
            this.getTextField(i).setText("" + day);
            ++i;
        }
        MutableText text = Text.translatable((String)"follower.hireText").append(" {days} ").append((Text)Text.translatable((String)"follower.days"));
        if (!this.role.dialogHire.isEmpty()) {
            text = Text.translatable((String)this.role.dialogHire);
        }
        this.addTextField(new GuiTextFieldNop(3, (Screen)this, this.guiLeft + 100, this.guiTop + 6, 286, 20, (Text)text));
        text = Text.translatable((String)"follower.farewellText").append(" {player}");
        if (!this.role.dialogFarewell.isEmpty()) {
            text = Text.translatable((String)this.role.dialogFarewell);
        }
        this.addTextField(new GuiTextFieldNop(4, (Screen)this, this.guiLeft + 100, this.guiTop + 30, 286, 20, (Text)text));
        this.addLabel(new GuiLabel(7, "follower.infiniteDays", this.guiLeft + 180, this.guiTop + 80));
        this.addButton(new GuiButtonYesNo((IGuiInterface)this, 7, this.guiLeft + 260, this.guiTop + 75, this.role.infiniteDays));
        this.addLabel(new GuiLabel(8, "follower.guiDisabled", this.guiLeft + 180, this.guiTop + 104));
        this.addButton(new GuiButtonYesNo((IGuiInterface)this, 8, this.guiLeft + 260, this.guiTop + 99, this.role.disableGui));
        this.addLabel(new GuiLabel(9, "follower.allowSoulstone", this.guiLeft + 180, this.guiTop + 128));
        this.addButton(new GuiButtonYesNo((IGuiInterface)this, 9, this.guiLeft + 260, this.guiTop + 123, !this.role.refuseSoulStone));
        this.addButton(new GuiButtonNop(this, 10, this.guiLeft + 195, this.guiTop + 147, 100, 20, "gui.reset"));
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        if (guibutton.id == 7) {
            this.role.infiniteDays = ((GuiButtonYesNo)guibutton).getBoolean();
        }
        if (guibutton.id == 8) {
            this.role.disableGui = ((GuiButtonYesNo)guibutton).getBoolean();
        }
        if (guibutton.id == 9) {
            boolean bl = this.role.refuseSoulStone = !((GuiButtonYesNo)guibutton).getBoolean();
        }
        if (guibutton.id == 10) {
            this.role.killed();
        }
    }

    @Override
    protected void drawForeground(DrawContext p_281635_, int p_282681_, int p_283686_) {
    }

    @Override
    public void save() {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 0; i < this.role.inventory.size(); ++i) {
            ItemStack item = this.role.inventory.getStack(i);
            if (item == null || item.isEmpty()) continue;
            int days = 1;
            if (!this.getTextField(i).isEmpty() && this.getTextField(i).isInteger()) {
                days = this.getTextField(i).getInteger();
            }
            if (days <= 0) {
                days = 1;
            }
            map.put(i, days);
        }
        this.role.rates = map;
        this.role.dialogHire = this.getTextField(3).getText();
        this.role.dialogFarewell = this.getTextField(4).getText();
        Packets.sendServer(new SPacketNpcRoleSave(this.role.save(new NbtCompound())));
    }
}

