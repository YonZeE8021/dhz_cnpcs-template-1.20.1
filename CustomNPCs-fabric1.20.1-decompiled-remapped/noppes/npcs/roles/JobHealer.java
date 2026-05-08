/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.NBTTags;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobInterface;
import noppes.npcs.util.ValueUtil;

public class JobHealer
extends JobInterface {
    private int healTicks = 0;
    public int range = 8;
    public byte type = (byte)2;
    public int speed = 20;
    public HashMap<Integer, Integer> effects = new HashMap();
    private List<LivingEntity> affected = new ArrayList<LivingEntity>();

    public JobHealer(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public NbtCompound save(NbtCompound nbttagcompound) {
        nbttagcompound.putInt("HealerRange", this.range);
        nbttagcompound.putByte("HealerType", this.type);
        nbttagcompound.put("BeaconEffects", (NbtElement)NBTTags.nbtIntegerIntegerMap(this.effects));
        nbttagcompound.putInt("HealerSpeed", this.speed);
        return nbttagcompound;
    }

    @Override
    public void load(NbtCompound nbttagcompound) {
        this.range = nbttagcompound.getInt("HealerRange");
        this.type = nbttagcompound.getByte("HealerType");
        this.effects = NBTTags.getIntegerIntegerMap(nbttagcompound.getList("BeaconEffects", 10));
        this.speed = ValueUtil.CorrectInt(nbttagcompound.getInt("HealerSpeed"), 10, Integer.MAX_VALUE);
    }

    @Override
    public boolean aiShouldExecute() {
        ++this.healTicks;
        if (this.healTicks < this.speed) {
            return false;
        }
        this.healTicks = 0;
        this.affected = this.npc.getWorld().getNonSpectatingEntities(LivingEntity.class, this.npc.getBoundingBox().expand((double)this.range, (double)this.range / 2.0, (double)this.range));
        return !this.affected.isEmpty();
    }

    @Override
    public boolean aiContinueExecute() {
        return false;
    }

    @Override
    public void aiStartExecuting() {
        for (LivingEntity entity : this.affected) {
            boolean isEnemy = false;
            isEnemy = entity instanceof PlayerEntity ? this.npc.faction.isAggressiveToPlayer((PlayerEntity)entity) : (entity instanceof EntityNPCInterface ? this.npc.faction.isAggressiveToNpc((EntityNPCInterface)entity) : entity instanceof MobEntity);
            if (entity == this.npc || this.type == 0 && isEnemy || this.type == 1 && !isEnemy) continue;
            for (Integer potionEffect : this.effects.keySet()) {
                StatusEffect p = StatusEffect.byRawId((int)potionEffect);
                if (p == null) continue;
                entity.addStatusEffect(new StatusEffectInstance(p, 100, this.effects.get(potionEffect).intValue()));
            }
        }
        this.affected.clear();
    }

    @Override
    public int getType() {
        return 2;
    }
}

