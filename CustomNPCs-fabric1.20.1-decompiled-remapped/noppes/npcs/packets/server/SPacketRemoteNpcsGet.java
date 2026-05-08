/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.packets.server;

import java.text.DecimalFormat;
import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiScrollSelected;

public class SPacketRemoteNpcsGet
extends PacketServerBasic {
    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketRemoteNpcsGet msg, PacketByteBuf buf) {
    }

    public static SPacketRemoteNpcsGet decode(PacketByteBuf buf) {
        return new SPacketRemoteNpcsGet();
    }

    @Override
    protected void handle() {
        SPacketRemoteNpcsGet.sendNearbyNpcs(this.player);
        Packets.send(this.player, new PacketGuiScrollSelected(CustomNpcs.FreezeNPCs ? "Unfreeze Npcs" : "Freeze Npcs"));
    }

    public static void sendNearbyNpcs(ServerPlayerEntity player) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (Entity entity : ((ServerWorld)player.getWorld()).iterateEntities()) {
            EntityNPCInterface npc;
            if (!(entity instanceof EntityNPCInterface) || (npc = (EntityNPCInterface)entity).isRemoved()) continue;
            float distance = player.distanceTo((Entity)npc);
            DecimalFormat df = new DecimalFormat("#.#");
            Object s = df.format(distance);
            if (distance < 10.0f) {
                s = "0" + (String)s;
            }
            map.put((String)s + " : " + npc.display.getName(), npc.getId());
        }
        NoppesUtilServer.sendScrollData(player, map);
    }
}

