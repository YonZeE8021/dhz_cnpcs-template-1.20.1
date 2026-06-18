package noppes.npcs.roles;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.data.role.IRoleTrader;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.NBTJsonUtil;
import noppes.npcs.util.TraderLimitHelper;
import noppes.npcs.util.TraderVariableHelper;

public class RoleTrader
extends RoleInterface
implements IRoleTrader {
    public String marketName = "";
    public NpcMiscInventory inventoryCurrency = new NpcMiscInventory(36);
    public NpcMiscInventory inventorySold = new NpcMiscInventory(18);
    public boolean ignoreDamage = false;
    public boolean ignoreNBT = false;
    public boolean toSave = false;
    public TraderSlotLimit[] slotLimits = TraderSlotLimit.createDefaults();
    public List<TraderVariableDef> variableDefs = new ArrayList<>();
    public HashMap<String, Integer> localVarValues = new HashMap<>();
    public HashMap<String, Long> localVarRealPeriod = new HashMap<>();
    public HashMap<String, Long> localVarGamePeriod = new HashMap<>();
    public HashMap<String, Integer> globalVarValues = new HashMap<>();
    public HashMap<String, Long> globalVarRealPeriod = new HashMap<>();
    public HashMap<String, Long> globalVarGamePeriod = new HashMap<>();

    public RoleTrader(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public NbtCompound save(NbtCompound nbttagcompound) {
        nbttagcompound.putString("TraderMarket", this.marketName);
        this.writeNBT(nbttagcompound);
        if (this.toSave && !this.npc.isClientSide()) {
            RoleTrader.save(this, this.marketName);
        }
        this.toSave = false;
        return nbttagcompound;
    }

    public NbtCompound writeNBT(NbtCompound nbttagcompound) {
        nbttagcompound.put("TraderCurrency", (NbtElement)this.inventoryCurrency.getToNBT());
        nbttagcompound.put("TraderSold", (NbtElement)this.inventorySold.getToNBT());
        nbttagcompound.putBoolean("TraderIgnoreDamage", this.ignoreDamage);
        nbttagcompound.putBoolean("TraderIgnoreNBT", this.ignoreNBT);
        NbtList limitList = new NbtList();
        for (int i = 0; i < 18; ++i) {
            limitList.add(this.slotLimits[i].writeNBT());
        }
        nbttagcompound.put("TraderLimits", (NbtElement)limitList);
        NbtList varDefs = new NbtList();
        for (TraderVariableDef def : this.variableDefs) {
            varDefs.add(def.writeNBT());
        }
        nbttagcompound.put("TraderVarDefs", (NbtElement)varDefs);
        nbttagcompound.put("TraderLocalVars", (NbtElement)this.writeIntMap(this.localVarValues));
        nbttagcompound.put("TraderLocalReal", (NbtElement)this.writeLongMap(this.localVarRealPeriod));
        nbttagcompound.put("TraderLocalGame", (NbtElement)this.writeLongMap(this.localVarGamePeriod));
        nbttagcompound.put("TraderGlobalVars", (NbtElement)this.writeIntMap(this.globalVarValues));
        nbttagcompound.put("TraderGlobalReal", (NbtElement)this.writeLongMap(this.globalVarRealPeriod));
        nbttagcompound.put("TraderGlobalGame", (NbtElement)this.writeLongMap(this.globalVarGamePeriod));
        return nbttagcompound;
    }

    private NbtCompound writeIntMap(HashMap<String, Integer> map) {
        NbtCompound tag = new NbtCompound();
        for (String key : map.keySet()) {
            tag.putInt(key, map.get(key));
        }
        return tag;
    }

    private NbtCompound writeLongMap(HashMap<String, Long> map) {
        NbtCompound tag = new NbtCompound();
        for (String key : map.keySet()) {
            tag.putLong(key, map.get(key));
        }
        return tag;
    }

    private void readIntMap(NbtCompound tag, HashMap<String, Integer> map) {
        map.clear();
        for (String key : tag.getKeys()) {
            map.put(key, tag.getInt(key));
        }
    }

    private void readLongMap(NbtCompound tag, HashMap<String, Long> map) {
        map.clear();
        for (String key : tag.getKeys()) {
            map.put(key, tag.getLong(key));
        }
    }

    @Override
    public void load(NbtCompound nbttagcompound) {
        this.marketName = nbttagcompound.getString("TraderMarket");
        this.readNBT(nbttagcompound);
    }

    public void readNBT(NbtCompound nbttagcompound) {
        this.inventoryCurrency.setFromNBT(nbttagcompound.getCompound("TraderCurrency"));
        this.inventorySold.setFromNBT(nbttagcompound.getCompound("TraderSold"));
        this.ignoreDamage = nbttagcompound.getBoolean("TraderIgnoreDamage");
        this.ignoreNBT = nbttagcompound.getBoolean("TraderIgnoreNBT");
        if (nbttagcompound.contains("TraderLimits", 9)) {
            NbtList limitList = nbttagcompound.getList("TraderLimits", 10);
            for (int i = 0; i < 18 && i < limitList.size(); ++i) {
                this.slotLimits[i].readNBT(limitList.getCompound(i));
            }
        } else {
            this.slotLimits = TraderSlotLimit.createDefaults();
        }
        this.variableDefs.clear();
        if (nbttagcompound.contains("TraderVarDefs", 9)) {
            NbtList varDefs = nbttagcompound.getList("TraderVarDefs", 10);
            for (int i = 0; i < varDefs.size(); ++i) {
                TraderVariableDef def = new TraderVariableDef();
                def.readNBT(varDefs.getCompound(i));
                this.variableDefs.add(def);
            }
        } else if (nbttagcompound.contains("TraderStockRemaining")) {
            int[] stock = nbttagcompound.getIntArray("TraderStockRemaining");
            if (stock.length > 0 && !this.hasVariableNamed("stock")) {
                TraderVariableDef def = new TraderVariableDef();
                def.name = "stock";
                def.initialValue = stock[0] >= 0 ? stock[0] : 0;
                def.maxValue = -1;
                def.playerVisible = true;
                this.variableDefs.add(def);
            }
        }
        if (nbttagcompound.contains("TraderLocalVars", 10)) {
            this.readIntMap(nbttagcompound.getCompound("TraderLocalVars"), this.localVarValues);
        }
        if (nbttagcompound.contains("TraderLocalReal", 10)) {
            this.readLongMap(nbttagcompound.getCompound("TraderLocalReal"), this.localVarRealPeriod);
        }
        if (nbttagcompound.contains("TraderLocalGame", 10)) {
            this.readLongMap(nbttagcompound.getCompound("TraderLocalGame"), this.localVarGamePeriod);
        }
        if (nbttagcompound.contains("TraderGlobalVars", 10)) {
            this.readIntMap(nbttagcompound.getCompound("TraderGlobalVars"), this.globalVarValues);
        }
        if (nbttagcompound.contains("TraderGlobalReal", 10)) {
            this.readLongMap(nbttagcompound.getCompound("TraderGlobalReal"), this.globalVarRealPeriod);
        }
        if (nbttagcompound.contains("TraderGlobalGame", 10)) {
            this.readLongMap(nbttagcompound.getCompound("TraderGlobalGame"), this.globalVarGamePeriod);
        }
        TraderVariableHelper.initVariableValues(this);
    }

    private boolean hasVariableNamed(String name) {
        for (TraderVariableDef def : this.variableDefs) {
            if (def.name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void interact(PlayerEntity player) {
        this.npc.say(player, this.npc.advanced.getInteractLine());
        try {
            RoleTrader.load(this, this.marketName);
        }
        catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        NoppesUtilServer.sendOpenGui(player, EnumGuiType.PlayerTrader, this.npc);
    }

    public boolean hasCurrency(ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        }
        for (ItemStack item : this.inventoryCurrency.items) {
            if (item.isEmpty() || !NoppesUtilPlayer.compareItems(item, itemstack, this.ignoreDamage, this.ignoreNBT)) continue;
            return true;
        }
        return false;
    }

    @Override
    public IItemStack getSold(int slot) {
        return NpcAPI.Instance().getIItemStack(this.inventorySold.getStack(slot));
    }

    @Override
    public IItemStack getCurrency1(int slot) {
        return NpcAPI.Instance().getIItemStack(this.inventoryCurrency.getStack(slot));
    }

    @Override
    public IItemStack getCurrency2(int slot) {
        return NpcAPI.Instance().getIItemStack(this.inventoryCurrency.getStack(slot + 18));
    }

    @Override
    public void set(int slot, IItemStack currency, IItemStack currency2, IItemStack sold) {
        if (sold == null) {
            throw new CustomNPCsException("Sold item was null", new Object[0]);
        }
        if (slot >= 18 || slot < 0) {
            throw new CustomNPCsException("Invalid slot: " + slot, new Object[0]);
        }
        if (currency == null) {
            currency = currency2;
            currency2 = null;
        }
        if (currency != null) {
            this.inventoryCurrency.items.set(slot, currency.getMCItemStack());
        } else {
            this.inventoryCurrency.items.set(slot, ItemStack.EMPTY);
        }
        if (currency2 != null) {
            this.inventoryCurrency.items.set(slot + 18, currency2.getMCItemStack());
        } else {
            this.inventoryCurrency.items.set(slot + 18, ItemStack.EMPTY);
        }
        this.inventorySold.items.set(slot, sold.getMCItemStack());
    }

    public TraderSlotLimit getSlotLimit(int slot) {
        if (slot < 0 || slot >= 18) {
            return new TraderSlotLimit();
        }
        return this.slotLimits[slot];
    }

    public static String getScopeKey(RoleTrader role) {
        if (role.marketName != null && !role.marketName.isEmpty()) {
            return role.marketName.toLowerCase();
        }
        return "npc_" + role.npc.getUuid();
    }

    public void persistVariables() {
        if (!this.marketName.isEmpty() && !this.npc.isClientSide()) {
            this.toSave = true;
            RoleTrader.save(this, this.marketName);
        }
    }

    @Override
    public void remove(int slot) {
        if (slot >= 18 || slot < 0) {
            throw new CustomNPCsException("Invalid slot: " + slot, new Object[0]);
        }
        this.inventoryCurrency.items.set(slot, ItemStack.EMPTY);
        this.inventoryCurrency.items.set(slot + 18, ItemStack.EMPTY);
        this.inventorySold.items.set(slot, ItemStack.EMPTY);
        this.slotLimits[slot] = new TraderSlotLimit();
    }

    @Override
    public void setMarket(String name) {
        this.marketName = name;
        RoleTrader.load(this, name);
    }

    @Override
    public String getMarket() {
        return this.marketName;
    }

    public static void save(RoleTrader r, String name) {
        if (name.isEmpty()) {
            return;
        }
        File file = RoleTrader.getFile(name + "_new");
        File file1 = RoleTrader.getFile(name);
        try {
            NBTJsonUtil.SaveFile(file, r.writeNBT(new NbtCompound()));
            if (file1.exists()) {
                file1.delete();
            }
            file.renameTo(file1);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static void load(RoleTrader role, String name) {
        if (role.npc.getWorld().isClient) {
            return;
        }
        File file = RoleTrader.getFile(name);
        if (!file.exists()) {
            return;
        }
        try {
            role.readNBT(NBTJsonUtil.LoadFile(file));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static File getFile(String name) {
        File dir = new File(CustomNpcs.getLevelSaveDirectory(), "markets");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return new File(dir, name.toLowerCase() + ".json");
    }

    public static void setMarket(EntityNPCInterface npc, String marketName) {
        if (marketName.isEmpty()) {
            return;
        }
        if (!RoleTrader.getFile(marketName).exists()) {
            RoleTrader.save((RoleTrader)npc.role, marketName);
        }
        RoleTrader.load((RoleTrader)npc.role, marketName);
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public int getPlayerLimitType(int slot) {
        return this.getSlotLimit(slot).playerLimitType;
    }

    @Override
    public void setPlayerLimitType(int slot, int type) {
        this.getSlotLimit(slot).playerLimitType = type;
    }

    @Override
    public int getPlayerLimitCount(int slot) {
        return this.getSlotLimit(slot).playerLimitCount;
    }

    @Override
    public void setPlayerLimitCount(int slot, int count) {
        this.getSlotLimit(slot).playerLimitCount = count;
    }

    @Override
    public int getPlayerLimitTime(int slot) {
        return this.getSlotLimit(slot).playerLimitTime;
    }

    @Override
    public void setPlayerLimitTime(int slot, int time) {
        this.getSlotLimit(slot).playerLimitTime = time;
    }

    @Override
    public int getPlayerLimitTimeUnit(int slot) {
        return this.getSlotLimit(slot).playerLimitTimeUnit;
    }

    @Override
    public void setPlayerLimitTimeUnit(int slot, int unit) {
        this.getSlotLimit(slot).playerLimitTimeUnit = unit;
    }

    @Override
    public int getPlayerRemaining(noppes.npcs.api.entity.IPlayer player, int slot) {
        if (player == null || player.getMCEntity() == null) {
            return -1;
        }
        return TraderLimitHelper.getPlayerRemaining((PlayerEntity)player.getMCEntity(), this, slot);
    }

    @Override
    public int getVariableValue(String name) {
        TraderVariableDef def = TraderVariableHelper.findDef(this, name);
        if (def == null) {
            return 0;
        }
        return TraderVariableHelper.getValue(this, def);
    }
}
