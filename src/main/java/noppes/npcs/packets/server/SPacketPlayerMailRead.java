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
import noppes.npcs.controllers.PlayerQuestController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerMail;
import noppes.npcs.controllers.data.PlayerMailData;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketPlayerMailRead
extends PacketServerBasic {
    private final long time;
    private final String username;

    public SPacketPlayerMailRead(long time, String username) {
        this.time = time;
        this.username = username;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketPlayerMailRead msg, PacketByteBuf buf) {
        buf.writeLong(msg.time);
        buf.writeString(msg.username);
    }

    public static SPacketPlayerMailRead decode(PacketByteBuf buf) {
        return new SPacketPlayerMailRead(buf.readLong(), buf.readString(Short.MAX_VALUE));
    }

    @Override
    protected void handle() {
        PlayerMailData data = PlayerData.get((PlayerEntity)this.player).mailData;
        for (PlayerMail mail : data.playermail) {
            if (mail.beenRead || mail.time != this.time || !mail.sender.equals(this.username)) continue;
            if (mail.hasQuest()) {
                PlayerQuestController.addActiveQuest(mail.getQuest(), (PlayerEntity)this.player);
            }
            mail.beenRead = true;
        }
    }
}

