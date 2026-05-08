/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.controllers.data;

import java.util.Set;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.NBTTags;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobInterface;
import noppes.npcs.roles.RoleInterface;

public class DataTransform {
    public NbtCompound display;
    public NbtCompound ai;
    public NbtCompound advanced;
    public NbtCompound inv;
    public NbtCompound stats;
    public NbtCompound role;
    public NbtCompound job;
    public boolean hasDisplay;
    public boolean hasAi;
    public boolean hasAdvanced;
    public boolean hasInv;
    public boolean hasStats;
    public boolean hasRole;
    public boolean hasJob;
    public boolean isActive;
    private EntityNPCInterface npc;
    public boolean editingModus = false;

    public DataTransform(EntityNPCInterface npc) {
        this.npc = npc;
    }

    public NbtCompound save(NbtCompound compound) {
        compound.putBoolean("TransformIsActive", this.isActive);
        this.writeOptions(compound);
        if (this.hasDisplay) {
            compound.put("TransformDisplay", (NbtElement)this.display);
        }
        if (this.hasAi) {
            compound.put("TransformAI", (NbtElement)this.ai);
        }
        if (this.hasAdvanced) {
            compound.put("TransformAdvanced", (NbtElement)this.advanced);
        }
        if (this.hasInv) {
            compound.put("TransformInv", (NbtElement)this.inv);
        }
        if (this.hasStats) {
            compound.put("TransformStats", (NbtElement)this.stats);
        }
        if (this.hasRole) {
            compound.put("TransformRole", (NbtElement)this.role);
        }
        if (this.hasJob) {
            compound.put("TransformJob", (NbtElement)this.job);
        }
        return compound;
    }

    public NbtCompound writeOptions(NbtCompound compound) {
        compound.putBoolean("TransformHasDisplay", this.hasDisplay);
        compound.putBoolean("TransformHasAI", this.hasAi);
        compound.putBoolean("TransformHasAdvanced", this.hasAdvanced);
        compound.putBoolean("TransformHasInv", this.hasInv);
        compound.putBoolean("TransformHasStats", this.hasStats);
        compound.putBoolean("TransformHasRole", this.hasRole);
        compound.putBoolean("TransformHasJob", this.hasJob);
        compound.putBoolean("TransformEditingModus", this.editingModus);
        return compound;
    }

    public void readToNBT(NbtCompound compound) {
        this.isActive = compound.getBoolean("TransformIsActive");
        this.readOptions(compound);
        this.display = this.hasDisplay ? compound.getCompound("TransformDisplay") : this.getDisplay();
        this.ai = this.hasAi ? compound.getCompound("TransformAI") : this.npc.ais.save(new NbtCompound());
        this.advanced = this.hasAdvanced ? compound.getCompound("TransformAdvanced") : this.getAdvanced();
        this.inv = this.hasInv ? compound.getCompound("TransformInv") : this.npc.inventory.save(new NbtCompound());
        this.stats = this.hasStats ? compound.getCompound("TransformStats") : this.npc.stats.save(new NbtCompound());
        this.job = this.hasJob ? compound.getCompound("TransformJob") : this.getJob();
        this.role = this.hasRole ? compound.getCompound("TransformRole") : this.getRole();
    }

    public NbtCompound getJob() {
        NbtCompound compound = new NbtCompound();
        compound.putInt("NpcJob", this.npc.job.getType());
        this.npc.job.save(compound);
        return compound;
    }

    public NbtCompound getRole() {
        NbtCompound compound = new NbtCompound();
        compound.putInt("Role", this.npc.role.getType());
        this.npc.role.save(compound);
        return compound;
    }

    public NbtCompound getDisplay() {
        NbtCompound compound = this.npc.display.save(new NbtCompound());
        if (this.npc instanceof EntityCustomNpc) {
            compound.put("ModelData", (NbtElement)((EntityCustomNpc)this.npc).modelData.save());
        }
        return compound;
    }

    public NbtCompound getAdvanced() {
        JobInterface jopType = this.npc.job;
        RoleInterface roleType = this.npc.role;
        this.npc.job = JobInterface.NONE;
        this.npc.role = RoleInterface.NONE;
        NbtCompound compound = this.npc.advanced.save(new NbtCompound());
        this.npc.job = jopType;
        this.npc.role = roleType;
        return compound;
    }

