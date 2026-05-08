/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.util.Identifier
 */
package noppes.npcs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import noppes.npcs.ModelEyeData;
import noppes.npcs.ModelPartConfig;
import noppes.npcs.client.parts.MpmPart;
import noppes.npcs.client.parts.MpmPartData;
import noppes.npcs.constants.BodyPart;
import noppes.npcs.constants.EnumParts;

public abstract class ModelDataShared {
    public ModelPartConfig arm1 = new ModelPartConfig();
    public ModelPartConfig arm2 = new ModelPartConfig();
    public ModelPartConfig body = new ModelPartConfig();
    public ModelPartConfig leg1 = new ModelPartConfig();
    public ModelPartConfig leg2 = new ModelPartConfig();
    public ModelPartConfig head = new ModelPartConfig();
    protected Identifier entityName = null;
    protected LivingEntity entity;
    public NbtCompound extra = new NbtCompound();
    public NbtList oldPartData = new NbtList();
    public List<MpmPartData> mpmParts = new ArrayList<MpmPartData>();
    public List<BodyPart> hiddenParts = new ArrayList<BodyPart>();
    public int wingMode = 0;
    public String url = "";
    public String displayName = "";
    public long lastEdited = System.currentTimeMillis();
    public int inLove = 0;
    public int animationTime = -1;
    public int modelType = 0;
    public int moveAnimation = 16;
    public int prevMoveAnimation = 16;
    public boolean startMoveAnimation = false;
    public int animation = 0;
    public int prevAnimation = 0;
    public boolean startAnimation = false;
    public int animationStart = 0;
    public float sleepRotation;

    public NbtCompound save() {
        NbtCompound compound = new NbtCompound();
        if (this.entityName != null) {
            compound.putString("EntityName", this.entityName.toString());
        }
        compound.put("ArmsConfig", (NbtElement)this.arm1.writeToNBT());
        compound.put("Arms2Config", (NbtElement)this.arm2.writeToNBT());
        compound.put("BodyConfig", (NbtElement)this.body.writeToNBT());
        compound.put("LegsConfig", (NbtElement)this.leg1.writeToNBT());
        compound.put("Legs2Config", (NbtElement)this.leg2.writeToNBT());
        compound.put("HeadConfig", (NbtElement)this.head.writeToNBT());
        compound.put("ExtraData", (NbtElement)this.extra);
        compound.putInt("WingMode", this.wingMode);
        compound.putString("CustomSkinUrl", this.url);
        compound.putString("DisplayName", this.displayName);
        compound.putInt("Animation", this.animation);
        compound.putInt("MoveAnimation", this.moveAnimation);
        compound.putInt("ModelType", this.modelType);
        compound.putLong("LastEdited", this.lastEdited);
        compound.put("Parts", (NbtElement)this.oldPartData);
        NbtList list = new NbtList();
        for (MpmPartData e : this.mpmParts) {
            list.add(e.getNbt());
        }
        compound.put("NewParts", (NbtElement)list);
        return compound;
    }

    public void load(NbtCompound compound) {
        int i;
        String rl = compound.getString("EntityName");
        this.setEntity(rl.isEmpty() ? null : new Identifier(rl));
        this.arm1.readFromNBT(compound.getCompound("ArmsConfig"));
        this.arm2.readFromNBT(compound.getCompound("Arms2Config"));
        this.body.readFromNBT(compound.getCompound("BodyConfig"));
        this.leg1.readFromNBT(compound.getCompound("LegsConfig"));
        this.leg2.readFromNBT(compound.getCompound("Legs2Config"));
        this.head.readFromNBT(compound.getCompound("HeadConfig"));
        this.extra = compound.getCompound("ExtraData");
        this.wingMode = compound.getInt("WingMode");
        this.url = compound.getString("CustomSkinUrl");
        this.displayName = compound.getString("DisplayName");
        this.animation = compound.getInt("Animation");
        this.moveAnimation = compound.getInt("MoveAnimation");
        this.modelType = compound.getInt("ModelType");
        this.lastEdited = compound.getLong("LastEdited");
        ArrayList<MpmPartData> mpmParts = new ArrayList<MpmPartData>();
        NbtList list = compound.getList("NewParts", 10);
        for (i = 0; i < list.size(); ++i) {
            MpmPartData part = new MpmPartData();
            part.setNbt(list.getCompound(i));
            if (part.partId.equals(ModelEyeData.RESOURCE) || part.partId.equals(ModelEyeData.RESOURCE_RIGHT) || part.partId.equals(ModelEyeData.RESOURCE_LEFT)) {
                part = new ModelEyeData();
                part.setNbt(list.getCompound(i));
            }
            mpmParts.add(part);
        }
        this.mpmParts = mpmParts;
        this.oldPartData = compound.getList("Parts", 10);
        if (this.mpmParts.isEmpty()) {
            for (i = 0; i < list.size(); ++i) {
                this.mpmParts.add(EnumParts.convertOldPart(list.getCompound(i)));
            }
        }
        this.refreshParts();
        this.updateTransate();
    }

