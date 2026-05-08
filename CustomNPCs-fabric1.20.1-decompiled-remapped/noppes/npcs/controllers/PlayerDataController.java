/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.server.MinecraftServer
 */
package noppes.npcs.controllers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.BankController;
import noppes.npcs.controllers.data.Bank;
import noppes.npcs.controllers.data.PlayerBankData;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.NBTJsonUtil;

public class PlayerDataController {
    public static PlayerDataController instance;
    public Map<String, String> nameUUIDs;

    public PlayerDataController() {
        instance = this;
        File dir = CustomNpcs.getLevelSaveDirectory("playerdata");
        HashMap<String, String> map = new HashMap<String, String>();
        for (File file : dir.listFiles()) {
            if (file.isDirectory() || !file.getName().endsWith(".json")) continue;
            try {
                NbtCompound compound = NBTJsonUtil.LoadFile(file);
                if (!compound.contains("PlayerName")) continue;
                map.put(compound.getString("PlayerName"), file.getName().substring(0, file.getName().length() - 5));
            }
            catch (Exception e) {
                LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
            }
        }
        this.nameUUIDs = map;
    }

    public PlayerBankData getBankData(PlayerEntity player, int bankId) {
        Bank bank = BankController.getInstance().getBank(bankId);
        PlayerBankData data = PlayerData.get((PlayerEntity)player).bankData;
        if (!data.hasBank(bank.id)) {
            data.loadNew(bank.id);
        }
        return data;
    }

    public String hasPlayer(String username) {
        for (String name : this.nameUUIDs.keySet()) {
            if (!name.equalsIgnoreCase(username)) continue;
            return name;
        }
        return "";
    }

    public PlayerData getDataFromUsername(MinecraftServer server, String username) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(username);
        PlayerData data = null;
        if (player == null) {
            for (String name : this.nameUUIDs.keySet()) {
                if (!name.equalsIgnoreCase(username)) continue;
                data = new PlayerData();
                data.setNBT(PlayerData.loadPlayerData(this.nameUUIDs.get(name)));
                break;
            }
        } else {
            data = PlayerData.get((PlayerEntity)player);
        }
        return data;
    }

    public void addPlayerMessage(MinecraftServer server, String username, PlayerMail mail) {
        mail.time = System.currentTimeMillis();
        PlayerData data = this.getDataFromUsername(server, username);
        data.mailData.playermail.add(mail.copy());
        data.save(false);
    }
}

