/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.util.Identifier
 */
package noppes.npcs.controllers.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.data.PlayerBankData;
import noppes.npcs.controllers.data.PlayerDialogData;
import noppes.npcs.controllers.data.PlayerFactionData;
import noppes.npcs.controllers.data.PlayerItemGiverData;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.controllers.data.PlayerScriptData;
import noppes.npcs.controllers.data.PlayerTraderData;
import noppes.npcs.controllers.data.PlayerTransportData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataTimers;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.util.NBTJsonUtil;

public class PlayerData {
    private static final Map<UUID, PlayerData> dataMap = new HashMap<UUID, PlayerData>();
    private static final long QUEST_SAVE_DEBOUNCE_MS = 30000L;
    public BlockPos scriptBlockPos = BlockPos.ORIGIN;
    public PlayerDialogData dialogData = new PlayerDialogData();
    public PlayerBankData bankData = new PlayerBankData();
    public PlayerQuestData questData = new PlayerQuestData();
    public PlayerTransportData transportData = new PlayerTransportData();
    public PlayerFactionData factionData = new PlayerFactionData();
    public PlayerItemGiverData itemgiverData = new PlayerItemGiverData();
    public PlayerTraderData traderData = new PlayerTraderData();
    public PlayerMailData mailData = new PlayerMailData();
    public PlayerSkinData skinData = new PlayerSkinData();
    public PlayerScriptData scriptData;
    public NbtCompound scriptStoreddata = new NbtCompound();
    public DataTimers timers = new DataTimers(this);
    public EntityNPCInterface editingNpc;
    public NbtCompound cloned;
    public PlayerEntity player;
    public String playername = "";
    public String uuid = "";
    private EntityNPCInterface activeCompanion = null;
    public int companionID = 0;
    public int playerLevel = 0;
    public boolean updateClient = false;
    public int dialogId = -1;
    public ItemStack prevHeldItem = ItemStack.EMPTY;
    public Entity mounted;
    public UUID iAmStealingYourDatas = UUID.randomUUID();
    private long lastQuestProgressSaveMs = 0L;
    private static final Identifier key = new Identifier("customnpcs", "playerdata");

