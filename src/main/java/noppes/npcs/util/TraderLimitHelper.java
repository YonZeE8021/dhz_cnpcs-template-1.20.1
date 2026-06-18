package noppes.npcs.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import noppes.npcs.client.ClientTraderLimitCache;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerTraderData;
import noppes.npcs.roles.RoleTrader;
import noppes.npcs.roles.TraderSlotLimit;
import noppes.npcs.roles.TraderTimeUnit;
import noppes.npcs.roles.TraderVariableDef;
import noppes.npcs.roles.TraderVariableOperation;

public class TraderLimitHelper {
    public static final int REASON_NONE = 0;
    public static final int REASON_PLAYER_LIMIT = 1;
    public static final int REASON_VARIABLE = 2;
    public static final int REASON_COOLDOWN = 3;
    public static final int REASON_STOCK_FULL = 4;

    public static final int REFRESH_NONE = 0;
    public static final int REFRESH_REAL_MS = 1;
    public static final int REFRESH_GAME_TICKS = 2;
    public static final int REFRESH_ONLINE_TICKS = 3;

    public static class LimitResult {
        public final boolean allowed;
        public final int reason;
        public final int playerRemaining;

        public LimitResult(boolean allowed, int reason, int playerRemaining) {
            this.allowed = allowed;
            this.reason = reason;
            this.playerRemaining = playerRemaining;
        }
    }

    public static LimitResult checkPurchase(PlayerEntity player, RoleTrader role, int slot) {
        TraderSlotLimit limit = role.getSlotLimit(slot);
        PlayerTraderData data = PlayerData.get(player).traderData;
        String scopeKey = RoleTrader.getScopeKey(role);
        TraderLimitHelper.resetPlayerPeriodIfNeeded(player, data, scopeKey, slot, limit, role.npc.getWorld());
        int playerRemaining = TraderLimitHelper.getPlayerRemaining(data, scopeKey, slot, limit);
        if (limit.hasPlayerLimit() && playerRemaining <= 0) {
            int reason = limit.playerLimitType == TraderSlotLimit.PLAYER_REAL_TIME && limit.usesTimedPeriod() ? REASON_COOLDOWN : REASON_PLAYER_LIMIT;
            return new LimitResult(false, reason, playerRemaining);
        }
        if (limit.hasVariableOps()) {
            int varReason = TraderVariableHelper.getBlockReason(role, limit.variableOps);
            if (varReason != REASON_NONE) {
                return new LimitResult(false, varReason, playerRemaining);
            }
        }
        return new LimitResult(true, REASON_NONE, playerRemaining);
    }

    public static boolean canPurchase(PlayerEntity player, RoleTrader role, int slot) {
        return TraderLimitHelper.checkPurchase(player, role, slot).allowed;
    }

    public static void recordPurchase(PlayerEntity player, RoleTrader role, int slot) {
        TraderSlotLimit limit = role.getSlotLimit(slot);
        if (limit.hasPlayerLimit()) {
            PlayerTraderData data = PlayerData.get(player).traderData;
            String scopeKey = RoleTrader.getScopeKey(role);
            TraderLimitHelper.resetPlayerPeriodIfNeeded(player, data, scopeKey, slot, limit, role.npc.getWorld());
            PlayerTraderData.SlotRecord record = data.getRecord(scopeKey, slot);
            ++record.count;
            record.lastTime = System.currentTimeMillis();
            if (limit.playerLimitType == TraderSlotLimit.PLAYER_GAME_TIME) {
                record.periodMarker = role.npc.getWorld().getTime();
            } else if (limit.playerLimitType == TraderSlotLimit.PLAYER_ONLINE_TIME) {
                record.onlinePeriodStart = TraderVariableHelper.getPlayerPlayTicks(player);
            } else if (limit.playerLimitType == TraderSlotLimit.PLAYER_REAL_TIME) {
                record.periodMarker = System.currentTimeMillis();
            }
            PlayerData.get(player).save(false);
        }
        if (limit.hasVariableOps()) {
            TraderVariableHelper.applyOperations(role, limit.variableOps);
        }
    }

    public static int getPlayerRemaining(PlayerEntity player, RoleTrader role, int slot) {
        TraderSlotLimit limit = role.getSlotLimit(slot);
        if (!limit.hasPlayerLimit()) {
            return -1;
        }
        PlayerTraderData data = PlayerData.get(player).traderData;
        String scopeKey = RoleTrader.getScopeKey(role);
        TraderLimitHelper.resetPlayerPeriodIfNeeded(player, data, scopeKey, slot, limit, role.npc.getWorld());
        return TraderLimitHelper.getPlayerRemaining(data, scopeKey, slot, limit);
    }

