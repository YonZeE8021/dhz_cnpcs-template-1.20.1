/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  com.google.gson.JsonParseException
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.EntityGroup
 *  net.minecraft.entity.attribute.EntityAttribute
 *  net.minecraft.entity.attribute.EntityAttributeModifier
 *  net.minecraft.entity.attribute.EntityAttributeModifier$Operation
 *  net.minecraft.item.ArmorItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.SwordItem
 *  net.minecraft.item.WritableBookItem
 *  net.minecraft.item.WrittenBookItem
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.AbstractNbtNumber
 *  net.minecraft.nbt.NbtString
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.text.Text
 *  net.minecraft.text.Text$Serializer
 *  net.minecraft.util.Identifier
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.item.Equipment
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.api.wrapper;

import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.WritableBookItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.AbstractNbtNumber;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Equipment;
import net.minecraft.registry.Registries;
import noppes.npcs.ItemStackEmptyWrapper;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IMob;
import noppes.npcs.api.entity.data.IData;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemArmorWrapper;
import noppes.npcs.api.wrapper.ItemBlockWrapper;
import noppes.npcs.api.wrapper.ItemBookWrapper;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.items.ItemScripted;

public class ItemStackWrapper
implements IItemStack {
    private Map<String, Object> tempData = new HashMap<String, Object>();
    private static Map<Integer, ItemStackWrapper> dataMap = new HashMap<Integer, ItemStackWrapper>();
    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[]{EquipmentSlot.field_6169, EquipmentSlot.field_6174, EquipmentSlot.field_6172, EquipmentSlot.field_6166};
    public ItemStack item;
    private NbtCompound storedData = new NbtCompound();
    public static ItemStackWrapper AIR = new ItemStackEmptyWrapper();
    private final IData tempdata = new IData(){

        @Override
        public void put(String key, Object value) {
            ItemStackWrapper.this.tempData.put(key, value);
        }

        @Override
        public Object get(String key) {
            return ItemStackWrapper.this.tempData.get(key);
        }

        @Override
        public void remove(String key) {
            ItemStackWrapper.this.tempData.remove(key);
        }

        @Override
        public boolean has(String key) {
            return ItemStackWrapper.this.tempData.containsKey(key);
        }

        @Override
        public void clear() {
            ItemStackWrapper.this.tempData.clear();
        }

        @Override
        public String[] getKeys() {
            return ItemStackWrapper.this.tempData.keySet().toArray(new String[ItemStackWrapper.this.tempData.size()]);
        }
    };
    private final IData storeddata = new IData(){

        @Override
        public void put(String key, Object value) {
            if (value instanceof Number) {
                ItemStackWrapper.this.storedData.putDouble(key, ((Number)value).doubleValue());
            } else if (value instanceof String) {
                ItemStackWrapper.this.storedData.putString(key, (String)value);
            }
        }

        @Override
        public Object get(String key) {
            if (!ItemStackWrapper.this.storedData.contains(key)) {
                return null;
            }
            NbtElement base = ItemStackWrapper.this.storedData.get(key);
            if (base instanceof AbstractNbtNumber) {
                return ((AbstractNbtNumber)base).doubleValue();
            }
            return base.asString();
        }

        @Override
        public void remove(String key) {
            ItemStackWrapper.this.storedData.remove(key);
        }

        @Override
        public boolean has(String key) {
            return ItemStackWrapper.this.storedData.contains(key);
        }

        @Override
        public void clear() {
            ItemStackWrapper.this.storedData = new NbtCompound();
        }

        @Override
        public String[] getKeys() {
            return ItemStackWrapper.this.storedData.getKeys().toArray(new String[ItemStackWrapper.this.storedData.getKeys().size()]);
        }
    };
    private static final Identifier key = new Identifier("customnpcs", "itemscripteddata");

    public ItemStackWrapper(ItemStack item) {
        this.item = item;
    }

    @Override
    public IData getTempdata() {
        return this.tempdata;
    }

    @Override
    public IData getStoreddata() {
        return this.storeddata;
    }

    @Override
    public int getStackSize() {
        return this.item.getCount();
    }

    @Override
    public void setStackSize(int size) {
        if (size > this.getMaxStackSize()) {
            throw new CustomNPCsException("Can't set the stacksize bigger than MaxStacksize", new Object[0]);
        }
        this.item.setCount(size);
    }

    @Override
    public void setAttribute(String name, double value) {
        this.setAttribute(name, value, -1);
    }

    @Override
    public void setAttribute(String name, double value, int slot) {
        if (slot < -1 || slot > 5) {
            throw new CustomNPCsException("Slot has to be between -1 and 5, given was: " + slot, new Object[0]);
        }
        NbtCompound compound = this.item.getNbt();
        if (compound == null) {
            compound = new NbtCompound();
            this.item.setNbt(compound);
        }
        NbtList nbttaglist = compound.getList("AttributeModifiers", 10);
        NbtList newList = new NbtList();
        UUID uuid = null;
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NbtCompound c = nbttaglist.getCompound(i);
            if (!c.getString("AttributeName").equals(name)) {
                newList.add((Object)c);
                continue;
            }
            uuid = c.getUuid("UUID");
        }
        if (value != 0.0) {
            NbtCompound nbttagcompound = new EntityAttributeModifier(name, value, EntityAttributeModifier.Operation.ADDITION).toNbt();
            nbttagcompound.putString("AttributeName", name);
            if (slot >= 0) {
                nbttagcompound.putString("Slot", EquipmentSlot.values()[slot].getName());
            }
            if (uuid != null) {
                nbttagcompound.putUuid("UUID", uuid);
            }
            newList.add((Object)nbttagcompound);
        }
        compound.put("AttributeModifiers", (NbtElement)newList);
    }

    @Override
    public double getAttribute(String name) {
        NbtCompound compound = this.item.getNbt();
        if (compound == null) {
            return 0.0;
        }
        Multimap map = this.item.getAttributeModifiers(EquipmentSlot.field_6173);
        for (Map.Entry entry : map.entries()) {
            if (!((EntityAttribute)entry.getKey()).getTranslationKey().equals(name)) continue;
            EntityAttributeModifier mod = (EntityAttributeModifier)entry.getValue();
            return mod.getValue();
        }
        return 0.0;
    }

    @Override
    public boolean hasAttribute(String name) {
        NbtCompound compound = this.item.getNbt();
        if (compound == null) {
            return false;
        }
        NbtList nbttaglist = compound.getList("AttributeModifiers", 10);
        for (int i = 0; i < nbttaglist.size(); ++i) {
            NbtCompound c = nbttaglist.getCompound(i);
            if (!c.getString("AttributeName").equals(name)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void addEnchantment(String id, int strenght) {
        Enchantment ench = (Enchantment)Registries.ENCHANTMENT.get(new Identifier(id));
        if (ench == null) {
            throw new CustomNPCsException("Unknown enchant id:" + id, new Object[0]);
        }
        this.item.addEnchantment(ench, strenght);
    }

    @Override
    public boolean isEnchanted() {
        return this.item.hasEnchantments();
    }

    @Override
    public boolean hasEnchant(String id) {
        Enchantment ench = (Enchantment)Registries.ENCHANTMENT.get(new Identifier(id));
        if (ench == null) {
            throw new CustomNPCsException("Unknown enchant id:" + id, new Object[0]);
        }
        if (!this.isEnchanted()) {
            return false;
        }
        NbtList list = this.item.getEnchantments();
        for (int i = 0; i < list.size(); ++i) {
            NbtCompound compound = list.getCompound(i);
            if (!compound.getString("id").equalsIgnoreCase(id)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEnchant(String id) {
        Enchantment ench = (Enchantment)Registries.ENCHANTMENT.get(new Identifier(id));
        if (ench == null) {
            throw new CustomNPCsException("Unknown enchant id:" + id, new Object[0]);
        }
        if (!this.isEnchanted()) {
            return false;
        }
        NbtList list = this.item.getEnchantments();
        NbtList newList = new NbtList();
        for (int i = 0; i < list.size(); ++i) {
            NbtCompound compound = list.getCompound(i);
            if (compound.getString("id").equalsIgnoreCase(id)) continue;
            newList.add((Object)compound);
        }
        if (list.size() == newList.size()) {
            return false;
        }
        this.item.getNbt().put("ench", (NbtElement)newList);
        return true;
    }

    @Override
    public boolean isBlock() {
        Block block = Block.getBlockFromItem((Item)this.item.getItem());
        return block != null && block != Blocks.field_10124;
    }

    @Override
    public boolean hasCustomName() {
        return this.item.hasCustomName();
    }

    @Override
    public void setCustomName(String name) {
        this.item.setCustomName((Text)Text.translatable((String)name));
    }

    @Override
    public String getDisplayName() {
        return this.item.getName().getString();
    }

    @Override
    public String getItemName() {
        return this.item.getItem().getName(this.item).getString();
    }

    @Override
    public String getName() {
        return Registries.ITEM.getId((Object)this.item.getItem()).toString();
    }

    @Override
    public INbt getNbt() {
        NbtCompound compound = this.item.getNbt();
        if (compound == null) {
            compound = new NbtCompound();
            this.item.setNbt(compound);
        }
        return NpcAPI.Instance().getINbt(compound);
    }

    @Override
    public boolean hasNbt() {
        NbtCompound compound = this.item.getNbt();
        return compound != null && !compound.isEmpty();
    }

    @Override
    public ItemStack getMCItemStack() {
        return this.item;
    }

    public static ItemStack MCItem(IItemStack item) {
        if (item == null) {
            return ItemStack.EMPTY;
        }
        return item.getMCItemStack();
    }

    @Override
    public void damageItem(int damage, IMob living) {
        if (living != null) {
            this.item.damage(damage, living == null ? null : (LivingEntity)living.getMCEntity(), e -> e.sendEquipmentBreakStatus(EquipmentSlot.field_6173));
        } else if (this.item.isDamageable()) {
            if (this.item.getDamage() <= damage) {
                this.item.decrement(1);
                this.item.setDamage(0);
            } else {
                this.item.setDamage(this.item.getDamage() - damage);
            }
        }
    }

    @Override
    public boolean isBook() {
        return false;
    }

    @Override
    public int getFoodLevel() {
        if (this.item.getItem().getFoodComponent() != null) {
            return this.item.getItem().getFoodComponent().getHunger();
        }
        return 0;
    }

    @Override
    public IItemStack copy() {
        return ItemStackWrapper.createNew(this.item.copy());
    }

    @Override
    public int getMaxStackSize() {
        return this.item.getMaxCount();
    }

    @Override
    public boolean isDamageable() {
        return this.item.isDamageable();
    }

    @Override
    public int getDamage() {
        return this.item.getDamage();
    }

    @Override
    public void setDamage(int value) {
        this.item.setDamage(value);
    }

    @Deprecated
    public int getItemDamage() {
        return this.item.getDamage();
    }

    @Deprecated
    public void setItemDamage(int value) {
        this.item.setDamage(value);
    }

    @Override
    public int getMaxDamage() {
        return this.item.getMaxDamage();
    }

    @Override
    public INbt getItemNbt() {
        NbtCompound compound = new NbtCompound();
        this.item.writeNbt(compound);
        return NpcAPI.Instance().getINbt(compound);
    }

    @Override
    public double getAttackDamage() {
        Multimap map = this.item.getAttributeModifiers(EquipmentSlot.field_6173);
        double damage = 0.0;
        for (Map.Entry entry : map.entries()) {
            if (entry.getKey() != EntityAttributes.field_23721) continue;
            EntityAttributeModifier mod = (EntityAttributeModifier)entry.getValue();
            damage = mod.getValue();
        }
        return damage + (double)EnchantmentHelper.getAttackDamage((ItemStack)this.item, (EntityGroup)EntityGroup.DEFAULT);
    }

    @Override
    public boolean isEmpty() {
        return this.item.isEmpty();
    }

    @Override
    public int getType() {
        if (this.item.getItem() instanceof SwordItem) {
            return 4;
        }
        return 0;
    }

    @Override
    public boolean isWearable() {
        return this.item.getItem() instanceof Equipment;
    }

    private static ItemStackWrapper createNew(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return AIR;
        }
        if (item.getItem() instanceof ItemScripted) {
            return new ItemScriptedWrapper(item);
        }
        if (item.getItem() == Items.field_8360 || item.getItem() == Items.field_8674 || item.getItem() instanceof WritableBookItem || item.getItem() instanceof WrittenBookItem) {
            return new ItemBookWrapper(item);
        }
        if (item.getItem() instanceof ArmorItem) {
            return new ItemArmorWrapper(item);
        }
        Block block = Block.getBlockFromItem((Item)item.getItem());
        if (block != Blocks.field_10124) {
            return new ItemBlockWrapper(item);
        }
        return new ItemStackWrapper(item);
    }

    @Override
    public String[] getLore() {
        NbtCompound compound = this.item.getSubNbt("display");
        if (compound == null || compound.getType("Lore") != 9) {
            return new String[0];
        }
        NbtList nbttaglist = compound.getList("Lore", 8);
        if (nbttaglist.isEmpty()) {
            return new String[0];
        }
        ArrayList<String> lore = new ArrayList<String>();
        for (int i = 0; i < nbttaglist.size(); ++i) {
            lore.add(nbttaglist.getString(i));
        }
        return lore.toArray(new String[lore.size()]);
    }

    @Override
    public void setLore(String[] lore) {
        NbtCompound compound = this.item.getOrCreateSubNbt("display");
        if (lore == null || lore.length == 0) {
            compound.remove("Lore");
            return;
        }
        NbtList nbtlist = new NbtList();
        for (String s : lore) {
            try {
                Text.Serializer.fromJson((String)s);
            }
            catch (JsonParseException jsonparseexception) {
                s = Text.Serializer.toJson((Text)Text.translatable((String)s));
            }
            nbtlist.add((Object)NbtString.of((String)s));
        }
        compound.put("Lore", (NbtElement)nbtlist);
    }

    public NbtCompound serializeNBT() {
        return this.getMCNbt();
    }

    public void deserializeNBT(NbtCompound nbt) {
        this.setMCNbt(nbt);
    }

    public NbtCompound getMCNbt() {
        NbtCompound compound = new NbtCompound();
        if (!this.storedData.isEmpty()) {
            compound.put("StoredData", (NbtElement)this.storedData);
        }
        return compound;
    }

    public void setMCNbt(NbtCompound compound) {
        this.storedData = compound == null ? new NbtCompound() : compound.getCompound("StoredData");
    }

    @Override
    public void removeNbt() {
        this.item.setNbt(null);
    }

    @Override
    public boolean compare(IItemStack item, boolean ignoreNBT) {
        if (item == null) {
            item = AIR;
        }
        return NoppesUtilPlayer.compareItems(this.getMCItemStack(), item.getMCItemStack(), false, ignoreNBT);
    }
}

