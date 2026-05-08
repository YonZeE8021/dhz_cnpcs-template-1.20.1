/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityGroup
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.entity.attribute.EntityAttributes
 */
package noppes.npcs.entity.data;

import net.minecraft.entity.EntityGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.entity.attribute.EntityAttributes;
import noppes.npcs.Resistances;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.data.INPCMelee;
import noppes.npcs.api.entity.data.INPCRanged;
import noppes.npcs.api.entity.data.INPCStats;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataMelee;
import noppes.npcs.entity.data.DataRanged;
import noppes.npcs.util.ValueUtil;

public class DataStats
implements INPCStats {
    public int aggroRange = 16;
    public int maxHealth = 20;
    public int respawnTime = 20;
    public int spawnCycle = 0;
    public boolean hideKilledBody = false;
    public Resistances resistances = new Resistances();
    public boolean immuneToFire = false;
    public boolean potionImmune = false;
    public boolean canDrown = true;
    public boolean burnInSun = false;
    public boolean noFallDamage = false;
    public boolean ignoreCobweb = false;
    public int healthRegen = 1;
    public int combatRegen = 0;
    public EntityGroup creatureType = EntityGroup.DEFAULT;
    public DataMelee melee;
    public DataRanged ranged;
    private EntityNPCInterface npc;

    public DataStats(EntityNPCInterface npc) {
        this.npc = npc;
        this.melee = new DataMelee(npc);
        this.ranged = new DataRanged(npc);
    }

    public void readToNBT(NbtCompound compound) {
        this.resistances.readToNBT(compound.getCompound("Resistances"));
        this.setMaxHealth(compound.getInt("MaxHealth"));
        this.hideKilledBody = compound.getBoolean("HideBodyWhenKilled");
        this.aggroRange = compound.getInt("AggroRange");
        this.respawnTime = compound.getInt("RespawnTime");
        this.spawnCycle = compound.getInt("SpawnCycle");
        this.setCreatureType(compound.getInt("CreatureType"));
        this.healthRegen = compound.getInt("HealthRegen");
        this.combatRegen = compound.getInt("CombatRegen");
        this.immuneToFire = compound.getBoolean("ImmuneToFire");
        this.potionImmune = compound.getBoolean("PotionImmune");
        this.canDrown = compound.getBoolean("CanDrown");
        this.burnInSun = compound.getBoolean("BurnInSun");
        this.noFallDamage = compound.getBoolean("NoFallDamage");
        this.npc.setImmuneToFire(this.immuneToFire);
        this.ignoreCobweb = compound.getBoolean("IgnoreCobweb");
        this.melee.load(compound);
        this.ranged.load(compound);
    }

    public NbtCompound save(NbtCompound compound) {
        compound.put("Resistances", (NbtElement)this.resistances.save());
        compound.putInt("MaxHealth", this.maxHealth);
        compound.putInt("AggroRange", this.aggroRange);
        compound.putBoolean("HideBodyWhenKilled", this.hideKilledBody);
        compound.putInt("RespawnTime", this.respawnTime);
        compound.putInt("SpawnCycle", this.spawnCycle);
        compound.putInt("CreatureType", this.getCreatureType());
        compound.putInt("HealthRegen", this.healthRegen);
        compound.putInt("CombatRegen", this.combatRegen);
        compound.putBoolean("ImmuneToFire", this.immuneToFire);
        compound.putBoolean("PotionImmune", this.potionImmune);
        compound.putBoolean("CanDrown", this.canDrown);
        compound.putBoolean("BurnInSun", this.burnInSun);
        compound.putBoolean("NoFallDamage", this.noFallDamage);
        compound.putBoolean("IgnoreCobweb", this.ignoreCobweb);
        this.melee.save(compound);
        this.ranged.save(compound);
        return compound;
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        if (maxHealth == this.maxHealth) {
            return;
        }
        this.maxHealth = maxHealth;
        this.npc.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue((double)maxHealth);
        this.npc.updateClient = true;
    }

    @Override
    public int getMaxHealth() {
        return this.maxHealth;
    }

    @Override
    public float getResistance(int type) {
        if (type == 0) {
            return this.resistances.melee;
        }
        if (type == 1) {
            return this.resistances.arrow;
        }
        if (type == 2) {
            return this.resistances.explosion;
        }
        if (type == 3) {
            return this.resistances.knockback;
        }
        return 1.0f;
    }

    @Override
    public void setResistance(int type, float value) {
        value = ValueUtil.correctFloat(value, 0.0f, 2.0f);
        if (type == 0) {
            this.resistances.melee = value;
        } else if (type == 1) {
            this.resistances.arrow = value;
        } else if (type == 2) {
            this.resistances.explosion = value;
        } else if (type == 3) {
            this.resistances.knockback = value;
        }
    }

    @Override
    public int getCombatRegen() {
        return this.combatRegen;
    }

    @Override
    public void setCombatRegen(int regen) {
        this.combatRegen = regen;
    }

    @Override
    public int getHealthRegen() {
        return this.healthRegen;
    }

    @Override
    public void setHealthRegen(int regen) {
        this.healthRegen = regen;
    }

    @Override
    public INPCMelee getMelee() {
        return this.melee;
    }

    @Override
    public INPCRanged getRanged() {
        return this.ranged;
    }

    @Override
    public boolean getImmune(int type) {
        if (type == 0) {
            return this.potionImmune;
        }
        if (type == 1) {
            return !this.noFallDamage;
        }
        if (type == 2) {
            return this.burnInSun;
        }
        if (type == 3) {
            return this.immuneToFire;
        }
        if (type == 4) {
            return !this.canDrown;
        }
        if (type == 5) {
            return this.ignoreCobweb;
        }
        throw new CustomNPCsException("Unknown immune type: " + type, new Object[0]);
    }

    @Override
    public void setImmune(int type, boolean bo) {
        if (type == 0) {
            this.potionImmune = bo;
        } else if (type == 1) {
            this.noFallDamage = !bo;
        } else if (type == 2) {
            this.burnInSun = bo;
        } else if (type == 3) {
            this.npc.setImmuneToFire(bo);
        } else if (type == 4) {
            this.canDrown = !bo;
        } else if (type == 5) {
            this.ignoreCobweb = bo;
        } else {
            throw new CustomNPCsException("Unknown immune type: " + type, new Object[0]);
        }
    }

    @Override
    public int getCreatureType() {
        if (this.creatureType == EntityGroup.UNDEAD) {
            return 1;
        }
        if (this.creatureType == EntityGroup.ARTHROPOD) {
            return 2;
        }
        if (this.creatureType == EntityGroup.ILLAGER) {
            return 3;
        }
        if (this.creatureType == EntityGroup.AQUATIC) {
            return 4;
        }
        return 0;
    }

    @Override
    public void setCreatureType(int type) {
        this.creatureType = type == 1 ? EntityGroup.UNDEAD : (type == 2 ? EntityGroup.ARTHROPOD : (type == 3 ? EntityGroup.ILLAGER : (type == 4 ? EntityGroup.AQUATIC : EntityGroup.DEFAULT)));
    }

    @Override
    public int getRespawnType() {
        return this.spawnCycle;
    }

    @Override
    public void setRespawnType(int type) {
        this.spawnCycle = type;
    }

    @Override
    public int getRespawnTime() {
        return this.respawnTime;
    }

    @Override
    public void setRespawnTime(int seconds) {
        this.respawnTime = seconds;
    }

    @Override
    public boolean getHideDeadBody() {
        return this.hideKilledBody;
    }

    @Override
    public void setHideDeadBody(boolean hide) {
        this.hideKilledBody = hide;
        this.npc.updateClient = true;
    }

    @Override
    public int getAggroRange() {
        return this.aggroRange;
    }

    @Override
    public void setAggroRange(int range) {
        this.aggroRange = range;
        this.npc.setPositionTarget(this.npc.ais.startPos(), this.aggroRange * 2);
    }
}

