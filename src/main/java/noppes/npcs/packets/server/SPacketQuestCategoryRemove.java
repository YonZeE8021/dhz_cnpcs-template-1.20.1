/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketQuestCategoryRemove
extends PacketServerBasic {
    private int id;

    public SPacketQuestCategoryRemove(int id) {
        this.id = id;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_QUEST;
    }

    public static void encode(SPacketQuestCategoryRemove msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketQuestCategoryRemove decode(PacketByteBuf buf) {
        return new SPacketQuestCategoryRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        QuestController.instance.removeCategory(this.id);
        Packets.send(this.player, new PacketGuiUpdate());
    }
}

