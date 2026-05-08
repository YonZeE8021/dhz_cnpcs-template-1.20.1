/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.roles;

import java.util.HashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import noppes.npcs.api.entity.data.INPCRole;
import noppes.npcs.entity.EntityNPCInterface;

public abstract class RoleInterface
implements INPCRole {
    public static final RoleInterface NONE = new RoleInterface(null){

        @Override
        public NbtCompound save(NbtCompound compound) {
            return compound;
        }

        @Override
        public void load(NbtCompound compound) {
        }

        @Override
        public void interact(PlayerEntity player) {
        }

        @Override
        public int getType() {
            return 0;
        }
    };
    public EntityNPCInterface npc;
    public HashMap<String, String> dataString = new HashMap();

    public RoleInterface(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public abstract NbtCompound save(NbtCompound var1);

    public abstract void load(NbtCompound var1);

    public abstract void interact(PlayerEntity var1);

    public void killed() {
    }

    public void delete() {
    }

    public boolean aiShouldExecute() {
        return false;
    }

    public boolean aiContinueExecute() {
        return false;
    }

    public void aiStartExecuting() {
    }

    public void aiUpdateTask() {
    }

    public boolean defendOwner() {
        return false;
    }

    public boolean isFollowing() {
        return false;
    }

    public void clientUpdate() {
    }
}

