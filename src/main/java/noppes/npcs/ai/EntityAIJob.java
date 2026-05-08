/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.goal.Goal
 *  net.minecraft.entity.ai.goal.Goal$Control
 */
package noppes.npcs.ai;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIJob
extends Goal {
    private EntityNPCInterface npc;

    public EntityAIJob(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public boolean canStart() {
        if (this.npc.isKilled()) {
            return false;
        }
        return this.npc.job.aiShouldExecute();
    }

    public void start() {
        this.npc.job.aiStartExecuting();
    }

    public boolean shouldContinue() {
        if (this.npc.isKilled()) {
            return false;
        }
        return this.npc.job.aiContinueExecute();
    }

    public void tick() {
        this.npc.job.aiUpdateTask();
    }

    public void stop() {
        this.npc.job.stop();
    }

    public EnumSet<Goal.Control> getControls() {
        return this.npc.job.getFlags();
    }
}

