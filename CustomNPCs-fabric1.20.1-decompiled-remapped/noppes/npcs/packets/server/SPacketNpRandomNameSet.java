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

public class SPacketNpRandomNameSet
extends PacketServerBasic {
    private int id;
    private int gender;

    public SPacketNpRandomNameSet(int id, int gender) {
        this.id = id;
        this.gender = gender;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_DISPLAY;
    }

    public static void encode(SPacketNpRandomNameSet msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
        buf.writeInt(msg.gender);
    }

    public static SPacketNpRandomNameSet decode(PacketByteBuf buf) {
        return new SPacketNpRandomNameSet(buf.readInt(), buf.readInt());
    }

    @Override
    protected void handle() {
        this.npc.display.setMarkovGeneratorId(this.id);
        this.npc.display.setMarkovGender(this.gender);
        this.npc.display.setName(this.npc.display.getRandomName());
        NbtCompound data = new NbtCompound();
        this.npc.display.save(data);
        Packets.send(this.player, new PacketGuiData(data));
    }
}

