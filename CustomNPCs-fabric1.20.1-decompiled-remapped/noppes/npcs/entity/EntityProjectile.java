/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.entity.Entity$MoveEffect
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.EndermanEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.ProjectileUtil
 *  net.minecraft.entity.projectile.thrown.ThrownEntity
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.GameRules
 *  net.minecraft.world.World
 *  net.minecraft.world.World$ExplosionSourceType
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.particle.ItemStackParticleEffect
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtHelper
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.text.Text
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.listener.ClientPlayPacketListener
 *  net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.block.BlockState
 *  net.minecraft.entity.data.TrackedData
 *  net.minecraft.entity.data.TrackedDataHandler
 *  net.minecraft.entity.data.TrackedDataHandlerRegistry
 *  net.minecraft.entity.data.DataTracker
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.EntityHitResult
 *  net.minecraft.entity.EntityDimensions
 *  net.minecraft.entity.EntityPose
 *  net.minecraft.registry.RegistryEntryLookup
 *  net.minecraft.registry.RegistryKeys
 */
package noppes.npcs.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import noppes.npcs.CustomEntities;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.ParticleType;
import noppes.npcs.api.constants.PotionEffectType;
import noppes.npcs.api.entity.IProjectile;
import noppes.npcs.api.event.ProjectileEvent;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataRanged;

