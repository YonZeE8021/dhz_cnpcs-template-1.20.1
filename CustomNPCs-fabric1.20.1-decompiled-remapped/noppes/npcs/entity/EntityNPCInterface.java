/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.entity.FakePlayer
 *  net.minecraft.entity.boss.BossBar$Color
 *  net.minecraft.entity.boss.BossBar$Style
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ActionResult
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.EntityGroup
 *  net.minecraft.entity.mob.PathAwareEntity
 *  net.minecraft.entity.ai.control.MoveControl
 *  net.minecraft.entity.ai.goal.Goal
 *  net.minecraft.entity.ai.goal.Goal$Control
 *  net.minecraft.entity.ai.goal.GoalSelector
 *  net.minecraft.entity.ai.goal.AvoidSunlightGoal
 *  net.minecraft.entity.ai.goal.RevengeGoal
 *  net.minecraft.entity.ai.pathing.BirdNavigation
 *  net.minecraft.entity.ai.pathing.EntityNavigation
 *  net.minecraft.entity.ai.pathing.MobNavigation
 *  net.minecraft.entity.ai.pathing.SwimNavigation
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.entity.mob.HostileEntity
 *  net.minecraft.entity.ai.RangedAttackMob
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.vehicle.BoatEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.server.command.CommandOutput
 *  net.minecraft.server.command.ServerCommandSource
 *  net.minecraft.block.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtInt
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.text.Text
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.block.BlockState
 *  net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket
 *  net.minecraft.entity.data.TrackedData
 *  net.minecraft.entity.data.TrackedDataHandler
 *  net.minecraft.entity.data.TrackedDataHandlerRegistry
 *  net.minecraft.entity.data.DataTracker
 *  net.minecraft.entity.data.DataTracker$Entry
 *  net.minecraft.entity.data.DataTracker$SerializedEntry
 *  net.minecraft.util.Identifier
 *  net.minecraft.entity.boss.ServerBossBar
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.block.piston.PistonBehavior
 *  net.minecraft.entity.EntityDimensions
 *  net.minecraft.entity.EntityPose
 *  net.minecraft.entity.ai.goal.PrioritizedGoal
 *  net.minecraft.entity.ai.goal.LongDoorInteractGoal
 *  net.minecraft.entity.attribute.DefaultAttributeContainer$Builder
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.entry.RegistryEntry$Reference
 *  net.minecraft.registry.RegistryKeys
 */
package noppes.npcs.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.AvoidSunlightGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.util.Identifier;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.LongDoorInteractGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.RegistryKeys;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.IChatMessages;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.NpcDamageSource;
import noppes.npcs.VersionCompatibility;
import noppes.npcs.ai.CombatHandler;
import noppes.npcs.ai.EntityAIAnimation;
import noppes.npcs.ai.EntityAIAttackTarget;
import noppes.npcs.ai.EntityAIAvoidTarget;
import noppes.npcs.ai.EntityAIBustDoor;
import noppes.npcs.ai.EntityAIFindShade;
import noppes.npcs.ai.EntityAIFollow;
import noppes.npcs.ai.EntityAIJob;
import noppes.npcs.ai.EntityAILook;
import noppes.npcs.ai.EntityAIMoveIndoors;
import noppes.npcs.ai.EntityAIMovingPath;
import noppes.npcs.ai.EntityAIPanic;
import noppes.npcs.ai.EntityAIPounceTarget;
import noppes.npcs.ai.EntityAIRangedAttack;
import noppes.npcs.ai.EntityAIReturn;
import noppes.npcs.ai.EntityAIRole;
import noppes.npcs.ai.EntityAISprintToTarget;
import noppes.npcs.ai.EntityAITransform;
import noppes.npcs.ai.EntityAIWander;
import noppes.npcs.ai.EntityAIWatchClosest;
import noppes.npcs.ai.EntityAIWaterNav;
import noppes.npcs.ai.EntityAIWorldLines;
import noppes.npcs.ai.FlyingMoveHelper;
import noppes.npcs.ai.NpcGroundPathNavigator;
import noppes.npcs.ai.selector.NPCAttackSelector;
import noppes.npcs.ai.target.EntityAIClearTarget;
import noppes.npcs.ai.target.EntityAIOwnerHurtByTarget;
import noppes.npcs.ai.target.EntityAIOwnerHurtTarget;
import noppes.npcs.ai.target.NpcNearestAttackableTargetGoal;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.PotionEffectType;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.IProjectile;
import noppes.npcs.api.event.NpcEvent;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.api.wrapper.NPCWrapper;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.client.ISynchedEntityData;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.LinkedNpcController;
import noppes.npcs.controllers.VisibilityController;
import noppes.npcs.controllers.data.DataTransform;
import noppes.npcs.controllers.data.Dialog;
import noppes.npcs.controllers.data.DialogOption;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.controllers.data.QuestData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityProjectile;
import noppes.npcs.entity.data.DataAI;
import noppes.npcs.entity.data.DataAbilities;
import noppes.npcs.entity.data.DataAdvanced;
import noppes.npcs.entity.data.DataDisplay;
import noppes.npcs.entity.data.DataInventory;
import noppes.npcs.entity.data.DataScript;
import noppes.npcs.entity.data.DataStats;
import noppes.npcs.entity.data.DataTimers;
import noppes.npcs.items.ItemSoulstoneFilled;
import noppes.npcs.mixin.EntityIMixin;
import noppes.npcs.mixin.GoalSelectorMixin;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketChatBubble;
import noppes.npcs.packets.client.PacketNpcUpdate;
import noppes.npcs.packets.client.PacketNpcVisibleFalse;
import noppes.npcs.packets.client.PacketNpcVisibleTrue;
import noppes.npcs.packets.client.PacketPlaySound;
import noppes.npcs.packets.client.PacketQuestCompletion;
import noppes.npcs.packets.client.PacketUpdatePhysics;
import noppes.npcs.roles.JobBard;
import noppes.npcs.roles.JobFollower;
import noppes.npcs.roles.JobInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.roles.RoleInterface;
import noppes.npcs.util.GameProfileAlt;

