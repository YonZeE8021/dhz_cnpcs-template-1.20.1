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
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.server.SPacketFactionsGet;

public class SPacketFactionRemove
extends PacketServerBasic {
    private int id;

    public SPacketFactionRemove(int id) {
        this.id = id;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_FACTION;
    }

    public static void encode(SPacketFactionRemove msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketFactionRemove decode(PacketByteBuf buf) {
        return new SPacketFactionRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        FactionController.instance.delete(this.id);
        SPacketFactionsGet.sendFactionDataAll(this.player);
        NbtCompound compound = new NbtCompound();
        new Faction().writeNBT(compound);
        Packets.send(this.player, new PacketGuiData(compound));
    }
}

