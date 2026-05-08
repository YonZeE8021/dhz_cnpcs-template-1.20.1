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

public class SPacketNpcTransform
extends PacketServerBasic {
    private boolean isActive;

    public SPacketNpcTransform(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketNpcTransform msg, PacketByteBuf buf) {
        buf.writeBoolean(msg.isActive);
    }

    public static SPacketNpcTransform decode(PacketByteBuf buf) {
        return new SPacketNpcTransform(buf.readBoolean());
    }

    @Override
    protected void handle() {
        if (this.npc.transform.isValid()) {
            this.npc.transform.transform(this.isActive);
        }
    }
}

