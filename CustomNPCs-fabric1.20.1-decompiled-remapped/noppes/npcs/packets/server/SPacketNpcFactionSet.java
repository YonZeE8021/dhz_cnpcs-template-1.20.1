/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNpcFactionSet
extends PacketServerBasic {
    private int faction;

    public SPacketNpcFactionSet(int faction) {
        this.faction = faction;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketNpcFactionSet msg, PacketByteBuf buf) {
        buf.writeInt(msg.faction);
    }

    public static SPacketNpcFactionSet decode(PacketByteBuf buf) {
        return new SPacketNpcFactionSet(buf.readInt());
    }

    @Override
    protected void handle() {
        this.npc.setFaction(this.faction);
    }
}

