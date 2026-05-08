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
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.roles.RoleTransporter;

public class SPacketTransportSave
extends PacketServerBasic {
    private int category;
    private NbtCompound data;

    public SPacketTransportSave(int category, NbtCompound data) {
        this.data = data;
        this.category = category;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketTransportSave msg, PacketByteBuf buf) {
        buf.writeInt(msg.category);
        buf.writeNbt(msg.data);
    }

    public static SPacketTransportSave decode(PacketByteBuf buf) {
        return new SPacketTransportSave(buf.readInt(), buf.readNbt());
    }

    @Override
    protected void handle() {
        TransportLocation location = TransportController.getInstance().saveLocation(this.category, this.data, this.player, this.npc);
        if (location != null) {
            if (this.npc.role.getType() != 4) {
                return;
            }
            RoleTransporter role = (RoleTransporter)this.npc.role;
            role.setTransport(location);
        }
    }
}

