/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.roles;

import java.util.List;
import net.minecraft.nbt.NbtCompound;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.entity.ICustomNpc;
import noppes.npcs.api.entity.data.role.IJobFollower;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobInterface;

public class JobFollower
extends JobInterface
implements IJobFollower {
    public EntityNPCInterface following = null;
    private int ticks = 40;
    private int range = 20;
    public String name = "";

    public JobFollower(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public NbtCompound save(NbtCompound compound) {
        compound.putString("FollowingEntityName", this.name);
        return compound;
    }

    @Override
    public void load(NbtCompound compound) {
        this.name = compound.getString("FollowingEntityName");
    }

    @Override
    public boolean aiShouldExecute() {
        if (this.npc.isAttacking()) {
            return false;
        }
        --this.ticks;
        if (this.ticks > 0) {
            return false;
        }
        this.ticks = 10;
        this.following = null;
        List<EntityNPCInterface> list = this.npc.getWorld().getNonSpectatingEntities(EntityNPCInterface.class, this.npc.getBoundingBox().expand((double)this.getRange(), (double)this.getRange(), (double)this.getRange()));
        for (EntityNPCInterface entity : list) {
            if (entity == this.npc || entity.isKilled() || !entity.display.getName().equalsIgnoreCase(this.name)) continue;
            this.following = entity;
            break;
        }
        return false;
    }

    private int getRange() {
        if (this.range > CustomNpcs.NpcNavRange) {
            return CustomNpcs.NpcNavRange;
        }
        return this.range;
    }

    @Override
    public boolean isFollowing() {
        return this.following != null;
    }

    @Override
    public void reset() {
    }

    @Override
    public void stop() {
        this.following = null;
    }

    public boolean hasOwner() {
        return !this.name.isEmpty();
    }

    @Override
    public String getFollowing() {
        return this.name;
    }

    @Override
    public void setFollowing(String name) {
        this.name = name;
    }

    @Override
    public ICustomNpc getFollowingNpc() {
        if (this.following == null) {
            return null;
        }
        return this.following.wrappedNPC;
    }

    @Override
    public int getType() {
        return 5;
    }
}

