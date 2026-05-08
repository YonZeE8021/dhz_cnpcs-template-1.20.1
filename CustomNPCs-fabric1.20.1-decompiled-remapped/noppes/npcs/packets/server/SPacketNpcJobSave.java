/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import java.util.Set;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketNpcJobSave
extends PacketServerBasic {
    private NbtCompound data;

    public SPacketNpcJobSave(NbtCompound data) {
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

    public static void encode(SPacketNpcJobSave msg, PacketByteBuf buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketNpcJobSave decode(PacketByteBuf buf) {
        return new SPacketNpcJobSave(buf.readNbt());
    }

    @Override
    protected void handle() {
        NbtCompound original = this.npc.job.save(new NbtCompound());
        Set names = this.data.getKeys();
        for (String name : names) {
            original.put(name, this.data.get(name));
        }
        this.npc.job.load(original);
        this.npc.updateClient = true;
    }
}

