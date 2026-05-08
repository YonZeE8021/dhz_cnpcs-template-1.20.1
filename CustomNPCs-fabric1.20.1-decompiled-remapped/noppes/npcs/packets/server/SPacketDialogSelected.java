/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiClose;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleDialog;

public class SPacketDialogSelected
extends PacketServerBasic {
    private final int dialogId;
    private final int optionId;

    public SPacketDialogSelected(int dialogId, int optionId) {
        this.dialogId = dialogId;
        this.optionId = optionId;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    public static void encode(SPacketDialogSelected msg, PacketByteBuf buf) {
        buf.writeInt(msg.dialogId);
        buf.writeInt(msg.optionId);
    }

    public static SPacketDialogSelected decode(PacketByteBuf buf) {
        return new SPacketDialogSelected(buf.readInt(), buf.readInt());
    }

    @Override
    protected void handle() {
        PlayerData data = PlayerData.get((PlayerEntity)this.player);
        if (data.dialogId != this.dialogId) {
            return;
        }
        if (data.dialogId < 0 && this.npc.role.getType() == 7) {
            String text = ((RoleDialog)this.npc.role).optionsTexts.get(this.optionId);
            if (text != null && !text.isEmpty()) {
                Dialog d = new Dialog(null);
                d.text = text;
                NoppesUtilServer.openDialog((PlayerEntity)this.player, this.npc, d);
            }
            return;
        }
        Dialog dialog = DialogController.instance.dialogs.get(data.dialogId);
        if (dialog == null) {
            return;
        }
        if (!dialog.hasDialogs((PlayerEntity)this.player) && !dialog.hasOtherOptions()) {
            this.closeDialog(this.player, this.npc, true);
            return;
        }
        DialogOption option = dialog.options.get(this.optionId);
        if (option == null || EventHooks.onNPCDialogOption(this.npc, this.player, dialog, option) || option.optionType == 1 && (!option.isAvailable((PlayerEntity)this.player) || !option.hasDialog()) || option.optionType == 2 || option.optionType == 0) {
            this.closeDialog(this.player, this.npc, true);
            return;
        }
        if (option.optionType == 3) {
            this.closeDialog(this.player, this.npc, true);
            if (this.npc.role.getType() == 6) {
                ((RoleCompanion)this.npc.role).interact((PlayerEntity)this.player, true);
            } else {
                this.npc.role.interact((PlayerEntity)this.player);
            }
        } else if (option.optionType == 1) {
            this.closeDialog(this.player, this.npc, false);
            NoppesUtilServer.openDialog((PlayerEntity)this.player, this.npc, option.getDialog());
        } else if (option.optionType == 4) {
            this.closeDialog(this.player, this.npc, true);
            NoppesUtilServer.runCommand((Entity)this.npc, this.npc.getName().getString(), option.command, (PlayerEntity)this.player);
        } else {
            this.closeDialog(this.player, this.npc, true);
        }
    }

    public void closeDialog(ServerPlayerEntity player, EntityNPCInterface npc, boolean notifyClient) {
        PlayerData data = PlayerData.get((PlayerEntity)player);
        Dialog dialog = DialogController.instance.dialogs.get(data.dialogId);
        EventHooks.onNPCDialogClose(npc, player, dialog);
        if (notifyClient) {
            Packets.send(player, new PacketGuiClose(new NbtCompound()));
        }
        data.dialogId = -1;
    }
}

