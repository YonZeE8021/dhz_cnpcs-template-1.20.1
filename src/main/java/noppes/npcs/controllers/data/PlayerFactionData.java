/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.controllers.data;

import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.event.PlayerEvent;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerScriptData;

public class PlayerFactionData {
    public HashMap<Integer, Integer> factionData = new HashMap();

    public void loadNBTData(NbtCompound compound) {
        HashMap<Integer, Integer> factionData = new HashMap<Integer, Integer>();
        if (compound == null) {
            return;
        }
        NbtList list = compound.getList("FactionData", 10);
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); ++i) {
            NbtCompound nbttagcompound = list.getCompound(i);
            factionData.put(nbttagcompound.getInt("Faction"), nbttagcompound.getInt("Points"));
        }
        this.factionData = factionData;
    }

    public void saveNBTData(NbtCompound compound) {
        NbtList list = new NbtList();
        for (int faction : this.factionData.keySet()) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putInt("Faction", faction);
            nbttagcompound.putInt("Points", this.factionData.get(faction).intValue());
            list.add(nbttagcompound);
        }
        compound.put("FactionData", (NbtElement)list);
    }

    public int getFactionPoints(PlayerEntity player, int factionId) {
        Faction faction = FactionController.instance.getFaction(factionId);
        if (faction == null) {
            return 0;
        }
        if (!this.factionData.containsKey(factionId)) {
            if (player.getWorld().isClient) {
                this.factionData.put(factionId, faction.defaultPoints);
                return faction.defaultPoints;
            }
            PlayerScriptData handler = PlayerData.get((PlayerEntity)player).scriptData;
            PlayerWrapper wrapper = (PlayerWrapper)NpcAPI.Instance().getIEntity((Entity)player);
            PlayerEvent.FactionUpdateEvent event = new PlayerEvent.FactionUpdateEvent(wrapper, faction, faction.defaultPoints, true);
            EventHooks.OnPlayerFactionChange(handler, event);
            this.factionData.put(factionId, event.points);
            PlayerData data = PlayerData.get(player);
            data.updateClient = true;
        }
        return this.factionData.get(factionId);
    }

    public void increasePoints(PlayerEntity player, int factionId, int points) {
        PlayerEvent.FactionUpdateEvent event;
        Faction faction = FactionController.instance.getFaction(factionId);
        if (faction == null || player == null || player.getWorld().isClient) {
            return;
        }
        PlayerScriptData handler = PlayerData.get((PlayerEntity)player).scriptData;
        PlayerWrapper wrapper = (PlayerWrapper)NpcAPI.Instance().getIEntity((Entity)player);
        if (!this.factionData.containsKey(factionId)) {
            event = new PlayerEvent.FactionUpdateEvent(wrapper, faction, faction.defaultPoints, true);
            EventHooks.OnPlayerFactionChange(handler, event);
            this.factionData.put(factionId, event.points);
        }
        event = new PlayerEvent.FactionUpdateEvent(wrapper, faction, points, false);
        EventHooks.OnPlayerFactionChange(handler, event);
        this.factionData.put(factionId, this.factionData.get(factionId) + points);
    }

    public NbtCompound getPlayerGuiData() {
        NbtCompound compound = new NbtCompound();
        this.saveNBTData(compound);
        NbtList list = new NbtList();
        for (int id : this.factionData.keySet()) {
            Faction faction = FactionController.instance.getFaction(id);
            if (faction == null || faction.hideFaction) continue;
            NbtCompound com = new NbtCompound();
            faction.writeNBT(com);
            list.add(com);
        }
        compound.put("FactionList", (NbtElement)list);
        return compound;
    }
}

