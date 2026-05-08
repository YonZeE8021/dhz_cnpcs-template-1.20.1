/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.packets.server;

import java.util.HashMap;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.EventHooks;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.containers.ContainerNPCFollowerHire;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.shared.client.util.NoppesStringUtils;

public class SPacketFollowerHire
extends PacketServerBasic {
    @Override
    public boolean toolAllowed(ItemStack item) {
        return true;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    public static void encode(SPacketFollowerHire msg, PacketByteBuf buf) {
    }

    public static SPacketFollowerHire decode(PacketByteBuf buf) {
        return new SPacketFollowerHire();
    }

    @Override
    protected void handle() {
        if (this.npc.role.getType() != 2) {
            return;
        }
        if (this.npc.role.getType() != 2) {
            return;
        }
        ScreenHandler con = this.player.currentScreenHandler;
        if (con == null || !(con instanceof ContainerNPCFollowerHire)) {
            return;
        }
        ContainerNPCFollowerHire container = (ContainerNPCFollowerHire)con;
        RoleFollower role = (RoleFollower)this.npc.role;
        SPacketFollowerHire.followerBuy(role, (Inventory)container.currencyMatrix, this.player, this.npc);
    }

    public static void followerBuy(RoleFollower role, Inventory currencyInv, ServerPlayerEntity player, EntityNPCInterface npc) {
        ItemStack currency = currencyInv.getStack(0);
        if (currency == null || currency.isEmpty()) {
            return;
        }
        HashMap<ItemStack, Integer> cd = new HashMap<ItemStack, Integer>();
        for (int slot = 0; slot < role.inventory.items.size(); ++slot) {
            ItemStack is = (ItemStack)role.inventory.items.get(slot);
            if (is.isEmpty() || is.getItem() != currency.getItem()) continue;
            int days = 1;
            if (role.rates.containsKey(slot)) {
                days = role.rates.get(slot);
            }
            cd.put(is, days);
        }
        if (cd.size() == 0) {
            return;
        }
        int stackSize = currency.getCount();
        int days = 0;
        int possibleDays = 0;
        int possibleSize = stackSize;
        while (true) {
            for (ItemStack item : cd.keySet()) {
                int newStackSize;
                int size;
                int posDays;
                int rDays = (Integer)cd.get(item);
                int rValue = item.getCount();
                if (rValue > stackSize || possibleDays > (posDays = (size = stackSize - (newStackSize = stackSize % rValue)) / rValue * rDays)) continue;
                possibleDays = posDays;
                possibleSize = newStackSize;
            }
            if (stackSize == possibleSize) break;
            stackSize = possibleSize;
            days += possibleDays;
            possibleDays = 0;
        }
        RoleEvent.FollowerHireEvent event = new RoleEvent.FollowerHireEvent((PlayerEntity)player, npc.wrappedNPC, days);
        if (EventHooks.onNPCRole(npc, event)) {
            return;
        }
        if (event.days == 0) {
            return;
        }
        if (stackSize <= 0) {
            currencyInv.setStack(0, ItemStack.EMPTY);
        } else {
            currencyInv.setStack(0, currency.split(stackSize));
        }
        npc.say((PlayerEntity)player, new Line(NoppesStringUtils.formatText(role.dialogHire.replace("{days}", "" + days), new Object[]{player, npc})));
        role.setOwner((PlayerEntity)player);
        role.addDays(days);
    }
}