    public void setMoveAnimation(int ani) {
        this.startMoveAnimation = this.moveAnimation != ani;
        this.moveAnimation = ani;
    }

    public int getMoveAnimtion(LivingEntity player) {
        if (player.hasVehicle()) {
            return 1;
        }
        if (player.isSleeping()) {
            return 2;
        }
        if (this.moveAnimation == 16 && player.isInSneakingPose()) {
            return 4;
        }
        return this.moveAnimation;
    }

    public boolean isMovementAnimation(int ani) {
        return ani == 2 || ani == 7 || ani == 4 || ani == 1 || ani == 14 || ani == 15 || ani == 16 || ani == 18 || ani == 17;
    }

    public void setAnimation(int ani) {
        if (this.isMovementAnimation(ani)) {
            this.setMoveAnimation(ani);
            return;
        }
        this.animationTime = -1;
        this.animation = ani;
        this.lastEdited = System.currentTimeMillis();
        boolean bl = this.startAnimation = this.animation != ani;
        if (this.animation == 10) {
            this.animationTime = 80;
        }
        if (this.animation == 13 || this.animation == 12) {
            this.animationTime = 60;
        }
        this.animationStart = this.getOwner() == null || ani == 0 ? -1 : this.getOwner().age;
    }

    public void updateTransate() {
        for (EnumParts part : EnumParts.values()) {
            float y;
            float x;
            ModelPartConfig body;
            ModelPartConfig config = this.getPartConfig(part);
            if (config == null) continue;
            if (part == EnumParts.HEAD) {
                config.setTranslate(0.0f, this.getBodyY(), 0.0f);
                continue;
            }
            if (part == EnumParts.ARM_LEFT) {
                body = this.getPartConfig(EnumParts.BODY);
                x = (1.0f - body.scaleX) * 0.25f + (1.0f - config.scaleX) * 0.0625f;
                y = this.getBodyY() + (1.0f - config.scaleY) * -0.125f;
                config.setTranslate(-x, y, 0.0f);
                if (config.notShared) continue;
                ModelPartConfig arm = this.getPartConfig(EnumParts.ARM_RIGHT);
                arm.copyValues(config);
                continue;
            }
            if (part == EnumParts.ARM_RIGHT) {
                body = this.getPartConfig(EnumParts.BODY);
                x = (1.0f - body.scaleX) * 0.25f + (1.0f - config.scaleX) * 0.0625f;
                y = this.getBodyY() + (1.0f - config.scaleY) * -0.125f;
                config.setTranslate(x, y, 0.0f);
                continue;
            }
            if (part == EnumParts.LEG_LEFT) {
                config.setTranslate(-(1.0f - config.scaleX) * 0.118f, this.getLegsY(), -(1.0f - config.scaleZ) * 0.00625f);
                if (config.notShared) continue;
                ModelPartConfig leg = this.getPartConfig(EnumParts.LEG_RIGHT);
                leg.copyValues(config);
                continue;
            }
            if (part == EnumParts.LEG_RIGHT) {
                config.setTranslate((1.0f - config.scaleX) * 0.118f, this.getLegsY(), -(1.0f - config.scaleZ) * 0.00625f);
                continue;
            }
            if (part != EnumParts.BODY) continue;
            config.setTranslate(0.0f, this.getBodyY(), 0.0f);
        }
    }

    public void setEntity(Identifier resourceLocation) {
        this.entityName = resourceLocation;
        this.clearEntity();
        this.extra = new NbtCompound();
    }

    public Identifier getEntityName() {
        return this.entityName;
    }

    public boolean hasEntity() {
        return this.entityName != null;
    }

    public float offsetY() {
        if (this.entity == null) {
            return -this.getBodyY();
        }
        return this.entity.getHeight() - 1.8f;
    }

    public void clearEntity() {
        this.entity = null;
    }

    public ModelPartConfig getPartConfig(EnumParts type) {
        if (type == EnumParts.BODY) {
            return this.body;
        }
        if (type == EnumParts.ARM_LEFT) {
            return this.arm1;
        }
        if (type == EnumParts.ARM_RIGHT) {
            return this.arm2;
        }
        if (type == EnumParts.LEG_LEFT) {
            return this.leg1;
        }
        if (type == EnumParts.LEG_RIGHT) {
            return this.leg2;
        }
        return this.head;
    }

    public abstract LivingEntity getOwner();

    public float getBodyY() {
        if (this.entity != null) {
            return this.entity.getHeight();
        }
        return (1.0f - this.body.scaleY) * 0.75f + this.getLegsY();
    }

    public float getLegsY() {
        ModelPartConfig legs = this.leg1;
        if (this.leg1.notShared && this.leg2.scaleY > this.leg1.scaleY) {
            legs = this.leg2;
        }
        return (1.0f - legs.scaleY) * 0.75f;
    }

    public void refreshParts() {
        this.hiddenParts = this.mpmParts.stream().flatMap(part -> {
            MpmPart p = part.getPart();
            if (p != null) {
                return p.hiddenParts.stream();
            }
            return Stream.empty();
        }).distinct().collect(Collectors.toList());
    }
}

