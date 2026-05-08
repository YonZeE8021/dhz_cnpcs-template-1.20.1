/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.sound.PositionedSoundInstance
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.util.Util
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.registry.entry.RegistryEntry
 */
package noppes.npcs.client;

import java.io.File;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.util.Util;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.registry.entry.RegistryEntry;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketGuiOpen;

public class NoppesUtil {
    private static EntityNPCInterface lastNpc;

    public static void requestOpenGUI(EnumGuiType gui) {
        NoppesUtil.requestOpenGUI(gui, BlockPos.ORIGIN);
    }

    public static void requestOpenGUI(EnumGuiType gui, BlockPos pos) {
        Packets.sendServer(new SPacketGuiOpen(gui, pos));
    }

    public static EntityNPCInterface getLastNpc() {
        return lastNpc;
    }

    public static void setLastNpc(EntityNPCInterface npc) {
        lastNpc = npc;
    }

    public static void openGUI(PlayerEntity player, Object guiscreen) {
        CustomNpcs.proxy.openGui(player, guiscreen);
    }

    public static void openFolder(File dir) {
        Util.getOperatingSystem().open(dir);
    }

    public static void clickSound() {
        MinecraftClient.getInstance().getSoundManager().play((SoundInstance)PositionedSoundInstance.master((RegistryEntry)SoundEvents.field_15015, (float)1.0f));
    }
}

