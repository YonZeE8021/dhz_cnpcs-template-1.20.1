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
import noppes.npcs.controllers.data.PlayerSkinData;
import noppes.npcs.controllers.data.PlayerTransportData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataTimers;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.util.NBTJsonUtil;

public class PlayerData {
    private static Map<Integer, PlayerData> dataMap = new HashMap<Integer, PlayerData>();
    public BlockPos scriptBlockPos = BlockPos.ORIGIN;
    public PlayerDialogData dialogData = new PlayerDialogData();
    public PlayerBankData bankData = new PlayerBankData();
    public PlayerQuestData questData = new PlayerQuestData();
    public PlayerTransportData transportData = new PlayerTransportData();
    public PlayerFactionData factionData = new PlayerFactionData();
    public PlayerItemGiverData itemgiverData = new PlayerItemGiverData();
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
    private static final Identifier key = new Identifier("customnpcs", "playerdata");

    public void setNBT(NbtCompound data) {
        this.dialogData.loadNBTData(data);
        this.bankData.loadNBTData(data);
        this.questData.loadNBTData(data);
        this.transportData.loadNBTData(data);
        this.factionData.loadNBTData(data);
        this.itemgiverData.loadNBTData(data);
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
        NbtCompound compound = this.getNBT();
        String filename = this.uuid + ".json";
        CustomNPCsScheduler.runTack(() -> {
            try {
                File saveDir = CustomNpcs.getLevelSaveDirectory("playerdata");
                File file = new File(saveDir, filename + "_new");
                File file1 = new File(saveDir, filename);
                NBTJsonUtil.SaveFile(file, compound);
                if (file1.exists()) {
                    file1.delete();
                }
                file.renameTo(file1);
            }
            catch (Exception e) {
                LogWriter.except(e);
            }
        });
        if (update) {
            this.updateClient = true;
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
        }
        return new NbtCompound();
    }

    public static PlayerData get(PlayerEntity player) {
        if (player.getWorld().isClient) {
            return CustomNpcs.proxy.getPlayerData(player);
        }
        PlayerData data = dataMap.computeIfAbsent(player.getId(), i -> new PlayerData());
        if (data.player == null) {
            data.player = player;
            data.playerLevel = player.experienceLevel;
            data.scriptData = new PlayerScriptData(player);
            NbtCompound compound = PlayerData.loadPlayerData(player.getUuid().toString());
            data.setNBT(compound);
        }
        return data;
    }
}

