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
import noppes.npcs.controllers.data.QuestCategory;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketQuestCategorySave
extends PacketServerBasic {
    private NbtCompound data;

    public SPacketQuestCategorySave(NbtCompound data) {
        this.data = data;
    }

    public SPacketQuestCategorySave(PacketByteBuf buf) {
        this.data = buf.readNbt();
    }

    public static SPacketQuestCategorySave decode(PacketByteBuf buf) {
        return new SPacketQuestCategorySave(buf);
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_QUEST;
    }

    @Override
    protected void handle() {
        QuestCategory category = new QuestCategory();
        category.readNBT(this.data);
        QuestController.instance.saveCategory(category);
        Packets.send(this.player, new PacketGuiUpdate());
    }

    public static void encode(SPacketQuestCategorySave msg, PacketByteBuf buf) {
        buf.writeNbt(msg.data);
    }
}

