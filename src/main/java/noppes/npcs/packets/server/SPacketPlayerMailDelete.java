/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import java.util.Iterator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketPlayerMailDelete
extends PacketServerBasic {
    private final long time;
    private final String username;

    public SPacketPlayerMailDelete(long time, String username) {
        this.time = time;
        this.username = username;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketPlayerMailDelete msg, PacketByteBuf buf) {
        buf.writeLong(msg.time);
        buf.writeString(msg.username);
    }

    public static SPacketPlayerMailDelete decode(PacketByteBuf buf) {
        return new SPacketPlayerMailDelete(buf.readLong(), buf.readString(Short.MAX_VALUE));
    }

    @Override
    protected void handle() {
        PlayerMailData data = PlayerData.get((PlayerEntity)this.player).mailData;
        Iterator<PlayerMail> it = data.playermail.iterator();
        while (it.hasNext()) {
            PlayerMail mail = it.next();
            if (mail.time != this.time || !mail.sender.equals(this.username)) continue;
            it.remove();
        }
        Packets.send(this.player, new PacketGuiData(data.saveNBTData(new NbtCompound())));
    }
}

