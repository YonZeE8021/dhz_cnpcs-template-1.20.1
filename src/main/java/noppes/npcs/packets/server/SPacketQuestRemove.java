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
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketQuestRemove
extends PacketServerBasic {
    private int id;

    public SPacketQuestRemove(int id) {
        this.id = id;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_QUEST;
    }

    public static void encode(SPacketQuestRemove msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
    }

    public static SPacketQuestRemove decode(PacketByteBuf buf) {
        return new SPacketQuestRemove(buf.readInt());
    }

    @Override
    protected void handle() {
        Quest quest = QuestController.instance.quests.get(this.id);
        if (quest != null) {
            QuestController.instance.removeQuest(quest);
            Packets.send(this.player, new PacketGuiUpdate());
        }
    }
}

