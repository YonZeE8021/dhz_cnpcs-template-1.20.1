package noppes.npcs.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import noppes.npcs.controllers.TraderGlobalVariableController;
import noppes.npcs.roles.RoleTrader;
import noppes.npcs.roles.TraderTimeUnit;
import noppes.npcs.roles.TraderVariableDef;
import noppes.npcs.roles.TraderVariableOperation;

public class TraderVariableHelper {
    public static TraderVariableDef findDef(RoleTrader role, String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        for (TraderVariableDef def : role.variableDefs) {
            if (def.name.equalsIgnoreCase(name)) {
                return def;
            }
        }
        return null;
    }

    public static int getValue(RoleTrader role, TraderVariableDef def) {
        TraderVariableHelper.resetVariableIfNeeded(role, def);
        if (def.isGlobal()) {
            if (!role.marketName.isEmpty() && role.globalVarValues.containsKey(def.getStorageKey())) {
                return role.globalVarValues.get(def.getStorageKey());
            }
            if (TraderGlobalVariableController.instance != null) {
                return TraderGlobalVariableController.instance.getValue(def.getStorageKey());
            }
            return def.initialValue;
        }
        return role.localVarValues.getOrDefault(def.getStorageKey(), def.initialValue);
    }

    public static void setValue(RoleTrader role, TraderVariableDef def, int value) {
        if (def.maxValue >= 0) {
            value = Math.min(value, def.maxValue);
        }
        value = Math.max(0, value);
        if (def.isGlobal()) {
            if (!role.marketName.isEmpty()) {
                role.globalVarValues.put(def.getStorageKey(), value);
            } else if (TraderGlobalVariableController.instance != null) {
                TraderGlobalVariableController.instance.setValue(def.getStorageKey(), value);
                TraderGlobalVariableController.instance.saveData();
            }
        } else {
            role.localVarValues.put(def.getStorageKey(), value);
        }
    }

    public static void resetVariableIfNeeded(RoleTrader role, TraderVariableDef def) {
        World world = role.npc.getWorld();
        if (world == null || def.resetType == TraderVariableDef.RESET_NONE) {
            return;
        }
        String key = def.getStorageKey();
        if (def.resetType == TraderVariableDef.RESET_REAL) {
            long periodMs = TraderTimeUnit.toRealMillis(def.resetTime, def.resetTimeUnit);
            if (periodMs <= 0L) {
                return;
            }
            long now = System.currentTimeMillis();
            long marker = TraderVariableHelper.getRealPeriod(role, def);
            if (marker <= 0L || now - marker >= periodMs) {
                TraderVariableHelper.setValue(role, def, def.initialValue);
                TraderVariableHelper.setRealPeriod(role, def, now);
            }
        } else if (def.resetType == TraderVariableDef.RESET_GAME) {
            long periodTicks = TraderTimeUnit.toGameTicks(def.resetTime, def.resetTimeUnit);
            if (periodTicks <= 0L) {
                return;
            }
            long now = world.getTime();
            long marker = TraderVariableHelper.getGamePeriod(role, def);
            if (marker <= 0L || now - marker >= periodTicks) {
                TraderVariableHelper.setValue(role, def, def.initialValue);
                TraderVariableHelper.setGamePeriod(role, def, now);
            }
        }
    }

    private static long getRealPeriod(RoleTrader role, TraderVariableDef def) {
        String key = def.getStorageKey();
        if (def.isGlobal()) {
            if (!role.marketName.isEmpty()) {
                return role.globalVarRealPeriod.getOrDefault(key, 0L);
            }
            if (TraderGlobalVariableController.instance != null) {
                return TraderGlobalVariableController.instance.getRealPeriod(key);
            }
        }
        return role.localVarRealPeriod.getOrDefault(key, 0L);
    }

    private static void setRealPeriod(RoleTrader role, TraderVariableDef def, long marker) {
        String key = def.getStorageKey();
        if (def.isGlobal()) {
            if (!role.marketName.isEmpty()) {
                role.globalVarRealPeriod.put(key, marker);
            } else if (TraderGlobalVariableController.instance != null) {
                TraderGlobalVariableController.instance.setRealPeriod(key, marker);
            }
        } else {
            role.localVarRealPeriod.put(key, marker);
        }
    }

    private static long getGamePeriod(RoleTrader role, TraderVariableDef def) {
        String key = def.getStorageKey();
        if (def.isGlobal()) {
            if (!role.marketName.isEmpty()) {
                return role.globalVarGamePeriod.getOrDefault(key, 0L);
            }
            if (TraderGlobalVariableController.instance != null) {
                return TraderGlobalVariableController.instance.getGamePeriod(key);
            }
        }
        return role.localVarGamePeriod.getOrDefault(key, 0L);
    }