    public static int[] getPlayerRemainingAll(PlayerEntity player, RoleTrader role) {
        int[] remaining = new int[18];
        for (int i = 0; i < 18; ++i) {
            remaining[i] = TraderLimitHelper.getPlayerRemaining(player, role, i);
        }
        return remaining;
    }

    public static int[] getPlayerMaxAll(RoleTrader role) {
        int[] max = new int[18];
        for (int i = 0; i < 18; ++i) {
            TraderSlotLimit limit = role.getSlotLimit(i);
            max[i] = limit.hasPlayerLimit() ? limit.getEffectivePlayerLimitCount() : -1;
        }
        return max;
    }

    public static boolean[] getTradeBlockedAll(PlayerEntity player, RoleTrader role) {
        boolean[] blocked = new boolean[18];
        for (int i = 0; i < 18; ++i) {
            blocked[i] = !TraderLimitHelper.checkPurchase(player, role, i).allowed;
        }
        return blocked;
    }

    public static int[] getBlockReasonAll(PlayerEntity player, RoleTrader role) {
        int[] reasons = new int[18];
        for (int i = 0; i < 18; ++i) {
            reasons[i] = TraderLimitHelper.checkPurchase(player, role, i).reason;
        }
        return reasons;
    }

    public static String getLimitShortLabel(TraderSlotLimit limit) {
        switch (limit.playerLimitType) {
            case TraderSlotLimit.PLAYER_ONCE: {
                return "终";
            }
            case TraderSlotLimit.PLAYER_REAL_TIME: {
                return "现";
            }
            case TraderSlotLimit.PLAYER_GAME_TIME: {
                return "游";
            }
            case TraderSlotLimit.PLAYER_ONLINE_TIME: {
                return "在";
            }
            default: {
                return "不";
            }
        }
    }

    public static long[] getLimitRefreshRemainingAll(PlayerEntity player, RoleTrader role) {
        long[] remaining = new long[18];
        for (int i = 0; i < 18; ++i) {
            remaining[i] = TraderLimitHelper.getLimitRefreshRemaining(player, role, i);
        }
        return remaining;
    }

    public static int[] getLimitRefreshTypeAll(RoleTrader role) {
        int[] types = new int[18];
        for (int i = 0; i < 18; ++i) {
            types[i] = TraderLimitHelper.getLimitRefreshType(role.getSlotLimit(i));
        }
        return types;
    }

    @SuppressWarnings("unchecked")
    public static List<ClientTraderLimitCache.VariableTip>[] getSlotVariableTipsAll(PlayerEntity player, RoleTrader role) {
        List<ClientTraderLimitCache.VariableTip>[] tips = new List[18];
        for (int i = 0; i < 18; ++i) {
            tips[i] = TraderLimitHelper.getSlotVariableTips(player, role, i);
        }
        return tips;
    }

    public static List<ClientTraderLimitCache.VariableTip> getSlotVariableTips(PlayerEntity player, RoleTrader role, int slot) {
        ArrayList<ClientTraderLimitCache.VariableTip> tips = new ArrayList<>();
        TraderSlotLimit limit = role.getSlotLimit(slot);
        for (TraderVariableOperation op : limit.variableOps) {
            if (op.variableName == null || op.variableName.isEmpty()) {
                continue;
            }
            TraderVariableDef def = TraderVariableHelper.findDef(role, op.variableName);
            if (def == null || !def.playerVisible) {
                continue;
            }
            long restock = TraderVariableHelper.getRestockRemaining(role, def);
            tips.add(new ClientTraderLimitCache.VariableTip(def.name, TraderVariableHelper.getValue(role, def), def.maxValue, restock, TraderVariableHelper.getRestockType(def)));
        }
        return tips;
    }