public class EntityProjectile
extends ThrownEntity {
    private static final TrackedData<Boolean> Gravity = DataTracker.registerData(EntityProjectile.class, (TrackedDataHandler)TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> Arrow = DataTracker.registerData(EntityProjectile.class, (TrackedDataHandler)TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> Is3d = DataTracker.registerData(EntityProjectile.class, (TrackedDataHandler)TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> Glows = DataTracker.registerData(EntityProjectile.class, (TrackedDataHandler)TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> Rotating = DataTracker.registerData(EntityProjectile.class, (TrackedDataHandler)TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> Sticks = DataTracker.registerData(EntityProjectile.class, (TrackedDataHandler)TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<ItemStack> ItemStackThrown = DataTracker.registerData(EntityProjectile.class, (TrackedDataHandler)TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Integer> Velocity = DataTracker.registerData(EntityProjectile.class, (TrackedDataHandler)TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> Size = DataTracker.registerData(EntityProjectile.class, (TrackedDataHandler)TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> Particle = DataTracker.registerData(EntityProjectile.class, (TrackedDataHandler)TrackedDataHandlerRegistry.INTEGER);
    private BlockPos tilePos = BlockPos.ORIGIN;
    private BlockState inBlock;
    protected boolean inGround = false;
    public int throwableShake = 0;
    public int arrowShake = 0;
    public boolean canBePickedUp = false;
    public boolean destroyedOnEntityHit = true;
    private Entity thrower;
    private EntityNPCInterface npc;
    private String throwerName = null;
    private int ticksInGround;
    public int ticksInAir = 0;
    private double accelerationX;
    private double accelerationY;
    private double accelerationZ;
    public float damage = 5.0f;
    public int punch = 0;
    public boolean accelerate = false;
    public boolean explosiveDamage = true;
    public int explosiveRadius = 0;
    public int effect = 0;
    public int duration = 5;
    public int amplify = 0;
    public int accuracy = 60;
    public IProjectileCallback callback;
    public List<ScriptContainer> scripts = new ArrayList<ScriptContainer>();

    public EntityProjectile(EntityType type, World par1Level) {
        super(type, par1Level);
    }

    protected void initDataTracker() {
        this.dataTracker.startTracking(ItemStackThrown, (Object)ItemStack.EMPTY);
        this.dataTracker.startTracking(Velocity, (Object)10);
        this.dataTracker.startTracking(Size, (Object)10);
        this.dataTracker.startTracking(Particle, (Object)0);
        this.dataTracker.startTracking(Gravity, (Object)false);
        this.dataTracker.startTracking(Glows, (Object)false);
        this.dataTracker.startTracking(Arrow, (Object)false);
        this.dataTracker.startTracking(Is3d, (Object)false);
        this.dataTracker.startTracking(Rotating, (Object)false);
        this.dataTracker.startTracking(Sticks, (Object)false);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double par1) {
        double d1 = this.getBoundingBox().getAverageSideLength() * 4.0;
        return par1 < (d1 *= 64.0) * d1;
    }

    public EntityProjectile(World level, LivingEntity limbSwingAmountEntityLiving, ItemStack item, boolean isNPC) {
        super(CustomEntities.entityProjectile, level);
        this.thrower = limbSwingAmountEntityLiving;
        if (this.thrower != null) {
            this.throwerName = this.thrower.getUuid().toString();
        }
        this.setThrownItem(item);
        this.dataTracker.set(Arrow, (Object)(this.getItem() == Items.field_8107 ? 1 : 0));
        this.refreshPositionAndAngles(limbSwingAmountEntityLiving.getX(), limbSwingAmountEntityLiving.getY() + (double)limbSwingAmountEntityLiving.getStandingEyeHeight(), limbSwingAmountEntityLiving.getZ(), limbSwingAmountEntityLiving.getYaw(), limbSwingAmountEntityLiving.getPitch());
        double posX = this.getX() - (double)(MathHelper.cos((float)(this.getYaw() / 180.0f * (float)Math.PI)) * 0.1f);
        double posY = this.getY() - (double)0.1f;
        double posZ = this.getZ() - (double)(MathHelper.sin((float)(this.getYaw() / 180.0f * (float)Math.PI)) * 0.1f);
        this.setPosition(posX, posY, posZ);
        if (isNPC) {
            this.npc = (EntityNPCInterface)this.thrower;
            this.getStatProperties(this.npc.stats.ranged);
            this.calculateDimensions();
        }
    }

    public void onTrackedDataSet(TrackedData<?> para) {
        if (Size.equals(para)) {
            this.calculateDimensions();
        }
    }

    public void setThrownItem(ItemStack item) {
        this.dataTracker.set(ItemStackThrown, (Object)item);
    }

    public int getSize() {
        return (Integer)this.dataTracker.get(Size);
    }

    public EntityDimensions getDimensions(EntityPose pose) {
        return new EntityDimensions((float)this.getSize() / 10.0f, (float)this.getSize() / 10.0f, false);
    }

    public void setVelocity(double par1, double par3, double par5, float par7, float par8) {
        double f2 = Math.sqrt(par1 * par1 + par3 * par3 + par5 * par5);
        double f3 = Math.sqrt(par1 * par1 + par5 * par5);
        float yaw = (float)(Math.atan2(par1, par5) * 180.0 / Math.PI);
        float pitch = this.hasGravity() ? par7 : (float)(Math.atan2(par3, f3) * 180.0 / Math.PI);
        this.prevYaw = yaw;
        this.prevPitch = pitch;
        this.setYaw(yaw);
        this.setPitch(pitch);
        Vec3d m = new Vec3d((double)(MathHelper.sin((float)(yaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(pitch / 180.0f * (float)Math.PI))), (double)MathHelper.sin((float)((pitch + 1.0f) / 180.0f * (float)Math.PI)), (double)(MathHelper.cos((float)(yaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(pitch / 180.0f * (float)Math.PI)))).add(this.random.nextGaussian() * 0.0075 * (double)par8, this.random.nextGaussian() * 0.0075 * (double)par8, this.random.nextGaussian() * 0.0075 * (double)par8).multiply((double)this.getSpeed());
        this.setVelocity(m);
        this.accelerationX = par1 / f2 * 0.1;
        this.accelerationY = par3 / f2 * 0.1;
        this.accelerationZ = par5 / f2 * 0.1;
        this.ticksInGround = 0;
    }

    public float getAngleForXYZ(double varX, double varY, double varZ, float horiDist, boolean arc) {
        float g = this.getGravity();
        float var1 = this.getSpeed() * this.getSpeed();
        float var2 = g * horiDist;
        float var3 = (float)((double)(g * horiDist * horiDist) + 2.0 * varY * (double)var1);
        float var4 = var1 * var1 - g * var3;
        if (var4 < 0.0f) {
            return 30.0f;
        }
        float var6 = arc ? var1 + MathHelper.sqrt((float)var4) : var1 - MathHelper.sqrt((float)var4);
        float var7 = (float)(Math.atan2(var6, var2) * 180.0 / Math.PI);
        return var7;
    }

    public void shoot(float speed) {
        double varX = -MathHelper.sin((float)(this.getYaw() / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(this.getPitch() / 180.0f * (float)Math.PI));
        double varZ = MathHelper.cos((float)(this.getYaw() / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(this.getPitch() / 180.0f * (float)Math.PI));
        double varY = -MathHelper.sin((float)(this.getPitch() / 180.0f * (float)Math.PI));
        this.setVelocity(varX, varY, varZ, -this.getPitch(), speed);
    }

    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double par1, double par3, double par5, float par7, float par8, int par9, boolean bo) {
        if (this.getWorld().isClient && this.inGround) {
            return;
        }
        this.setPosition(par1, par3, par5);
        this.setRotation(par7, par8);
    }

    public void tick() {
        Box axisalignedbb;
        VoxelShape shape;
        super.baseTick();
        if (++this.age % 10 == 0) {
            EventHooks.onProjectileTick(this);
        }
        Vec3d motion = this.getVelocity();
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            double f = motion.horizontalLength();
            this.setYaw((float)(MathHelper.atan2((double)motion.x, (double)motion.z) * 57.2957763671875));
            this.setPitch((float)(MathHelper.atan2((double)motion.y, (double)f) * 57.2957763671875));
            this.prevYaw = this.getYaw();
            this.prevPitch = this.getPitch();
        }
        if (this.effect != 666 || !this.inGround) {
            // empty if block
        }
        BlockState state = this.getWorld().getBlockState(this.tilePos);
        if ((this.isArrow() || this.sticksToWalls()) && this.tilePos != BlockPos.ORIGIN && !(shape = state.getOutlineShape((BlockView)this.getWorld(), this.tilePos)).isEmpty() && (axisalignedbb = shape.getBoundingBox()) != null && axisalignedbb.contains(this.getPos())) {
            this.inGround = true;
        }
        if (this.arrowShake > 0) {
            --this.arrowShake;
        }
        if (this.inGround) {
            if (state == this.inBlock) {
                ++this.ticksInGround;
                if (this.ticksInGround == 1200) {
                    this.remove(Entity.RemovalReason.DISCARDED);
                }
            } else {
                this.inGround = false;
                this.setVelocity(this.getVelocity().multiply((double)(this.random.nextFloat() * 0.2f), (double)(this.random.nextFloat() * 0.2f), (double)(this.random.nextFloat() * 0.2f)));
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        } else {
            ++this.ticksInAir;
            if (this.ticksInAir == 1200) {
                this.remove(Entity.RemovalReason.DISCARDED);
            }
            Vec3d pos = this.getPos();
            Vec3d nextpos = pos.add(motion);
            HitResult hitresult = ProjectileUtil.getCollision((Entity)this, this::canHit);
            if (hitresult != null && hitresult.getType() != HitResult.Type.field_1333) {
                this.dataTracker.set(Rotating, (Object)false);
                this.onCollision(hitresult);
            }
            motion = this.getVelocity();
            double f1 = motion.horizontalLength();
            this.setPitch(EntityProjectile.updateRotation((float)this.prevPitch, (float)((float)(MathHelper.atan2((double)motion.y, (double)f1) * 57.2957763671875))));
            this.setYaw(EntityProjectile.updateRotation((float)this.prevYaw, (float)((float)(MathHelper.atan2((double)motion.x, (double)motion.z) * 57.2957763671875))));
            if (this.isRotating()) {
                int spin = this.isBlock() ? 10 : 20;
                this.setPitch(this.getPitch() - (float)spin * this.getSpeed());
            }
            float f2 = this.getMotionFactor();
            float f3 = this.getGravity();
            if (this.isTouchingWater()) {
                for (int j = 0; j < 4; ++j) {
                    float f4 = 0.25f;
                    this.getWorld().addParticle((ParticleEffect)ParticleTypes.field_11247, nextpos.x - motion.x * 0.25, nextpos.y - motion.y * 0.25, nextpos.z - motion.z * 0.25, motion.x, motion.y, motion.z);
                }
                f2 = 0.6f;
            }
            motion = motion.multiply((double)f2);
            if (this.hasGravity()) {
                motion = motion.subtract(0.0, (double)f3, 0.0);
            }
            if (this.accelerate) {
                motion = motion.add(this.accelerationX, this.accelerationY, this.accelerationZ);
            }
            if (this.getWorld().isClient && (Integer)this.dataTracker.get(Particle) > 0) {
                this.getWorld().addParticle(ParticleType.getMCType((Integer)this.dataTracker.get(Particle)), this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
            this.setVelocity(motion);
            this.setPosition(nextpos.x, nextpos.y, nextpos.z);
            this.checkBlockCollision();
        }
    }

    protected boolean canHit(Entity entity) {
        if (!super.canHit(entity) || entity == this.thrower || this.npc != null && (entity == this.npc || this.npc.isTeammate(entity))) {
            return false;
        }
        if (entity instanceof PlayerEntity) {
            PlayerEntity entityplayer = (PlayerEntity)entity;
            if (entityplayer.getAbilities().invulnerable || this.thrower instanceof PlayerEntity && !((PlayerEntity)this.thrower).shouldDamagePlayer(entityplayer)) {
                return false;
            }
        }
        return true;
    }

    public boolean isBlock() {
        ItemStack item = this.getItemDisplay();
        if (item.isEmpty()) {
            return false;
        }
        return item.getItem() instanceof BlockItem;
    }

    private Item getItem() {
        ItemStack item = this.getItemDisplay();
        if (item.isEmpty()) {
            return Items.AIR;
        }
        return item.getItem();
    }

    protected float getMotionFactor() {
        return this.accelerate ? 0.95f : 1.0f;
    }

    protected void onCollision(HitResult movingobjectposition) {
        block31: {
            block29: {
                block30: {
                    Vec3d m;
                    double f3;
                    if (!this.getWorld().isClient) {
                        ProjectileEvent.ImpactEvent event;
                        BlockPos pos = BlockPos.ORIGIN;
                        Entity e = null;
                        if (movingobjectposition.getType() == HitResult.Type.field_1331) {
                            e = ((EntityHitResult)movingobjectposition).getEntity();
                            pos = e.getBlockPos();
                            event = new ProjectileEvent.ImpactEvent((IProjectile)NpcAPI.Instance().getIEntity((Entity)this), 0, e);
                        } else {
                            pos = ((BlockHitResult)movingobjectposition).getBlockPos();
                            BlockState state = this.getWorld().getBlockState(pos);
                            event = new ProjectileEvent.ImpactEvent((IProjectile)NpcAPI.Instance().getIEntity((Entity)this), 1, NpcAPI.Instance().getIBlock(this.getWorld(), pos));
                        }
                        if (pos == BlockPos.ORIGIN) {
                            pos = new BlockPos((int)movingobjectposition.getPos().x, (int)movingobjectposition.getPos().y, (int)movingobjectposition.getPos().z);
                        }
                        if (this.callback != null && this.callback.onImpact(this, pos, e)) {
                            return;
                        }
                        EventHooks.onProjectileImpact(this, event);
                    }
                    if (movingobjectposition.getType() != HitResult.Type.field_1331) break block29;
                    Entity e = ((EntityHitResult)movingobjectposition).getEntity();
                    float damage = this.damage;
                    if (damage == 0.0f) {
                        damage = 0.001f;
                    }
                    if (!e.damage(this.getDamageSources().thrown((Entity)this, this.getOwner()), damage)) break block30;
                    if (e instanceof LivingEntity) {
                        LivingEntity entityliving = (LivingEntity)e;
                        if (!this.getWorld().isClient && (this.isArrow() || this.sticksToWalls())) {
                            entityliving.setStuckArrowCount(entityliving.getStuckArrowCount() + 1);
                        }
                        if (this.destroyedOnEntityHit && !(e instanceof EndermanEntity)) {
                            this.remove(Entity.RemovalReason.DISCARDED);
                        }
                        if (this.effect != 0) {
                            if (this.effect != 666) {
                                StatusEffect p = PotionEffectType.getMCType(this.effect);
                                entityliving.addStatusEffect(new StatusEffectInstance(p, this.duration * 20, this.amplify));
                            } else {
                                entityliving.setFireTicks(this.duration * 20);
                            }
                        }
                    }
                    if (this.isBlock()) {
                        this.getWorld().syncWorldEvent((PlayerEntity)null, 2001, e.getBlockPos(), Block.getRawIdFromState((BlockState)((BlockItem)this.getItem()).getBlock().getDefaultState()));
                    } else if (!this.isArrow() && !this.sticksToWalls()) {
                        for (int i = 0; i < 8; ++i) {
                            this.getWorld().addParticle((ParticleEffect)new ItemStackParticleEffect(ParticleTypes.field_11218, this.getItemDisplay()), this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.15, this.random.nextGaussian() * 0.2, this.random.nextGaussian() * 0.15);
                        }
                    }
                    if (this.punch > 0 && (f3 = (m = this.getVelocity()).horizontalLength()) > 0.0) {
                        e.addVelocity(m.getX() * (double)this.punch * 0.6 / f3, 0.1, m.getZ() * (double)this.punch * 0.6 / f3);
                    }
                    break block31;
                }
                if (!this.hasGravity() || !this.isArrow() && !this.sticksToWalls()) break block31;
                this.setVelocity(this.getVelocity().multiply(-0.1));
                this.setYaw(this.getYaw() + 180.0f);
                this.prevYaw += 180.0f;
                this.ticksInAir = 0;
                break block31;
            }
            if (this.isArrow() || this.sticksToWalls()) {
                this.tilePos = ((BlockHitResult)movingobjectposition).getBlockPos();
                this.inBlock = this.getWorld().getBlockState(this.tilePos);
                Vec3d m = movingobjectposition.getPos().subtract(this.getPos());
                this.setVelocity(m);
                Vec3d vector3d1 = m.normalize().multiply((double)0.05f);
                this.setPos(this.getX() - vector3d1.x, this.getY() - vector3d1.y, this.getZ() - vector3d1.z);
                this.inGround = true;
                this.arrowShake = 7;
                if (!this.hasGravity()) {
                    this.dataTracker.set(Gravity, (Object)true);
                }
                if (this.inBlock != null) {
                    this.inBlock.onEntityCollision(this.getWorld(), this.tilePos, (Entity)this);
                }
            } else if (this.isBlock()) {
                this.getWorld().syncWorldEvent((PlayerEntity)null, 2001, this.getBlockPos(), Block.getRawIdFromState((BlockState)((BlockItem)this.getItem()).getBlock().getDefaultState()));
            } else {
                for (int i = 0; i < 8; ++i) {
                    this.getWorld().addParticle((ParticleEffect)new ItemStackParticleEffect(ParticleTypes.field_11218, this.getItemDisplay()), this.getX(), this.getY(), this.getZ(), this.random.nextGaussian() * 0.15, this.random.nextGaussian() * 0.2, this.random.nextGaussian() * 0.15);
                }
            }
        }
        if (this.explosiveRadius > 0) {
            boolean terraindamage = this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && this.explosiveDamage;
            this.getWorld().createExplosion((Entity)(this.getOwner() == null ? this : this.getOwner()), this.getX(), this.getY(), this.getZ(), (float)this.explosiveRadius, this.effect == 666, terraindamage ? World.ExplosionSourceType.field_40891 : World.ExplosionSourceType.field_40888);
            if (this.effect != 0) {
                Box axisalignedbb = this.getBoundingBox().expand((double)(this.explosiveRadius * 2), (double)(this.explosiveRadius * 2), (double)(this.explosiveRadius * 2));
                List list1 = this.getWorld().getNonSpectatingEntities(LivingEntity.class, axisalignedbb);
                StatusEffect p = PotionEffectType.getMCType(this.effect);
                for (LivingEntity entity : list1) {
                    if (this.effect != 666) {
                        entity.addStatusEffect(new StatusEffectInstance(p, this.duration * 20, this.amplify));
                        continue;
                    }
                    entity.setFireTicks(this.duration * 20);
                }
                this.getWorld().syncWorldEvent((PlayerEntity)null, 2002, this.getBlockPos(), this.getPotionColor(this.effect));
            }
            this.remove(Entity.RemovalReason.DISCARDED);
        }
        if (!(this.getWorld().isClient || this.isArrow() || this.sticksToWalls())) {
            this.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    private void blockParticles() {
    }

    public void writeCustomDataToNbt(NbtCompound par1CompoundTag) {
        par1CompoundTag.putShort("xTile", (short)this.tilePos.getX());
        par1CompoundTag.putShort("yTile", (short)this.tilePos.getY());
        par1CompoundTag.putShort("zTile", (short)this.tilePos.getZ());
        if (this.inBlock != null) {
            par1CompoundTag.put("inBlockState", (NbtElement)NbtHelper.fromBlockState((BlockState)this.inBlock));
        }
        par1CompoundTag.putByte("shake", (byte)this.throwableShake);
        par1CompoundTag.putBoolean("inGround", this.inGround);
        par1CompoundTag.putBoolean("isArrow", this.isArrow());
        Vec3d m = this.getVelocity();
        par1CompoundTag.put("direction", (NbtElement)this.toNbtList(new double[]{m.x, m.y, m.z}));
        par1CompoundTag.putBoolean("canBePickedUp", this.canBePickedUp);
        if ((this.throwerName == null || this.throwerName.length() == 0) && this.thrower != null && this.thrower instanceof PlayerEntity) {
            this.throwerName = this.thrower.getUuid().toString();
        }
        par1CompoundTag.putString("ownerName", this.throwerName == null ? "" : this.throwerName);
        par1CompoundTag.put("Item", (NbtElement)this.getItemDisplay().writeNbt(new NbtCompound()));
        par1CompoundTag.putFloat("damagev2", this.damage);
        par1CompoundTag.putInt("punch", this.punch);
        par1CompoundTag.putInt("size", ((Integer)this.dataTracker.get(Size)).intValue());
        par1CompoundTag.putInt("velocity", ((Integer)this.dataTracker.get(Velocity)).intValue());
        par1CompoundTag.putInt("explosiveRadius", this.explosiveRadius);
        par1CompoundTag.putInt("effectDuration", this.duration);
        par1CompoundTag.putBoolean("gravity", this.hasGravity());
        par1CompoundTag.putBoolean("accelerate", this.accelerate);
        par1CompoundTag.putBoolean("glows", ((Boolean)this.dataTracker.get(Glows)).booleanValue());
        par1CompoundTag.putInt("PotionEffect", this.effect);
        par1CompoundTag.putInt("trailenum", ((Integer)this.dataTracker.get(Particle)).intValue());
        par1CompoundTag.putBoolean("Render3D", ((Boolean)this.dataTracker.get(Is3d)).booleanValue());
        par1CompoundTag.putBoolean("Spins", ((Boolean)this.dataTracker.get(Rotating)).booleanValue());
        par1CompoundTag.putBoolean("Sticks", ((Boolean)this.dataTracker.get(Sticks)).booleanValue());
        par1CompoundTag.putInt("accuracy", this.accuracy);
    }

    public void readCustomDataFromNbt(NbtCompound compound) {
        NbtCompound var2;
        ItemStack item;
        this.tilePos = new BlockPos((int)compound.getShort("xTile"), (int)compound.getShort("yTile"), (int)compound.getShort("zTile"));
        if (compound.contains("inBlockState", 10)) {
            this.inBlock = NbtHelper.toBlockState((RegistryEntryLookup)this.getWorld().createCommandRegistryWrapper(RegistryKeys.field_41254), (NbtCompound)compound.getCompound("inBlockState"));
        }
        this.throwableShake = compound.getByte("shake") & 0xFF;
        this.inGround = compound.getByte("inGround") == 1;
        this.dataTracker.set(Arrow, (Object)compound.getBoolean("isArrow"));
        this.throwerName = compound.getString("ownerName");
        this.canBePickedUp = compound.getBoolean("canBePickedUp");
        this.damage = compound.getFloat("damagev2");
        this.punch = compound.getInt("punch");
        this.explosiveRadius = compound.getInt("explosiveRadius");
        this.duration = compound.getInt("effectDuration");
        this.accelerate = compound.getBoolean("accelerate");
        this.effect = compound.getInt("PotionEffect");
        this.accuracy = compound.getInt("accuracy");
        this.dataTracker.set(Particle, (Object)compound.getInt("trailenum"));
        this.dataTracker.set(Size, (Object)compound.getInt("size"));
        this.dataTracker.set(Glows, (Object)compound.getBoolean("glows"));
        this.dataTracker.set(Velocity, (Object)compound.getInt("velocity"));
        this.dataTracker.set(Gravity, (Object)compound.getBoolean("gravity"));
        this.dataTracker.set(Is3d, (Object)compound.getBoolean("Render3D"));
        this.dataTracker.set(Rotating, (Object)compound.getBoolean("Spins"));
        this.dataTracker.set(Sticks, (Object)compound.getBoolean("Sticks"));
        if (this.throwerName != null && this.throwerName.length() == 0) {
            this.throwerName = null;
        }
        if (compound.contains("direction")) {
            NbtList nbttaglist = compound.getList("direction", 6);
            this.setVelocity(new Vec3d(nbttaglist.getDouble(0), nbttaglist.getDouble(1), nbttaglist.getDouble(2)));
        }
        if ((item = ItemStack.fromNbt((NbtCompound)(var2 = compound.getCompound("Item")))).isEmpty()) {
            this.discard();
        } else {
            this.dataTracker.set(ItemStackThrown, (Object)item);
        }
    }

    public Entity getOwner() {
        if (this.throwerName == null || this.throwerName.isEmpty()) {
            return null;
        }
        try {
            UUID uuid = UUID.fromString(this.throwerName);
            if (this.thrower == null && uuid != null) {
                this.thrower = this.getWorld().getPlayerByUuid(uuid);
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return this.thrower;
    }

    private int getPotionColor(int p) {
        switch (p) {
            case 19: {
                return 32660;
            }
            case 17: {
                return 32660;
            }
            case 18: {
                return 32696;
            }
            case 2: {
                return 32698;
            }
            case 9: {
                return 32732;
            }
            case 15: {
                return 15;
            }
            case 20: {
                return 32732;
            }
        }
        return 0;
    }

    public void getStatProperties(DataRanged stats) {
        this.damage = stats.getStrength();
        this.punch = stats.getKnockback();
        this.accelerate = stats.getAccelerate();
        this.explosiveRadius = stats.getExplodeSize();
        this.effect = stats.getEffectType();
        this.duration = stats.getEffectTime();
        this.amplify = stats.getEffectStrength();
        this.setParticleEffect(stats.getParticle());
        this.dataTracker.set(Size, (Object)stats.getSize());
        this.dataTracker.set(Glows, (Object)stats.getGlows());
        this.setSpeed(stats.getSpeed());
        this.setHasGravity(stats.getHasGravity());
        this.setIs3D(stats.getRender3D());
        this.setRotating(stats.getSpins());
        this.setStickInWall(stats.getSticks());
    }

    public void setParticleEffect(int type) {
        this.dataTracker.set(Particle, (Object)type);
    }

    public void setHasGravity(boolean bo) {
        this.dataTracker.set(Gravity, (Object)bo);
    }

    public void setIs3D(boolean bo) {
        this.dataTracker.set(Is3d, (Object)bo);
    }

    public void setStickInWall(boolean bo) {
        this.dataTracker.set(Sticks, (Object)bo);
    }

    public ItemStack getItemDisplay() {
        return (ItemStack)this.dataTracker.get(ItemStackThrown);
    }

    public float getBrightnessAtEyes() {
        return (Boolean)this.dataTracker.get(Glows) != false ? 1.0f : super.getBrightnessAtEyes();
    }

    public boolean hasGravity() {
        return (Boolean)this.dataTracker.get(Gravity);
    }

    public void setSpeed(int speed) {
        this.dataTracker.set(Velocity, (Object)speed);
    }

    public float getSpeed() {
        return (float)((Integer)this.dataTracker.get(Velocity)).intValue() / 10.0f;
    }

    public boolean isArrow() {
        return (Boolean)this.dataTracker.get(Arrow);
    }

    public void setRotating(boolean bo) {
        this.dataTracker.set(Rotating, (Object)bo);
    }

    public boolean isRotating() {
        return (Boolean)this.dataTracker.get(Rotating);
    }

    public boolean glows() {
        return (Boolean)this.dataTracker.get(Glows);
    }

    public boolean is3D() {
        return (Boolean)this.dataTracker.get(Is3d) != false || this.isBlock();
    }

    public boolean sticksToWalls() {
        return this.is3D() && (Boolean)this.dataTracker.get(Sticks) != false;
    }

    public void onPlayerCollision(PlayerEntity par1Player) {
        if (this.getWorld().isClient || !this.canBePickedUp || !this.inGround || this.arrowShake > 0) {
            return;
        }
        if (par1Player.getInventory().insertStack(this.getItemDisplay())) {
            this.inGround = false;
            this.playSound(SoundEvents.field_15197, 0.2f, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7f + 1.0f) * 2.0f);
            par1Player.sendPickup((Entity)this, 1);
            this.discard();
        }
    }

    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.field_28630;
    }

    public Text getDisplayName() {
        if (!this.getItemDisplay().isEmpty()) {
            return this.getItemDisplay().toHoverableText();
        }
        return super.getDisplayName();
    }

    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        Entity entity = this.getOwner();
        return new EntitySpawnS2CPacket((Entity)this, entity == null ? 0 : entity.getId());
    }

    public static interface IProjectileCallback {
        public boolean onImpact(EntityProjectile var1, BlockPos var2, Entity var3);
    }
}

