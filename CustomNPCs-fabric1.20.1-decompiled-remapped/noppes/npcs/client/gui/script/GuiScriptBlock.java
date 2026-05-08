/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.client.gui.script;

import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.client.gui.script.GuiScriptInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketScriptGet;
import noppes.npcs.packets.server.SPacketScriptSave;

public class GuiScriptBlock
extends GuiScriptInterface {
    private TileScripted script;

    public GuiScriptBlock(BlockPos pos) {
        this.script = (TileScripted)this.player.getWorld().getBlockEntity(pos);
        this.handler = this.script;
        Packets.sendServer(new SPacketScriptGet(1));
    }

    @Override
    public void setGuiData(NbtCompound compound) {
        this.script.setNBT(compound);
        super.setGuiData(compound);
    }

    @Override
    public void save() {
        super.save();
        Packets.sendServer(new SPacketScriptSave(1, this.script.getNBT(new NbtCompound())));
    }
}

