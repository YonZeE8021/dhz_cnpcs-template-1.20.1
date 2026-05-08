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

public class SPacketNpcRoleSave
extends PacketServerBasic {
    private NbtCompound data;

    public SPacketNpcRoleSave(NbtCompound data) {
        this.data = data;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketNpcRoleSave msg, PacketByteBuf buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketNpcRoleSave decode(PacketByteBuf buf) {
        return new SPacketNpcRoleSave(buf.readNbt());
    }

    @Override
    protected void handle() {
        this.npc.role.load(this.data);
        this.npc.updateClient = true;
    }
}

