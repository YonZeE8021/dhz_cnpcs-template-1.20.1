/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketNpcRoleGet
extends PacketServerBasic {
    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    public static void encode(SPacketNpcRoleGet msg, PacketByteBuf buf) {
    }

    public static SPacketNpcRoleGet decode(PacketByteBuf buf) {
        return new SPacketNpcRoleGet();
    }

    @Override
    protected void handle() {
        if (this.npc.role.getType() == 0) {
            return;
        }
        NbtCompound compound = new NbtCompound();
        compound.putBoolean("RoleData", true);
        Packets.send(this.player, new PacketGuiData(this.npc.role.save(compound)));
    }
}

