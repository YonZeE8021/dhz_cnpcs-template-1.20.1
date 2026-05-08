/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.minecraft.client.option.KeyBinding
 */
package noppes.npcs.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.ClientProxy;

@Environment(value=EnvType.CLIENT)
public class CustomKeybinds {
    @Environment(value=EnvType.CLIENT)
    public static void registerKeys() {
        ClientProxy.QuestLog = new KeyBinding("Quest Log", 76, "key.categories.gameplay");
        if (CustomNpcs.SceneButtonsEnabled) {
            ClientProxy.Scene1 = new KeyBinding("Scene1 start/pause", 321, "key.categories.gameplay");
            ClientProxy.Scene2 = new KeyBinding("Scene2 start/pause", 322, "key.categories.gameplay");
            ClientProxy.Scene3 = new KeyBinding("Scene3 start/pause", 323, "key.categories.gameplay");
            ClientProxy.SceneReset = new KeyBinding("Scene reset", 320, "key.categories.gameplay");
            KeyBindingHelper.registerKeyBinding((KeyBinding)ClientProxy.Scene1);
            KeyBindingHelper.registerKeyBinding((KeyBinding)ClientProxy.Scene2);
            KeyBindingHelper.registerKeyBinding((KeyBinding)ClientProxy.Scene3);
            KeyBindingHelper.registerKeyBinding((KeyBinding)ClientProxy.SceneReset);
        }
        KeyBindingHelper.registerKeyBinding((KeyBinding)ClientProxy.QuestLog);
    }
}

