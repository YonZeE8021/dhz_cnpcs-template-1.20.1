/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Identifier
 */
package noppes.npcs.client.controllers;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.Identifier;
import noppes.npcs.client.SkinUtil;
import noppes.npcs.controllers.data.PlayerSkinData;

public class ClientSkinController {
    private static final Map<String, PlayerSkinData> skinInfo = new HashMap<String, PlayerSkinData>();

    public static void addSkinForPlayer(String playerName, PlayerSkinData skinData) {
        SkinUtil.createPlayerSkin(skinData);
        skinInfo.put(playerName, skinData);
    }

    public static Identifier getSkinForPlayer(String playerName) {
        PlayerSkinData skin = skinInfo.get(playerName);
        if (skin == null) {
            return null;
        }
        return skin.getResLoc();
    }
}

