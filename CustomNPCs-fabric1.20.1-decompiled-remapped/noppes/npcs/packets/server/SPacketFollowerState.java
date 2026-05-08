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
import noppes.npcs.roles.RoleFollower;

public class SPacketFollowerState
extends PacketServerBasic {
    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    public static void encode(SPacketFollowerState msg, PacketByteBuf buf) {
    }

    public static SPacketFollowerState decode(PacketByteBuf buf) {
        return new SPacketFollowerState();
    }

    @Override
    protected void handle() {
        if (this.npc.role.getType() != 2) {
            return;
        }
        RoleFollower role = (RoleFollower)this.npc.role;
        if (role.owner == null || !role.owner.getName().equals(this.player.getName())) {
            return;
        }
        role.isFollowing = !role.isFollowing;
        Packets.send(this.player, new PacketGuiData(this.npc.role.save(new NbtCompound())));
    }
}

