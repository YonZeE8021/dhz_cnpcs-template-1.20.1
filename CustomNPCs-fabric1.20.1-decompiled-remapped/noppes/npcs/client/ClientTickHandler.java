/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents$StartTick
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.PlayerScreenHandler
 *  net.minecraft.world.World
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.util.InputUtil
 */
package noppes.npcs.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.world.World;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.client.gui.player.GuiQuestLog;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketPlayerKeyPressed;
import noppes.npcs.packets.server.SPacketQuestCompletionCheckAll;
import noppes.npcs.packets.server.SPacketSceneReset;
import noppes.npcs.packets.server.SPacketSceneStart;

public class ClientTickHandler
implements ClientTickEvents.StartTick {
    private World prevLevel;
    private boolean otherContainer = false;
    private final int[] ignoreKeys = new int[]{341, 340, 342, 343, 345, 344, 346, 347};

    public void onStartTick(MinecraftClient client) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && mc.player.currentScreenHandler instanceof PlayerScreenHandler) {
            if (this.otherContainer) {
                Packets.sendServer(new SPacketQuestCompletionCheckAll());
                this.otherContainer = false;
            }
        } else {
            this.otherContainer = true;
        }
        ++CustomNpcs.ticks;
        ++RenderNPCInterface.LastTextureTick;
        if (this.prevLevel != mc.world) {
            this.prevLevel = mc.world;
            MusicController.Instance.stopMusic();
        }
    }

    public static void onKey(int key, int scancode, int modifiers, int action) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.world == null || mc.getNetworkHandler() == null) {
            return;
        }
        if (CustomNpcs.SceneButtonsEnabled) {
            if (ClientProxy.Scene1.isPressed()) {
                Packets.sendServer(new SPacketSceneStart(1));
            }
            if (ClientProxy.Scene2.isPressed()) {
                Packets.sendServer(new SPacketSceneStart(2));
            }
            if (ClientProxy.Scene3.isPressed()) {
                Packets.sendServer(new SPacketSceneStart(3));
            }
            if (ClientProxy.SceneReset.isPressed()) {
                Packets.sendServer(new SPacketSceneReset());
            }
        }
        if (ClientProxy.QuestLog.isPressed()) {
            if (mc.currentScreen == null) {
                NoppesUtil.openGUI((PlayerEntity)mc.player, new GuiQuestLog((PlayerEntity)mc.player));
            } else if (mc.currentScreen instanceof GuiQuestLog) {
                mc.mouse.lockCursor();
            }
        }
        if (action == 1 || action == 0) {
            boolean isCtrlPressed = InputUtil.isKeyPressed((long)MinecraftClient.getInstance().getWindow().getHandle(), (int)341) || InputUtil.isKeyPressed((long)MinecraftClient.getInstance().getWindow().getHandle(), (int)345);
            boolean isShiftPressed = InputUtil.isKeyPressed((long)MinecraftClient.getInstance().getWindow().getHandle(), (int)340) || InputUtil.isKeyPressed((long)MinecraftClient.getInstance().getWindow().getHandle(), (int)344);
            boolean isAltPressed = InputUtil.isKeyPressed((long)MinecraftClient.getInstance().getWindow().getHandle(), (int)342) || InputUtil.isKeyPressed((long)MinecraftClient.getInstance().getWindow().getHandle(), (int)346);
            boolean isMetaPressed = InputUtil.isKeyPressed((long)MinecraftClient.getInstance().getWindow().getHandle(), (int)343) || InputUtil.isKeyPressed((long)MinecraftClient.getInstance().getWindow().getHandle(), (int)347);
            String openGui = mc.currentScreen == null ? "" : mc.currentScreen.getClass().getName();
            Packets.sendServer(new SPacketPlayerKeyPressed(key, isCtrlPressed, isShiftPressed, isAltPressed, isMetaPressed, action == 0, openGui));
        }
    }

    private boolean isIgnoredKey(int key) {
        for (int i : this.ignoreKeys) {
            if (i != key) continue;
            return true;
        }
        return false;
    }
}

