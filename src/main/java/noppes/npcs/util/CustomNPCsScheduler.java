/*
 * Decompiled with CFR 0.152.
 */
package noppes.npcs.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import net.minecraft.nbt.NbtCompound;
import noppes.npcs.controllers.data.PlayerData;

public class CustomNPCsScheduler {
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private static final ConcurrentHashMap<String, NbtCompound> pendingPlayerWrites = new ConcurrentHashMap<>();
    private static final Object drainLock = new Object();
    private static volatile boolean drainScheduled = false;

    public static void runTack(Runnable task, int delay) {
        executor.schedule(task, (long)delay, TimeUnit.MILLISECONDS);
    }

    public static void runTack(Runnable task) {
        executor.schedule(task, 0L, TimeUnit.MILLISECONDS);
    }

    public static void queuePlayerDataSave(String uuid, NbtCompound compound, boolean sync) {
        if (uuid == null || uuid.isEmpty()) {
            return;
        }
        pendingPlayerWrites.put(uuid, compound);
        if (sync) {
            drainPendingPlayerWrites();
            return;
        }
        schedulePlayerWriteDrain();
    }

    private static void schedulePlayerWriteDrain() {
        synchronized (drainLock) {
            if (drainScheduled) {
                return;
            }
            drainScheduled = true;
        }
        runTack(() -> {
            try {
                drainPendingPlayerWrites();
            }
            finally {
                synchronized (drainLock) {
                    drainScheduled = false;
                    if (!pendingPlayerWrites.isEmpty()) {
                        schedulePlayerWriteDrain();
                    }
                }
            }
        });
    }

    private static void drainPendingPlayerWrites() {
        Map<String, NbtCompound> batch;
        synchronized (drainLock) {
            if (pendingPlayerWrites.isEmpty()) {
                return;
            }
            batch = new HashMap<String, NbtCompound>(pendingPlayerWrites);
            pendingPlayerWrites.clear();
        }
        for (Map.Entry<String, NbtCompound> entry : batch.entrySet()) {
            PlayerData.writePlayerDataFile(entry.getKey(), entry.getValue());
        }
    }

    public static void flushAll() {
        drainPendingPlayerWrites();
    }

    public static void shutDown() {
        flushAll();
        if (!executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(30L, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            }
            catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