    public void setNBT(NbtCompound data) {
        this.dialogData.loadNBTData(data);
        this.bankData.loadNBTData(data);
        this.questData.loadNBTData(data);
        this.transportData.loadNBTData(data);
        this.factionData.loadNBTData(data);
        this.itemgiverData.loadNBTData(data);
        this.traderData.loadNBTData(data);
        this.mailData.loadNBTData(data);
        this.skinData.loadNBTData(data);
        this.timers.load(data);
        if (this.player != null) {
            this.playername = this.player.getName().getString();
            this.uuid = this.player.getUuid().toString();
        } else {
            this.playername = data.getString("PlayerName");
            this.uuid = data.getString("UUID");
        }
        this.companionID = data.getInt("PlayerCompanionId");
        if (data.contains("PlayerCompanion") && !this.hasCompanion() && this.player != null) {
            EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, this.player.getWorld());
            npc.readCustomDataFromNbt(data.getCompound("PlayerCompanion"));
            npc.setPosition(this.player.getX(), this.player.getY(), this.player.getZ());
            if (npc.role.getType() == 6) {
                ((RoleCompanion)npc.role).setSitting(false);
                this.player.getWorld().spawnEntity((Entity)npc);
                this.setCompanion(npc);
            }
        }
        this.scriptStoreddata = data.getCompound("ScriptStoreddata");
    }

    public NbtCompound getSyncNBT() {
        NbtCompound compound = new NbtCompound();
        this.dialogData.saveNBTData(compound);
        this.questData.saveNBTData(compound);
        this.factionData.saveNBTData(compound);
        return compound;
    }

    public NbtCompound getNBT() {
        NbtCompound nbt;
        if (this.player != null) {
            this.playername = this.player.getName().getString();
            this.uuid = this.player.getUuid().toString();
        }
        NbtCompound compound = new NbtCompound();
        this.dialogData.saveNBTData(compound);
        this.bankData.saveNBTData(compound);
        this.questData.saveNBTData(compound);
        this.transportData.saveNBTData(compound);
        this.factionData.saveNBTData(compound);
        this.itemgiverData.saveNBTData(compound);
        this.traderData.saveNBTData(compound);
        this.mailData.saveNBTData(compound);
        this.skinData.saveNBTData(compound);
        this.timers.save(compound);
        compound.putString("PlayerName", this.playername);
        compound.putString("UUID", this.uuid);
        compound.putInt("PlayerCompanionId", this.companionID);
        compound.put("ScriptStoreddata", (NbtElement)this.scriptStoreddata);
        if (this.hasCompanion() && this.activeCompanion.saveSelfNbt(nbt = new NbtCompound())) {
            compound.put("PlayerCompanion", (NbtElement)nbt);
        }
        return compound;
    }

    public boolean hasCompanion() {
        return this.activeCompanion != null && !this.activeCompanion.isRemoved();
    }

    public void setCompanion(EntityNPCInterface npc) {
        if (npc != null && npc.role.getType() != 6) {
            return;
        }
        ++this.companionID;
        this.activeCompanion = npc;
        if (npc != null) {
            ((RoleCompanion)npc.role).companionID = this.companionID;
        }
        this.save(false);
    }

    public void updateCompanion(World level) {
        if (!this.hasCompanion() || level == this.activeCompanion.getWorld()) {
            return;
        }
        RoleCompanion role = (RoleCompanion)this.activeCompanion.role;
        role.owner = this.player;
        if (!role.isFollowing()) {
            return;
        }
        NbtCompound nbt = new NbtCompound();
        this.activeCompanion.saveSelfNbt(nbt);
        this.activeCompanion.discard();
        EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, level);
        npc.readCustomDataFromNbt(nbt);
        npc.setPosition(this.player.getX(), this.player.getY(), this.player.getZ());
        this.setCompanion(npc);
        ((RoleCompanion)npc.role).setSitting(false);
        level.spawnEntity((Entity)npc);
    }

    public synchronized void save(boolean update) {
        this.save(update, false);
    }

    public synchronized void save(boolean update, boolean sync) {
        if (this.uuid == null || this.uuid.isEmpty()) {
            return;
        }
        NbtCompound compound = this.getNBT();
        CustomNPCsScheduler.queuePlayerDataSave(this.uuid, compound, sync);
        if (update) {
            this.updateClient = true;
        }
    }

    public void saveQuestProgress(boolean force) {
        long now = System.currentTimeMillis();
        if (!force && now - this.lastQuestProgressSaveMs < QUEST_SAVE_DEBOUNCE_MS) {
            return;
        }
        this.lastQuestProgressSaveMs = now;
        this.save(false);
    }

    public static void writePlayerDataFile(String uuid, NbtCompound compound) {
        try {
            File saveDir = CustomNpcs.getLevelSaveDirectory("playerdata");
            if (saveDir == null) {
                return;
            }
            String filename = uuid + ".json";
            File target = new File(saveDir, filename);
            File temp = new File(saveDir, filename + "_new");
            File backup = new File(saveDir, filename + ".bak");
            NBTJsonUtil.SaveFile(temp, compound);
            if (target.exists()) {
                if (backup.exists()) {
                    backup.delete();
                }
                target.renameTo(backup);
            }
            if (!temp.renameTo(target)) {
                LogWriter.error("Failed to rename player data file: " + target.getAbsolutePath());
            }
        }
        catch (Exception e) {
            LogWriter.except(e);
        }
    }

    public static NbtCompound loadPlayerData(String player) {
        File saveDir = CustomNpcs.getLevelSaveDirectory("playerdata");
        Object filename = player;
        if (((String)filename).isEmpty()) {
            filename = "noplayername";
        }
        filename = (String)filename + ".json";
        File file = null;
        try {
            file = new File(saveDir, (String)filename);
            if (file.exists()) {
                return NBTJsonUtil.LoadFile(file);
            }
        }
        catch (Exception e) {
            LogWriter.error("Error loading: " + file.getAbsolutePath(), e);
            File backup = new File(saveDir, (String)filename + ".bak");
            if (backup.exists()) {
                try {
                    LogWriter.warn("Loading player data from backup: " + backup.getAbsolutePath());
                    return NBTJsonUtil.LoadFile(backup);
                }
                catch (Exception backupError) {
                    LogWriter.error("Error loading backup: " + backup.getAbsolutePath(), backupError);
                }
            }
        }
        return new NbtCompound();
    }

    public static PlayerData get(PlayerEntity player) {
        if (player.getWorld().isClient) {
            return CustomNpcs.proxy.getPlayerData(player);
        }
        UUID playerUuid = player.getUuid();
        PlayerData data = dataMap.get(playerUuid);
        if (data == null) {
            data = new PlayerData();
            dataMap.put(playerUuid, data);
        }
        if (data.player == null) {
            data.player = player;
            data.playerLevel = player.experienceLevel;
            data.scriptData = new PlayerScriptData(player);
            NbtCompound compound = PlayerData.loadPlayerData(playerUuid.toString());
            data.setNBT(compound);
        } else {
            data.player = player;
            if (data.scriptData == null) {
                data.scriptData = new PlayerScriptData(player);
            }
        }
        return data;
    }

    public static void removeFromCache(UUID uuid) {
        dataMap.remove(uuid);
    }
}
