/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.containers.ContainerMail;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketPlayerMailOpen
extends PacketServerBasic {
    private final long time;
    private final String username;

    public SPacketPlayerMailOpen(long time, String username) {
        this.time = time;
        this.username = username;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketPlayerMailOpen msg, PacketByteBuf buf) {
        buf.writeLong(msg.time);
        buf.writeString(msg.username);
    }

    public static SPacketPlayerMailOpen decode(PacketByteBuf buf) {
        return new SPacketPlayerMailOpen(buf.readLong(), buf.readString(Short.MAX_VALUE));
    }

    @Override
    protected void handle() {
        this.player.closeHandledScreen();
        PlayerMailData data = PlayerData.get((PlayerEntity)this.player).mailData;
        for (PlayerMail mail : data.playermail) {
            if (mail.time != this.time || !mail.sender.equals(this.username)) continue;
            ContainerMail.staticmail = mail;
            NoppesUtilServer.openContainerGui(this.player, EnumGuiType.PlayerMailman, buf -> {
                buf.writeBoolean(false);
                buf.writeBoolean(false);
            });
            break;
        }
    }
}

