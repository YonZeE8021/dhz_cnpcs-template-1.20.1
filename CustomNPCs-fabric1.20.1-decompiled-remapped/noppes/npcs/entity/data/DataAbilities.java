/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.entity.data;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import noppes.npcs.ability.AbstractAbility;
import noppes.npcs.constants.EnumAbilityType;
import noppes.npcs.entity.EntityNPCInterface;

public class DataAbilities {
    public List<AbstractAbility> abilities = new ArrayList<AbstractAbility>();
    public EntityNPCInterface npc;

    public DataAbilities(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public NbtCompound save(NbtCompound compound) {
        return compound;
    }

    public void readToNBT(NbtCompound compound) {
    }

    public AbstractAbility getAbility(EnumAbilityType type) {
        LivingEntity target = this.npc.getTarget();
        for (AbstractAbility ability : this.abilities) {
            if (!ability.isType(type) || !ability.canRun(target)) continue;
            return ability;
        }
        return null;
    }
}

