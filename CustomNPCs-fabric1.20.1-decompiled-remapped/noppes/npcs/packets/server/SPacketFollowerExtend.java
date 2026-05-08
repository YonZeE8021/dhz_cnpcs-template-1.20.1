/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.containers.ContainerNPCFollower;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.server.SPacketFollowerHire;
import noppes.npcs.roles.RoleFollower;

public class SPacketFollowerExtend
extends PacketServerBasic {
    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    public static void encode(SPacketFollowerExtend msg, PacketByteBuf buf) {
    }

    public static SPacketFollowerExtend decode(PacketByteBuf buf) {
        return new SPacketFollowerExtend();
    }

    @Override
    protected void handle() {
        if (this.npc.role.getType() != 2) {
            return;
        }
        ScreenHandler con = this.player.currentScreenHandler;
        if (con == null || !(con instanceof ContainerNPCFollower)) {
            return;
        }
        ContainerNPCFollower container = (ContainerNPCFollower)con;
        RoleFollower role = (RoleFollower)this.npc.role;
        SPacketFollowerHire.followerBuy(role, container.currencyMatrix, this.player, this.npc);
        Packets.send(this.player, new PacketGuiData(this.npc.role.save(new NbtCompound())));
    }
}

