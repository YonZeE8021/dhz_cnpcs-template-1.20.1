package noppes.npcs.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import noppes.npcs.util.TraderLimitHelper;

public class ClientTraderLimitCache {
    public static String scopeKey = "";
    public static int[] playerRemaining = new int[18];
    public static int[] playerMax = new int[18];
    public static long[] limitRefreshRemaining = new long[18];
    public static int[] limitRefreshType = new int[18];
    public static boolean[] tradeBlocked = new boolean[18];
    public static int[] blockReason = new int[18];
    public static final Map<String, Integer> visibleVariables = new HashMap<>();
    public static final List<VariableTip>[] slotVariables = new List[18];
    private static long syncRealMs;
    private static long syncGameTime;

    static {
        for (int i = 0; i < 18; ++i) {
            ClientTraderLimitCache.slotVariables[i] = new ArrayList<>();
        }
    }

    public static class VariableTip {
        public final String name;
        public final int value;
        public final int maxValue;
        public final long restockRemaining;
        public final int restockType;

        public VariableTip(String name, int value, int maxValue, long restockRemaining, int restockType) {
            this.name = name;
            this.value = value;
            this.maxValue = maxValue;
            this.restockRemaining = restockRemaining;
            this.restockType = restockType;
        }
    }

    public static void set(String key, int[] player, int[] max, long[] refresh, int[] refreshType, boolean[] blocked, int[] reasons, Map<String, Integer> variables, List<VariableTip>[] slotVars) {
        ClientTraderLimitCache.scopeKey = key;
        ClientTraderLimitCache.playerRemaining = player;
        ClientTraderLimitCache.playerMax = max;
        ClientTraderLimitCache.limitRefreshRemaining = refresh;
        ClientTraderLimitCache.limitRefreshType = refreshType;
        ClientTraderLimitCache.tradeBlocked = blocked;
        ClientTraderLimitCache.blockReason = reasons;
        ClientTraderLimitCache.syncRealMs = System.currentTimeMillis();
        MinecraftClient client = MinecraftClient.getInstance();
        ClientTraderLimitCache.syncGameTime = client.world != null ? client.world.getTime() : 0L;
        ClientTraderLimitCache.visibleVariables.clear();
        if (variables != null) {
            ClientTraderLimitCache.visibleVariables.putAll(variables);
        }
        for (int i = 0; i < 18; ++i) {
            ClientTraderLimitCache.slotVariables[i].clear();
            if (slotVars != null && slotVars[i] != null) {
                ClientTraderLimitCache.slotVariables[i].addAll(slotVars[i]);
            }
        }
    }

    public static void clear() {
        ClientTraderLimitCache.scopeKey = "";
        ClientTraderLimitCache.playerRemaining = new int[18];
        ClientTraderLimitCache.playerMax = new int[18];
        ClientTraderLimitCache.limitRefreshRemaining = new long[18];
        ClientTraderLimitCache.limitRefreshType = new int[18];
        ClientTraderLimitCache.tradeBlocked = new boolean[18];
        ClientTraderLimitCache.blockReason = new int[18];
        ClientTraderLimitCache.visibleVariables.clear();
        ClientTraderLimitCache.syncRealMs = 0L;
        ClientTraderLimitCache.syncGameTime = 0L;
        for (int i = 0; i < 18; ++i) {
            ClientTraderLimitCache.slotVariables[i].clear();
        }
    }

    public static long getEffectiveRefreshRemaining(long base, int type) {
        if (base < 0L || type == TraderLimitHelper.REFRESH_NONE) {
            return -1L;
        }
        if (type == TraderLimitHelper.REFRESH_REAL_MS) {
            return Math.max(0L, base - (System.currentTimeMillis() - ClientTraderLimitCache.syncRealMs));
        }
        if (type == TraderLimitHelper.REFRESH_GAME_TICKS) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null) {
                return base;
            }
            return Math.max(0L, base - (client.world.getTime() - ClientTraderLimitCache.syncGameTime));
        }
        if (type == TraderLimitHelper.REFRESH_ONLINE_TICKS) {
            long elapsedTicks = (System.currentTimeMillis() - ClientTraderLimitCache.syncRealMs) / 50L;
            return Math.max(0L, base - elapsedTicks);
        }
        return base;
    }

    public static long getEffectiveLimitRefresh(int slot) {
        return ClientTraderLimitCache.getEffectiveRefreshRemaining(ClientTraderLimitCache.limitRefreshRemaining[slot], ClientTraderLimitCache.limitRefreshType[slot]);
    }
}
