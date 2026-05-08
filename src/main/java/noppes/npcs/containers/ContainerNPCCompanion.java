/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.ItemStack
 */
package noppes.npcs.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.containers.ContainerNpcInterface;
import noppes.npcs.containers.InventoryNPC;
import noppes.npcs.containers.SlotCompanionArmor;
import noppes.npcs.containers.SlotCompanionWeapon;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;

public class ContainerNPCCompanion
extends ContainerNpcInterface {
    public InventoryNPC currencyMatrix;
    public RoleCompanion role;

    public ContainerNPCCompanion(int containerId, PlayerInventory playerInventory, int entityId) {
        super(CustomContainer.container_companion, containerId, playerInventory);
        EntityNPCInterface npc = (EntityNPCInterface)this.player.getWorld().getEntityById(entityId);
        this.role = (RoleCompanion)npc.role;
        for (int k = 0; k < 3; ++k) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot((Inventory)playerInventory, j1 + k * 9 + 9, 6 + j1 * 18, 87 + k * 18));
            }
        }
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot((Inventory)playerInventory, l, 6 + l * 18, 145));
        }
        if (this.role.talents.containsKey(EnumCompanionTalent.INVENTORY)) {
            int size = (this.role.getTalentLevel(EnumCompanionTalent.INVENTORY) + 1) * 2;
            for (int i = 0; i < size; ++i) {
                this.addSlot(new Slot((Inventory)this.role.inventory, i, 114 + i % 3 * 18, 8 + i / 3 * 18));
            }
        }
        if (this.role.getTalentLevel(EnumCompanionTalent.ARMOR) > 0) {
            this.addSlot(new SlotCompanionArmor(this.role, (Inventory)npc.inventory, 0, 6, 8, EquipmentSlot.HEAD));
            this.addSlot(new SlotCompanionArmor(this.role, (Inventory)npc.inventory, 1, 6, 26, EquipmentSlot.CHEST));
            this.addSlot(new SlotCompanionArmor(this.role, (Inventory)npc.inventory, 2, 6, 44, EquipmentSlot.LEGS));
            this.addSlot(new SlotCompanionArmor(this.role, (Inventory)npc.inventory, 3, 6, 62, EquipmentSlot.FEET));
        }
        if (this.role.getTalentLevel(EnumCompanionTalent.SWORD) > 0) {
            this.addSlot(new SlotCompanionWeapon(this.role, (Inventory)npc.inventory, 4, 79, 17));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity par1Player, int i) {
        return ItemStack.EMPTY;
    }

    public void onClosed(PlayerEntity entityplayer) {
        super.onClosed(entityplayer);
        this.role.setStats();
    }
}

