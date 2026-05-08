/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.SimpleInventory
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.entity.ExperienceOrbEntity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.util.math.MathHelper
 */
package noppes.npcs.entity.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.data.INPCInventory;
import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

public class DataInventory
extends SimpleInventory
implements INPCInventory {
    public Map<Integer, IItemStack> drops = new HashMap<Integer, IItemStack>();
    public Map<Integer, Float> dropchance = new HashMap<Integer, Float>();
    public Map<Integer, IItemStack> weapons = new HashMap<Integer, IItemStack>();
    public Map<Integer, IItemStack> armor = new HashMap<Integer, IItemStack>();
    private int minExp = 0;
    private int maxExp = 0;
    public int lootMode = 0;
    private EntityNPCInterface npc;

    public DataInventory(EntityNPCInterface npc) {
        super(new ItemStack[0]);
        this.npc = npc;
    }

    public NbtCompound save(NbtCompound nbttagcompound) {
        nbttagcompound.putInt("MinExp", this.minExp);
        nbttagcompound.putInt("MaxExp", this.maxExp);
        nbttagcompound.put("NpcInv", (NbtElement)NBTTags.nbtIItemStackMap(this.drops));
        nbttagcompound.put("Armor", (NbtElement)NBTTags.nbtIItemStackMap(this.armor));
        nbttagcompound.put("Weapons", (NbtElement)NBTTags.nbtIItemStackMap(this.weapons));
        nbttagcompound.put("DropChance", (NbtElement)NBTTags.nbtFloatMap(this.dropchance));
        nbttagcompound.putInt("LootMode", this.lootMode);
        return nbttagcompound;
    }

    public void load(NbtCompound nbttagcompound) {
        this.minExp = nbttagcompound.getInt("MinExp");
        this.maxExp = nbttagcompound.getInt("MaxExp");
        this.drops = NBTTags.getIItemStackMap(nbttagcompound.getList("NpcInv", 10));
        this.armor = NBTTags.getIItemStackMap(nbttagcompound.getList("Armor", 10));
        this.weapons = NBTTags.getIItemStackMap(nbttagcompound.getList("Weapons", 10));
        this.dropchance = NBTTags.getFloatIntegerMap(nbttagcompound.getList("DropChance", 10));
        this.lootMode = nbttagcompound.getInt("LootMode");
    }

    @Override
    public IItemStack getArmor(int slot) {
        return this.armor.get(slot);
    }

    @Override
    public void setArmor(int slot, IItemStack item) {
        this.armor.put(slot, item);
        this.npc.updateClient = true;
    }

    @Override
    public IItemStack getRightHand() {
        return this.weapons.get(0);
    }

    @Override
    public void setRightHand(IItemStack item) {
        this.weapons.put(0, item);
        this.npc.updateClient = true;
    }

    @Override
    public IItemStack getProjectile() {
        return this.weapons.get(1);
    }

    @Override
    public void setProjectile(IItemStack item) {
        this.weapons.put(1, item);
        this.npc.updateAI = true;
    }

    @Override
    public IItemStack getLeftHand() {
        return this.weapons.get(2);
    }

    @Override
    public void setLeftHand(IItemStack item) {
        this.weapons.put(2, item);
        this.npc.updateClient = true;
    }

    @Override
    public IItemStack getDropItem(int slot) {
        if (slot < 0 || slot > 20) {
            throw new CustomNPCsException("Bad slot number: " + slot, new Object[0]);
        }
        IItemStack item = this.npc.inventory.drops.get(slot);
        if (item == null) {
            return ItemStackWrapper.AIR;
        }
        return NpcAPI.Instance().getIItemStack(item.getMCItemStack());
    }

    @Override
    public void setDropItem(int slot, IItemStack item, float chance) {
        if (slot < 0 || slot > 20) {
            throw new CustomNPCsException("Bad slot number: " + slot, new Object[0]);
        }
        chance = ValueUtil.correctFloat(chance, 1.0f, 100.0f);
        if (item == null || item.isEmpty()) {
            this.dropchance.remove(slot);
            this.drops.remove(slot);
        } else {
            this.dropchance.put(slot, Float.valueOf(chance));
            this.drops.put(slot, item);
        }
    }

    @Override
    public IItemStack[] getItemsRNG() {
        ArrayList<IItemStack> list = new ArrayList<IItemStack>();
        for (int i : this.drops.keySet()) {
            float chance;
            IItemStack item = this.drops.get(i);
            if (item == null || item.isEmpty()) continue;
            float dchance = 100.0f;
            if (this.dropchance.containsKey(i)) {
                dchance = this.dropchance.get(i).floatValue();
            }
            if (!((chance = (float)this.npc.getWorld().random.nextInt(100) + dchance) >= 100.0f)) continue;
            list.add(item);
        }
        return list.toArray(new IItemStack[list.size()]);
    }

    public void dropStuff(NpcEvent.DiedEvent event, Entity entity, DamageSource damagesource) {
        int var2;
        ArrayList<ItemEntity> list = new ArrayList<ItemEntity>();
        if (event.droppedItems != null) {
            for (IItemStack item : event.droppedItems) {
                ItemEntity e = this.getItemEntity(item.getMCItemStack().copy());
                if (e == null) continue;
                list.add(e);
            }
        }
        int enchant = 0;
        if (damagesource.getAttacker() instanceof PlayerEntity) {
            enchant = EnchantmentHelper.getLooting((LivingEntity)((LivingEntity)damagesource.getAttacker()));
        }
        for (ItemEntity item : list) {
            if (this.lootMode == 1 && entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity)entity;
                item.setPickupDelay(2);
                this.npc.getWorld().spawnEntity((Entity)item);
                ItemStack stack = item.getStack();
                int i = stack.getCount();
                if (!player.getInventory().insertStack(stack)) continue;
                entity.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                player.sendPickup((Entity)item, i);
                if (stack.getCount() > 0) continue;
                item.remove(Entity.RemovalReason.DISCARDED);
                continue;
            }
            this.npc.getWorld().spawnEntity((Entity)item);
        }
        for (int exp = event.expDropped; exp > 0; exp -= var2) {
            var2 = ExperienceOrbEntity.roundToOrbSize((int)exp);
            if (this.lootMode == 1 && entity instanceof PlayerEntity) {
                this.npc.getWorld().spawnEntity((Entity)new ExperienceOrbEntity(entity.getWorld(), entity.getX(), entity.getY(), entity.getZ(), var2));
                continue;
            }
            this.npc.getWorld().spawnEntity((Entity)new ExperienceOrbEntity(this.npc.getWorld(), this.npc.getX(), this.npc.getY(), this.npc.getZ(), var2));
        }
    }

    public ItemEntity getItemEntity(ItemStack itemstack) {
        if (itemstack == null || itemstack.isEmpty()) {
            return null;
        }
        ItemEntity entityitem = new ItemEntity(this.npc.getWorld(), this.npc.getX(), this.npc.getY() - (double)0.3f + (double)this.npc.getStandingEyeHeight(), this.npc.getZ(), itemstack);
        entityitem.setPickupDelay(40);
        float f2 = this.npc.getRandom().nextFloat() * 0.5f;
        float f4 = this.npc.getRandom().nextFloat() * 3.141593f * 2.0f;
        entityitem.setVelocity((double)(-MathHelper.sin((float)f4) * f2), (double)0.2f, (double)(MathHelper.cos((float)f4) * f2));
        return entityitem;
    }

    public int size() {
        return 15;
    }

    public ItemStack getStack(int i) {
        if (i < 4) {
            return ItemStackWrapper.MCItem(this.getArmor(i));
        }
        if (i < 7) {
            return ItemStackWrapper.MCItem(this.weapons.get(i - 4));
        }
        return ItemStackWrapper.MCItem(this.drops.get(i - 7));
    }

    public ItemStack removeStack(int par1, int limbSwingAmount) {
        Map<Integer, IItemStack> var3;
        int i = 0;
        if (par1 >= 7) {
            var3 = this.drops;
            par1 -= 7;
        } else if (par1 >= 4) {
            var3 = this.weapons;
            par1 -= 4;
            i = 1;
        } else {
            var3 = this.armor;
            i = 2;
        }
        ItemStack var4 = null;
        if (var3.get(par1) != null) {
            if (var3.get(par1).getMCItemStack().getCount() <= limbSwingAmount) {
                var4 = var3.get(par1).getMCItemStack();
                var3.put(par1, null);
            } else {
                var4 = var3.get(par1).getMCItemStack().split(limbSwingAmount);
                if (var3.get(par1).getMCItemStack().getCount() == 0) {
                    var3.put(par1, null);
                }
            }
        }
        if (i == 1) {
            this.weapons = var3;
        }
        if (i == 2) {
            this.armor = var3;
        }
        if (var4 == null) {
            return ItemStack.EMPTY;
        }
        return var4;
    }

    public ItemStack removeStack(int par1) {
        Map<Integer, IItemStack> var2;
        int i = 0;
        if (par1 >= 7) {
            var2 = this.drops;
            par1 -= 7;
        } else if (par1 >= 4) {
            var2 = this.weapons;
            par1 -= 4;
            i = 1;
        } else {
            var2 = this.armor;
            i = 2;
        }
        if (var2.get(par1) != null) {
            ItemStack var3 = var2.get(par1).getMCItemStack();
            var2.put(par1, null);
            if (i == 1) {
                this.weapons = var2;
            }
            if (i == 2) {
                this.armor = var2;
            }
            return var3;
        }
        return ItemStack.EMPTY;
    }

    public void setStack(int par1, ItemStack limbSwingAmountItemStack) {
        Map<Integer, IItemStack> var3;
        int i = 0;
        if (par1 >= 7) {
            var3 = this.drops;
            par1 -= 7;
        } else if (par1 >= 4) {
            var3 = this.weapons;
            par1 -= 4;
            i = 1;
        } else {
            var3 = this.armor;
            i = 2;
        }
        var3.put(par1, NpcAPI.Instance().getIItemStack(limbSwingAmountItemStack));
        if (i == 1) {
            this.weapons = var3;
        }
        if (i == 2) {
            this.armor = var3;
        }
    }

    public int getMaxCountPerStack() {
        return 64;
    }

    public boolean canPlayerUse(PlayerEntity var1) {
        return true;
    }

    public boolean isValid(int i, ItemStack itemstack) {
        return true;
    }

    public void markDirty() {
    }

    public void onOpen(PlayerEntity player) {
    }

    public void onClose(PlayerEntity player) {
    }

    @Override
    public int getExpMin() {
        return this.npc.inventory.minExp;
    }

    @Override
    public int getExpMax() {
        return this.npc.inventory.maxExp;
    }

    @Override
    public int getExpRNG() {
        int exp = this.minExp;
        if (this.maxExp - this.minExp > 0) {
            exp += this.npc.getWorld().random.nextInt(this.maxExp - this.minExp);
        }
        return exp;
    }

    @Override
    public void setExp(int min, int max) {
        this.npc.inventory.minExp = min = Math.min(min, max);
        this.npc.inventory.maxExp = max;
    }

    public boolean isEmpty() {
        for (int slot = 0; slot < this.size(); ++slot) {
            ItemStack item = this.getStack(slot);
            if (NoppesUtilServer.IsItemStackNull(item) || item.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public void clear() {
    }
}

