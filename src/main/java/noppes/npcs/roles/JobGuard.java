/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.NBTTags;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobInterface;

public class JobGuard
extends JobInterface {
    public List<String> targets = new ArrayList<String>();

    public JobGuard(EntityNPCInterface npc) {
        super(npc);
    }

    public boolean isEntityApplicable(Entity entity) {
        if (entity instanceof PlayerEntity || entity instanceof EntityNPCInterface) {
            return false;
        }
        return this.targets.contains(entity.getType().getTranslationKey());
    }

    @Override
    public NbtCompound save(NbtCompound nbttagcompound) {
        nbttagcompound.put("GuardTargets", (NbtElement)NBTTags.nbtStringList(this.targets));
        return nbttagcompound;
    }

    @Override
    public void load(NbtCompound nbttagcompound) {
        this.targets = NBTTags.getStringList(nbttagcompound.getList("GuardTargets", 10));
    }

    @Override
    public int getType() {
        return 3;
    }
}

