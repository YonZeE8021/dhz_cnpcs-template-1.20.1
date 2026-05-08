/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.goal.Goal
 */
package noppes.npcs.ai;

import net.minecraft.entity.ai.goal.Goal;
import noppes.npcs.ability.AbstractAbility;
import noppes.npcs.ability.IAbilityUpdate;
import noppes.npcs.constants.EnumAbilityType;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityAIAbilities
extends Goal {
    private EntityNPCInterface npc;
    private IAbilityUpdate ability;

    public EntityAIAbilities(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public boolean canStart() {
        if (!this.npc.isAttacking()) {
            return false;
        }
        this.ability = (IAbilityUpdate)((Object)this.npc.abilities.getAbility(EnumAbilityType.UPDATE));
        return this.ability != null;
    }

    public boolean shouldContinue() {
        return this.npc.isAttacking() && this.ability.isActive();
    }

    public void tick() {
        this.ability.update();
    }

    public void stop() {
        ((AbstractAbility)((Object)this.ability)).endAbility();
        this.ability = null;
    }
}

