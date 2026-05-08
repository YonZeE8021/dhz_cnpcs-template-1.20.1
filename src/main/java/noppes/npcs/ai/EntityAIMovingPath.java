/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.goal.Goal
 *  net.minecraft.entity.ai.goal.Goal$Control
 */
package noppes.npcs.ai;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIMovingPath
extends Goal {
    private EntityNPCInterface npc;
    private int[] pos;
    private int retries = 0;

    public EntityAIMovingPath(EntityNPCInterface iNpc) {
        this.npc = iNpc;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    public boolean canStart() {
        if (this.npc.isAttacking() || this.npc.isInteracting() || this.npc.getRandom().nextInt(40) != 0 && this.npc.ais.movingPause || !this.npc.getNavigation().isIdle()) {
            return false;
        }
        List<int[]> list = this.npc.ais.getMovingPath();
        if (list.size() < 2) {
            return false;
        }
        this.npc.ais.incrementMovingPath();
        this.pos = this.npc.ais.getCurrentMovingPath();
        this.retries = 0;
        return true;
    }

    public boolean shouldContinue() {
        if (this.npc.isAttacking() || this.npc.isInteracting()) {
            this.npc.ais.decreaseMovingPath();
            return false;
        }
        if (this.npc.getNavigation().isIdle()) {
            this.npc.getNavigation().stop();
            if (this.npc.squaredDistanceTo(this.pos[0], this.pos[1], this.pos[2]) < 3.0) {
                return false;
            }
            if (this.retries++ < 3) {
                this.start();
                return true;
            }
            return false;
        }
        return true;
    }

    public void start() {
        this.npc.getNavigation().startMovingTo((double)this.pos[0] + 0.5, (double)this.pos[1], (double)this.pos[2] + 0.5, 1.0);
    }
}

