/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.entity.EntityDialogNpc;
import noppes.npcs.packets.client.PacketDialog;
import noppes.npcs.shared.common.PacketBasic;

public class PacketDialogDummy
extends PacketBasic {
    private final String name;
    private final NbtCompound data;

    public PacketDialogDummy(String name, NbtCompound data) {
        this.name = name;
        this.data = data;
    }

    public static void encode(PacketDialogDummy msg, PacketByteBuf buf) {
        buf.writeString(msg.name);
        buf.writeNbt(msg.data);
    }

    public static PacketDialogDummy decode(PacketByteBuf buf) {
        return new PacketDialogDummy(buf.readString(Short.MAX_VALUE), buf.readNbt());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    protected void handle() {
        EntityDialogNpc npc = new EntityDialogNpc(this.player.getWorld());
        npc.display.setName(I18n.translate((String)this.name, (Object[])new Object[0]));
        EntityUtil.Copy((LivingEntity)this.player, (LivingEntity)npc);
        Dialog dialog = new Dialog(null);
        dialog.readNBT(this.data);
        PacketDialog.openDialog(dialog, npc, this.player);
    }
}

