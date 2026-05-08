/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.client.gui.script;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.NbtCompound;
import noppes.npcs.CustomItems;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.client.gui.script.GuiScriptInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketScriptGet;
import noppes.npcs.packets.server.SPacketScriptSave;

public class GuiScriptItem
extends GuiScriptInterface {
    private ItemScriptedWrapper item = new ItemScriptedWrapper(new ItemStack((ItemConvertible)CustomItems.scripted_item));

    public GuiScriptItem(PlayerEntity player) {
        this.handler = this.item;
        Packets.sendServer(new SPacketScriptGet(2));
    }

    @Override
    public void setGuiData(NbtCompound compound) {
        this.item.setMCNbt(compound);
        super.setGuiData(compound);
    }

    @Override
    public void save() {
        super.save();
        Packets.sendServer(new SPacketScriptSave(2, this.item.getMCNbt()));
    }
}

