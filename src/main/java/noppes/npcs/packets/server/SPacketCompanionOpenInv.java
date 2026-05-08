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
import noppes.npcs.packets.PacketServerBasic;

public class SPacketCompanionOpenInv
extends PacketServerBasic {
    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    public static void encode(SPacketCompanionOpenInv msg, PacketByteBuf buf) {
    }

    public static SPacketCompanionOpenInv decode(PacketByteBuf buf) {
        return new SPacketCompanionOpenInv();
    }

    @Override
    protected void handle() {
        if (this.npc.role.getType() != 6 || this.player != this.npc.getOwner()) {
            return;
        }
        NoppesUtilServer.sendOpenGui((PlayerEntity)this.player, EnumGuiType.CompanionInv, this.npc);
    }
}

