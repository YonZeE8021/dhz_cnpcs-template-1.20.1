/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.roles.RoleTransporter;

public class SPacketPlayerTransport
extends PacketServerBasic {
    private final String name;

    public SPacketPlayerTransport(String name) {
        this.name = name;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    public static void encode(SPacketPlayerTransport msg, PacketByteBuf buf) {
        buf.writeString(msg.name);
    }

    public static SPacketPlayerTransport decode(PacketByteBuf buf) {
        return new SPacketPlayerTransport(buf.readString(Short.MAX_VALUE));
    }

    @Override
    protected void handle() {
        if (this.npc.role.getType() != 4) {
            return;
        }
        ((RoleTransporter)this.npc.role).transport(this.player, this.name);
    }
}

