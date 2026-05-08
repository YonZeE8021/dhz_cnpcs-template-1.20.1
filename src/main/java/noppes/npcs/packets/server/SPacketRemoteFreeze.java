/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiScrollSelected;

public class SPacketRemoteFreeze
extends PacketServerBasic {
    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_FREEZE;
    }

    public static void encode(SPacketRemoteFreeze msg, PacketByteBuf buf) {
    }

    public static SPacketRemoteFreeze decode(PacketByteBuf buf) {
        return new SPacketRemoteFreeze();
    }

    @Override
    protected void handle() {
        CustomNpcs.FreezeNPCs = !CustomNpcs.FreezeNPCs;
        Packets.send(this.player, new PacketGuiScrollSelected(CustomNpcs.FreezeNPCs ? "Unfreeze Npcs" : "Freeze Npcs"));
    }
}

