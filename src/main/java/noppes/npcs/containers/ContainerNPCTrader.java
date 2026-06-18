/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.ItemStack
 */
package noppes.npcs.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.EventHooks;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.event.RoleEvent;
import noppes.npcs.containers.ContainerNpcInterface;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketTraderLimitSync;
import noppes.npcs.roles.RoleTrader;
import noppes.npcs.util.TraderLimitHelper;
import net.minecraft.server.network.ServerPlayerEntity;

public class ContainerNPCTrader
extends ContainerNpcInterface {
    public RoleTrader role;
    private EntityNPCInterface npc;

    public ContainerNPCTrader(int containerId, PlayerInventory playerInventory, int entityId) {
        super(CustomContainer.container_trader, containerId, playerInventory);
        this.npc = (EntityNPCInterface)playerInventory.player.getWorld().getEntityById(entityId);
        this.role = (RoleTrader)this.npc.role;
        for (int i = 0; i < 18; ++i) {
            int x = 53;
            int y = 7;
            this.addSlot(new Slot((Inventory)this.role.inventorySold, i, x += i % 3 * 72, y += i / 3 * 21));
        }
        for (int i1 = 0; i1 < 3; ++i1) {
            for (int l1 = 0; l1 < 9; ++l1) {
                this.addSlot(new Slot((Inventory)playerInventory, l1 + i1 * 9 + 9, 32 + l1 * 18, 140 + i1 * 18));
            }
        }
        for (int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot((Inventory)playerInventory, j1, 32 + j1 * 18, 198));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity par1Player, int i) {
        return ItemStack.EMPTY;
    }

    public void onSlotClick(int i, int j, SlotActionType par3, PlayerEntity entityplayer) {
        ItemStack currency2;
        if (par3 != SlotActionType.PICKUP) {
            return;
        }
        if (i < 0 || i >= 18) {
            super.onSlotClick(i, j, par3, entityplayer);
            return;
        }
        if (j == 1) {
            return;
        }
        Slot slot = (Slot)this.slots.get(i);
        if (slot == null || slot.getStack() == null || slot.getStack().isEmpty()) {
            return;
        }
        ItemStack item = slot.getStack();
        if (!this.canGivePlayer(item, entityplayer)) {
            return;
        }
        TraderLimitHelper.LimitResult limitResult = TraderLimitHelper.checkPurchase(entityplayer, this.role, i);
        if (!limitResult.allowed) {
            RoleEvent.TradeLimitEvent limitEvent = new RoleEvent.TradeLimitEvent(entityplayer, this.npc.wrappedNPC, i, item, limitResult.reason, limitResult.playerRemaining);
            if (!EventHooks.onNPCRole(this.npc, limitEvent)) {
                return;
            }
        }
        ItemStack currency = this.role.inventoryCurrency.getStack(i);
        if (!this.canBuy(currency, currency2 = this.role.inventoryCurrency.getStack(i + 18), entityplayer)) {
            RoleEvent.TradeFailedEvent event = new RoleEvent.TradeFailedEvent(entityplayer, this.npc.wrappedNPC, i, item, currency, currency2);
            EventHooks.onNPCRole(this.npc, event);
            if (event.receiving != null) {
                this.setCursorStack(event.receiving.getMCItemStack());
            }
            return;
        }
        RoleEvent.TraderEvent event = new RoleEvent.TraderEvent(entityplayer, this.npc.wrappedNPC, i, item, currency, currency2);
        if (EventHooks.onNPCRole(this.npc, event)) {
            return;
        }
        if (event.currency1 != null && !event.currency1.isEmpty()) {
            currency = event.currency1.getMCItemStack();
        }
        if (event.currency2 != null && !event.currency2.isEmpty()) {
            currency2 = event.currency2.getMCItemStack();
        }
        if (!this.canBuy(currency, currency2, entityplayer)) {
            return;
        }
        NoppesUtilPlayer.consumeItem(entityplayer, currency, this.role.ignoreDamage, this.role.ignoreNBT);
        NoppesUtilPlayer.consumeItem(entityplayer, currency2, this.role.ignoreDamage, this.role.ignoreNBT);
        ItemStack soldItem = ItemStack.EMPTY;
        if (event.sold != null && !event.sold.isEmpty()) {
            soldItem = event.sold.getMCItemStack();
            this.givePlayer(soldItem.copy(), entityplayer);
        }
        TraderLimitHelper.recordPurchase(entityplayer, this.role, i);
        if (entityplayer instanceof ServerPlayerEntity) {
            Packets.send((ServerPlayerEntity)entityplayer, PacketTraderLimitSync.forTrader(entityplayer, this.role));
        }
    }

    public boolean canBuy(ItemStack currency, ItemStack currency2, PlayerEntity player) {
        if (NoppesUtilServer.IsItemStackNull(currency) && NoppesUtilServer.IsItemStackNull(currency2)) {
            return true;
        }
        if (NoppesUtilServer.IsItemStackNull(currency)) {
            currency = currency2;
            currency2 = ItemStack.EMPTY;
        }
        if (NoppesUtilPlayer.compareItems(currency, currency2, this.role.ignoreDamage, this.role.ignoreNBT)) {
            currency = currency.copy();
            currency.increment(currency2.getCount());
            currency2 = ItemStack.EMPTY;
        }
        if (NoppesUtilServer.IsItemStackNull(currency2)) {
            return NoppesUtilPlayer.compareItems(player, currency, this.role.ignoreDamage, this.role.ignoreNBT);
        }
        return NoppesUtilPlayer.compareItems(player, currency, this.role.ignoreDamage, this.role.ignoreNBT) && NoppesUtilPlayer.compareItems(player, currency2, this.role.ignoreDamage, this.role.ignoreNBT);
    }

    private boolean canGivePlayer(ItemStack item, PlayerEntity entityplayer) {
        int k1;
        ItemStack itemstack3 = entityplayer.currentScreenHandler.getCursorStack();
        if (NoppesUtilServer.IsItemStackNull(itemstack3)) {
            return true;
        }
        return NoppesUtilPlayer.compareItems(itemstack3, item, false, false) && (k1 = item.getCount()) > 0 && k1 + itemstack3.getCount() <= itemstack3.getMaxCount();
    }

    private void givePlayer(ItemStack item, PlayerEntity entityplayer) {
        int k1;
        ItemStack itemstack3 = entityplayer.currentScreenHandler.getCursorStack();
        if (NoppesUtilServer.IsItemStackNull(itemstack3)) {
            entityplayer.currentScreenHandler.setCursorStack(item);
        } else if (NoppesUtilPlayer.compareItems(itemstack3, item, false, false) && (k1 = item.getCount()) > 0 && k1 + itemstack3.getCount() <= itemstack3.getMaxCount()) {
            itemstack3.increment(k1);
        }
    }
}

