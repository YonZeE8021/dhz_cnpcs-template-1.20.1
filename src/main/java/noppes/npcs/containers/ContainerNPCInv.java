/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.ItemStack
 */
package noppes.npcs.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.containers.SlotNPCArmor;
import noppes.npcs.entity.EntityNPCInterface;

public class ContainerNPCInv
extends ScreenHandler {
    public ContainerNPCInv(int containerId, PlayerInventory playerInventory, int entityId) {
        super(CustomContainer.container_inv, containerId);
        EntityNPCInterface npc = (EntityNPCInterface)playerInventory.player.getWorld().getEntityById(entityId);
        this.addSlot(new SlotNPCArmor((Inventory)npc.inventory, 0, 9, 22, EquipmentSlot.HEAD));
        this.addSlot(new SlotNPCArmor((Inventory)npc.inventory, 1, 9, 40, EquipmentSlot.CHEST));
        this.addSlot(new SlotNPCArmor((Inventory)npc.inventory, 2, 9, 58, EquipmentSlot.LEGS));
        this.addSlot(new SlotNPCArmor((Inventory)npc.inventory, 3, 9, 76, EquipmentSlot.FEET));
        this.addSlot(new Slot((Inventory)npc.inventory, 4, 81, 22));
        this.addSlot(new Slot((Inventory)npc.inventory, 5, 81, 40));
        this.addSlot(new Slot((Inventory)npc.inventory, 6, 81, 58));
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot((Inventory)npc.inventory, l + 7, 191, 16 + l * 21));
        }
        for (int i1 = 0; i1 < 3; ++i1) {
            for (int l1 = 0; l1 < 9; ++l1) {
                this.addSlot(new Slot((Inventory)playerInventory, l1 + i1 * 9 + 9, l1 * 18 + 8, 113 + i1 * 18));
            }
        }
        for (int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot((Inventory)playerInventory, j1, j1 * 18 + 8, 171));
        }
    }

    public ItemStack quickMove(PlayerEntity par1Player, int i) {
        return ItemStack.EMPTY;
    }

    public boolean canUse(PlayerEntity entityplayer) {
        return true;
    }
}

