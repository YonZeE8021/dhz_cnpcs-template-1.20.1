/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.goal.Goal$Control
 *  net.minecraft.entity.ai.goal.GoalSelector
 *  net.minecraft.entity.ai.goal.PrioritizedGoal
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package noppes.npcs.mixin;

import java.util.EnumSet;
import java.util.Map;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={GoalSelector.class})
public interface GoalSelectorMixin {
    @Accessor("goalsByControl")
    public Map<Goal.Control, PrioritizedGoal> lockedFlags();

    @Accessor("disabledControls")
    public EnumSet<Goal.Control> disabledFlags();
}

