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
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketNpcDialogSet
extends PacketServerBasic {
    private int slot;
    private int dialog;

    public SPacketNpcDialogSet(int slot, int dialog) {
        this.slot = slot;
        this.dialog = dialog;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_ADVANCED;
    }

    public static void encode(SPacketNpcDialogSet msg, PacketByteBuf buf) {
        buf.writeInt(msg.slot);
        buf.writeInt(msg.dialog);
    }

    public static SPacketNpcDialogSet decode(PacketByteBuf buf) {
        return new SPacketNpcDialogSet(buf.readInt(), buf.readInt());
    }

    @Override
    protected void handle() {
        if (!this.npc.dialogs.containsKey(this.slot)) {
            this.npc.dialogs.put(this.slot, new DialogOption());
        }
        DialogOption option = this.npc.dialogs.get(this.slot);
        option.dialogId = this.dialog;
        option.optionType = 1;
        if (option.hasDialog()) {
            option.title = option.getDialog().title;
        }
        if (option.hasDialog()) {
            NbtCompound compound = option.writeNBT();
            compound.putInt("Position", this.slot);
            Packets.send(this.player, new PacketGuiData(compound));
        }
    }
}

