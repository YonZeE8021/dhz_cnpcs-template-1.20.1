/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.EquipmentSlot$Type
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.attribute.EntityAttributeModifier
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ArmorItem
 *  net.minecraft.item.ArmorMaterials
 *  net.minecraft.item.BowItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.SwordItem
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.particle.ItemStackParticleEffect
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.item.FoodComponent
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.registry.tag.DamageTypeTags
 */
package noppes.npcs.roles;

import com.google.common.collect.Multimap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.sound.SoundEvents;
import net.minecraft.item.FoodComponent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.math.random.Random;
import net.minecraft.registry.tag.DamageTypeTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcMiscInventory;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.constants.EnumCompanionStage;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.ArmorMaterialsMixin;
import noppes.npcs.roles.RoleInterface;
import noppes.npcs.roles.companion.CompanionFarmer;
import noppes.npcs.roles.companion.CompanionFoodStats;
import noppes.npcs.roles.companion.CompanionGuard;
import noppes.npcs.roles.companion.CompanionJobInterface;
import noppes.npcs.roles.companion.CompanionTrader;

public class RoleCompanion
extends RoleInterface {
    private static final CompanionJobInterface NONE = new CompanionJobInterface(){

        @Override
        public NbtCompound getNBT() {
            return null;
        }

        @Override
        public void setNBT(NbtCompound compound) {
        }

        @Override
        public EnumCompanionJobs getType() {
            return EnumCompanionJobs.NONE;
        }
    };
    public NpcMiscInventory inventory;
    public String uuid = "";
    public String ownerName = "";
    public Map<EnumCompanionTalent, Integer> talents = new TreeMap<EnumCompanionTalent, Integer>();
    public boolean canAge = true;
    public long ticksActive = 0L;
    public EnumCompanionStage stage = EnumCompanionStage.FULLGROWN;
    public PlayerEntity owner = null;
    public int companionID;
    public CompanionJobInterface companionJobInterface = NONE;
    public boolean hasInv = true;
    public boolean defendOwner = true;
    public CompanionFoodStats foodstats = new CompanionFoodStats();
    private int eatingTicks = 20;
    private IItemStack eating = null;
    private int eatingDelay = 0;
    public int currentExp = 0;

    public RoleCompanion(EntityNPCInterface npc) {
        super(npc);
        this.inventory = new NpcMiscInventory(12);
    }

    @Override
    public boolean aiShouldExecute() {
        PlayerEntity prev = this.owner;
        this.owner = this.getOwner();
        if (this.companionJobInterface.isSelfSufficient()) {
            return true;
        }
        if (this.owner == null && !this.uuid.isEmpty()) {
            this.npc.discard();
        } else if (prev != this.owner && this.owner != null) {
            this.ownerName = this.owner.getDisplayName().getString();
            PlayerData data = PlayerData.get(this.owner);
            if (data.companionID != this.companionID) {
                this.npc.discard();
            }
        }
        return this.owner != null;
    }

    @Override
    public void aiUpdateTask() {
        if (this.owner != null && !this.companionJobInterface.isSelfSufficient()) {
            this.foodstats.onUpdate(this.npc);
        }
        if (this.foodstats.getFoodLevel() >= 18) {
            this.npc.stats.healthRegen = 0;
            this.npc.stats.combatRegen = 0;
        }
        if (this.foodstats.needFood() && this.isSitting()) {
            if (this.eatingDelay > 0) {
                --this.eatingDelay;
                return;
            }
            IItemStack prev = this.eating;
            this.eating = this.getFood();
            if (prev != null && this.eating == null) {
                this.npc.setRoleData("");
            }
            if (prev == null && this.eating != null) {
                this.npc.setRoleData("eating");
                this.eatingTicks = 20;
            }
            if (this.isEating()) {
                this.doEating();
            }
        } else if (this.eating != null && !this.isSitting()) {
            this.eating = null;
            this.eatingDelay = 20;
            this.npc.setRoleData("");
        }
        ++this.ticksActive;
        if (this.canAge && this.stage != EnumCompanionStage.FULLGROWN) {
            if (this.stage == EnumCompanionStage.BABY && this.ticksActive > (long)EnumCompanionStage.CHILD.matureAge) {
                this.matureTo(EnumCompanionStage.CHILD);
            } else if (this.stage == EnumCompanionStage.CHILD && this.ticksActive > (long)EnumCompanionStage.TEEN.matureAge) {
                this.matureTo(EnumCompanionStage.TEEN);
            } else if (this.stage == EnumCompanionStage.TEEN && this.ticksActive > (long)EnumCompanionStage.ADULT.matureAge) {
                this.matureTo(EnumCompanionStage.ADULT);
            } else if (this.stage == EnumCompanionStage.ADULT && this.ticksActive > (long)EnumCompanionStage.FULLGROWN.matureAge) {
                this.matureTo(EnumCompanionStage.FULLGROWN);
            }
        }
    }

    @Override
    public void clientUpdate() {
        if (this.npc.getRoleData().equals("eating")) {
            this.eating = this.getFood();
            if (this.isEating()) {
                this.doEating();
            }
        } else if (this.eating != null) {
            this.eating = null;
        }
    }

    private void doEating() {
        if (this.eating == null || this.eating.isEmpty()) {
            return;
        }
        ItemStack eating = this.eating.getMCItemStack();
        if (this.npc.getWorld().isClient) {
            Random rand = this.npc.getRandom();
            for (int j = 0; j < 2; ++j) {
                Vec3d vec3 = new Vec3d(((double)rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
                vec3.rotateX(-this.npc.getPitch() * (float)Math.PI / 180.0f);
                vec3.rotateY(-this.npc.bodyYaw * (float)Math.PI / 180.0f);
                Vec3d vec31 = new Vec3d(((double)rand.nextFloat() - 0.5) * 0.3, (double)(-rand.nextFloat()) * 0.6 - 0.3, (double)(this.npc.getWidth() / 2.0f) + 0.1);
                vec31.rotateX(-this.npc.getPitch() * (float)Math.PI / 180.0f);
                vec31.rotateY(-this.npc.bodyYaw * (float)Math.PI / 180.0f);
                vec31 = vec31.add(this.npc.getX(), this.npc.getY() + (double)this.npc.getHeight() + 0.1, this.npc.getZ());
                String s = "iconcrack_" + Item.getRawId((Item)eating.getItem());
                this.npc.getWorld().addParticle((ParticleEffect)new ItemStackParticleEffect(ParticleTypes.ITEM, eating), vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05, vec3.z);
            }
        } else {
            --this.eatingTicks;
            if (this.eatingTicks <= 0) {
                FoodComponent food = eating.getItem().getFoodComponent();
                if (this.inventory.removeItem(eating, 1)) {
                    this.foodstats.onFoodEaten(food, eating);
                    this.npc.playSound(SoundEvents.ENTITY_PLAYER_BURP, 0.5f, this.npc.getRandom().nextFloat() * 0.1f + 0.9f);
                }
                this.eatingDelay = 20;
                this.npc.setRoleData("");
                this.eating = null;
            } else if (this.eatingTicks > 3 && this.eatingTicks % 2 == 0) {
                Random rand = this.npc.getRandom();
                this.npc.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5f + 0.5f * (float)rand.nextInt(2), (rand.nextFloat() - rand.nextFloat()) * 0.2f + 1.0f);
            }
        }
    }

    public void matureTo(EnumCompanionStage stage) {
        this.stage = stage;
        EntityCustomNpc npc = (EntityCustomNpc)this.npc;
        npc.ais.animationType = stage.animation;
        if (stage == EnumCompanionStage.BABY) {
            npc.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(0.5f, 0.5f, 0.5f);
            npc.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(0.5f, 0.5f, 0.5f);
            npc.modelData.getPartConfig(EnumParts.BODY).setScale(0.5f, 0.5f, 0.5f);
            npc.modelData.getPartConfig(EnumParts.HEAD).setScale(0.7f, 0.7f, 0.7f);
            npc.ais.onAttack = 1;
            npc.ais.setWalkingSpeed(3);
            if (!this.talents.containsKey(EnumCompanionTalent.INVENTORY)) {
                this.talents.put(EnumCompanionTalent.INVENTORY, 0);
            }
        }
        if (stage == EnumCompanionStage.CHILD) {
            npc.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(0.6f, 0.6f, 0.6f);
            npc.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(0.6f, 0.6f, 0.6f);
            npc.modelData.getPartConfig(EnumParts.BODY).setScale(0.6f, 0.6f, 0.6f);
            npc.modelData.getPartConfig(EnumParts.HEAD).setScale(0.8f, 0.8f, 0.8f);
            npc.ais.onAttack = 0;
            npc.ais.setWalkingSpeed(4);
            if (!this.talents.containsKey(EnumCompanionTalent.SWORD)) {
                this.talents.put(EnumCompanionTalent.SWORD, 0);
            }
        }
        if (stage == EnumCompanionStage.TEEN) {
            npc.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(0.8f, 0.8f, 0.8f);
            npc.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(0.8f, 0.8f, 0.8f);
            npc.modelData.getPartConfig(EnumParts.BODY).setScale(0.8f, 0.8f, 0.8f);
            npc.modelData.getPartConfig(EnumParts.HEAD).setScale(0.9f, 0.9f, 0.9f);
            npc.ais.onAttack = 0;
            npc.ais.setWalkingSpeed(5);
            if (!this.talents.containsKey(EnumCompanionTalent.ARMOR)) {
                this.talents.put(EnumCompanionTalent.ARMOR, 0);
            }
        }
        if (stage == EnumCompanionStage.ADULT || stage == EnumCompanionStage.FULLGROWN) {
            npc.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(1.0f, 1.0f, 1.0f);
            npc.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(1.0f, 1.0f, 1.0f);
            npc.modelData.getPartConfig(EnumParts.BODY).setScale(1.0f, 1.0f, 1.0f);
            npc.modelData.getPartConfig(EnumParts.HEAD).setScale(1.0f, 1.0f, 1.0f);
            npc.ais.onAttack = 0;
            npc.ais.setWalkingSpeed(5);
        }
    }

    @Override
    public NbtCompound save(NbtCompound compound) {
        compound.put("CompanionInventory", (NbtElement)this.inventory.getToNBT());
        compound.putString("CompanionOwner", this.uuid);
        compound.putString("CompanionOwnerName", this.ownerName);
        compound.putInt("CompanionID", this.companionID);
        compound.putInt("CompanionStage", this.stage.ordinal());
        compound.putInt("CompanionExp", this.currentExp);
        compound.putBoolean("CompanionCanAge", this.canAge);
        compound.putLong("CompanionAge", this.ticksActive);
        compound.putBoolean("CompanionHasInv", this.hasInv);
        compound.putBoolean("CompanionDefendOwner", this.defendOwner);
        this.foodstats.writeNBT(compound);
        compound.putInt("CompanionJob", this.companionJobInterface.getType().ordinal());
        if (this.companionJobInterface.getType() != EnumCompanionJobs.NONE) {
            compound.put("CompanionJobData", (NbtElement)this.companionJobInterface.getNBT());
        }
        NbtList list = new NbtList();
        for (EnumCompanionTalent talent : this.talents.keySet()) {
            NbtCompound c = new NbtCompound();
            c.putInt("Talent", talent.ordinal());
            c.putInt("Exp", this.talents.get(talent).intValue());
            list.add(c);
        }
        compound.put("CompanionTalents", (NbtElement)list);
        return compound;
    }

    @Override
    public void load(NbtCompound compound) {
        this.inventory.setFromNBT(compound.getCompound("CompanionInventory"));
        this.uuid = compound.getString("CompanionOwner");
        this.ownerName = compound.getString("CompanionOwnerName");
        this.companionID = compound.getInt("CompanionID");
        this.stage = EnumCompanionStage.values()[compound.getInt("CompanionStage")];
        this.currentExp = compound.getInt("CompanionExp");
        this.canAge = compound.getBoolean("CompanionCanAge");
        this.ticksActive = compound.getLong("CompanionAge");
        this.hasInv = compound.getBoolean("CompanionHasInv");
        this.defendOwner = compound.getBoolean("CompanionDefendOwner");
        this.foodstats.readNBT(compound);
        NbtList list = compound.getList("CompanionTalents", 10);
        TreeMap<EnumCompanionTalent, Integer> talents = new TreeMap<EnumCompanionTalent, Integer>();
        for (int i = 0; i < list.size(); ++i) {
            NbtCompound c = list.getCompound(i);
            EnumCompanionTalent talent = EnumCompanionTalent.values()[c.getInt("Talent")];
            talents.put(talent, c.getInt("Exp"));
        }
        this.talents = talents;
        this.setJob(compound.getInt("CompanionJob"));
        this.companionJobInterface.setNBT(compound.getCompound("CompanionJobData"));
        this.setStats();
    }

    private void setJob(int i) {
        EnumCompanionJobs companionJob = EnumCompanionJobs.values()[i];
        this.companionJobInterface = companionJob == EnumCompanionJobs.SHOP ? new CompanionTrader() : (companionJob == EnumCompanionJobs.FARMER ? new CompanionFarmer() : (companionJob == EnumCompanionJobs.GUARD ? new CompanionGuard() : NONE));
        this.companionJobInterface.npc = this.npc;
    }

    @Override
    public void interact(PlayerEntity player) {
        this.interact(player, false);
    }

    public void interact(PlayerEntity player, boolean openGui) {
        if (player != null && this.companionJobInterface.getType() == EnumCompanionJobs.SHOP) {
            ((CompanionTrader)this.companionJobInterface).interact(player);
        }
        if (player != this.owner || !this.npc.isAlive() || this.npc.isAttacking()) {
            return;
        }
        if (player.isInSneakingPose() || openGui) {
            this.openGui(player);
        } else {
            this.setSitting(!this.isSitting());
        }
    }

    public int getTotalLevel() {
        int level = 0;
        for (EnumCompanionTalent talent : this.talents.keySet()) {
            level += this.getTalentLevel(talent);
        }
        return level;
    }

    public int getMaxExp() {
        return 500 + this.getTotalLevel() * 200;
    }

    public void addExp(int exp) {
        if (this.canAddExp(exp)) {
            this.currentExp += exp;
        }
    }

    public boolean canAddExp(int exp) {
        int newExp = this.currentExp + exp;
        return newExp >= 0 && newExp < this.getMaxExp();
    }

    public void gainExp(int chance) {
        if (this.npc.getRandom().nextInt(chance) == 0) {
            this.addExp(1);
        }
    }

    private void openGui(PlayerEntity player) {
        NoppesUtilServer.sendOpenGui(player, EnumGuiType.Companion, this.npc);
    }

    public PlayerEntity getOwner() {
        if (this.uuid == null || this.uuid.isEmpty()) {
            return null;
        }
        try {
            UUID id = UUID.fromString(this.uuid);
            if (id != null) {
                return NoppesUtilServer.getPlayer(this.npc.getServer(), id);
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return null;
    }

    public void setOwner(PlayerEntity player) {
        this.uuid = player.getUuid().toString();
    }

    public boolean hasTalent(EnumCompanionTalent talent) {
        return this.getTalentLevel(talent) > 0;
    }

    public int getTalentLevel(EnumCompanionTalent talent) {
        if (!this.talents.containsKey(talent)) {
            return 0;
        }
        int exp = this.talents.get(talent);
        if (exp >= 5000) {
            return 5;
        }
        if (exp >= 3000) {
            return 4;
        }
        if (exp >= 1700) {
            return 3;
        }
        if (exp >= 1000) {
            return 2;
        }
        if (exp >= 400) {
            return 1;
        }
        return 0;
    }

    public Integer getNextLevel(EnumCompanionTalent talent) {
        if (!this.talents.containsKey(talent)) {
            return 0;
        }
        int exp = this.talents.get(talent);
        if (exp < 400) {
            return 400;
        }
        if (exp < 1000) {
            return 700;
        }
        if (exp < 1700) {
            return 1700;
        }
        if (exp < 3000) {
            return 3000;
        }
        return 5000;
    }

    public void levelSword() {
        if (!this.talents.containsKey(EnumCompanionTalent.SWORD)) {
            return;
        }
    }

    public void levelTalent(EnumCompanionTalent talent, int exp) {
        if (!this.talents.containsKey(EnumCompanionTalent.SWORD)) {
            return;
        }
        this.talents.put(talent, exp + this.talents.get(talent));
    }

    public int getExp(EnumCompanionTalent talent) {
        if (this.talents.containsKey(talent)) {
            return this.talents.get(talent);
        }
        return -1;
    }

    public void setExp(EnumCompanionTalent talent, int exp) {
        this.talents.put(talent, exp);
    }

    private boolean isWeapon(ItemStack item) {
        if (item == null || item.getItem() == null) {
            return false;
        }
        return item.getItem() instanceof SwordItem || item.getItem() instanceof BowItem || item.getItem() == Item.fromBlock((Block)Blocks.COBBLESTONE);
    }

    public boolean canWearWeapon(IItemStack stack) {
        if (stack == null || stack.getMCItemStack().getItem() == null) {
            return false;
        }
        Item item = stack.getMCItemStack().getItem();
        if (item instanceof SwordItem) {
            return this.canWearSword(stack);
        }
        if (item instanceof BowItem) {
            return this.getTalentLevel(EnumCompanionTalent.RANGED) > 2;
        }
        if (item == Item.fromBlock((Block)Blocks.COBBLESTONE)) {
            return this.getTalentLevel(EnumCompanionTalent.RANGED) > 1;
        }
        return false;
    }

    public boolean canWearArmor(ItemStack item) {
        int level = this.getTalentLevel(EnumCompanionTalent.ARMOR);
        if (item == null || !(item.getItem() instanceof ArmorItem) || level <= 0) {
            return false;
        }
        if (level >= 5) {
            return true;
        }
        ArmorItem armor = (ArmorItem)item.getItem();
        int reduction = 1;
        if (armor.getMaterial() instanceof ArmorMaterials) {
            reduction = ((ArmorMaterialsMixin)armor.getMaterial()).durabilityMultiplier();
        }
        if (reduction <= 5 && level >= 1) {
            return true;
        }
        if (reduction <= 7 && level >= 2) {
            return true;
        }
        if (reduction <= 15 && level >= 3) {
            return true;
        }
        return reduction <= 33 && level >= 4;
    }

    public boolean canWearSword(IItemStack item) {
        int level = this.getTalentLevel(EnumCompanionTalent.SWORD);
        if (item == null || !(item.getMCItemStack().getItem() instanceof SwordItem) || level <= 0) {
            return false;
        }
        if (level >= 5) {
            return true;
        }
        return this.getSwordDamage(item) - (double)level < 4.0;
    }

    private double getSwordDamage(IItemStack item) {
        if (item == null || !(item.getMCItemStack().getItem() instanceof SwordItem)) {
            return 0.0;
        }
        Multimap<net.minecraft.entity.attribute.EntityAttribute, EntityAttributeModifier> map = item.getMCItemStack().getAttributeModifiers(EquipmentSlot.MAINHAND);
        for (Map.Entry<net.minecraft.entity.attribute.EntityAttribute, EntityAttributeModifier> entry : map.entries()) {
            if (entry.getKey() != EntityAttributes.GENERIC_ATTACK_DAMAGE) continue;
            EntityAttributeModifier mod = entry.getValue();
            return mod.getValue();
        }
        return 0.0;
    }

    public void setStats() {
        IItemStack weapon = this.npc.inventory.getRightHand();
        this.npc.stats.melee.setStrength((int)(1.0 + this.getSwordDamage(weapon)));
        this.npc.stats.healthRegen = 0;
        this.npc.stats.combatRegen = 0;
        int ranged = this.getTalentLevel(EnumCompanionTalent.RANGED);
        if (ranged > 0 && weapon != null) {
            Item item = weapon.getMCItemStack().getItem();
            if (ranged > 0 && item == Item.fromBlock((Block)Blocks.COBBLESTONE)) {
                this.npc.inventory.setProjectile(weapon);
            }
            if (ranged > 0 && item instanceof BowItem) {
                this.npc.inventory.setProjectile(NpcAPI.Instance().getIItemStack(new ItemStack((ItemConvertible)Items.ARROW)));
            }
        }
        this.inventory.setSize(2 + this.getTalentLevel(EnumCompanionTalent.INVENTORY) * 2);
    }

    public void setSelfsuficient(boolean bo) {
        if (this.owner == null || bo == this.companionJobInterface.isSelfSufficient()) {
            return;
        }
        PlayerData data = PlayerData.get(this.owner);
        if (!bo && data.hasCompanion()) {
            return;
        }
        data.setCompanion(bo ? null : this.npc);
        if (this.companionJobInterface.getType() == EnumCompanionJobs.GUARD) {
            ((CompanionGuard)this.companionJobInterface).isStanding = bo;
        } else if (this.companionJobInterface.getType() == EnumCompanionJobs.FARMER) {
            ((CompanionFarmer)this.companionJobInterface).isStanding = bo;
        }
    }

    public void setSitting(boolean sit) {
        if (sit) {
            this.npc.ais.animationType = 1;
            this.npc.ais.onAttack = 3;
            this.npc.ais.setStartPos(this.npc.getBlockPos());
            this.npc.getNavigation().stop();
            this.npc.requestTeleport(this.npc.getStartXPos(), this.npc.getY(), this.npc.getStartZPos());
        } else {
            this.npc.ais.animationType = this.stage.animation;
            this.npc.ais.onAttack = 0;
        }
        this.npc.updateAI = true;
    }

    public boolean isSitting() {
        return this.npc.ais.animationType == 1;
    }

    public float getDamageAfterArmorAbsorb(DamageSource source, float damage) {
        if (!this.hasInv || this.getTalentLevel(EnumCompanionTalent.ARMOR) <= 0) {
            return damage;
        }
        if (!source.isIn(DamageTypeTags.BYPASSES_SHIELD)) {
            this.damageArmor(damage);
            int i = 25 - this.getTotalArmorValue();
            float f1 = damage * (float)i;
            damage = f1 / 25.0f;
        }
        return damage;
    }

    private void damageArmor(float damage) {
        if ((damage /= 4.0f) < 1.0f) {
            damage = 1.0f;
        }
        boolean hasArmor = false;
        Iterator<Map.Entry<Integer, IItemStack>> ita = this.npc.inventory.armor.entrySet().iterator();
        while (ita.hasNext()) {
            Map.Entry<Integer, IItemStack> entry = ita.next();
            IItemStack item = entry.getValue();
            if (item == null || !(item.getMCItemStack().getItem() instanceof ArmorItem)) continue;
            hasArmor = true;
            item.getMCItemStack().damage((int)damage, (LivingEntity)this.npc, entity -> entity.sendEquipmentBreakStatus(EquipmentSlot.fromTypeIndex((EquipmentSlot.Type)EquipmentSlot.Type.ARMOR, (int)((Integer)entry.getKey()))));
            if (item.getStackSize() > 0) continue;
            ita.remove();
        }
        this.gainExp(hasArmor ? 4 : 8);
    }

    public int getTotalArmorValue() {
        int armorValue = 0;
        for (IItemStack armor : this.npc.inventory.armor.values()) {
            if (armor == null || !(armor.getMCItemStack().getItem() instanceof ArmorItem)) continue;
            armorValue += ((ArmorItem)armor.getMCItemStack().getItem()).getProtection();
        }
        return armorValue;
    }

    @Override
    public boolean isFollowing() {
        if (this.companionJobInterface.isSelfSufficient()) {
            return false;
        }
        return this.owner != null && !this.isSitting();
    }

    @Override
    public boolean defendOwner() {
        return this.defendOwner && this.owner != null && this.stage != EnumCompanionStage.BABY && !this.companionJobInterface.isSelfSufficient();
    }

    public boolean hasOwner() {
        return !this.uuid.isEmpty();
    }

    public void addMovementStat(double x, double y, double z) {
        long i = Math.round(Math.sqrt(x * x + y * y + z * z) * 100.0);
        if (this.npc.isAttacking()) {
            this.foodstats.addExhaustion(0.04f * (float)i * 0.01f);
        } else {
            this.foodstats.addExhaustion(0.02f * (float)i * 0.01f);
        }
    }

    private IItemStack getFood() {
        for (ItemStack item : this.inventory.items) {
            if (item.isEmpty() || item.getItem().getFoodComponent() == null) continue;
            return NpcAPI.Instance().getIItemStack(item);
        }
        return null;
    }

    public IItemStack getItemInHand() {
        if (this.eating != null && !this.eating.isEmpty()) {
            return this.eating;
        }
        return this.npc.inventory.getRightHand();
    }

    public boolean isEating() {
        return this.eating != null && !this.eating.isEmpty();
    }

    public boolean hasInv() {
        if (!this.hasInv) {
            return false;
        }
        return this.hasTalent(EnumCompanionTalent.INVENTORY) || this.hasTalent(EnumCompanionTalent.ARMOR) || this.hasTalent(EnumCompanionTalent.SWORD);
    }

    public void attackedEntity(Entity entity) {
        IItemStack weapon = this.npc.inventory.getRightHand();
        this.gainExp(weapon == null ? 8 : 4);
        if (weapon == null) {
            return;
        }
        weapon.getMCItemStack().damage(1, (LivingEntity)this.npc, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        if (weapon.getMCItemStack().getCount() <= 0) {
            this.npc.inventory.setRightHand(null);
        }
    }

    public void addTalentExp(EnumCompanionTalent talent, int exp) {
        if (this.talents.containsKey(talent)) {
            exp += this.talents.get(talent).intValue();
        }
        this.talents.put(talent, exp);
    }

    @Override
    public int getType() {
        return 6;
    }
}

