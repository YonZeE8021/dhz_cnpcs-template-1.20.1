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
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.PlayerQuestData;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketQuestCompletionCheckAll
extends PacketServerBasic {
    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketQuestCompletionCheckAll msg, PacketByteBuf buf) {
    }

    public static SPacketQuestCompletionCheckAll decode(PacketByteBuf buf) {
        return new SPacketQuestCompletionCheckAll();
    }

    @Override
    protected void handle() {
        PlayerQuestData playerdata = PlayerData.get((PlayerEntity)this.player).questData;
        playerdata.checkQuestCompletion((PlayerEntity)this.player, -1);
    }
}

