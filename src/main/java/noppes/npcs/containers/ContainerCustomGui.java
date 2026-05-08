/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.inventory.SimpleInventory
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import noppes.npcs.CustomContainer;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.gui.IItemSlot;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.api.wrapper.gui.GuiComponentsScrollableWrapper;
import noppes.npcs.client.gui.custom.components.CustomGuiSlot;
import noppes.npcs.mixin.AbstractContainerMenuMixin;
import noppes.npcs.util.CustomNPCsScheduler;

public class ContainerCustomGui
extends ScreenHandler {
    public CustomGuiWrapper customGui;
    public CustomGuiWrapper activeGui;
    public SimpleInventory guiInventory;
    public NbtCompound data;

    public ContainerCustomGui(int containerId, NbtCompound data) {
        super(CustomContainer.container_customgui, containerId);
        this.data = data;
        this.guiInventory = new SimpleInventory(0);
    }

    public boolean canUse(PlayerEntity playerIn) {
        return true;
    }

    public void setGui(CustomGuiWrapper gui, PlayerEntity player) {
        this.activeGui = gui.getActiveGui();
        this.guiInventory = new SimpleInventory(this.activeGui.getSlots().size() + this.activeGui.getScrollingPanel().getSlots().size());
        this.customGui = gui;
        AbstractContainerMenuMixin mix = (AbstractContainerMenuMixin)(this);
        this.slots.clear();
        mix.remoteSlots().clear();
        mix.lastSlots().clear();
        for (IItemSlot slot : this.activeGui.getSlots()) {
            Slot s = this.addSlot(new CustomGuiSlot(gui, (Inventory)this.guiInventory, slot.getID(), slot, player));
            this.guiInventory.setStack(s.id, slot.getStack().getMCItemStack());
        }
        GuiComponentsScrollableWrapper panel = this.activeGui.getScrollingPanel();
        for (IItemSlot slot : panel.getSlots()) {
            Slot s = this.addSlot(new CustomGuiSlot(gui, (Inventory)this.guiInventory, slot.getID(), slot, player).update(panel.x, panel.y));
            this.guiInventory.setStack(s.id, slot.getStack().getMCItemStack());
        }
        for (IItemSlot slot : this.activeGui.getPlayerSlots()) {
            this.addSlot(new CustomGuiSlot(gui, (Inventory)player.getInventory(), slot.getID(), slot, player));
        }
        this.update();
    }

    public void update() {
        GuiComponentsScrollableWrapper panel = this.activeGui.getScrollingPanel();
        for (int i = 0; i < this.activeGui.getScrollingPanel().getSlots().size(); ++i) {
            CustomGuiSlot slot = (CustomGuiSlot)this.getSlot(i + this.activeGui.getSlots().size());
            if (panel.isVisible(slot.slot)) {
                slot.update(panel.x, panel.y - panel.scrollAmount);
                continue;
            }
            slot.update(-1073741824, -1073741824);
        }
    }

    public ItemStack quickMove(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < this.guiInventory.size() ? !this.insertItem(itemstack1, this.guiInventory.size(), this.slots.size(), true) : !this.insertItem(itemstack1, 0, this.guiInventory.size(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.isEmpty()) {
                slot.setStackNoCallbacks(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return itemstack;
    }

    public void onSlotClick(int slotId, int dragType, SlotActionType clickTypeIn, PlayerEntity player) {
        if (slotId < 0) {
            super.onSlotClick(slotId, dragType, clickTypeIn, player);
            return;
        }
        if (!player.getWorld().isClient) {
            CustomGuiSlot slot = (CustomGuiSlot)this.getSlot(slotId);
            if (!EventHooks.onCustomGuiSlotClicked((PlayerWrapper)NpcAPI.Instance().getIEntity((Entity)player), ((ContainerCustomGui)player.currentScreenHandler).activeGui, slot.slot, dragType, clickTypeIn.toString())) {
                super.onSlotClick(slotId, dragType, clickTypeIn, player);
                CustomNPCsScheduler.runTack(() -> ((ContainerCustomGui)this).syncState(), 10);
            }
        }
    }

    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        if (!player.getWorld().isClient) {
            EventHooks.onCustomGuiClose((PlayerWrapper)NpcAPI.Instance().getIEntity((Entity)player), this.customGui);
        }
    }
}

