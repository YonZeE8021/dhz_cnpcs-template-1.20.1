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
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketFactionGet
extends PacketServerBasic {
    private int id;

    public SPacketFactionGet(int id) {
        this.id = id;
    }

    public static void encode(SPacketFactionGet msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketFactionGet decode(PacketByteBuf buf) {
        return new SPacketFactionGet(buf.readInt());
    }

    @Override
    protected void handle() {
        NbtCompound compound = new NbtCompound();
        Faction faction = FactionController.instance.getFaction(this.id);
        faction.writeNBT(compound);
        Packets.send(this.player, new PacketGuiData(compound));
    }
}

