/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.packets.server;

import java.util.ArrayList;
import java.util.Vector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerTransportData;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiOpen;
import noppes.npcs.packets.client.PacketGuiScrollList;
import noppes.npcs.packets.client.PacketNpcRole;
import noppes.npcs.roles.RoleTransporter;
import noppes.npcs.util.CustomNPCsScheduler;

public class SPacketGuiOpen
extends PacketServerBasic {
    private EnumGuiType type;
    private BlockPos pos;

    public SPacketGuiOpen(EnumGuiType type, BlockPos pos) {
        this.type = type;
        this.pos = pos;
    }

    public static void encode(SPacketGuiOpen msg, PacketByteBuf buf) {
        buf.writeEnumConstant((Enum)msg.type);
        buf.writeBlockPos(msg.pos);
    }

    public static SPacketGuiOpen decode(PacketByteBuf buf) {
        return new SPacketGuiOpen((EnumGuiType)buf.readEnumConstant(EnumGuiType.class), buf.readBlockPos());
    }

    @Override
    protected void handle() {
        SPacketGuiOpen.sendOpenGui((PlayerEntity)this.player, this.type, this.npc, this.pos);
    }

    public static void sendOpenGui(PlayerEntity player, EnumGuiType gui, EntityNPCInterface npc, BlockPos pos) {
        if (!(player instanceof ServerPlayerEntity)) {
            return;
        }
        NoppesUtilServer.setEditingNpc(player, npc);
        if (gui == EnumGuiType.PlayerFollower || gui == EnumGuiType.PlayerFollowerHire || gui == EnumGuiType.PlayerTrader || gui == EnumGuiType.PlayerTransporter) {
            if (npc.role.getType() == 0) {
                return;
            }
            NbtCompound comp = new NbtCompound();
            npc.role.save(comp);
            comp.putInt("Role", npc.role.getType());
            Packets.send((ServerPlayerEntity)player, new PacketNpcRole(npc.getId(), comp));
        }
        CustomNPCsScheduler.runTack(() -> player.getServer().submit(() -> {
            if (!gui.hasContainer) {
                Packets.send((ServerPlayerEntity)player, new PacketGuiOpen(gui, pos));
            } else {
                NoppesUtilServer.openContainerGui((ServerPlayerEntity)player, gui, buffer -> {
                    buffer.writeInt(npc.getId());
                    if (pos != null) {
                        buffer.writeBlockPos(pos);
                    }
                });
            }
            ArrayList<String> list = SPacketGuiOpen.getScrollData(player, gui, npc);
            if (list == null || list.isEmpty()) {
                return;
            }
            Packets.send((ServerPlayerEntity)player, new PacketGuiScrollList(new Vector<String>(list)));
        }), 200);
    }

    private static ArrayList<String> getScrollData(PlayerEntity player, EnumGuiType gui, EntityNPCInterface npc) {
        if (gui == EnumGuiType.PlayerTransporter) {
            RoleTransporter role = (RoleTransporter)npc.role;
            ArrayList<String> list = new ArrayList<String>();
            TransportLocation location = role.getLocation();
            String name = role.getLocation().name;
            for (TransportLocation loc : location.category.getDefaultLocations()) {
                if (list.contains(loc.name)) continue;
                list.add(loc.name);
            }
            PlayerTransportData playerdata = PlayerData.get((PlayerEntity)player).transportData;
            for (int i : playerdata.transports) {
                TransportLocation loc = TransportController.getInstance().getTransport(i);
                if (loc == null || !location.category.locations.containsKey(loc.id) || list.contains(loc.name)) continue;
                list.add(loc.name);
            }
            list.remove(name);
            return list;
        }
        return null;
    }
}

