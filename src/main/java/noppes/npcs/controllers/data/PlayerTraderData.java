package noppes.npcs.controllers.data;

import java.util.HashMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class PlayerTraderData {
    public static class SlotRecord {
        public int count;
        public long lastTime;
        public long periodMarker;
        public long onlinePeriodStart;
    }

    private final HashMap<String, HashMap<Integer, SlotRecord>> records = new HashMap<>();

    public SlotRecord getRecord(String scopeKey, int slot) {
        HashMap<Integer, SlotRecord> scope = this.records.computeIfAbsent(scopeKey, k -> new HashMap<>());
        return scope.computeIfAbsent(slot, k -> new SlotRecord());
    }

    public void loadNBTData(NbtCompound compound) {
        this.records.clear();
        if (!compound.contains("TraderPurchases", 10)) {
            return;
        }
        NbtCompound scopes = compound.getCompound("TraderPurchases");
        for (String scopeKey : scopes.getKeys()) {
            NbtCompound slots = scopes.getCompound(scopeKey);
            HashMap<Integer, SlotRecord> scope = new HashMap<>();
            for (String slotKey : slots.getKeys()) {
                try {
                    int slot = Integer.parseInt(slotKey);
                    NbtCompound recordTag = slots.getCompound(slotKey);
                    SlotRecord record = new SlotRecord();
                    record.count = recordTag.getInt("Count");
                    record.lastTime = recordTag.getLong("LastTime");
                    record.periodMarker = recordTag.getLong("PeriodMarker");
                    record.onlinePeriodStart = recordTag.getLong("OnlineStart");
                    scope.put(slot, record);
                }
                catch (NumberFormatException ignored) {
                }
            }
            this.records.put(scopeKey, scope);
        }
    }

    public void saveNBTData(NbtCompound compound) {
        NbtCompound scopes = new NbtCompound();
        for (String scopeKey : this.records.keySet()) {
            HashMap<Integer, SlotRecord> scope = this.records.get(scopeKey);
            NbtCompound slots = new NbtCompound();
            for (Integer slot : scope.keySet()) {
                SlotRecord record = scope.get(slot);
                NbtCompound recordTag = new NbtCompound();
                recordTag.putInt("Count", record.count);
                recordTag.putLong("LastTime", record.lastTime);
                recordTag.putLong("PeriodMarker", record.periodMarker);
                recordTag.putLong("OnlineStart", record.onlinePeriodStart);
                slots.put(String.valueOf(slot), recordTag);
            }
            scopes.put(scopeKey, slots);
        }
        compound.put("TraderPurchases", scopes);
    }
}
