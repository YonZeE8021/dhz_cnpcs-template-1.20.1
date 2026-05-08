/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.goal.Goal
 */
package noppes.npcs.ai;

import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIRole
extends Goal {
    private EntityNPCInterface npc;

    public EntityAIRole(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public boolean canStart() {
        if (this.npc.isKilled()) {
            return false;
        }
        return this.npc.role.aiShouldExecute();
    }

    public void start() {
        this.npc.role.aiStartExecuting();
    }

    public boolean shouldContinue() {
        if (this.npc.isKilled()) {
            return false;
        }
        return this.npc.role.aiContinueExecute();
    }

    public void tick() {
        this.npc.role.aiUpdateTask();
    }
}

