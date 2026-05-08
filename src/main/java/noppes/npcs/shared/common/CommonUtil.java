/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Formatting
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.GameRules
 *  net.minecraft.world.World
 *  net.minecraft.text.Text
 *  net.minecraft.text.MutableText
 *  net.minecraft.server.MinecraftServer
 */
package noppes.npcs.shared.common;

import net.minecraft.util.Formatting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.shared.common.util.LogWriter;

public class CommonUtil {
    public static void NotifyOPs(MinecraftServer server, String message, Object ... obs) {
        CommonUtil.NotifyOPs(server, (Text)Text.translatable((String)message, (Object[])obs));
    }

    public static void NotifyOPs(MinecraftServer server, Text message) {
        MutableText chatcomponenttranslation = Text.literal((String)"").append(message).formatted(new Formatting[]{Formatting.GRAY, Formatting.ITALIC});
        for (PlayerEntity entityplayer : server.getPlayerManager().getPlayerList()) {
            if (!entityplayer.shouldBroadcastConsoleToOps() || !CommonUtil.isOp(entityplayer)) continue;
            entityplayer.sendMessage((Text)chatcomponenttranslation);
        }
        if (server.getWorld(World.OVERWORLD).getGameRules().getBoolean(GameRules.LOG_ADMIN_COMMANDS)) {
            LogWriter.info(chatcomponenttranslation.getString());
        }
    }

    public static boolean isOp(PlayerEntity player) {
        return player.getServer().getPlayerManager().isOperator(player.getGameProfile());
    }
}

