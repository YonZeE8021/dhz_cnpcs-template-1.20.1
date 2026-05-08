/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.ai.goal.Goal
 */
package noppes.npcs.ai.target;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIClearTarget
extends Goal {
    private EntityNPCInterface npc;
    private LivingEntity target;

    public EntityAIClearTarget(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public boolean canStart() {
        this.target = this.npc.getTarget();
        if (this.target == null) {
            return false;
        }
        if (this.npc.getOwner() != null && !this.npc.isInRange((Entity)this.npc.getOwner(), this.npc.stats.aggroRange * 2)) {
            return true;
        }
        return this.npc.combatHandler.checkTarget();
    }

    public void start() {
        this.npc.setTarget(null);
        if (this.target == this.npc.getAttacker()) {
            this.npc.setAttacker(null);
        }
        super.start();
    }

    public void stop() {
        this.npc.getNavigation().stop();
    }
}

