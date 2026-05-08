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
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketDialogSave
extends PacketServerBasic {
    private int category;
    private NbtCompound data;

    public SPacketDialogSave(int category, NbtCompound data) {
        this.data = data;
        this.category = category;
    }

    public SPacketDialogSave(PacketByteBuf buf) {
        this.category = buf.readInt();
        this.data = buf.readNbt();
    }

    public static SPacketDialogSave decode(PacketByteBuf buf) {
        return new SPacketDialogSave(buf);
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_DIALOG;
    }

    @Override
    protected void handle() {
        DialogCategory dcategory = DialogController.instance.categories.get(this.category);
        if (dcategory == null) {
            return;
        }
        Dialog dialog = new Dialog(dcategory);
        dialog.readNBT(this.data);
        DialogController.instance.saveDialog(dcategory, dialog);
        Packets.send(this.player, new PacketGuiUpdate());
    }

    public static void encode(SPacketDialogSave msg, PacketByteBuf buf) {
        buf.writeInt(msg.category);
        buf.writeNbt(msg.data);
    }
}

