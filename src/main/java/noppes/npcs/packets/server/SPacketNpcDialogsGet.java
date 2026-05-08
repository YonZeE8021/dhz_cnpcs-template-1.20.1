/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketNpcDialogsGet
extends PacketServerBasic {
    @Override
    public boolean requiresNpc() {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketNpcDialogsGet msg, PacketByteBuf buf) {
    }

    public static SPacketNpcDialogsGet decode(PacketByteBuf buf) {
        return new SPacketNpcDialogsGet();
    }

    @Override
    protected void handle() {
        for (int pos : this.npc.dialogs.keySet()) {
            DialogOption option = this.npc.dialogs.get(pos);
            if (option == null || !option.hasDialog()) continue;
            NbtCompound compound = option.writeNBT();
            compound.putInt("Position", pos);
            Packets.send(this.player, new PacketGuiData(compound));
        }
    }
}

