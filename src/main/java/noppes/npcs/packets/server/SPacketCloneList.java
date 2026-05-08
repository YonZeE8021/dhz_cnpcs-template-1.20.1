/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtString
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.CustomItems;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketCloneList
extends PacketServerBasic {
    private int tab;

    public SPacketCloneList(int tab) {
        this.tab = tab;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return item.getItem() == CustomItems.wand || item.getItem() == CustomItems.cloner || item.getItem() == CustomItems.mount;
    }

    public static void encode(SPacketCloneList msg, PacketByteBuf buf) {
        buf.writeInt(msg.tab);
    }

    public static SPacketCloneList decode(PacketByteBuf buf) {
        return new SPacketCloneList(buf.readInt());
    }

    @Override
    protected void handle() {
        SPacketCloneList.sendList(this.player, this.tab);
    }

    public static void sendList(ServerPlayerEntity player, int tab) {
        NbtList list = new NbtList();
        for (String name : ServerCloneController.Instance.getClones(tab)) {
            list.add(NbtString.of((String)name));
        }
        NbtCompound compound = new NbtCompound();
        compound.put("List", (NbtElement)list);
        Packets.send(player, new PacketGuiData(compound));
    }
}

