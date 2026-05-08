/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.client.gui.screen.Screen
 */
package noppes.npcs.client.gui;

import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.blocks.tiles.TileBorder;
import noppes.npcs.client.gui.SubGuiNpcAvailability;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTileEntityGet;
import noppes.npcs.packets.server.SPacketTileEntitySave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class GuiBorderBlock
extends GuiNPCInterface
implements IGuiData {
    private TileBorder tile;

    public GuiBorderBlock(BlockPos pos) {
        this.tile = (TileBorder)this.player.getWorld().getBlockEntity(pos);
        Packets.sendServer(new SPacketTileEntityGet(pos));
    }

    @Override
    public void init() {
        super.init();
        this.addButton(new GuiButtonNop(this, 4, this.guiLeft + 40, this.guiTop + 40, 120, 20, "Availability Options"));
        this.addLabel(new GuiLabel(0, "Height", this.guiLeft + 1, this.guiTop + 76, 0xFFFFFF));
        this.addTextField(new GuiTextFieldNop(0, (Screen)this, this.guiLeft + 60, this.guiTop + 71, 40, 20, "" + this.tile.height));
        this.getTextField((int)0).numbersOnly = true;
        this.getTextField(0).setMinMaxDefault(0, 500, 6);
        this.addLabel(new GuiLabel(1, "Message", this.guiLeft + 1, this.guiTop + 100, 0xFFFFFF));
        this.addTextField(new GuiTextFieldNop(1, (Screen)this, this.guiLeft + 60, this.guiTop + 95, 200, 20, this.tile.message));
        this.addButton(new GuiButtonNop(this, 0, this.guiLeft + 40, this.guiTop + 190, 120, 20, "Done"));
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        int id = guibutton.id;
        if (id == 0) {
            this.close();
        }
        if (id == 4) {
            this.save();
            this.setSubGui(new SubGuiNpcAvailability(this.tile.availability));
        }
    }

    @Override
    public void save() {
        if (this.tile == null) {
            return;
        }
        this.tile.height = this.getTextField(0).getInteger();
        this.tile.message = this.getTextField(1).getText();
        Packets.sendServer(new SPacketTileEntitySave(this.tile.createNbtWithIdentifyingData()));
    }

    @Override
    public void setGuiData(NbtCompound compound) {
        this.tile.readExtraNBT(compound);
        this.init();
    }
}