public abstract class EntityNPCInterface
extends PathAwareEntity
implements RangedAttackMob {
    public static final TrackedData<Boolean> Attacking = DataTracker.registerData(EntityNPCInterface.class, (TrackedDataHandler)TrackedDataHandlerRegistry.BOOLEAN);
    protected static final TrackedData<Integer> Animation = DataTracker.registerData(EntityNPCInterface.class, (TrackedDataHandler)TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<String> RoleData = DataTracker.registerData(EntityNPCInterface.class, (TrackedDataHandler)TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<String> JobData = DataTracker.registerData(EntityNPCInterface.class, (TrackedDataHandler)TrackedDataHandlerRegistry.STRING);
    private static final TrackedData<Integer> FactionData = DataTracker.registerData(EntityNPCInterface.class, (TrackedDataHandler)TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> Walking = DataTracker.registerData(EntityNPCInterface.class, (TrackedDataHandler)TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> Interacting = DataTracker.registerData(EntityNPCInterface.class, (TrackedDataHandler)TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IsDead = DataTracker.registerData(EntityNPCInterface.class, (TrackedDataHandler)TrackedDataHandlerRegistry.BOOLEAN);
    public static final GameProfileAlt CommandProfile = new GameProfileAlt();
    public static final GameProfileAlt ChatEventProfile = new GameProfileAlt();
    public static final GameProfileAlt GenericProfile = new GameProfileAlt();
    public static FakePlayer ChatEventPlayer;
    public static FakePlayer CommandPlayer;
    public static FakePlayer GenericPlayer;
    public ICustomNpc wrappedNPC;
    public final DataAbilities abilities = new DataAbilities(this);
    public DataDisplay display = new DataDisplay(this);
    public DataStats stats = new DataStats(this);
    public DataInventory inventory = new DataInventory(this);
    public final DataAI ais = new DataAI(this);
    public final DataAdvanced advanced = new DataAdvanced(this);
    public final DataScript script = new DataScript(this);
    public final DataTransform transform = new DataTransform(this);
    public final DataTimers timers = new DataTimers((Object)this);
    public CombatHandler combatHandler = new CombatHandler(this);
    public String linkedName = "";
    public long linkedLast = 0L;
    public LinkedNpcController.LinkedData linkedData;
    public EntityDimensions baseSize = new EntityDimensions(0.6f, 1.8f, false);
    private static final EntityDimensions sizeSleep;
    public float scaleX;
    public float scaleY;
    public float scaleZ;
    private boolean wasKilled = false;
    public RoleInterface role = RoleInterface.NONE;
    public JobInterface job = JobInterface.NONE;
    public HashMap<Integer, DialogOption> dialogs;
    public boolean hasDied = false;
    public long killedtime = 0L;
    public long totalTicksAlive = 0L;
    private int taskCount = 1;
    public int lastInteract = 0;
    public Faction faction;
    private EntityAIRangedAttack aiRange;
    private Goal aiAttackTarget;
    public EntityAILook lookAi;
    public EntityAIAnimation animateAi;
    public List<LivingEntity> interactingEntities = new ArrayList<LivingEntity>();
    public Identifier textureLocation = null;
    public Identifier textureGlowLocation = null;
    public Identifier textureCloakLocation = null;
    public int currentAnimation = 0;
    public int animationStart = 0;
    public int npcVersion = VersionCompatibility.ModRev;
    public IChatMessages messages;
    public boolean updateClient = false;
    public boolean updateAI = false;
    public final ServerBossBar bossInfo;
    public final HashSet<Integer> tracking = new HashSet();
    public double prevChasingPosX;
    public double prevChasingPosY;
    public double prevChasingPosZ;
    public double chasingPosX;
    public double chasingPosY;
    public double chasingPosZ;
    private double startYPos = -6666.0;

    public EntityNPCInterface(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        if (!this.isClientSide()) {
            this.wrappedNPC = new NPCWrapper<EntityNPCInterface>(this);
        }
        this.registerBaseAttributes();
        this.dialogs = new HashMap();
        if (!CustomNpcs.DefaultInteractLine.isEmpty()) {
            this.advanced.interactLines.lines.put(0, new Line(CustomNpcs.DefaultInteractLine));
        }
        this.experiencePoints = 0;
        this.scaleZ = 0.9375f;
        this.scaleY = 0.9375f;
        this.scaleX = 0.9375f;
        this.faction = this.getFaction();
        this.setFaction(this.faction.id);
        this.updateAI = true;
        this.bossInfo = new ServerBossBar(this.getDisplayName(), BossBar.Color.field_5783, BossBar.Style.field_5795);
        this.bossInfo.setVisible(false);
    }

    public boolean canBreatheInWater() {
        return this.ais.movementType == 2;
    }

    public boolean isPushedByFluids() {
        return this.ais.movementType != 2;
    }

    public LivingEntity getControllingPassenger() {
        return this.getPassengerList().isEmpty() || !(this.getPassengerList().get(0) instanceof LivingEntity) || !this.ais.mountControl ? null : (LivingEntity)this.getPassengerList().get(0);
    }

    private void registerBaseAttributes() {
        this.getAttributeInstance(EntityAttributes.field_23716).setBaseValue((double)this.stats.maxHealth);
        this.getAttributeInstance(EntityAttributes.field_23717).setBaseValue((double)CustomNpcs.NpcNavRange);
        this.getAttributeInstance(EntityAttributes.field_23719).setBaseValue((double)this.getMovementSpeed());
        this.getAttributeInstance(EntityAttributes.field_23721).setBaseValue((double)this.stats.melee.getStrength());
        this.getAttributeInstance(EntityAttributes.field_23720).setBaseValue((double)(this.getMovementSpeed() * 2.0f));
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes().add(EntityAttributes.field_23721).add(EntityAttributes.field_23720).add(EntityAttributes.field_23717);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(RoleData, (Object)String.valueOf(""));
        this.dataTracker.startTracking(JobData, (Object)String.valueOf(""));
        this.dataTracker.startTracking(FactionData, (Object)0);
        this.dataTracker.startTracking(Animation, (Object)0);
        this.dataTracker.startTracking(Walking, (Object)false);
        this.dataTracker.startTracking(Interacting, (Object)false);
        this.dataTracker.startTracking(IsDead, (Object)false);
        this.dataTracker.startTracking(Attacking, (Object)false);
    }

    public boolean isAlive() {
        return super.isAlive() && !this.isKilled();
    }

    public void tick() {
        super.tick();
        if (this.age % 10 == 0) {
            this.startYPos = this.calculateStartYPos(this.ais.startPos()) + 1.0;
            if (this.startYPos < (double)this.getWorld().getBottomY() && !this.isClientSide()) {
                this.discard();
            }
            EventHooks.onNPCTick(this);
        }
        this.timers.update();
        if (this.getWorld().isClient && this.wasKilled != this.isKilled() && this.wasKilled) {
            this.deathTime = 0;
            this.calculateDimensions();
        }
        this.wasKilled = this.isKilled();
        if (this.currentAnimation == 14) {
            this.deathTime = 19;
        }
    }

    public boolean tryAttack(Entity par1Entity) {
        RegistryEntry.Reference damageTypeHolder;
        boolean var4;
        float f = this.stats.melee.getStrength();
        if (this.stats.melee.getDelay() < 10) {
            par1Entity.timeUntilRegen = 0;
        }
        if (par1Entity instanceof LivingEntity) {
            NpcEvent.MeleeAttackEvent event = new NpcEvent.MeleeAttackEvent(this.wrappedNPC, (LivingEntity)par1Entity, f);
            if (EventHooks.onNPCAttacksMelee(this, event)) {
                return false;
            }
            f = event.damage;
        }
        if (var4 = par1Entity.damage(new DamageSource((RegistryEntry)(damageTypeHolder = this.getWorld().getRegistryManager().get(RegistryKeys.field_42534).entryOf(NpcDamageSource.NPC)), (Entity)this), f)) {
            if (this.getOwner() instanceof PlayerEntity) {
                EntityUtil.setRecentlyHit((LivingEntity)par1Entity);
            }
            if (this.stats.melee.getKnockback() > 0) {
                par1Entity.addVelocity((double)(-MathHelper.sin((float)(this.getYaw() * (float)Math.PI / 180.0f)) * (float)this.stats.melee.getKnockback() * 0.5f), 0.1, (double)(MathHelper.cos((float)(this.getYaw() * (float)Math.PI / 180.0f)) * (float)this.stats.melee.getKnockback() * 0.5f));
                this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
            }
            if (this.role.getType() == 6) {
                ((RoleCompanion)this.role).attackedEntity(par1Entity);
            }
        }
        if (this.stats.melee.getEffectType() != 0) {
            if (this.stats.melee.getEffectType() != 666) {
                ((LivingEntity)par1Entity).addStatusEffect(new StatusEffectInstance(PotionEffectType.getMCType(this.stats.melee.getEffectType()), this.stats.melee.getEffectTime() * 20, this.stats.melee.getEffectStrength()));
            } else {
                par1Entity.setFireTicks(this.stats.melee.getEffectTime() * 20);
            }
        }
        return var4;
    }

    public void tickMovement() {
        float f;
        if (CustomNpcs.FreezeNPCs) {
            return;
        }
        if (this.isAiDisabled()) {
            super.tickMovement();
            return;
        }
        ++this.totalTicksAlive;
        this.tickHandSwing();
        if (this.age % 20 == 0) {
            this.faction = this.getFaction();
        }
        if (!this.getWorld().isClient) {
            if (!this.isKilled() && this.age % 20 == 0) {
                this.advanced.scenes.update();
                if (this.getHealth() < this.getMaxHealth()) {
                    if (this.stats.healthRegen > 0 && !this.isAttacking()) {
                        this.heal(this.stats.healthRegen);
                    }
                    if (this.stats.combatRegen > 0 && this.isAttacking()) {
                        this.heal(this.stats.combatRegen);
                    }
                }
                if (this.faction.getsAttacked && !this.isAttacking()) {
                    List list = this.getWorld().getNonSpectatingEntities(HostileEntity.class, this.getBoundingBox().expand(16.0, 16.0, 16.0));
                    for (HostileEntity mob : list) {
                        if (mob.getTarget() != null || !this.canNpcSee((Entity)mob)) continue;
                        mob.setTarget((LivingEntity)this);
                    }
                }
                if (this.linkedData != null && this.linkedData.time > this.linkedLast) {
                    LinkedNpcController.Instance.loadNpcData(this);
                }
                if (this.updateClient) {
                    this.updateClient();
                }
                if (this.updateAI) {
                    this.updateTasks();
                    this.updateAI = false;
                }
            }
            if (this.getHealth() <= 0.0f && !this.isKilled()) {
                this.clearStatusEffects();
                this.dataTracker.set(IsDead, (Object)true);
                this.updateTasks();
                this.calculateDimensions();
            }
            if (this.display.getBossbar() == 2) {
                this.bossInfo.setVisible(this.getTarget() != null);
            }
            this.dataTracker.set(Walking, (Object)(!this.getNavigation().isIdle() ? 1 : 0));
            this.dataTracker.set(Interacting, (Object)this.isInteracting());
            this.combatHandler.update();
            this.onCollide();
        }
        if (this.wasKilled != this.isKilled() && this.wasKilled) {
            this.reset();
        }
        if (this.getWorld().isDay() && !this.getWorld().isClient && this.stats.burnInSun && (f = this.getBrightnessAtEyes()) > 0.5f && this.random.nextFloat() * 30.0f < (f - 0.4f) * 2.0f && this.getWorld().isSkyVisible(this.getBlockPos())) {
            this.setFireTicks(160);
        }
        super.tickMovement();
        if (this.getWorld().isClient) {
            this.role.clientUpdate();
            if (this.textureCloakLocation != null) {
                this.cloakUpdate();
            }
            if (this.currentAnimation != (Integer)this.dataTracker.get(Animation)) {
                this.currentAnimation = (Integer)this.dataTracker.get(Animation);
                this.animationStart = this.age;
                this.calculateDimensions();
            }
            if (this.job.getType() == 1) {
                ((JobBard)this.job).aiStep();
            }
        }
        if (this.display.getBossbar() > 0) {
            this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
        }
    }

    public void updateClient() {
        Packets.sendNearby((Entity)this, new PacketNpcUpdate(this.getId(), this.writeSpawnData()));
        this.updateClient = false;
    }

    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (this.getWorld().isClient) {
            return this.isAttacking() ? ActionResult.FAIL : ActionResult.PASS;
        }
        if (hand != Hand.field_5808) {
            return ActionResult.PASS;
        }
        ItemStack stack = player.getStackInHand(hand);
        if (stack != null) {
            Item item = stack.getItem();
            if (item == CustomItems.cloner || item == CustomItems.wand || item == CustomItems.mount || item == CustomItems.scripter) {
                this.setTarget(null);
                this.setAttacker(null);
                return ActionResult.SUCCESS;
            }
            if (item == CustomItems.moving) {
                this.setTarget(null);
                stack.setSubNbt("NPCID", (NbtElement)NbtInt.of((int)this.getId()));
                player.sendMessage((Text)Text.translatable((String)"message.pather.register", (Object[])new Object[]{this.getName()}));
                return ActionResult.SUCCESS;
            }
        }
        if (EventHooks.onNPCInteract(this, player)) {
            return ActionResult.FAIL;
        }
        if (this.getFaction().isAggressiveToPlayer(player) || this.isAttacking()) {
            return ActionResult.FAIL;
        }
        this.addInteract((LivingEntity)player);
        Dialog dialog = this.getDialog(player);
        QuestData data = PlayerData.get((PlayerEntity)player).questData.getQuestCompletion(player, this);
        if (data != null) {
            Packets.send((ServerPlayerEntity)player, new PacketQuestCompletion(data.quest.id));
        } else if (dialog != null) {
            NoppesUtilServer.openDialog(player, this, dialog);
        } else if (this.role.getType() != 0) {
            this.role.interact(player);
        } else {
            this.say(player, this.advanced.getInteractLine());
        }
        return ActionResult.PASS;
    }

    public void addInteract(LivingEntity entity) {
        if (!this.ais.stopAndInteract || this.isAttacking() || !entity.isAlive() || this.isAiDisabled()) {
            return;
        }
        if (this.age - this.lastInteract < 180) {
            this.interactingEntities.clear();
        }
        this.getNavigation().stop();
        this.lastInteract = this.age;
        if (!this.interactingEntities.contains(entity)) {
            this.interactingEntities.add(entity);
        }
    }

    public boolean isInteracting() {
        if (this.age - this.lastInteract < 40 || this.isClientSide() && ((Boolean)this.dataTracker.get(Interacting)).booleanValue()) {
            return true;
        }
        return this.ais.stopAndInteract && !this.interactingEntities.isEmpty() && this.age - this.lastInteract < 180;
    }

    private Dialog getDialog(PlayerEntity player) {
        for (DialogOption option : this.dialogs.values()) {
            if (option == null || !option.hasDialog()) continue;
            Dialog dialog = option.getDialog();
            if (!dialog.availability.isAvailable(player)) continue;
            return dialog;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean damage(DamageSource damagesource, float i) {
        if (this.getWorld().isClient || CustomNpcs.FreezeNPCs || damagesource.getName().equals("inWall")) {
            return false;
        }
        if (damagesource.getName().equals("outOfLevel") && this.isKilled()) {
            this.reset();
        }
        i = this.stats.resistances.applyResistance(damagesource, i);
        float f = this.timeUntilRegen;
        Objects.requireNonNull(this);
        if (f > 20.0f / 2.0f && i <= this.lastDamageTaken) {
            return false;
        }
        Entity entity = NoppesUtilServer.GetDamageSourcee(damagesource);
        LivingEntity attackingEntity = null;
        if (entity instanceof LivingEntity) {
            attackingEntity = (LivingEntity)entity;
        }
        if (attackingEntity != null && attackingEntity == this.getOwner()) {
            return false;
        }
        if (attackingEntity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface)attackingEntity;
            if (npc.faction.id == this.faction.id) {
                return false;
            }
            if (npc.getOwner() instanceof PlayerEntity) {
                this.hurtTime = 100;
            }
        } else if (attackingEntity instanceof PlayerEntity && this.faction.isFriendlyToPlayer((PlayerEntity)attackingEntity)) {
            return false;
        }
        NpcEvent.DamagedEvent event = new NpcEvent.DamagedEvent(this.wrappedNPC, entity, i, damagesource);
        if (EventHooks.onNPCDamaged(this, event)) {
            return false;
        }
        i = event.damage;
        if (this.isKilled()) {
            return false;
        }
        if (attackingEntity == null) {
            return super.damage(damagesource, i);
        }
        try {
            if (this.isAttacking()) {
                if (this.getTarget() != null && this.squaredDistanceTo((Entity)this.getTarget()) > this.squaredDistanceTo((Entity)attackingEntity)) {
                    this.setTarget(attackingEntity);
                }
                boolean bl = super.damage(damagesource, i);
                return bl;
            }
            if (i > 0.0f) {
                List inRange = this.getWorld().getNonSpectatingEntities(EntityNPCInterface.class, this.getBoundingBox().expand(32.0, 16.0, 32.0));
                for (EntityNPCInterface npc : inRange) {
                    if (npc.isKilled() || !npc.advanced.defendFaction || npc.faction.id != this.faction.id || !npc.canNpcSee((Entity)this) && !npc.ais.directLOS && !npc.canNpcSee((Entity)attackingEntity)) continue;
                    npc.onAttack(attackingEntity);
                }
                this.setTarget(attackingEntity);
            }
            boolean bl = super.damage(damagesource, i);
            return bl;
        }
        finally {
            if (event.clearTarget) {
                this.setTarget(null);
                this.setAttacker(null);
            }
        }
    }

    protected void applyDamage(DamageSource damageSrc, float damageAmount) {
        super.applyDamage(damageSrc, damageAmount);
        this.combatHandler.damage(damageSrc, damageAmount);
    }

    public void onAttack(LivingEntity entity) {
        if (entity == null || entity == this || this.isAttacking() || this.ais.onAttack == 3 || entity == this.getOwner()) {
            return;
        }
        super.setTarget(entity);
    }

    public void setTarget(LivingEntity entity) {
        Line line;
        if (entity instanceof PlayerEntity && ((PlayerEntity)entity).getAbilities().invulnerable || entity != null && entity == this.getOwner() || this.getTarget() == entity) {
            return;
        }
        if (entity != null) {
            NpcEvent.TargetEvent event = new NpcEvent.TargetEvent(this.wrappedNPC, (LivingEntity)entity);
            if (EventHooks.onNPCTarget(this, event)) {
                return;
            }
            entity = event.entity == null ? null : event.entity.getMCEntity();
        } else {
            for (PrioritizedGoal en : this.targetSelector.getGoals()) {
                en.stop();
            }
            if (EventHooks.onNPCTargetLost(this, this.getTarget())) {
                return;
            }
        }
        if (entity != null && entity != this && this.ais.onAttack != 3 && !this.isAttacking() && !this.isClientSide() && (line = this.advanced.getAttackLine()) != null) {
            this.saySurrounding(Line.formatTarget(line, entity));
        }
        super.setTarget(entity);
    }

    public void attack(LivingEntity entity, float f) {
        ItemStack proj = ItemStackWrapper.MCItem(this.inventory.getProjectile());
        if (proj == null) {
            this.updateAI = true;
            return;
        }
        NpcEvent.RangedLaunchedEvent event = new NpcEvent.RangedLaunchedEvent(this.wrappedNPC, entity, this.stats.ranged.getStrength());
        for (int i = 0; i < this.stats.ranged.getShotCount(); ++i) {
            EntityProjectile projectile = this.shoot(entity, this.stats.ranged.getAccuracy(), proj, f == 1.0f);
            projectile.damage = event.damage;
            projectile.callback = (projectile1, pos, entity1) -> {
                SoundEvent sound;
                Entity e;
                if (proj.getItem() == CustomItems.soulstoneFull && (e = ItemSoulstoneFilled.Spawn(null, proj, this.getWorld(), pos)) instanceof LivingEntity && entity1 instanceof LivingEntity) {
                    if (e instanceof MobEntity) {
                        ((MobEntity)e).setTarget((LivingEntity)entity1);
                    } else {
                        ((LivingEntity)e).setAttacker((LivingEntity)entity1);
                    }
                }
                if ((sound = this.stats.ranged.getSoundEvent(entity1 != null ? 1 : 2)) != null) {
                    projectile1.playSound(sound, 1.0f, 1.2f / (this.getRandom().nextFloat() * 0.2f + 0.9f));
                }
                return false;
            };
            SoundEvent sound = this.stats.ranged.getSoundEvent(0);
            if (sound != null) {
                this.playSound(sound, 2.0f, 1.0f);
            }
            event.projectiles.add((IProjectile)NpcAPI.Instance().getIEntity((Entity)projectile));
        }
        EventHooks.onNPCRangedLaunched(this, event);
    }

    public EntityProjectile shoot(LivingEntity entity, int accuracy, ItemStack proj, boolean indirect) {
        return this.shoot(entity.getX(), entity.getBoundingBox().minY + (double)(entity.getHeight() / 2.0f), entity.getZ(), accuracy, proj, indirect);
    }

    public EntityProjectile shoot(double x, double y, double z, int accuracy, ItemStack proj, boolean indirect) {
        EntityProjectile projectile = new EntityProjectile(this.getWorld(), (LivingEntity)this, proj.copy(), true);
        double varX = x - this.getX();
        double varY = y - (this.getY() + (double)this.getStandingEyeHeight());
        double varZ = z - this.getZ();
        float varF = projectile.hasGravity() ? (float)Math.sqrt(varX * varX + varZ * varZ) : 0.0f;
        float angle = projectile.getAngleForXYZ(varX, varY, varZ, varF, indirect);
        float acc = 20.0f - (float)MathHelper.floor((float)((float)accuracy / 5.0f));
        projectile.setVelocity(varX, varY, varZ, angle, acc);
        this.getWorld().spawnEntity((Entity)projectile);
        return projectile;
    }

    private void clearTasks(GoalSelector tasks) {
        ArrayList list = new ArrayList(tasks.getGoals());
        for (PrioritizedGoal entityaitaskentry : list) {
            tasks.remove((Goal)entityaitaskentry);
        }
        tasks.getGoals().clear();
        ((GoalSelectorMixin)tasks).lockedFlags().clear();
        ((GoalSelectorMixin)tasks).disabledFlags().clear();
    }

    private void updateTasks() {
        if (this.getWorld() == null || this.getWorld().isClient || !(this.getWorld() instanceof ServerWorld)) {
            return;
        }
        ServerWorld sLevel = (ServerWorld)this.getWorld();
        this.clearTasks(this.goalSelector);
        this.clearTasks(this.targetSelector);
        if (this.isKilled()) {
            return;
        }
        this.targetSelector.add(0, (Goal)new EntityAIClearTarget(this));
        this.targetSelector.add(1, (Goal)new RevengeGoal((PathAwareEntity)this, new Class[0]));
        this.targetSelector.add(2, new NpcNearestAttackableTargetGoal<LivingEntity>(this, LivingEntity.class, 4, this.ais.directLOS, false, (Predicate<LivingEntity>)((Object)new NPCAttackSelector(this))));
        this.targetSelector.add(3, (Goal)new EntityAIOwnerHurtByTarget(this));
        this.targetSelector.add(4, (Goal)new EntityAIOwnerHurtTarget(this));
        if (this.ais.movementType == 1) {
            this.moveControl = new FlyingMoveHelper(this);
            if (!(this.navigation instanceof BirdNavigation)) {
                this.navigation = new BirdNavigation((MobEntity)this, this.getWorld()){

                    public boolean isValidPosition(BlockPos p_26439_) {
                        return true;
                    }
                };
            }
        } else if (this.ais.movementType == 2) {
            this.moveControl = new FlyingMoveHelper(this);
            if (!(this.navigation instanceof SwimNavigation)) {
                this.navigation = new SwimNavigation((MobEntity)this, this.getWorld());
            }
        } else {
            this.moveControl = new MoveControl((MobEntity)this);
            if (!(this.navigation instanceof MobNavigation)) {
                this.navigation = new NpcGroundPathNavigator((MobEntity)this, this.getWorld());
            }
            this.goalSelector.add(0, (Goal)new EntityAIWaterNav(this));
        }
        this.taskCount = 1;
        this.addRegularEntries();
        this.doorInteractType();
        this.seekShelter();
        this.setResponse();
        this.setMoveType();
    }

    protected EntityNavigation createNavigation(World p_21480_) {
        return new NpcGroundPathNavigator((MobEntity)this, p_21480_);
    }

    private void setResponse() {
        this.aiRange = null;
        this.aiAttackTarget = null;
        if (this.ais.canSprint) {
            this.goalSelector.add(this.taskCount++, (Goal)new EntityAISprintToTarget(this));
        }
        if (this.ais.onAttack == 1) {
            this.goalSelector.add(this.taskCount++, (Goal)new EntityAIPanic(this, 1.2f));
        } else if (this.ais.onAttack == 2) {
            this.goalSelector.add(this.taskCount++, (Goal)new EntityAIAvoidTarget(this));
        } else if (this.ais.onAttack == 0) {
            if (this.ais.canLeap) {
                this.goalSelector.add(this.taskCount++, (Goal)new EntityAIPounceTarget(this));
            }
            this.aiAttackTarget = new EntityAIAttackTarget(this);
            this.goalSelector.add(this.taskCount, this.aiAttackTarget);
            if (this.inventory.getProjectile() != null) {
                this.aiRange = new EntityAIRangedAttack(this);
                this.goalSelector.add(this.taskCount++, (Goal)this.aiRange);
            }
        } else if (this.ais.onAttack == 3) {
            // empty if block
        }
    }

    public boolean canFly() {
        return this.navigation instanceof BirdNavigation;
    }

    public void setMoveType() {
        if (this.ais.getMovingType() == 1) {
            this.goalSelector.add(this.taskCount++, (Goal)new EntityAIWander(this));
        }
        if (this.ais.getMovingType() == 2) {
            this.goalSelector.add(this.taskCount++, (Goal)new EntityAIMovingPath(this));
        }
    }

    public void doorInteractType() {
        if (this.navigation instanceof MobNavigation) {
            Object aiDoor = null;
            if (this.ais.doorInteract == 1) {
                aiDoor = new LongDoorInteractGoal((MobEntity)this, true);
                this.goalSelector.add(this.taskCount++, (Goal)aiDoor);
            } else if (this.ais.doorInteract == 0) {
                aiDoor = new EntityAIBustDoor((MobEntity)this);
                this.goalSelector.add(this.taskCount++, (Goal)aiDoor);
            }
            ((MobNavigation)this.navigation).setCanPathThroughDoors(aiDoor != null);
        }
    }

    public void seekShelter() {
        if (this.ais.findShelter == 0) {
            this.goalSelector.add(this.taskCount++, (Goal)new EntityAIMoveIndoors(this));
        } else if (this.ais.findShelter == 1) {
            if (!this.canFly()) {
                this.goalSelector.add(this.taskCount++, (Goal)new AvoidSunlightGoal((PathAwareEntity)this));
            }
            this.goalSelector.add(this.taskCount++, (Goal)new EntityAIFindShade(this));
        }
    }

    public void addRegularEntries() {
        this.goalSelector.add(this.taskCount++, (Goal)new EntityAIReturn(this));
        this.goalSelector.add(this.taskCount++, (Goal)new EntityAIFollow(this));
        if (this.ais.getStandingType() != 1 && this.ais.getStandingType() != 3) {
            this.goalSelector.add(this.taskCount++, (Goal)new EntityAIWatchClosest(this, LivingEntity.class, 5.0f));
        }
        this.lookAi = new EntityAILook(this);
        this.goalSelector.add(this.taskCount++, (Goal)this.lookAi);
        this.goalSelector.add(this.taskCount++, (Goal)new EntityAIWorldLines(this));
        this.goalSelector.add(this.taskCount++, (Goal)new EntityAIJob(this));
        this.goalSelector.add(this.taskCount++, (Goal)new EntityAIRole(this));
        this.animateAi = new EntityAIAnimation(this);
        this.goalSelector.add(this.taskCount++, (Goal)this.animateAi);
        if (this.transform.isValid()) {
            this.goalSelector.add(this.taskCount++, (Goal)new EntityAITransform(this));
        }
    }

    public float getMovementSpeed() {
        return (float)this.ais.getWalkingSpeed() / 20.0f;
    }

    protected float getBaseMovementSpeedMultiplier() {
        return this.ais.movementType == 2 ? 0.95f : 0.8f;
    }

    public float getPathfindingFavor(BlockPos pos) {
        if (this.ais.movementType == 2) {
            return this.isTouchingWater() ? 10.0f : 0.0f;
        }
        float weight = (float)this.getWorld().getLuminance(pos) - 0.5f;
        if (this.getWorld().getBlockState(pos).isOpaqueFullCube((BlockView)this.getWorld(), pos)) {
            weight += 10.0f;
        }
        return weight;
    }

    protected int getNextAirUnderwater(int par1) {
        if (!this.stats.canDrown) {
            return par1;
        }
        return super.getNextAirUnderwater(par1);
    }

    public EntityGroup getGroup() {
        return this.stats == null ? null : this.stats.creatureType;
    }

    public int getMinAmbientSoundDelay() {
        return 160;
    }

    public void playAmbientSound() {
        if (!this.isAlive()) {
            return;
        }
        this.advanced.playSound(this.getTarget() != null ? 1 : 0, this.getSoundVolume(), this.getSoundPitch());
    }

    protected void playHurtSound(DamageSource source) {
        this.advanced.playSound(2, this.getSoundVolume(), this.getSoundPitch());
    }

    public SoundEvent getDeathSound() {
        return null;
    }

    public float getSoundPitch() {
        if (this.advanced.disablePitch) {
            return 1.0f;
        }
        return super.getSoundPitch();
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (this.advanced.getSound(4) != null) {
            this.advanced.playSound(4, 0.15f, 1.0f);
        } else {
            super.playStepSound(pos, state);
        }
    }

    public ServerPlayerEntity getFakeChatPlayer() {
        if (this.getWorld().isClient) {
            return null;
        }
        EntityUtil.Copy((LivingEntity)this, (LivingEntity)ChatEventPlayer);
        EntityNPCInterface.ChatEventProfile.npc = this;
        ((EntityIMixin)ChatEventPlayer).setLevel((World)((ServerWorld)this.getWorld()));
        ChatEventPlayer.setPosition(this.getX(), this.getY(), this.getZ());
        return ChatEventPlayer;
    }

    public void saySurrounding(Line line) {
        if (line == null) {
            return;
        }
        if (!line.getShowText() || !line.getText().isEmpty()) {
            // empty if block
        }
        List inRange = this.getWorld().getNonSpectatingEntities(PlayerEntity.class, this.getBoundingBox().expand(20.0, 20.0, 20.0));
        for (PlayerEntity player : inRange) {
            this.say(player, line);
        }
    }

    public void say(PlayerEntity player, Line line) {
        if (line == null || !this.canNpcSee((Entity)player)) {
            return;
        }
        if (!line.getSound().isEmpty()) {
            BlockPos pos = this.getBlockPos();
            Packets.send((ServerPlayerEntity)player, new PacketPlaySound(line.getSound(), pos, this.getSoundVolume(), this.getSoundPitch()));
        }
        if (!line.getText().isEmpty()) {
            Packets.send((ServerPlayerEntity)player, new PacketChatBubble(this.getId(), (Text)Text.translatable((String)line.getText()), line.getShowText()));
        }
    }

    public boolean shouldRenderName() {
        return true;
    }

    public void addVelocity(double d, double d1, double d2) {
        if (this.isWalking() && !this.isKilled()) {
            super.addVelocity(d, d1, d2);
        }
    }

    public void readCustomDataFromNbt(NbtCompound compound) {
        super.readCustomDataFromNbt(compound);
        this.npcVersion = compound.getInt("ModRev");
        VersionCompatibility.CheckNpcCompatibility(this, compound);
        this.display.readToNBT(compound);
        this.stats.readToNBT(compound);
        this.ais.readToNBT(compound);
        this.script.load(compound);
        this.timers.load(compound);
        this.advanced.readToNBT(compound);
        this.role.load(compound);
        this.job.load(compound);
        this.inventory.load(compound);
        this.transform.readToNBT(compound);
        this.killedtime = compound.getLong("KilledTime");
        this.totalTicksAlive = compound.getLong("TotalTicksAlive");
        this.linkedName = compound.getString("LinkedNpcName");
        if (!this.isClientSide()) {
            LinkedNpcController.Instance.loadNpcData(this);
        }
        this.getAttributeInstance(EntityAttributes.field_23717).setBaseValue((double)CustomNpcs.NpcNavRange);
        this.updateAI = true;
    }

    public void writeCustomDataToNbt(NbtCompound compound) {
        super.writeCustomDataToNbt(compound);
        this.display.save(compound);
        this.stats.save(compound);
        this.ais.save(compound);
        this.script.save(compound);
        this.timers.save(compound);
        this.advanced.save(compound);
        this.role.save(compound);
        this.job.save(compound);
        this.inventory.save(compound);
        this.transform.save(compound);
        compound.putLong("KilledTime", this.killedtime);
        compound.putLong("TotalTicksAlive", this.totalTicksAlive);
        compound.putInt("ModRev", this.npcVersion);
        compound.putString("LinkedNpcName", this.linkedName);
    }

    public EntityDimensions getDimensions(EntityPose poseIn) {
        EntityDimensions size = this.baseSize;
        if (this.currentAnimation == 2 || this.currentAnimation == 7 || this.deathTime > 0) {
            size = sizeSleep;
        } else if (this.hasVehicle() || this.currentAnimation == 1) {
            size = this.baseSize.scaled(1.0f, 0.77f);
        }
        size = size.scaled((float)this.display.getSize() * 0.2f);
        if (this.display.getHitboxState() == 1 || this.isKilled() && this.stats.hideKilledBody) {
            size = EntityDimensions.changing((float)1.0E-5f, (float)size.height);
        }
        return size;
    }

    public void updatePostDeath() {
        if (this.stats.spawnCycle == 3 || this.stats.spawnCycle == 4) {
            super.updatePostDeath();
            return;
        }
        ++this.deathTime;
        if (this.getWorld().isClient) {
            return;
        }
        if (!this.hasDied) {
            this.remove(Entity.RemovalReason.KILLED);
        }
        if (this.killedtime < System.currentTimeMillis() && (this.stats.spawnCycle == 0 || this.getWorld().isDay() && this.stats.spawnCycle == 1 || !this.getWorld().isDay() && this.stats.spawnCycle == 2)) {
            this.reset();
        }
    }

    public void reset() {
        boolean needsSync = this.hasDied;
        this.hasDied = false;
        this.unsetRemoved();
        this.dead = false;
        this.wasKilled = false;
        this.setSprinting(false);
        this.setHealth(this.getMaxHealth());
        this.dataTracker.set(Animation, (Object)0);
        this.dataTracker.set(Walking, (Object)false);
        this.dataTracker.set(IsDead, (Object)false);
        this.dataTracker.set(Interacting, (Object)false);
        this.interactingEntities.clear();
        this.combatHandler.reset();
        this.setTarget(null);
        this.setAttacker(null);
        this.deathTime = 0;
        if (this.ais.returnToStart && !this.hasOwner() && !this.isClientSide() && !this.hasVehicle()) {
            this.refreshPositionAndAngles(this.getStartXPos(), this.getStartYPos(), this.getStartZPos(), this.getYaw(), this.getPitch());
        }
        this.killedtime = 0L;
        this.extinguish();
        this.clearStatusEffects();
        this.travel(Vec3d.ZERO);
        this.horizontalSpeed = 0.0f;
        this.prevHorizontalSpeed = 0.0f;
        this.getNavigation().stop();
        this.currentAnimation = 0;
        this.calculateDimensions();
        this.updateAI = true;
        this.ais.movingPos = 0;
        if (this.getOwner() != null) {
            this.getOwner().onAttacking(null);
        }
        this.bossInfo.setVisible(this.display.getBossbar() == 1);
        this.job.reset();
        EventHooks.onNPCInit(this);
        if (needsSync) {
            List data = this.getDataTracker().getChangedEntries();
            for (ServerPlayerEntity player : this.getWorld().getServer().getPlayerManager().getPlayerList()) {
                if (!this.display.isVisibleTo(player) && !player.isSpectator() && player.getMainHandStack().getItem() != CustomItems.wand) continue;
                Packets.send(player, new PacketUpdatePhysics((Entity)this));
                if (data != null) {
                    player.networkHandler.sendPacket((Packet)new EntityTrackerUpdateS2CPacket(this.getId(), data));
                }
                Packets.send(player, new PacketNpcUpdate(this.getId(), this.writeSpawnData()));
            }
        }
    }

    public void onCollide() {
        if (!this.isAlive() || this.age % 4 != 0 || this.getWorld().isClient) {
            return;
        }
        Box axisalignedbb = null;
        axisalignedbb = this.getVehicle() != null && this.getVehicle().isAlive() ? this.getBoundingBox().union(this.getVehicle().getBoundingBox()).expand(1.0, 0.0, 1.0) : this.getBoundingBox().expand(1.0, 0.5, 1.0);
        List list = this.getWorld().getNonSpectatingEntities(LivingEntity.class, axisalignedbb);
        if (list == null) {
            return;
        }
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = (Entity)list.get(i);
            if (entity == this || !entity.isAlive()) continue;
            EventHooks.onNPCCollide(this, entity);
        }
    }

    public void setInNetherPortal(BlockPos pos) {
    }

    public void cloakUpdate() {
        this.prevChasingPosX = this.chasingPosX;
        this.prevChasingPosY = this.chasingPosY;
        this.prevChasingPosZ = this.chasingPosZ;
        double d0 = this.getX() - this.chasingPosX;
        double d1 = this.getY() - this.chasingPosY;
        double d2 = this.getZ() - this.chasingPosZ;
        double d3 = 10.0;
        if (d0 > 10.0) {
            this.prevChasingPosX = this.chasingPosX = this.getX();
        }
        if (d2 > 10.0) {
            this.prevChasingPosZ = this.chasingPosZ = this.getZ();
        }
        if (d1 > 10.0) {
            this.prevChasingPosY = this.chasingPosY = this.getY();
        }
        if (d0 < -10.0) {
            this.prevChasingPosX = this.chasingPosX = this.getX();
        }
        if (d2 < -10.0) {
            this.prevChasingPosZ = this.chasingPosZ = this.getZ();
        }
        if (d1 < -10.0) {
            this.prevChasingPosY = this.chasingPosY = this.getY();
        }
        this.chasingPosX += d0 * 0.25;
        this.chasingPosZ += d2 * 0.25;
        this.chasingPosY += d1 * 0.25;
    }

    public boolean canImmediatelyDespawn(double distanceToPlayer) {
        return this.stats != null && this.stats.spawnCycle == 4;
    }

    public ItemStack getMainHandStack() {
        IItemStack item = null;
        item = this.isAttacking() ? this.inventory.getRightHand() : (this.role.getType() == 6 ? ((RoleCompanion)this.role).getItemInHand() : (this.job.overrideMainHand ? this.job.getMainhand() : this.inventory.getRightHand()));
        return ItemStackWrapper.MCItem(item);
    }

    public ItemStack getOffHandStack() {
        IItemStack item = null;
        item = this.isAttacking() ? this.inventory.getLeftHand() : (this.job.overrideOffHand ? this.job.getOffhand() : this.inventory.getLeftHand());
        return ItemStackWrapper.MCItem(item);
    }

    public ItemStack getEquippedStack(EquipmentSlot slot) {
        if (slot == EquipmentSlot.field_6173) {
            return this.getMainHandStack();
        }
        if (slot == EquipmentSlot.field_6171) {
            return this.getOffHandStack();
        }
        return ItemStackWrapper.MCItem(this.inventory.getArmor(3 - slot.getEntitySlotId()));
    }

    public void equipStack(EquipmentSlot slot, ItemStack item) {
        if (slot == EquipmentSlot.field_6173) {
            this.inventory.weapons.put(0, NpcAPI.Instance().getIItemStack(item));
        } else if (slot == EquipmentSlot.field_6171) {
            this.inventory.weapons.put(2, NpcAPI.Instance().getIItemStack(item));
        } else {
            this.inventory.armor.put(3 - slot.getEntitySlotId(), NpcAPI.Instance().getIItemStack(item));
        }
    }

    public Iterable<ItemStack> getArmorItems() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for (int i = 0; i < 4; ++i) {
            list.add(ItemStackWrapper.MCItem(this.inventory.armor.get(3 - i)));
        }
        return list;
    }

    public Iterable<ItemStack> getItemsEquipped() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(ItemStackWrapper.MCItem(this.inventory.weapons.get(0)));
        list.add(ItemStackWrapper.MCItem(this.inventory.weapons.get(2)));
        return list;
    }

    protected void dropEquipment(DamageSource source, int looting, boolean recentlyHitIn) {
    }

    protected void dropLoot(DamageSource damageSourceIn, boolean attackedRecently) {
    }

    public void onDeath(DamageSource damagesource) {
        this.setSprinting(false);
        this.getNavigation().stop();
        this.extinguish();
        this.clearStatusEffects();
        if (!this.isClientSide()) {
            this.advanced.playSound(3, this.getSoundVolume(), this.getSoundPitch());
            Entity attackingEntity = NoppesUtilServer.GetDamageSourcee(damagesource);
            NpcEvent.DiedEvent event = new NpcEvent.DiedEvent(this.wrappedNPC, damagesource, attackingEntity);
            event.droppedItems = this.inventory.getItemsRNG();
            event.expDropped = this.inventory.getExpRNG();
            event.line = this.advanced.getKilledLine();
            EventHooks.onNPCDied(this, event);
            this.bossInfo.setVisible(false);
            this.inventory.dropStuff(event, attackingEntity, damagesource);
            if (event.line != null) {
                this.saySurrounding(Line.formatTarget((Line)event.line, attackingEntity instanceof LivingEntity ? (LivingEntity)attackingEntity : null));
            }
        }
        super.onDeath(damagesource);
    }

    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        this.bossInfo.addPlayer(player);
    }

    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossInfo.removePlayer(player);
    }

    public void remove(Entity.RemovalReason reason) {
        if (reason != Entity.RemovalReason.KILLED) {
            super.remove(reason);
            return;
        }
        this.hasDied = true;
        this.removeAllPassengers();
        this.stopRiding();
        if (this.getWorld().isClient || this.stats.spawnCycle == 3 || this.stats.spawnCycle == 4) {
            this.delete();
        } else {
            this.setHealth(-1.0f);
            this.setSprinting(false);
            this.getNavigation().stop();
            this.setCurrentAnimation(2);
            this.calculateDimensions();
            if (this.killedtime <= 0L) {
                this.killedtime = (long)(this.stats.respawnTime * 1000) + System.currentTimeMillis();
            }
            this.role.killed();
            this.job.killed();
        }
    }

    public void delete() {
        VisibilityController.instance.remove(this);
        this.role.delete();
        this.job.delete();
        super.remove(Entity.RemovalReason.DISCARDED);
    }

    public float getStartXPos() {
        return (float)this.ais.startPos().getX() + this.ais.bodyOffsetX / 10.0f;
    }

    public float getStartZPos() {
        return (float)this.ais.startPos().getZ() + this.ais.bodyOffsetZ / 10.0f;
    }

    public boolean isVeryNearAssignedPlace() {
        double xx = this.getX() - (double)this.getStartXPos();
        double zz = this.getZ() - (double)this.getStartZPos();
        if (xx < -0.2 || xx > 0.2) {
            return false;
        }
        return !(zz < -0.2) && !(zz > 0.2);
    }

    public double getStartYPos() {
        if (this.startYPos < (double)this.getWorld().getBottomY()) {
            return this.calculateStartYPos(this.ais.startPos());
        }
        return this.startYPos;
    }

    private double calculateStartYPos(BlockPos pos) {
        BlockPos startPos = this.ais.startPos();
        while (pos.getY() > this.getWorld().getBottomY()) {
            BlockState state = this.getWorld().getBlockState(pos);
            VoxelShape shape = state.getOutlineShape((BlockView)this.getWorld(), pos);
            if (shape.isEmpty()) {
                pos = pos.down();
                continue;
            }
            Box bb = shape.getBoundingBox().offset(pos);
            if (this.ais.movementType == 2 && startPos.getY() <= pos.getY() && state.isOf(Blocks.field_10382)) {
                pos = pos.down();
                continue;
            }
            return bb.maxY;
        }
        return this.getWorld().getBottomY();
    }

    private BlockPos calculateTopPos(BlockPos pos) {
        BlockPos check = pos;
        while (check.getY() > this.getWorld().getBottomY()) {
            Box bb;
            BlockState state = this.getWorld().getBlockState(pos);
            VoxelShape shape = state.getOutlineShape((BlockView)this.getWorld(), pos);
            if (!shape.isEmpty() && (bb = shape.getBoundingBox().offset(pos)) != null) {
                return check;
            }
            check = check.down();
        }
        return pos;
    }

    public boolean isInRange(Entity entity, double range) {
        return this.isInRange(entity.getX(), entity.getY(), entity.getZ(), range);
    }

    public boolean isInRange(double posX, double posY, double posZ, double range) {
        double y = Math.abs(this.getY() - posY);
        if (posY >= (double)this.getWorld().getBottomY() && y > range) {
            return false;
        }
        double x = Math.abs(this.getX() - posX);
        double z = Math.abs(this.getZ() - posZ);
        return x <= range && z <= range;
    }

    public void givePlayerItem(PlayerEntity player, ItemStack item) {
        if (this.getWorld().isClient) {
            return;
        }
        item = item.copy();
        float f = 0.7f;
        double d = (double)(this.getWorld().random.nextFloat() * f) + (double)(1.0f - f);
        double d1 = (double)(this.getWorld().random.nextFloat() * f) + (double)(1.0f - f);
        double d2 = (double)(this.getWorld().random.nextFloat() * f) + (double)(1.0f - f);
        ItemEntity entityitem = new ItemEntity(this.getWorld(), this.getX() + d, this.getY() + d1, this.getZ() + d2, item);
        entityitem.setPickupDelay(2);
        this.getWorld().spawnEntity((Entity)entityitem);
        int i = item.getCount();
        if (player.getInventory().insertStack(item)) {
            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.field_15197, SoundCategory.field_15248, 0.2f, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            player.sendPickup((Entity)entityitem, i);
            if (item.getCount() <= 0) {
                entityitem.discard();
            }
        }
    }

    public boolean isSleeping() {
        return this.currentAnimation == 2 && !this.isAttacking();
    }

    public boolean isWalking() {
        return this.ais.getMovingType() != 0 || this.isAttacking() || this.isFollower() || (Boolean)this.dataTracker.get(Walking) != false;
    }

    public boolean isInSneakingPose() {
        return this.currentAnimation == 4;
    }

    public void takeKnockback(double strength, double ratioX, double ratioZ) {
        super.takeKnockback(strength * (double)(2.0f - this.stats.resistances.knockback), ratioX, ratioZ);
    }

    public Faction getFaction() {
        Faction fac = FactionController.instance.getFaction((Integer)this.dataTracker.get(FactionData));
        if (fac == null) {
            return FactionController.instance.getFaction(FactionController.instance.getFirstFactionId());
        }
        return fac;
    }

    public boolean isClientSide() {
        return this.getWorld() == null || this.getWorld().isClient;
    }

    public void setFaction(int id) {
        if (id < 0 || this.isClientSide()) {
            return;
        }
        this.dataTracker.set(FactionData, (Object)id);
    }

    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        if (this.stats.potionImmune) {
            return false;
        }
        if (this.getGroup() == EntityGroup.ARTHROPOD && effect.getEffectType() == StatusEffects.field_5899) {
            return false;
        }
        return super.canHaveStatusEffect(effect);
    }

    public boolean isAttacking() {
        return (Boolean)this.dataTracker.get(Attacking);
    }

    public boolean isKilled() {
        return this.isRemoved() || (Boolean)this.dataTracker.get(IsDead) != false;
    }

    public void writeSpawnData(PacketByteBuf buffer) {
        buffer.writeNbt(this.writeSpawnData());
    }

    public NbtCompound writeSpawnData() {
        NbtCompound bard;
        NbtCompound compound = new NbtCompound();
        this.display.save(compound);
        compound.putInt("MaxHealth", this.stats.maxHealth);
        compound.put("Armor", (NbtElement)NBTTags.nbtIItemStackMap(this.inventory.armor));
        compound.put("Weapons", (NbtElement)NBTTags.nbtIItemStackMap(this.inventory.weapons));
        compound.putInt("Speed", this.ais.getWalkingSpeed());
        compound.putBoolean("MountControl", this.ais.mountControl);
        compound.putBoolean("DeadBody", this.stats.hideKilledBody);
        compound.putInt("StandingState", this.ais.getStandingType());
        compound.putInt("MovingState", this.ais.getMovingType());
        compound.putInt("Orientation", this.ais.orientation);
        compound.putFloat("PositionXOffset", this.ais.bodyOffsetX);
        compound.putFloat("PositionYOffset", this.ais.bodyOffsetY);
        compound.putFloat("PositionZOffset", this.ais.bodyOffsetZ);
        compound.putInt("Role", this.role.getType());
        compound.putInt("Job", this.job.getType());
        if (this.job.getType() == 1) {
            bard = new NbtCompound();
            this.job.save(bard);
            compound.put("Bard", (NbtElement)bard);
        }
        if (this.job.getType() == 9) {
            bard = new NbtCompound();
            this.job.save(bard);
            compound.put("Puppet", (NbtElement)bard);
        }
        if (this.role.getType() == 6) {
            bard = new NbtCompound();
            this.role.save(bard);
            compound.put("Companion", (NbtElement)bard);
        }
        if (this instanceof EntityCustomNpc) {
            compound.put("ModelData", (NbtElement)((EntityCustomNpc)this).modelData.save());
        }
        return compound;
    }

    public void readSpawnData(PacketByteBuf buf) {
        this.readSpawnData(buf.readNbt());
    }

    public void readSpawnData(NbtCompound compound) {
        NbtCompound puppet;
        this.stats.setMaxHealth(compound.getInt("MaxHealth"));
        this.ais.setWalkingSpeed(compound.getInt("Speed"));
        this.stats.hideKilledBody = compound.getBoolean("DeadBody");
        this.ais.setStandingType(compound.getInt("StandingState"));
        this.ais.mountControl = compound.getBoolean("MountControl");
        this.ais.setMovingType(compound.getInt("MovingState"));
        this.ais.orientation = compound.getInt("Orientation");
        this.ais.bodyOffsetX = compound.getFloat("PositionXOffset");
        this.ais.bodyOffsetY = compound.getFloat("PositionYOffset");
        this.ais.bodyOffsetZ = compound.getFloat("PositionZOffset");
        this.inventory.armor = NBTTags.getIItemStackMap(compound.getList("Armor", 10));
        this.inventory.weapons = NBTTags.getIItemStackMap(compound.getList("Weapons", 10));
        this.advanced.setRole(compound.getInt("Role"));
        this.advanced.setJob(compound.getInt("Job"));
        if (this.job.getType() == 1) {
            NbtCompound bard = compound.getCompound("Bard");
            this.job.load(bard);
        }
        if (this.job.getType() == 9) {
            puppet = compound.getCompound("Puppet");
            this.job.load(puppet);
        }
        if (this.role.getType() == 6) {
            puppet = compound.getCompound("Companion");
            this.role.load(puppet);
        }
        if (this instanceof EntityCustomNpc) {
            ((EntityCustomNpc)this).modelData.load(compound.getCompound("ModelData"));
        }
        this.display.readToNBT(compound);
        this.calculateDimensions();
    }

    public ServerCommandSource getCommandSource() {
        if (this.getWorld().isClient) {
            return super.getCommandSource();
        }
        EntityUtil.Copy((LivingEntity)this, (LivingEntity)CommandPlayer);
        ((EntityIMixin)CommandPlayer).setLevel((World)((ServerWorld)this.getWorld()));
        CommandPlayer.setPosition(this.getX(), this.getY(), this.getZ());
        return new ServerCommandSource((CommandOutput)this, this.getPos(), this.getRotationClient(), this.getWorld() instanceof ServerWorld ? (ServerWorld)this.getWorld() : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.getWorld().getServer(), (Entity)this);
    }

    public Text getName() {
        return Text.translatable((String)this.display.getName());
    }

    public void setImmuneToFire(boolean immuneToFire) {
        this.stats.immuneToFire = immuneToFire;
    }

    public boolean isFireImmune() {
        return this.stats.immuneToFire;
    }

    public boolean handleFallDamage(float distance, float modifier, DamageSource source) {
        if (!this.stats.noFallDamage) {
            return super.handleFallDamage(distance, modifier, source);
        }
        return false;
    }

    public void slowMovement(BlockState state, Vec3d motionMultiplierIn) {
        if (state != null && !state.isOf(Blocks.field_10343) || !this.stats.ignoreCobweb) {
            super.slowMovement(state, motionMultiplierIn);
        }
    }

    public boolean isCollidable() {
        return !this.isKilled() && this.display.getHitboxState() == 2;
    }

    protected void tickCramming() {
        if (this.display.getHitboxState() != 0) {
            return;
        }
        super.tickCramming();
    }

    public boolean isPushable() {
        return this.isWalking() && !this.isKilled();
    }

    public PistonBehavior getPistonBehavior() {
        return this.display.getHitboxState() == 0 ? super.getPistonBehavior() : PistonBehavior.field_15975;
    }

    public EntityAIRangedAttack getRangedTask() {
        return this.aiRange;
    }

    public String getRoleData() {
        return (String)this.dataTracker.get(RoleData);
    }

    public void setRoleData(String s) {
        this.dataTracker.set(RoleData, (Object)s);
    }

    public String getJobData() {
        return (String)this.dataTracker.get(RoleData);
    }

    public void setJobData(String s) {
        this.dataTracker.set(RoleData, (Object)s);
    }

    public World getEntityWorld() {
        return this.getWorld();
    }

    public boolean isInvisibleTo(PlayerEntity player) {
        return this.display.getVisible() == 1 && player.getMainHandStack().getItem() != CustomItems.wand && !this.display.availability.hasOptions();
    }

    public boolean isInvisible() {
        return this.display.getVisible() != 0 && !this.display.availability.hasOptions();
    }

    public void setInvisible(ServerPlayerEntity playerMP) {
        if (this.tracking.contains(playerMP.getId())) {
            this.tracking.remove(playerMP.getId());
            Packets.send(playerMP, new PacketNpcVisibleFalse(this.getId()));
        }
    }

    public void setVisible(ServerPlayerEntity playerMP) {
        if (!this.tracking.contains(playerMP.getId())) {
            this.tracking.add(playerMP.getId());
            Packets.send(playerMP, new PacketNpcVisibleTrue((Entity)this));
            List data = this.getDataTracker().getChangedEntries();
            if (data != null) {
                playerMP.networkHandler.sendPacket((Packet)new EntityTrackerUpdateS2CPacket(this.getId(), data));
            }
        }
        Packets.send(playerMP, new PacketNpcUpdate(this.getId(), this.writeSpawnData()));
    }

    public void setCurrentAnimation(int animation) {
        this.currentAnimation = animation;
        this.dataTracker.set(Animation, (Object)animation);
    }

    public boolean canNpcSee(Entity entity) {
        return this.getVisibilityCache().canSee(entity);
    }

    public boolean isFollower() {
        if (this.advanced.scenes.getOwner() != null) {
            return true;
        }
        return this.role.isFollowing() || this.job.isFollowing();
    }

    public LivingEntity getOwner() {
        if (this.advanced.scenes.getOwner() != null) {
            return this.advanced.scenes.getOwner();
        }
        if (this.role.getType() == 2 && this.role instanceof RoleFollower) {
            return ((RoleFollower)this.role).owner;
        }
        if (this.role.getType() == 6 && this.role instanceof RoleCompanion) {
            return ((RoleCompanion)this.role).owner;
        }
        if (this.job.getType() == 5 && this.job instanceof JobFollower) {
            return ((JobFollower)this.job).following;
        }
        return null;
    }

    public boolean hasOwner() {
        if (this.advanced.scenes.getOwner() != null) {
            return true;
        }
        return this.role.getType() == 2 && ((RoleFollower)this.role).hasOwner() || this.role.getType() == 6 && ((RoleCompanion)this.role).hasOwner() || this.job.getType() == 5 && ((JobFollower)this.job).hasOwner();
    }

    public int followRange() {
        if (this.advanced.scenes.getOwner() != null) {
            return 4;
        }
        if (this.role.getType() == 2 && this.role.isFollowing()) {
            return 6;
        }
        if (this.role.getType() == 6 && this.role.isFollowing()) {
            return 4;
        }
        if (this.job.getType() == 5 && this.job.isFollowing()) {
            return 4;
        }
        return 15;
    }

    protected float applyArmorToDamage(DamageSource source, float damage) {
        if (this.role.getType() == 6) {
            damage = ((RoleCompanion)this.role).getDamageAfterArmorAbsorb(source, damage);
        }
        return damage;
    }

    public boolean isTeammate(Entity entity) {
        if (!this.isClientSide()) {
            if (entity instanceof PlayerEntity && this.getFaction().isFriendlyToPlayer((PlayerEntity)entity)) {
                return true;
            }
            if (entity == this.getOwner()) {
                return true;
            }
            if (entity instanceof EntityNPCInterface && ((EntityNPCInterface)entity).faction.id == this.faction.id) {
                return true;
            }
        }
        return super.isTeammate(entity);
    }

    public void setDataWatcher(DataTracker entityData) {
        ArrayList<DataTracker.SerializedEntry> list = new ArrayList<DataTracker.SerializedEntry>();
        for (DataTracker.Entry<?> entry : ((ISynchedEntityData)entityData).getAll()) {
            if (!(entry.get() instanceof DataTracker.SerializedEntry)) continue;
            list.add((DataTracker.SerializedEntry)entry.get());
        }
        this.dataTracker.writeUpdatedEntries(list);
    }

    public void travel(Vec3d travelVector) {
        BlockPos pos = this.getBlockPos();
        if (this.isAlive() && this.hasPassengers() && this.ais.mountControl && this.getControllingPassenger() != null) {
            LivingEntity livingentity = this.getControllingPassenger();
            this.setYaw(livingentity.getYaw());
            this.prevYaw = this.getYaw();
            this.setPitch(livingentity.getPitch() * 0.5f);
            this.setRotation(this.getYaw(), this.getPitch());
            this.headYaw = this.bodyYaw = this.getYaw();
            float f = livingentity.sidewaysSpeed * 0.5f;
            float f1 = livingentity.forwardSpeed;
            if (f1 <= 0.0f) {
                f1 *= 0.25f;
            }
            this.setStepHeight(1.1f);
            super.travel(new Vec3d((double)f, travelVector.y, (double)f1));
        } else {
            this.setStepHeight(0.5f);
            super.travel(travelVector);
        }
        if (this.role.getType() == 6 && !this.isClientSide()) {
            BlockPos delta = this.getBlockPos().subtract((Vec3i)pos);
            ((RoleCompanion)this.role).addMovementStat(delta.getX(), delta.getY(), delta.getZ());
        }
    }

    public boolean canBeLeashedBy(PlayerEntity player) {
        return false;
    }

    public boolean isLeashed() {
        return false;
    }

    public boolean nearPosition(BlockPos pos) {
        BlockPos npcpos = this.getBlockPos();
        float x = npcpos.getX() - pos.getX();
        float z = npcpos.getZ() - pos.getZ();
        float y = npcpos.getY() - pos.getY();
        float height = MathHelper.ceil((float)(this.getHeight() + 1.0f)) * MathHelper.ceil((float)(this.getHeight() + 1.0f));
        return (double)(x * x + z * z) < 2.5 && (double)(y * y) < (double)height + 2.5;
    }

    public void tpTo(LivingEntity owner) {
        if (owner == null) {
            return;
        }
        Direction facing = owner.getHorizontalFacing().getOpposite();
        BlockPos pos = new BlockPos((int)owner.getX(), (int)owner.getBoundingBox().minY, (int)owner.getZ());
        pos = pos.add(facing.getOffsetX(), 0, facing.getOffsetZ());
        pos = this.calculateTopPos(pos);
        block0: for (int i = -1; i < 2; ++i) {
            for (int j = 0; j < 3; ++j) {
                BlockPos check = facing.getOffsetX() == 0 ? pos.add(i, 0, j * facing.getOffsetZ()) : pos.add(j * facing.getOffsetX(), 0, i);
                check = this.calculateTopPos(check);
                if (this.getWorld().getBlockState(check).isOpaqueFullCube((BlockView)this.getWorld(), check) || this.getWorld().getBlockState(check.up()).isOpaqueFullCube((BlockView)this.getWorld(), check.up())) continue;
                this.refreshPositionAndAngles((float)check.getX() + 0.5f, check.getY(), (float)check.getZ() + 0.5f, this.getYaw(), this.getPitch());
                this.getNavigation().stop();
                continue block0;
            }
        }
    }

    public void onTrackedDataSet(TrackedData<?> para) {
        super.onTrackedDataSet(para);
        if (Animation.equals(para)) {
            this.calculateDimensions();
        }
    }

    protected void updateGoalControls() {
        boolean flag1 = !(this.getVehicle() instanceof BoatEntity);
        this.goalSelector.setControlEnabled(Goal.Control.field_18405, true);
        this.goalSelector.setControlEnabled(Goal.Control.field_18407, flag1);
        this.goalSelector.setControlEnabled(Goal.Control.field_18406, true);
    }

    public void checkDespawn() {
        double range;
        double d0;
        PlayerEntity entity;
        super.checkDespawn();
        if (this.getDespawnCounter() != 0 && this.getWorld() != null && (entity = this.getWorld().getClosestPlayer((Entity)this, -1.0)) != null && (d0 = entity.squaredDistanceTo((Entity)this)) < (range = (double)this.ais.activeRange * (double)this.ais.activeRange)) {
            this.despawnCounter = 0;
        }
    }

    static {
        sizeSleep = new EntityDimensions(0.8f, 0.4f, false);
    }
}

