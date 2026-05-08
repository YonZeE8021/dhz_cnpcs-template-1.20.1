/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.goal.Goal
 */
package noppes.npcs.ai;

import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIWaterNav
extends Goal {
    private EntityNPCInterface entity;

    public EntityAIWaterNav(EntityNPCInterface npc) {
        this.entity = npc;
        npc.getNavigation().setCanSwim(true);
    }

    public boolean canStart() {
        if (this.entity.isTouchingWater() || this.entity.isInLava()) {
            if (this.entity.ais.canSwim) {
                return true;
            }
            return this.entity.horizontalCollision;
        }
        return false;
    }

    public void tick() {
        if (this.entity.getRandom().nextFloat() < 0.8f) {
            this.entity.getJumpControl().setActive();
        }
    }
}

