/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.packets.server;

import java.io.File;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.constants.EnumPlayerData;
import noppes.npcs.controllers.PlayerDataController;
import noppes.npcs.controllers.SyncController;
import noppes.npcs.controllers.data.PlayerBankData;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerDialogData;
import noppes.npcs.controllers.data.PlayerFactionData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.PlayerTransportData;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.server.SPacketPlayerDataGet;

public class SPacketPlayerDataRemove
extends PacketServerBasic {
    private EnumPlayerData type;
    private String name;
    private int id;

    public SPacketPlayerDataRemove(EnumPlayerData type, String name, int id) {
        this.type = type;
        this.name = name;
        this.id = id;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_PLAYERDATA;
    }

    public static void encode(SPacketPlayerDataRemove msg, PacketByteBuf buf) {
        buf.writeEnumConstant((Enum)msg.type);
        buf.writeString(msg.name);
        buf.writeInt(msg.id);
    }

    public static SPacketPlayerDataRemove decode(PacketByteBuf buf) {
        return new SPacketPlayerDataRemove((EnumPlayerData)buf.readEnumConstant(EnumPlayerData.class), buf.readString(Short.MAX_VALUE), buf.readInt());
    }

    @Override
    protected void handle() {
        Object data;
        if (this.name == null || this.name.isEmpty()) {
            return;
        }
        ServerPlayerEntity pl = this.player.getServer().getPlayerManager().getPlayer(this.name);
        PlayerData playerdata = null;
        playerdata = pl == null ? PlayerDataController.instance.getDataFromUsername(this.player.getServer(), this.name) : PlayerData.get((PlayerEntity)pl);
        if (this.type == EnumPlayerData.Players) {
            File file = new File(CustomNpcs.getLevelSaveDirectory("playerdata"), playerdata.uuid + ".json");
            if (file.exists()) {
                file.delete();
            }
            if (pl != null) {
                playerdata.setNBT(new NbtCompound());
                SPacketPlayerDataGet.sendPlayerData(this.type, this.player, this.name);
                playerdata.save(true);
                return;
            }
            PlayerDataController.instance.nameUUIDs.remove(this.name);
        }
        if (this.type == EnumPlayerData.Quest) {
            data = playerdata.questData;
            ((PlayerQuestData)data).activeQuests.remove(this.id);
            ((PlayerQuestData)data).finishedQuests.remove(this.id);
            playerdata.save(true);
        }
        if (this.type == EnumPlayerData.Dialog) {
            data = playerdata.dialogData;
            ((PlayerDialogData)data).dialogsRead.remove(this.id);
            playerdata.save(true);
        }
        if (this.type == EnumPlayerData.Transport) {
            data = playerdata.transportData;
            ((PlayerTransportData)data).transports.remove(this.id);
            playerdata.save(true);
        }
        if (this.type == EnumPlayerData.Bank) {
            data = playerdata.bankData;
            ((PlayerBankData)data).banks.remove(this.id);
            playerdata.save(true);
        }
        if (this.type == EnumPlayerData.Factions) {
            data = playerdata.factionData;
            ((PlayerFactionData)data).factionData.remove(this.id);
            playerdata.save(true);
        }
        if (pl != null) {
            SyncController.syncPlayer(pl);
        }
        SPacketPlayerDataGet.sendPlayerData(this.type, this.player, this.name);
    }
}

