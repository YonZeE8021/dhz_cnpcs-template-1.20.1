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
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketQuestDialogTitles
extends PacketServerBasic {
    private int dialogId1;
    private int dialogId2;
    private int dialogId3;

    public SPacketQuestDialogTitles(int dialogId1, int dialogId2, int dialogId3) {
        this.dialogId1 = dialogId1;
        this.dialogId2 = dialogId2;
        this.dialogId3 = dialogId3;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_QUEST;
    }

    public static void encode(SPacketQuestDialogTitles msg, PacketByteBuf buf) {
        buf.writeInt(msg.dialogId1);
        buf.writeInt(msg.dialogId2);
        buf.writeInt(msg.dialogId3);
    }

    public static SPacketQuestDialogTitles decode(PacketByteBuf buf) {
        return new SPacketQuestDialogTitles(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    protected void handle() {
        Dialog dialog = DialogController.instance.dialogs.get(this.dialogId1);
        Dialog dialog2 = DialogController.instance.dialogs.get(this.dialogId2);
        Dialog dialog3 = DialogController.instance.dialogs.get(this.dialogId3);
        NbtCompound compound = new NbtCompound();
        if (dialog != null) {
            compound.putString("1", dialog.title);
        }
        if (dialog2 != null) {
            compound.putString("2", dialog2.title);
        }
        if (dialog3 != null) {
            compound.putString("3", dialog3.title);
        }
        Packets.send(this.player, new PacketGuiData(compound));
    }
}

