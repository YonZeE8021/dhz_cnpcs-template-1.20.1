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
import noppes.npcs.roles.RoleCompanion;

public class SlotCompanionArmor
extends Slot {
    public static final Identifier[] ARMOR_SLOT_TEXTURES = new Identifier[]{PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE};
    final EquipmentSlot armorType;
    final RoleCompanion role;

    public SlotCompanionArmor(RoleCompanion role, Inventory iinventory, int id, int x, int y, EquipmentSlot type) {
        super(iinventory, id, x, y);
        this.armorType = type;
        this.role = role;
    }

    public int getMaxItemCount() {
        return 1;
    }

    public Pair<Identifier, Identifier> getBackgroundSprite() {
        return Pair.of((Object)PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, (Object)ARMOR_SLOT_TEXTURES[this.armorType.getEntitySlotId()]);
    }

    public boolean canInsert(ItemStack itemstack) {
        if (itemstack.getItem() instanceof ArmorItem && this.role.canWearArmor(itemstack)) {
            return ((ArmorItem)itemstack.getItem()).getSlotType() == this.armorType;
        }
        if (itemstack.getItem() instanceof BlockItem) {
            return this.armorType == EquipmentSlot.field_6169;
        }
        return false;
    }
}

