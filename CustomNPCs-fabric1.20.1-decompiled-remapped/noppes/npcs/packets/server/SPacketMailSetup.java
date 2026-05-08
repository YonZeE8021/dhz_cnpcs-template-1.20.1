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
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketMailSetup
extends PacketServerBasic {
    private NbtCompound data;

    public SPacketMailSetup(NbtCompound data) {
        this.data = data;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketMailSetup msg, PacketByteBuf buf) {
        buf.writeNbt(msg.data);
    }

    public static SPacketMailSetup decode(PacketByteBuf buf) {
        return new SPacketMailSetup(buf.readNbt());
    }

    @Override
    protected void handle() {
        PlayerMail mail = new PlayerMail();
        mail.readNBT(this.data);
        ContainerMail.staticmail = mail;
        NoppesUtilServer.openContainerGui(this.player, EnumGuiType.PlayerMailman, buf -> {
            buf.writeBoolean(true);
            buf.writeBoolean(false);
        });
    }
}

