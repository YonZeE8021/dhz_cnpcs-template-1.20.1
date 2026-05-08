/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.ItemStack
 */
package noppes.npcs.client.gui.custom.components;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.gui.IItemSlot;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiItemSlotWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.containers.ContainerCustomGui;

public class CustomGuiSlot
extends Slot {
    private final PlayerEntity player;
    public final IItemSlot slot;
    private final CustomGuiWrapper gui;
    private static Field xField;
    private static Field yField;

    public CustomGuiSlot(CustomGuiWrapper gui, Inventory inventoryIn, int id, IItemSlot slot, PlayerEntity player) {
        super(inventoryIn, id, -666667, -666666);
        this.gui = gui;
        this.player = player;
        this.slot = slot;
        if (yField == null) {
            for (Field f : Slot.class.getDeclaredFields()) {
                if (Modifier.isPrivate(f.getModifiers())) continue;
                try {
                    Integer i;
                    Object object = f.get(this);
                    if (object instanceof Integer && (i = (Integer)object) == -666666) {
                        yField = f;
                        yField.setAccessible(true);
                    }
                    if (!((object = f.get(this)) instanceof Integer) || (i = (Integer)object) != -666667) continue;
                    xField = f;
                    xField.setAccessible(true);
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        this.update(0, 0);
    }

    public CustomGuiSlot update(int x, int y) {
        try {
            xField.set(this, x + this.slot.getPosX());
            yField.set(this, y + this.slot.getPosY());
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void setStackNoCallbacks(ItemStack is) {
        super.setStackNoCallbacks(is);
        if (!this.player.getWorld().isClient && this.getStack() != this.slot.getStack().getMCItemStack()) {
            ScreenHandler class_17032;
            if (!this.slot.isPlayerSlot()) {
                this.slot.setStack(NpcAPI.Instance().getIItemStack(this.getStack()));
                ((CustomGuiItemSlotWrapper)this.slot).onUpdate(this.gui);
            }
            if ((class_17032 = this.player.currentScreenHandler) instanceof ContainerCustomGui) {
                ContainerCustomGui container = (ContainerCustomGui)class_17032;
                EventHooks.onCustomGuiSlot((PlayerWrapper)NpcAPI.Instance().getIEntity((Entity)this.player), container.customGui, this.slot);
            }
        }
    }
}