    public static long getLimitRefreshRemaining(PlayerEntity player, RoleTrader role, int slot) {
        TraderSlotLimit limit = role.getSlotLimit(slot);
        if (!limit.usesTimedPeriod()) {
            return -1L;
        }
        PlayerTraderData data = PlayerData.get(player).traderData;
        String scopeKey = RoleTrader.getScopeKey(role);
        PlayerTraderData.SlotRecord record = data.getRecord(scopeKey, slot);
        World world = role.npc.getWorld();
        if (limit.playerLimitType == TraderSlotLimit.PLAYER_REAL_TIME) {
            long periodMs = TraderTimeUnit.toRealMillis(limit.playerLimitTime, limit.playerLimitTimeUnit);
            if (periodMs <= 0L || record.periodMarker <= 0L) {
                return periodMs;
            }
            return Math.max(0L, periodMs - (System.currentTimeMillis() - record.periodMarker));
        }
        if (limit.playerLimitType == TraderSlotLimit.PLAYER_GAME_TIME) {
            long periodTicks = TraderTimeUnit.toGameTicks(limit.playerLimitTime, limit.playerLimitTimeUnit);
            if (periodTicks <= 0L || record.periodMarker <= 0L) {
                return periodTicks;
            }
            return Math.max(0L, periodTicks - (world.getTime() - record.periodMarker));
        }
        if (limit.playerLimitType == TraderSlotLimit.PLAYER_ONLINE_TIME) {
            long periodTicks = TraderTimeUnit.toOnlineTicks(limit.playerLimitTime, limit.playerLimitTimeUnit);
            if (periodTicks <= 0L || record.onlinePeriodStart <= 0L) {
                return periodTicks;
            }
            return Math.max(0L, periodTicks - (TraderVariableHelper.getPlayerPlayTicks(player) - record.onlinePeriodStart));
        }
        return -1L;
    }

    public static int getLimitRefreshType(TraderSlotLimit limit) {
        if (!limit.usesTimedPeriod()) {
            return REFRESH_NONE;
        }
        if (limit.playerLimitType == TraderSlotLimit.PLAYER_REAL_TIME) {
            return REFRESH_REAL_MS;
        }
        if (limit.playerLimitType == TraderSlotLimit.PLAYER_GAME_TIME) {
            return REFRESH_GAME_TICKS;
        }
        if (limit.playerLimitType == TraderSlotLimit.PLAYER_ONLINE_TIME) {
            return REFRESH_ONLINE_TICKS;
        }
        return REFRESH_NONE;
    }

    private static void resetPlayerPeriodIfNeeded(PlayerEntity player, PlayerTraderData data, String scopeKey, int slot, TraderSlotLimit limit, World world) {
        if (!limit.hasPlayerLimit() || limit.playerLimitType == TraderSlotLimit.PLAYER_ONCE) {
            return;
        }
        PlayerTraderData.SlotRecord record = data.getRecord(scopeKey, slot);
        if (limit.playerLimitType == TraderSlotLimit.PLAYER_REAL_TIME) {
            long periodMs = TraderTimeUnit.toRealMillis(limit.playerLimitTime, limit.playerLimitTimeUnit);
            if (periodMs <= 0L) {
                return;
            }
            if (record.periodMarker <= 0L || System.currentTimeMillis() - record.periodMarker >= periodMs) {
                record.count = 0;
                record.periodMarker = System.currentTimeMillis();
            }
        } else if (limit.playerLimitType == TraderSlotLimit.PLAYER_GAME_TIME) {
            long periodTicks = TraderTimeUnit.toGameTicks(limit.playerLimitTime, limit.playerLimitTimeUnit);
            if (periodTicks <= 0L) {
                return;
            }
            if (record.periodMarker <= 0L || world.getTime() - record.periodMarker >= periodTicks) {
                record.count = 0;
                record.periodMarker = world.getTime();
            }
        } else if (limit.playerLimitType == TraderSlotLimit.PLAYER_ONLINE_TIME) {
            long periodTicks = TraderTimeUnit.toOnlineTicks(limit.playerLimitTime, limit.playerLimitTimeUnit);
            if (periodTicks <= 0L) {
                return;
            }
            long playTicks = TraderVariableHelper.getPlayerPlayTicks(player);
            if (record.onlinePeriodStart <= 0L || playTicks - record.onlinePeriodStart >= periodTicks) {
                record.count = 0;
                record.onlinePeriodStart = playTicks;
            }
        }
    }

    private static int getPlayerRemaining(PlayerTraderData data, String scopeKey, int slot, TraderSlotLimit limit) {
        if (!limit.hasPlayerLimit()) {
            return -1;
        }
        int max = limit.getEffectivePlayerLimitCount();
        PlayerTraderData.SlotRecord record = data.getRecord(scopeKey, slot);
        return Math.max(0, max - record.count);
    }
}
