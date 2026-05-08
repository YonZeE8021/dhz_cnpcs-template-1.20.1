/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.screen.PlayerScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.ArmorItem
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Identifier
 */
package noppes.npcs.containers;

import com.mojang.datafixers.util.Pair;
import net.minecraft.inventory.Inventory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import noppes.npcs.containers.SlotCompanionArmor;

class SlotNPCArmor
extends Slot {
    final EquipmentSlot armorType;

    SlotNPCArmor(Inventory iinventory, int i, int j, int k, EquipmentSlot l) {
        super(iinventory, i, j, k);
        this.armorType = l;
    }

    public int getMaxItemCount() {
        return 1;
    }

    public Pair<Identifier, Identifier> getBackgroundSprite() {
        return Pair.of((Object)PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, (Object)SlotCompanionArmor.ARMOR_SLOT_TEXTURES[this.armorType.getEntitySlotId()]);
    }

    public boolean canInsert(ItemStack itemstack) {
        if (itemstack.getItem() instanceof ArmorItem) {
            return ((ArmorItem)itemstack.getItem()).getSlotType() == this.armorType;
        }
        if (itemstack.getItem() instanceof BlockItem) {
            return this.armorType == EquipmentSlot.field_6169;
        }
        return false;
    }
}

