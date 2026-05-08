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
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketNpcJobGet
extends PacketServerBasic {
    @Override
    public boolean requiresNpc() {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketNpcJobGet msg, PacketByteBuf buf) {
    }

    public static SPacketNpcJobGet decode(PacketByteBuf buf) {
        return new SPacketNpcJobGet();
    }

    @Override
    protected void handle() {
        if (this.npc.job.getType() == 0) {
            return;
        }
        NbtCompound compound = new NbtCompound();
        compound.putBoolean("JobData", true);
        this.npc.job.save(compound);
        Packets.send(this.player, new PacketGuiData(compound));
    }
}