    private static void setGamePeriod(RoleTrader role, TraderVariableDef def, long marker) {
        String key = def.getStorageKey();
        if (def.isGlobal()) {
            if (!role.marketName.isEmpty()) {
                role.globalVarGamePeriod.put(key, marker);
            } else if (TraderGlobalVariableController.instance != null) {
                TraderGlobalVariableController.instance.setGamePeriod(key, marker);
            }
        } else {
            role.localVarGamePeriod.put(key, marker);
        }
    }

    public static boolean canApplyOperations(RoleTrader role, List<TraderVariableOperation> ops) {
        return TraderVariableHelper.getBlockReason(role, ops) == TraderLimitHelper.REASON_NONE;
    }

    public static int getBlockReason(RoleTrader role, List<TraderVariableOperation> ops) {
        for (TraderVariableOperation op : ops) {
            if (op.variableName == null || op.variableName.isEmpty() || op.delta == 0) {
                continue;
            }
            TraderVariableDef def = TraderVariableHelper.findDef(role, op.variableName);
            if (def == null) {
                return TraderLimitHelper.REASON_VARIABLE;
            }
            int current = TraderVariableHelper.getValue(role, def);
            int next = current + op.delta;
            if (next < 0) {
                return TraderLimitHelper.REASON_VARIABLE;
            }
            if (def.maxValue >= 0 && next > def.maxValue) {
                return TraderLimitHelper.REASON_STOCK_FULL;
            }
        }
        return TraderLimitHelper.REASON_NONE;
    }

    public static void applyOperations(RoleTrader role, List<TraderVariableOperation> ops) {
        for (TraderVariableOperation op : ops) {
            if (op.variableName == null || op.variableName.isEmpty() || op.delta == 0) {
                continue;
            }
            TraderVariableDef def = TraderVariableHelper.findDef(role, op.variableName);
            if (def == null) {
                continue;
            }
            int current = TraderVariableHelper.getValue(role, def);
            TraderVariableHelper.setValue(role, def, current + op.delta);
        }
        role.persistVariables();
    }

    public static Map<String, Integer> getVisibleValues(RoleTrader role) {
        HashMap<String, Integer> map = new HashMap<>();
        for (TraderVariableDef def : role.variableDefs) {
            if (!def.playerVisible || def.name.isEmpty()) {
                continue;
            }
            map.put(def.name, TraderVariableHelper.getValue(role, def));
        }
        return map;
    }

    public static int getRestockType(TraderVariableDef def) {
        if (def.resetType == TraderVariableDef.RESET_REAL) {
            return TraderLimitHelper.REFRESH_REAL_MS;
        }
        if (def.resetType == TraderVariableDef.RESET_GAME) {
            return TraderLimitHelper.REFRESH_GAME_TICKS;
        }
        return TraderLimitHelper.REFRESH_NONE;
    }

    public static long getRestockRemaining(RoleTrader role, TraderVariableDef def) {
        World world = role.npc.getWorld();
        if (world == null || def.resetType == TraderVariableDef.RESET_NONE) {
            return -1L;
        }
        if (def.resetType == TraderVariableDef.RESET_REAL) {
            long periodMs = TraderTimeUnit.toRealMillis(def.resetTime, def.resetTimeUnit);
            if (periodMs <= 0L) {
                return -1L;
            }
            long marker = TraderVariableHelper.getRealPeriod(role, def);
            if (marker <= 0L) {
                return periodMs;
            }
            return Math.max(0L, periodMs - (System.currentTimeMillis() - marker));
        }
        long periodTicks = TraderTimeUnit.toGameTicks(def.resetTime, def.resetTimeUnit);
        if (periodTicks <= 0L) {
            return -1L;
        }
        long marker = TraderVariableHelper.getGamePeriod(role, def);
        if (marker <= 0L) {
            return periodTicks;
        }
        return Math.max(0L, periodTicks - (world.getTime() - marker));
    }

    public static void initVariableValues(RoleTrader role) {
        for (TraderVariableDef def : role.variableDefs) {
            TraderVariableHelper.resetVariableIfNeeded(role, def);
            String key = def.getStorageKey();
            if (def.isGlobal()) {
                if (!role.globalVarValues.containsKey(key)) {
                    role.globalVarValues.put(key, def.initialValue);
                }
            } else if (!role.localVarValues.containsKey(key)) {
                role.localVarValues.put(key, def.initialValue);
            }
        }
    }

    public static long getPlayerPlayTicks(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            return ((ServerPlayerEntity)player).getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_TIME));
        }
        return 0L;
    }
}
