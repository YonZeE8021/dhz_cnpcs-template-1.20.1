/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.PathAwareEntity
 *  net.minecraft.entity.ai.goal.Goal
 *  net.minecraft.entity.ai.goal.Goal$Control
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.entity.ai.NoPenaltyTargeting
 */
package noppes.npcs.ai;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import noppes.npcs.CustomNpcs;
import noppes.npcs.ai.selector.NPCInteractSelector;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIWander
extends Goal {
    private EntityNPCInterface entity;
    public final NPCInteractSelector selector;
    private double x;
    private double y;
    private double zPosition;
    private EntityNPCInterface nearbyNPC;

    public EntityAIWander(EntityNPCInterface npc) {
        this.entity = npc;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
        this.selector = new NPCInteractSelector(npc);
    }

    public boolean canStart() {
        if (this.entity.getDespawnCounter() >= 100 || !this.entity.getNavigation().isIdle() || this.entity.isInteracting() || this.entity.hasVehicle() || this.entity.ais.movingPause && this.entity.getRandom().nextInt(80) != 0) {
            return false;
        }
        if (this.entity.ais.npcInteracting && this.entity.getRandom().nextInt(this.entity.ais.movingPause ? 6 : 16) == 1) {
            this.nearbyNPC = this.getNearbyNPC();
        }
        if (this.nearbyNPC != null) {
            this.x = MathHelper.floor((double)this.nearbyNPC.getX());
            this.y = MathHelper.floor((double)this.nearbyNPC.getY());
            this.zPosition = MathHelper.floor((double)this.nearbyNPC.getZ());
            this.nearbyNPC.addInteract((LivingEntity)this.entity);
        } else {
            Vec3d vec = this.getVec();
            if (vec == null) {
                return false;
            }
            this.x = vec.x;
            this.y = vec.y;
            if (this.entity.ais.movementType == 1) {
                this.y = this.entity.getStartYPos() + (double)this.entity.getRandom().nextFloat() * 0.75 * (double)this.entity.ais.walkingRange;
            }
            this.zPosition = vec.z;
        }
        return true;
    }

    public void tick() {
        if (this.nearbyNPC != null) {
            this.nearbyNPC.getNavigation().stop();
        }
    }

    private EntityNPCInterface getNearbyNPC() {
        List list = this.entity.getWorld().getOtherEntities((Entity)this.entity, this.entity.getBoundingBox().expand((double)this.entity.ais.walkingRange, this.entity.ais.walkingRange > 7 ? 7.0 : (double)this.entity.ais.walkingRange, (double)this.entity.ais.walkingRange), (Predicate)(this.selector));
        Iterator ita = list.iterator();
        while (ita.hasNext()) {
            EntityNPCInterface npc = (EntityNPCInterface)(ita.next());
            if (npc.ais.stopAndInteract && !npc.isAttacking() && npc.isAlive() && !this.entity.faction.isAggressiveToNpc(npc)) continue;
            ita.remove();
        }
        if (list.isEmpty()) {
            return null;
        }
        return (EntityNPCInterface)(list.get(this.entity.getRandom().nextInt(list.size())));
    }

    private Vec3d getVec() {
        if (this.entity.ais.walkingRange > 0) {
            BlockPos start = new BlockPos((int)this.entity.getStartXPos(), (int)this.entity.getStartYPos(), (int)this.entity.getStartZPos());
            int distance = (int)Math.sqrt(this.entity.getBlockPos().getSquaredDistance((Vec3i)start));
            int range = Math.min(this.entity.ais.walkingRange, CustomNpcs.NpcNavRange);
            if (range - distance < 4) {
                Vec3d pos2 = new Vec3d((this.entity.getX() + (double)start.getX()) / 2.0, (this.entity.getY() + (double)start.getY()) / 2.0, (this.entity.getZ() + (double)start.getZ()) / 2.0);
                return NoPenaltyTargeting.findTo((PathAwareEntity)this.entity, (int)(range / 2), (int)Math.min(range / 2, 7), (Vec3d)pos2, (double)1.5707963267948966);
            }
            return NoPenaltyTargeting.find((PathAwareEntity)this.entity, (int)(range / 2), (int)Math.min(range / 2, 7));
        }
        return NoPenaltyTargeting.find((PathAwareEntity)this.entity, (int)CustomNpcs.NpcNavRange, (int)7);
    }

    public boolean shouldContinue() {
        if (this.nearbyNPC != null && (!this.selector.apply(this.nearbyNPC) || this.entity.isInRange((Entity)this.nearbyNPC, this.entity.getWidth()))) {
            return false;
        }
        return !this.entity.getNavigation().isIdle() && this.entity.isAlive() && !this.entity.isInteracting();
    }

    public void start() {
        this.entity.getNavigation().startMovingAlong(this.entity.getNavigation().findPathTo(this.x, this.y, this.zPosition, 0), 1.0);
    }

    public void stop() {
        if (this.nearbyNPC != null && this.entity.isInRange((Entity)this.nearbyNPC, 3.5)) {
            Line line;
            EntityNPCInterface talk = this.entity;
            if (this.entity.getRandom().nextBoolean()) {
                talk = this.nearbyNPC;
            }
            if ((line = talk.advanced.getNPCInteractLine()) == null) {
                line = new Line(".........");
            }
            line.setShowText(false);
            talk.saySurrounding(line);
            this.entity.addInteract((LivingEntity)this.nearbyNPC);
            this.nearbyNPC.addInteract((LivingEntity)this.entity);
        }
        this.nearbyNPC = null;
    }
}

