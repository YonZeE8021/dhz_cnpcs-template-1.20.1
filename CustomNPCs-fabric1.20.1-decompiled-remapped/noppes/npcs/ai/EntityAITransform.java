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

public class EntityAITransform
extends Goal {
    private EntityNPCInterface npc;

    public EntityAITransform(EntityNPCInterface npc) {
        this.npc = npc;
        this.setControls(EnumSet.of(Goal.Control.field_18405));
    }

    public boolean canStart() {
        if (this.npc.isKilled() || this.npc.isAttacking() || this.npc.transform.editingModus) {
            return false;
        }
        return this.npc.getWorld().isDay() ? this.npc.transform.isActive : !this.npc.transform.isActive;
    }

    public void start() {
        this.npc.transform.transform(!this.npc.transform.isActive);
    }
}

