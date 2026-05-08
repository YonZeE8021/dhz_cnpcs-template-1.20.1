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
import noppes.npcs.roles.JobSpawner;

public class SPacketNpcJobSpawnerSet
extends PacketServerBasic {
    private final int tab;
    private final String name;
    private final int slot;

    public SPacketNpcJobSpawnerSet(int tab, String name, int slot) {
        this.tab = tab;
        this.name = name;
        this.slot = slot;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketNpcJobSpawnerSet msg, PacketByteBuf buf) {
        buf.writeInt(msg.tab);
        buf.writeString(msg.name);
        buf.writeInt(msg.slot);
    }

    public static SPacketNpcJobSpawnerSet decode(PacketByteBuf buf) {
        return new SPacketNpcJobSpawnerSet(buf.readInt(), buf.readString(Short.MAX_VALUE), buf.readInt());
    }

    @Override
    protected void handle() {
        if (this.npc.job.getType() != 6) {
            return;
        }
        JobSpawner job = (JobSpawner)this.npc.job;
        job.setJobCompound(this.slot, this.tab, this.name);
    }
}

