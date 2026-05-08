/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.listener.ServerPlayPacketListener
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.server.network.ServerPlayNetworkHandler
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package noppes.npcs.packets;

import java.util.concurrent.CompletableFuture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.util.LogWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class IPacketServer
implements Packet<ServerPlayPacketListener> {
    private static final Logger LOGGER = LogManager.getLogger();
    public ServerPlayerEntity player;
    public EntityNPCInterface npc;

    public void handle(ServerPlayPacketListener handler) {
        this.enqueueWork(() -> {
            try {
                this.player = ((ServerPlayNetworkHandler)handler).player;
                this.npc = NoppesUtilServer.getEditingNpc((PlayerEntity)this.player);
                if (this.requiresNpc() && this.npc == null) {
                    return;
                }
                if (this.getPermission() != null && !CustomNpcsPermissions.hasPermission(this.player, this.getPermission())) {
                    return;
                }
                if (!this.toolAllowed(this.player.getInventory().getMainHandStack())) {
                    this.warn("tried to use custom npcs without a tool in hand, possibly a hacker");
                    return;
                }
                this.handle();
            }
            catch (Throwable e) {
                LogWriter.except(e);
                throw e;
            }
        });
    }

    public boolean requiresNpc() {
        return false;
    }

    public PermissionNodeCompat<Boolean> getPermission() {
        return null;
    }

    public boolean toolAllowed(ItemStack item) {
        return item.getItem() == CustomItems.wand;
    }

    public abstract void handle();

    private void warn(String warning) {
        LOGGER.warn(this.player.getName().getString() + ": " + warning + " - " + String.valueOf(this));
    }

    public CompletableFuture<Void> enqueueWork(Runnable runnable) {
        runnable.run();
        return CompletableFuture.completedFuture(null);
    }
}

