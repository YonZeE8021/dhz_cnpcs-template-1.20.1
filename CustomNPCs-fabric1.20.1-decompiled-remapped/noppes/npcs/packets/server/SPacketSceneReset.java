/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.entity.data.DataScenes;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketSceneReset
extends PacketServerBasic {
    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.SCENES;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketSceneReset msg, PacketByteBuf buf) {
    }

    public static SPacketSceneReset decode(PacketByteBuf buf) {
        return new SPacketSceneReset();
    }

    @Override
    protected void handle() {
        if (CustomNpcs.SceneButtonsEnabled) {
            DataScenes.Reset(this.player.getCommandSource(), null);
        }
    }
}

