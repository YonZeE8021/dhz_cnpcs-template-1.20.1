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
import noppes.npcs.controllers.data.DialogCategory;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiUpdate;

public class SPacketDialogCategorySave
extends PacketServerBasic {
    private NbtCompound data;

    public SPacketDialogCategorySave(NbtCompound data) {
        this.data = data;
    }

    public SPacketDialogCategorySave(PacketByteBuf buf) {
        this.data = buf.readNbt();
    }

    public static SPacketDialogCategorySave decode(PacketByteBuf buf) {
        return new SPacketDialogCategorySave(buf);
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.GLOBAL_DIALOG;
    }

    @Override
    protected void handle() {
        DialogCategory category = new DialogCategory();
        category.readNBT(this.data);
        DialogController.instance.saveCategory(category);
        Packets.send(this.player, new PacketGuiUpdate());
    }

    public static void encode(SPacketDialogCategorySave msg, PacketByteBuf buf) {
        buf.writeNbt(msg.data);
    }
}

