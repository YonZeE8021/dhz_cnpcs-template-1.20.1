package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import noppes.npcs.containers.ContainerNPCTrader;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketTraderLimitSync;
import noppes.npcs.roles.RoleTrader;

public class SPacketTraderLimitRefresh
extends PacketServerBasic {
    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    public static void encode(SPacketTraderLimitRefresh msg, PacketByteBuf buf) {
    }

    public static SPacketTraderLimitRefresh decode(PacketByteBuf buf) {
        return new SPacketTraderLimitRefresh();
    }

    @Override
    protected void handle() {
        ScreenHandler handler = this.player.currentScreenHandler;
        if (!(handler instanceof ContainerNPCTrader)) {
            return;
        }
        RoleTrader role = ((ContainerNPCTrader)handler).role;
        if (role == null) {
            return;
        }
        Packets.send(this.player, PacketTraderLimitSync.forTrader(this.player, role));
    }
}
