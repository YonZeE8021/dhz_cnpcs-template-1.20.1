/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.gui.player.GuiDialogInteract;
import noppes.npcs.controllers.DialogController;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDialog
extends PacketBasic {
    private final int entityId;
    private final int dialogId;

    public PacketDialog(int entityId, int dialogId) {
        this.entityId = entityId;
        this.dialogId = dialogId;
    }

    public static void encode(PacketDialog msg, PacketByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeInt(msg.dialogId);
    }

    public static PacketDialog decode(PacketByteBuf buf) {
        return new PacketDialog(buf.readInt(), buf.readInt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        Entity entity = MinecraftClient.getInstance().world.getEntityById(this.entityId);
        if (entity == null || !(entity instanceof EntityNPCInterface)) {
            return;
        }
        Dialog dialog = DialogController.instance.dialogs.get(this.dialogId);
        PacketDialog.openDialog(dialog, (EntityNPCInterface)entity, this.player);
    }

    public static void openDialog(Dialog dialog, EntityNPCInterface npc, PlayerEntity player) {
        Screen gui = MinecraftClient.getInstance().currentScreen;
        if (gui == null || !(gui instanceof GuiDialogInteract)) {
            CustomNpcs.proxy.openGui(player, new GuiDialogInteract(npc, dialog));
        } else {
            GuiDialogInteract dia = (GuiDialogInteract)gui;
            dia.appendDialog(dialog);
        }
    }
}

