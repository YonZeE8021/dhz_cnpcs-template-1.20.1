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

public class SPacketFactionSave
extends PacketServerBasic {
    private NbtCompound data;

    public SPacketFactionSave(NbtCompound data) {
        this.data = data;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_FACTION;
    }

    public static void encode(SPacketFactionSave msg, PacketByteBuf buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketFactionSave decode(PacketByteBuf buf) {
        return new SPacketFactionSave(buf.readNbt());
    }

    @Override
    protected void handle() {
        Faction faction = new Faction();
        faction.readNBT(this.data);
        FactionController.instance.saveFaction(faction);
        SPacketFactionsGet.sendFactionDataAll(this.player);
        NbtCompound compound = new NbtCompound();
        faction.writeNBT(compound);
        Packets.send(this.player, new PacketGuiData(compound));
    }
}

