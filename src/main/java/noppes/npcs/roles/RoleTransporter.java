/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.text.Text
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.roles;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.entity.data.role.IRoleTransporter;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.TransportController;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerTransportData;
import noppes.npcs.controllers.data.TransportLocation;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketChatBubble;
import noppes.npcs.packets.server.SPacketDimensionTeleport;
import noppes.npcs.roles.RoleInterface;

public class RoleTransporter
extends RoleInterface
implements IRoleTransporter {
    public int transportId = -1;
    public String name;
    private int ticks = 10;

    public RoleTransporter(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public NbtCompound save(NbtCompound nbttagcompound) {
        nbttagcompound.putInt("TransporterId", this.transportId);
        return nbttagcompound;
    }

    @Override
    public void load(NbtCompound nbttagcompound) {
        this.transportId = nbttagcompound.getInt("TransporterId");
        TransportLocation loc = this.getLocation();
        if (loc != null) {
            this.name = loc.name;
        }
    }

    @Override
    public boolean aiShouldExecute() {
        --this.ticks;
        if (this.ticks > 0) {
            return false;
        }
        this.ticks = 10;
        if (!this.hasTransport()) {
            return false;
        }
        TransportLocation loc = this.getLocation();
        if (loc.type != 0) {
            return false;
        }
        List<PlayerEntity> inRange = this.npc.getWorld().getNonSpectatingEntities(PlayerEntity.class, this.npc.getBoundingBox().expand(6.0, 6.0, 6.0));
        for (PlayerEntity player : inRange) {
            if (!this.npc.canNpcSee((Entity)player)) continue;
            this.unlock(player, loc);
        }
        return false;
    }

    @Override
    public void interact(PlayerEntity player) {
        if (this.hasTransport()) {
            TransportLocation loc = this.getLocation();
            if (loc.type == 2) {
                this.unlock(player, loc);
            }
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerTransporter, this.npc);
        }
    }

    public void transport(ServerPlayerEntity player, String location) {
        TransportLocation loc = TransportController.getInstance().getTransport(location);
        PlayerTransportData playerdata = PlayerData.get((PlayerEntity)player).transportData;
        if (loc == null || !loc.isDefault() && !playerdata.transports.contains(loc.id)) {
            return;
        }
        RoleEvent.TransporterUseEvent event = new RoleEvent.TransporterUseEvent((PlayerEntity)player, this.npc.wrappedNPC, loc);
        if (EventHooks.onNPCRole(this.npc, event)) {
            return;
        }
        SPacketDimensionTeleport.teleportPlayer(player, loc.pos.getX(), loc.pos.getY(), loc.pos.getZ(), loc.dimension);
    }

    private void unlock(PlayerEntity player, TransportLocation loc) {
        PlayerTransportData data = PlayerData.get((PlayerEntity)player).transportData;
        if (data.transports.contains(this.transportId)) {
            return;
        }
        RoleEvent.TransporterUnlockedEvent event = new RoleEvent.TransporterUnlockedEvent(player, this.npc.wrappedNPC);
        if (EventHooks.onNPCRole(this.npc, event)) {
            return;
        }
        data.transports.add(this.transportId);
        this.npc.say(player, new Line("mailbox.gotmail"));
        Packets.send((ServerPlayerEntity)player, new PacketChatBubble(this.npc.getId(), (Text)Text.translatable((String)"transporter.unlock", (Object[])new Object[]{loc.name}), true));
    }

    @Override
    public TransportLocation getLocation() {
        if (this.npc.isClientSide()) {
            return null;
        }
        return TransportController.getInstance().getTransport(this.transportId);
    }

    public boolean hasTransport() {
        TransportLocation loc = this.getLocation();
        return loc != null && loc.id == this.transportId;
    }

    public void setTransport(TransportLocation location) {
        this.transportId = location.id;
        this.name = location.name;
    }

    @Override
    public int getType() {
        return 4;
    }
}

