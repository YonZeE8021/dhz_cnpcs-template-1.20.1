/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.util.math.MathHelper
 */
package noppes.npcs.roles;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.entity.data.role.IJobPuppet;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobInterface;
import noppes.npcs.util.ValueUtil;

public class JobPuppet
extends JobInterface
implements IJobPuppet {
    public PartConfig head = new PartConfig();
    public PartConfig larm = new PartConfig();
    public PartConfig rarm = new PartConfig();
    public PartConfig body = new PartConfig();
    public PartConfig lleg = new PartConfig();
    public PartConfig rleg = new PartConfig();
    public PartConfig head2 = new PartConfig();
    public PartConfig larm2 = new PartConfig();
    public PartConfig rarm2 = new PartConfig();
    public PartConfig body2 = new PartConfig();
    public PartConfig lleg2 = new PartConfig();
    public PartConfig rleg2 = new PartConfig();
    public boolean whileStanding = true;
    public boolean whileAttacking = false;
    public boolean whileMoving = false;
    public boolean animate = false;
    public int animationSpeed = 4;
    private int prevTicks = 0;
    private int startTick = 0;
    private float val = 0.0f;
    private float valNext = 0.0f;

    public JobPuppet(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public IJobPuppet.IJobPuppetPart getPart(int part) {
        if (part == 0) {
            return this.head;
        }
        if (part == 1) {
            return this.larm;
        }
        if (part == 2) {
            return this.rarm;
        }
        if (part == 3) {
            return this.body;
        }
        if (part == 4) {
            return this.lleg;
        }
        if (part == 5) {
            return this.rleg;
        }
        if (part == 6) {
            return this.head2;
        }
        if (part == 7) {
            return this.larm2;
        }
        if (part == 8) {
            return this.rarm2;
        }
        if (part == 9) {
            return this.body2;
        }
        if (part == 10) {
            return this.lleg2;
        }
        if (part == 11) {
            return this.rleg2;
        }
        throw new CustomNPCsException("Unknown part " + part, new Object[0]);
    }

    @Override
    public NbtCompound save(NbtCompound compound) {
        compound.put("PuppetHead", (NbtElement)this.head.writeNBT());
        compound.put("PuppetLArm", (NbtElement)this.larm.writeNBT());
        compound.put("PuppetRArm", (NbtElement)this.rarm.writeNBT());
        compound.put("PuppetBody", (NbtElement)this.body.writeNBT());
        compound.put("PuppetLLeg", (NbtElement)this.lleg.writeNBT());
        compound.put("PuppetRLeg", (NbtElement)this.rleg.writeNBT());
        compound.put("PuppetHead2", (NbtElement)this.head2.writeNBT());
        compound.put("PuppetLArm2", (NbtElement)this.larm2.writeNBT());
        compound.put("PuppetRArm2", (NbtElement)this.rarm2.writeNBT());
        compound.put("PuppetBody2", (NbtElement)this.body2.writeNBT());
        compound.put("PuppetLLeg2", (NbtElement)this.lleg2.writeNBT());
        compound.put("PuppetRLeg2", (NbtElement)this.rleg2.writeNBT());
        compound.putBoolean("PuppetStanding", this.whileStanding);
        compound.putBoolean("PuppetAttacking", this.whileAttacking);
        compound.putBoolean("PuppetMoving", this.whileMoving);
        compound.putBoolean("PuppetAnimate", this.animate);
        compound.putInt("PuppetAnimationSpeed", this.animationSpeed);
        return compound;
    }

    @Override
    public void load(NbtCompound compound) {
        this.head.readNBT(compound.getCompound("PuppetHead"));
        this.larm.readNBT(compound.getCompound("PuppetLArm"));
        this.rarm.readNBT(compound.getCompound("PuppetRArm"));
        this.body.readNBT(compound.getCompound("PuppetBody"));
        this.lleg.readNBT(compound.getCompound("PuppetLLeg"));
        this.rleg.readNBT(compound.getCompound("PuppetRLeg"));
        this.head2.readNBT(compound.getCompound("PuppetHead2"));
        this.larm2.readNBT(compound.getCompound("PuppetLArm2"));
        this.rarm2.readNBT(compound.getCompound("PuppetRArm2"));
        this.body2.readNBT(compound.getCompound("PuppetBody2"));
        this.lleg2.readNBT(compound.getCompound("PuppetLLeg2"));
        this.rleg2.readNBT(compound.getCompound("PuppetRLeg2"));
        this.whileStanding = compound.getBoolean("PuppetStanding");
        this.whileAttacking = compound.getBoolean("PuppetAttacking");
        this.whileMoving = compound.getBoolean("PuppetMoving");
        this.setIsAnimated(compound.getBoolean("PuppetAnimate"));
        this.setAnimationSpeed(compound.getInt("PuppetAnimationSpeed"));
    }

    @Override
    public boolean aiShouldExecute() {
        return false;
    }

    private float calcRotation(float r, float r2, float partialTicks) {
        if (!this.animate) {
            return r;
        }
        if (this.prevTicks != this.npc.age) {
            float speed = 0.0f;
            if (this.animationSpeed == 0) {
                speed = 40.0f;
            } else if (this.animationSpeed == 1) {
                speed = 24.0f;
            } else if (this.animationSpeed == 2) {
                speed = 13.0f;
            } else if (this.animationSpeed == 3) {
                speed = 10.0f;
            } else if (this.animationSpeed == 4) {
                speed = 7.0f;
            } else if (this.animationSpeed == 5) {
                speed = 4.0f;
            } else if (this.animationSpeed == 6) {
                speed = 3.0f;
            } else if (this.animationSpeed == 7) {
                speed = 2.0f;
            }
            int ticks = this.npc.age - this.startTick;
            this.val = 1.0f - (MathHelper.cos((float)((float)ticks / speed * (float)Math.PI / 2.0f)) + 1.0f) / 2.0f;
            this.valNext = 1.0f - (MathHelper.cos((float)((float)(ticks + 1) / speed * (float)Math.PI / 2.0f)) + 1.0f) / 2.0f;
            this.prevTicks = this.npc.age;
        }
        float f = this.val + (this.valNext - this.val) * partialTicks;
        return r + (r2 - r) * f;
    }

    public float getRotationX(PartConfig part1, PartConfig part2, float partialTicks) {
        return this.calcRotation(part1.rotationX, part2.rotationX, partialTicks);
    }

    public float getRotationY(PartConfig part1, PartConfig part2, float partialTicks) {
        return this.calcRotation(part1.rotationY, part2.rotationY, partialTicks);
    }

    public float getRotationZ(PartConfig part1, PartConfig part2, float partialTicks) {
        return this.calcRotation(part1.rotationZ, part2.rotationZ, partialTicks);
    }

    @Override
    public void reset() {
        this.val = 0.0f;
        this.valNext = 0.0f;
        this.prevTicks = 0;
        this.startTick = this.npc.age;
    }

    @Override
    public void delete() {
    }

    public boolean isActive() {
        if (!this.npc.isAlive()) {
            return false;
        }
        return this.whileAttacking && this.npc.isAttacking() || this.whileMoving && this.npc.isWalking() || this.whileStanding && !this.npc.isWalking();
    }

    @Override
    public boolean getIsAnimated() {
        return this.animate;
    }

    @Override
    public void setIsAnimated(boolean bo) {
        this.animate = bo;
        if (!bo) {
            this.val = 0.0f;
            this.valNext = 0.0f;
            this.prevTicks = 0;
        } else {
            this.startTick = this.npc.age;
        }
        this.npc.updateClient = true;
    }

    @Override
    public int getAnimationSpeed() {
        return this.animationSpeed;
    }

    @Override
    public void setAnimationSpeed(int speed) {
        this.animationSpeed = ValueUtil.CorrectInt(speed, 0, 7);
        this.npc.updateClient = true;
    }

    @Override
    public int getType() {
        return 9;
    }

    public class PartConfig
    implements IJobPuppet.IJobPuppetPart {
        public float rotationX = 0.0f;
        public float rotationY = 0.0f;
        public float rotationZ = 0.0f;
        public boolean disabled = false;

        public NbtCompound writeNBT() {
            NbtCompound compound = new NbtCompound();
            compound.putFloat("RotationX", this.rotationX);
            compound.putFloat("RotationY", this.rotationY);
            compound.putFloat("RotationZ", this.rotationZ);
            compound.putBoolean("Disabled", this.disabled);
            return compound;
        }

        public void readNBT(NbtCompound compound) {
            this.rotationX = ValueUtil.correctFloat(compound.getFloat("RotationX"), -1.0f, 1.0f);
            this.rotationY = ValueUtil.correctFloat(compound.getFloat("RotationY"), -1.0f, 1.0f);
            this.rotationZ = ValueUtil.correctFloat(compound.getFloat("RotationZ"), -1.0f, 1.0f);
            this.disabled = compound.getBoolean("Disabled");
        }

        @Override
        public int getRotationX() {
            return (int)((this.rotationX + 1.0f) * 180.0f);
        }

        @Override
        public int getRotationY() {
            return (int)((this.rotationY + 1.0f) * 180.0f);
        }

        @Override
        public int getRotationZ() {
            return (int)((this.rotationZ + 1.0f) * 180.0f);
        }

        @Override
        public void setRotation(int x, int y, int z) {
            this.disabled = false;
            this.rotationX = ValueUtil.correctFloat((float)x / 180.0f - 1.0f, -1.0f, 1.0f);
            this.rotationY = ValueUtil.correctFloat((float)y / 180.0f - 1.0f, -1.0f, 1.0f);
            this.rotationZ = ValueUtil.correctFloat((float)z / 180.0f - 1.0f, -1.0f, 1.0f);
            JobPuppet.this.npc.updateClient = true;
        }
    }
}