    public void readOptions(NbtCompound compound) {
        boolean hadDisplay = this.hasDisplay;
        boolean hadAI = this.hasAi;
        boolean hadAdvanced = this.hasAdvanced;
        boolean hadInv = this.hasInv;
        boolean hadStats = this.hasStats;
        boolean hadRole = this.hasRole;
        boolean hadJob = this.hasJob;
        this.hasDisplay = compound.getBoolean("TransformHasDisplay");
        this.hasAi = compound.getBoolean("TransformHasAI");
        this.hasAdvanced = compound.getBoolean("TransformHasAdvanced");
        this.hasInv = compound.getBoolean("TransformHasInv");
        this.hasStats = compound.getBoolean("TransformHasStats");
        this.hasRole = compound.getBoolean("TransformHasRole");
        this.hasJob = compound.getBoolean("TransformHasJob");
        this.editingModus = compound.getBoolean("TransformEditingModus");
        if (this.hasDisplay && !hadDisplay) {
            this.display = this.getDisplay();
        }
        if (this.hasAi && !hadAI) {
            this.ai = this.npc.ais.save(new NbtCompound());
        }
        if (this.hasStats && !hadStats) {
            this.stats = this.npc.stats.save(new NbtCompound());
        }
        if (this.hasInv && !hadInv) {
            this.inv = this.npc.inventory.save(new NbtCompound());
        }
        if (this.hasAdvanced && !hadAdvanced) {
            this.advanced = this.getAdvanced();
        }
        if (this.hasJob && !hadJob) {
            this.job = this.getJob();
        }
        if (this.hasRole && !hadRole) {
            this.role = this.getRole();
        }
    }

    public boolean isValid() {
        return this.hasAdvanced || this.hasAi || this.hasDisplay || this.hasInv || this.hasStats || this.hasJob || this.hasRole;
    }

    public NbtCompound processAdvanced(NbtCompound compoundAdv, NbtCompound compoundRole, NbtCompound compoundJob) {
        if (this.hasAdvanced) {
            compoundAdv = this.advanced;
        }
        if (this.hasRole) {
            compoundRole = this.role;
        }
        if (this.hasJob) {
            compoundJob = this.job;
        }
        Set names = compoundRole.getKeys();
        for (String name : names) {
            compoundAdv.put(name, compoundRole.get(name));
        }
        names = compoundJob.getKeys();
        for (String name : names) {
            compoundAdv.put(name, compoundJob.get(name));
        }
        return compoundAdv;
    }

    public void transform(boolean isActive) {
        NbtCompound compound;
        if (this.isActive == isActive) {
            return;
        }
        if (this.hasDisplay) {
            compound = this.getDisplay();
            this.npc.display.readToNBT(NBTTags.NBTMerge(compound, this.display));
            if (this.npc instanceof EntityCustomNpc) {
                ((EntityCustomNpc)this.npc).modelData.load(NBTTags.NBTMerge(compound.getCompound("ModelData"), this.display.getCompound("ModelData")));
            }
            this.display = compound;
        }
        if (this.hasStats) {
            compound = this.npc.stats.save(new NbtCompound());
            this.npc.stats.readToNBT(NBTTags.NBTMerge(compound, this.stats));
            this.stats = compound;
        }
        if (this.hasAdvanced || this.hasJob || this.hasRole) {
            NbtCompound compoundAdv = this.getAdvanced();
            NbtCompound compoundRole = this.getRole();
            NbtCompound compoundJob = this.getJob();
            NbtCompound compound2 = this.processAdvanced(compoundAdv, compoundRole, compoundJob);
            this.npc.advanced.readToNBT(compound2);
            if (this.npc.role.getType() != 0) {
                this.npc.role.load(NBTTags.NBTMerge(compoundRole, compound2));
            }
            if (this.npc.job.getType() != 0) {
                this.npc.job.load(NBTTags.NBTMerge(compoundJob, compound2));
            }
            if (this.hasAdvanced) {
                this.advanced = compoundAdv;
            }
            if (this.hasRole) {
                this.role = compoundRole;
            }
            if (this.hasJob) {
                this.job = compoundJob;
            }
        }
        if (this.hasAi) {
            compound = this.npc.ais.save(new NbtCompound());
            this.npc.ais.readToNBT(NBTTags.NBTMerge(compound, this.ai));
            this.ai = compound;
            this.npc.setCurrentAnimation(0);
        }
        if (this.hasInv) {
            compound = this.npc.inventory.save(new NbtCompound());
            this.npc.inventory.load(NBTTags.NBTMerge(compound, this.inv));
            this.inv = compound;
        }
        this.npc.updateAI = true;
        this.isActive = isActive;
        this.npc.updateClient = true;
    }
}

