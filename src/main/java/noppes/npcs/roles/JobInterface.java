/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.ai.goal.Goal$Control
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.util.Identifier
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.roles;

import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import noppes.npcs.api.entity.data.INPCJob;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.entity.EntityNPCInterface;

public abstract class JobInterface
implements INPCJob {
    public static final JobInterface NONE = new JobInterface(null){

        @Override
        public NbtCompound save(NbtCompound compound) {
            return null;
        }

        @Override
        public void load(NbtCompound compound) {
        }

        @Override
        public int getType() {
            return 0;
        }
    };
    public EntityNPCInterface npc;
    public boolean overrideMainHand = false;
    public boolean overrideOffHand = false;

    public JobInterface(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public abstract NbtCompound save(NbtCompound var1);

    public abstract void load(NbtCompound var1);

    public void killed() {
    }

    public void delete() {
    }

    public boolean aiShouldExecute() {
        return false;
    }

    public boolean aiContinueExecute() {
        return this.aiShouldExecute();
    }

    public void aiStartExecuting() {
    }

    public void aiUpdateTask() {
    }

    public void reset() {
    }

    public void stop() {
    }

    public IItemStack getMainhand() {
        return null;
    }

    public IItemStack getOffhand() {
        return null;
    }

    public boolean isFollowing() {
        return false;
    }

    public EnumSet<Goal.Control> getFlags() {
        return EnumSet.noneOf(Goal.Control.class);
    }

    public ItemStack stringToItem(String s) {
        if (s.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return new ItemStack((ItemConvertible)Registries.ITEM.get(new Identifier(s)));
    }

    public String itemToString(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return "";
        }
        return Registries.ITEM.getId(item.getItem()).toString();
    }
}

