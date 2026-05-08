/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.server.MinecraftServer
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package noppes.npcs.packets;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PacketServerBasic {
    private static final Logger LOGGER = LogManager.getLogger();
    public ServerPlayerEntity player;
    public EntityNPCInterface npc;

    public boolean requiresNpc() {
        return false;
    }

    public PermissionNodeCompat<Boolean> getPermission() {
        return null;
    }

    public boolean toolAllowed(ItemStack item) {
        return item.getItem() == CustomItems.wand;
    }

    public static void handle(PacketServerBasic msg, MinecraftServer server, ServerPlayerEntity player) {
        server.execute(() -> {
            msg.player = player;
            msg.npc = NoppesUtilServer.getEditingNpc((PlayerEntity)msg.player);
            if (msg.requiresNpc() && msg.npc == null) {
                return;
            }
            if (msg.getPermission() != null && !CustomNpcsPermissions.hasPermission(msg.player, msg.getPermission())) {
                return;
            }
            if (!msg.toolAllowed(msg.player.getInventory().getMainHandStack())) {
                msg.warn("tried to use custom npcs without a tool in hand, possibly a hacker");
                return;
            }
            msg.handle();
        });
    }

    private void warn(String warning) {
        LOGGER.warn(this.player.getName().getString() + ": " + warning + " - " + String.valueOf(this));
    }

    protected abstract void handle();
}

