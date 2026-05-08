/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.controllers.GlobalDataController;
import noppes.npcs.controllers.data.Availability;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerItemGiverData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobInterface;

public class JobItemGiver
extends JobInterface {
    public int cooldownType = 0;
    public int givingMethod = 0;
    public int cooldown = 10;
    public NpcMiscInventory inventory;
    public int itemGiverId = 0;
    public List<String> lines = new ArrayList<String>();
    private int ticks = 10;
    private List<PlayerEntity> recentlyChecked = new ArrayList<PlayerEntity>();
    private List<PlayerEntity> toCheck;
    public Availability availability = new Availability();

    public JobItemGiver(EntityNPCInterface npc) {
        super(npc);
        this.inventory = new NpcMiscInventory(9);
        this.lines.add("Have these items {player}");
    }

    @Override
    public NbtCompound save(NbtCompound nbttagcompound) {
        nbttagcompound.putInt("igCooldownType", this.cooldownType);
        nbttagcompound.putInt("igGivingMethod", this.givingMethod);
        nbttagcompound.putInt("igCooldown", this.cooldown);
        nbttagcompound.putInt("ItemGiverId", this.itemGiverId);
        nbttagcompound.put("igLines", (NbtElement)NBTTags.nbtStringList(this.lines));
        nbttagcompound.put("igJobInventory", (NbtElement)this.inventory.getToNBT());
        nbttagcompound.put("igAvailability", (NbtElement)this.availability.save(new NbtCompound()));
        return nbttagcompound;
    }

    @Override
    public void load(NbtCompound nbttagcompound) {
        this.itemGiverId = nbttagcompound.getInt("ItemGiverId");
        this.cooldownType = nbttagcompound.getInt("igCooldownType");
        this.givingMethod = nbttagcompound.getInt("igGivingMethod");
        this.cooldown = nbttagcompound.getInt("igCooldown");
        this.lines = NBTTags.getStringList(nbttagcompound.getList("igLines", 10));
        this.inventory.setFromNBT(nbttagcompound.getCompound("igJobInventory"));
        if (this.itemGiverId == 0 && GlobalDataController.instance != null) {
            this.itemGiverId = GlobalDataController.instance.incrementItemGiverId();
        }
        this.availability.load(nbttagcompound.getCompound("igAvailability"));
    }

    public NbtList newHashMapNBTList(HashMap<String, Long> lines) {
        NbtList nbttaglist = new NbtList();
        HashMap<String, Long> lines2 = lines;
        for (String s : lines2.keySet()) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putString("Line", s);
            nbttagcompound.putLong("Time", lines.get(s).longValue());
            nbttaglist.add(nbttagcompound);
        }
        return nbttaglist;
    }

    public HashMap<String, Long> getNBTLines(NbtList tagList) {
        HashMap<String, Long> map = new HashMap<String, Long>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            String line = nbttagcompound.getString("Line");
            long time = nbttagcompound.getLong("Time");
            map.put(line, time);
        }
        return map;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean giveItems(PlayerEntity player) {
        PlayerItemGiverData data = PlayerData.get((PlayerEntity)player).itemgiverData;
        if (!this.canPlayerInteract(data)) {
            return false;
        }
        Vector<ItemStack> items = new Vector<ItemStack>();
        Vector<ItemStack> toGive = new Vector<ItemStack>();
        for (ItemStack is : this.inventory.items) {
            if (is.isEmpty()) continue;
            items.add(is.copy());
        }
        if (items.isEmpty()) {
            return false;
        }
        if (this.isAllGiver()) {
            toGive = items;
        } else if (this.isRemainingGiver()) {
            for (ItemStack is : items) {
                if (this.playerHasItem(player, is.getItem())) continue;
                toGive.add(is);
            }
        } else if (this.isRandomGiver()) {
            toGive.add(((ItemStack)items.get(this.npc.getWorld().random.nextInt(items.size()))).copy());
        } else if (this.isGiverWhenNotOwnedAny()) {
            boolean ownsItems = false;
            for (ItemStack is : items) {
                if (!this.playerHasItem(player, is.getItem())) continue;
                return false;
            }
            if (ownsItems) return false;
            toGive = items;
        } else if (this.isChainedGiver()) {
            int itemIndex = data.getItemIndex(this);
            int i = 0;
            for (ItemStack item : this.inventory.items) {
                if (i == itemIndex) {
                    toGive.add(item);
                    break;
                }
                ++i;
            }
        }
        if (toGive.isEmpty()) {
            return false;
        }
        if (!this.givePlayerItems(player, toGive)) return false;
        if (!this.lines.isEmpty()) {
            this.npc.say(player, new Line(this.lines.get(this.npc.getRandom().nextInt(this.lines.size()))));
        }
        if (this.isDaily()) {
            data.setTime(this, this.getDay());
        } else {
            data.setTime(this, System.currentTimeMillis());
        }
        if (!this.isChainedGiver()) return true;
        data.setItemIndex(this, (data.getItemIndex(this) + 1) % this.inventory.items.size());
        return true;
    }

    private int getDay() {
        return (int)(this.npc.getWorld().getTime() / 24000L);
    }

    private boolean canPlayerInteract(PlayerItemGiverData data) {
        if (this.inventory.items.isEmpty()) {
            return false;
        }
        if (this.isOnTimer()) {
            if (!data.hasInteractedBefore(this)) {
                return true;
            }
            return data.getTime(this) + (long)(this.cooldown * 1000) < System.currentTimeMillis();
        }
        if (this.isGiveOnce()) {
            return !data.hasInteractedBefore(this);
        }
        if (this.isDaily()) {
            if (!data.hasInteractedBefore(this)) {
                return true;
            }
            return (long)this.getDay() > data.getTime(this);
        }
        return false;
    }

    private boolean givePlayerItems(PlayerEntity player, Vector<ItemStack> toGive) {
        if (toGive.isEmpty()) {
            return false;
        }
        if (this.freeInventorySlots(player) < toGive.size()) {
            return false;
        }
        for (ItemStack is : toGive) {
            this.npc.givePlayerItem(player, is);
        }
        return true;
    }

    private boolean playerHasItem(PlayerEntity player, Item item) {
        for (ItemStack is : player.getInventory().main) {
            if (is.isEmpty() || is.getItem() != item) continue;
            return true;
        }
        for (ItemStack is : player.getInventory().armor) {
            if (is.isEmpty() || is.getItem() != item) continue;
            return true;
        }
        return false;
    }

    private int freeInventorySlots(PlayerEntity player) {
        int i = 0;
        for (ItemStack is : player.getInventory().main) {
            if (!NoppesUtilServer.IsItemStackNull(is)) continue;
            ++i;
        }
        return i;
    }

    private boolean isRandomGiver() {
        return this.givingMethod == 0;
    }

    private boolean isAllGiver() {
        return this.givingMethod == 1;
    }

    private boolean isRemainingGiver() {
        return this.givingMethod == 2;
    }

    private boolean isGiverWhenNotOwnedAny() {
        return this.givingMethod == 3;
    }

    private boolean isChainedGiver() {
        return this.givingMethod == 4;
    }

    public boolean isOnTimer() {
        return this.cooldownType == 0;
    }

    private boolean isGiveOnce() {
        return this.cooldownType == 1;
    }

    private boolean isDaily() {
        return this.cooldownType == 2;
    }

    @Override
    public boolean aiShouldExecute() {
        if (this.npc.isAttacking()) {
            return false;
        }
        --this.ticks;
        if (this.ticks > 0) {
            return false;
        }
        this.ticks = 10;
        this.toCheck = this.npc.getWorld().getNonSpectatingEntities(PlayerEntity.class, this.npc.getBoundingBox().expand(3.0, 3.0, 3.0));
        this.toCheck.removeAll(this.recentlyChecked);
        List listMax = this.npc.getWorld().getNonSpectatingEntities(PlayerEntity.class, this.npc.getBoundingBox().expand(10.0, 10.0, 10.0));
        this.recentlyChecked.retainAll(listMax);
        this.recentlyChecked.addAll(this.toCheck);
        return this.toCheck.size() > 0;
    }

    @Override
    public boolean aiContinueExecute() {
        return false;
    }

    @Override
    public void aiStartExecuting() {
        for (PlayerEntity player : this.toCheck) {
            if (!this.npc.canNpcSee((Entity)player) || !this.availability.isAvailable(player)) continue;
            this.recentlyChecked.add(player);
            this.interact(player);
        }
    }

    @Override
    public void killed() {
    }

    private boolean interact(PlayerEntity player) {
        if (!this.giveItems(player)) {
            this.npc.say(player, this.npc.advanced.getInteractLine());
        }
        return true;
    }

    @Override
    public void delete() {
    }

    @Override
    public int getType() {
        return 4;
    }
}

