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

public class SPacketSceneStart
extends PacketServerBasic {
    private int scene;

    public SPacketSceneStart(int scene) {
        this.scene = scene;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.SCENES;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketSceneStart msg, PacketByteBuf buf) {
        buf.writeInt(msg.scene);
    }

    public static SPacketSceneStart decode(PacketByteBuf buf) {
        return new SPacketSceneStart(buf.readInt());
    }

    @Override
    protected void handle() {
        if (CustomNpcs.SceneButtonsEnabled) {
            DataScenes.Toggle(this.player.getServer(), this.scene + "btn");
        }
    }
}

