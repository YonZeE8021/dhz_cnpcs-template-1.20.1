/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.damage.DamageSource
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 */
package noppes.npcs.ai;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.ability.AbstractAbility;
import noppes.npcs.entity.EntityNPCInterface;

public class CombatHandler {
    private Map<LivingEntity, Float> aggressors = new HashMap<LivingEntity, Float>();
    private EntityNPCInterface npc;
    private long startTime = 0L;
    private int combatResetTimer = 0;

    public CombatHandler(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public void update() {
        if (this.npc.isKilled()) {
            if (this.npc.isAttacking()) {
                this.reset();
            }
            return;
        }
        if (this.npc.getTarget() != null && !this.npc.isAttacking()) {
            this.start();
        }
        if (!this.shouldCombatContinue()) {
            if (this.combatResetTimer++ > 40) {
                this.reset();
            }
            return;
        }
        this.combatResetTimer = 0;
    }

    private boolean shouldCombatContinue() {
        if (this.npc.getTarget() == null) {
            return false;
        }
        return this.isValidTarget(this.npc.getTarget());
    }

    public void damage(DamageSource source, float damageAmount) {
        this.combatResetTimer = 0;
        Entity e = NoppesUtilServer.GetDamageSourcee(source);
        if (e instanceof LivingEntity) {
            LivingEntity el = (LivingEntity)e;
            Float f = this.aggressors.get(el);
            if (f == null) {
                f = Float.valueOf(0.0f);
            }
            this.aggressors.put(el, Float.valueOf(f.floatValue() + damageAmount));
        }
    }

    public void start() {
        this.combatResetTimer = 0;
        this.startTime = this.npc.getWorld().getLevelProperties().getTime();
        this.npc.getDataTracker().set(EntityNPCInterface.Attacking, true);
        for (AbstractAbility ab : this.npc.abilities.abilities) {
            ab.startCombat();
        }
    }

    public void reset() {
        this.combatResetTimer = 0;
        this.aggressors.clear();
        this.npc.getDataTracker().set(EntityNPCInterface.Attacking, false);
        this.npc.setTarget(null);
    }

    public boolean checkTarget() {
        if (this.aggressors.isEmpty() || this.npc.age % 10 != 0) {
            return false;
        }
        LivingEntity target = this.npc.getTarget();
        Float current = Float.valueOf(0.0f);
        if (this.isValidTarget(target)) {
            current = this.aggressors.get(target);
            if (current == null) {
                current = Float.valueOf(0.0f);
            }
        } else {
            target = null;
        }
        for (Map.Entry<LivingEntity, Float> entry : this.aggressors.entrySet()) {
            if (!(entry.getValue().floatValue() > current.floatValue()) || !this.isValidTarget(entry.getKey())) continue;
            current = entry.getValue();
            target = entry.getKey();
        }
        return target == null;
    }

    public boolean isValidTarget(LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (target instanceof PlayerEntity && ((PlayerEntity)target).getAbilities().invulnerable) {
            return false;
        }
        return this.npc.isInRange((Entity)target, this.npc.stats.aggroRange);
    }
}

