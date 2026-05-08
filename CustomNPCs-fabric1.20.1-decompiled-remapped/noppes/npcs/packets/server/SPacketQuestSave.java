/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.controllers.data.Quest;
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketQuestSave
extends PacketServerBasic {
    private int categoryId;
    private NbtCompound data;

    public SPacketQuestSave(int categoryId, NbtCompound data) {
        this.data = data;
        this.categoryId = categoryId;
    }

    public SPacketQuestSave(PacketByteBuf buf) {
        this.categoryId = buf.readInt();
        this.data = buf.readNbt();
    }

    public static SPacketQuestSave decode(PacketByteBuf buf) {
        return new SPacketQuestSave(buf);
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_QUEST;
    }

    @Override
    protected void handle() {
        QuestCategory category = QuestController.instance.categories.get(this.categoryId);
        if (category == null) {
            return;
        }
        Quest quest = new Quest(category);
        quest.readNBT(this.data);
        QuestController.instance.saveQuest(category, quest);
        Packets.send(this.player, new PacketGuiUpdate());
    }

    public static void encode(SPacketQuestSave msg, PacketByteBuf buf) {
        buf.writeInt(msg.categoryId);
        buf.writeNbt(msg.data);
    }
}

