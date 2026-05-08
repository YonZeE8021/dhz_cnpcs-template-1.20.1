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
import noppes.npcs.blocks.tiles.TileWaypoint;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketTileEntityGet;
import noppes.npcs.packets.server.SPacketTileEntitySave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class GuiNpcWaypoint
extends GuiNPCInterface
implements IGuiData {
    private TileWaypoint tile;

    public GuiNpcWaypoint(BlockPos pos) {
        this.tile = (TileWaypoint)this.player.getWorld().getBlockEntity(pos);
        Packets.sendServer(new SPacketTileEntityGet(pos));
        this.imageWidth = 265;
    }

    @Override
    public void init() {
        super.init();
        if (this.tile == null) {
            this.close();
        }
        this.addLabel(new GuiLabel(0, "gui.name", this.guiLeft + 1, this.guiTop + 76, 0xFFFFFF));
        this.addTextField(new GuiTextFieldNop(0, (Screen)this, this.guiLeft + 60, this.guiTop + 71, 200, 20, this.tile.name));
        this.addLabel(new GuiLabel(1, "gui.range", this.guiLeft + 1, this.guiTop + 97, 0xFFFFFF));
        this.addTextField(new GuiTextFieldNop(1, (Screen)this, this.guiLeft + 60, this.guiTop + 92, 200, 20, "" + this.tile.range));
        this.getTextField((int)1).numbersOnly = true;
        this.getTextField(1).setMinMaxDefault(2, 60, 10);
        this.addButton(new GuiButtonNop(this, 0, this.guiLeft + 40, this.guiTop + 190, 120, 20, "Done"));
    }

    @Override
    public void buttonEvent(GuiButtonNop guibutton) {
        int id = guibutton.id;
        if (id == 0) {
            this.close();
        }
    }

    @Override
    public void save() {
        this.tile.name = this.getTextField(0).getText();
        this.tile.range = this.getTextField(1).getInteger();
        Packets.sendServer(new SPacketTileEntitySave(this.tile.createNbtWithIdentifyingData()));
    }

    @Override
    public void setGuiData(NbtCompound compound) {
        this.tile.readNbt(compound);
        this.init();
    }
}

