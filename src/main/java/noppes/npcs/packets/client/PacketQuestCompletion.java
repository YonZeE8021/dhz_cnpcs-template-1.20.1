/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.player.GuiQuestCompletion;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketQuestCompletionCheck;
import noppes.npcs.shared.common.PacketBasic;

public class PacketQuestCompletion
extends PacketBasic {
    private final int id;

    public PacketQuestCompletion(int id) {
        this.id = id;
    }

    public static void encode(PacketQuestCompletion msg, PacketByteBuf buf) {
        buf.writeInt(msg.id);
    }

    public static PacketQuestCompletion decode(PacketByteBuf buf) {
        return new PacketQuestCompletion(buf.readInt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Quest quest = QuestController.instance.get(this.id);
        if (!quest.getCompleteText().isEmpty()) {
            NoppesUtil.openGUI(this.player, new GuiQuestCompletion(quest));
        } else {
            Packets.sendServer(new SPacketQuestCompletionCheck(this.id));
        }
    }
}

