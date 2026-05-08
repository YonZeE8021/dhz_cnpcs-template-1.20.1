/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelPart
 */
package noppes.npcs.client.parts;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.ModelPart;
import noppes.npcs.client.parts.AnimationContainer;
import noppes.npcs.shared.client.model.NopModelPart;
import noppes.npcs.shared.common.util.NopVector3f;

public class ModelPartWrapper {
    protected ModelPart mcPart = null;
    protected NopModelPart mpmPart = null;
    public final NopVector3f oriPos;
    public final NopVector3f oriRot;
    public final NopVector3f oriScale;
    public Map<Integer, AnimationContainer> animations = new HashMap<Integer, AnimationContainer>();

    public ModelPartWrapper(ModelPart mcPart, NopVector3f oriPos, NopVector3f oriRot) {
        this.mcPart = mcPart;
        this.oriRot = oriRot;
        this.oriPos = oriPos;
        this.oriScale = new NopVector3f(1.0f, 1.0f, 1.0f);
    }

    public ModelPartWrapper(NopModelPart mpmPart, NopVector3f oriPos, NopVector3f oriRot) {
        this.mpmPart = mpmPart;
        this.oriRot = oriRot;
        this.oriPos = oriPos;
        this.oriScale = new NopVector3f(1.0f, 1.0f, 1.0f);
    }

    public NopVector3f getPos() {
        if (this.mcPart != null) {
            return new NopVector3f(this.mcPart.pivotX, this.mcPart.pivotY, this.mcPart.pivotZ);
        }
        return new NopVector3f(this.mpmPart.x, this.mpmPart.y, this.mpmPart.z);
    }

    public void setPos(NopVector3f pos) {
        if (this.mcPart != null) {
            this.mcPart.setPivot(pos.x, pos.y, pos.z);
        } else {
            this.mpmPart.setPos(pos.x, pos.y, pos.z);
        }
    }

    public NopVector3f getRot() {
        if (this.mcPart != null) {
            return new NopVector3f(this.mcPart.pitch, this.mcPart.yaw, this.mcPart.roll);
        }
        return new NopVector3f(this.mpmPart.xRot, this.mpmPart.yRot, this.mpmPart.zRot);
    }

    public void setRot(NopVector3f rot) {
        if (this.mcPart != null) {
            this.mcPart.setAngles(rot.x, rot.y, rot.z);
        } else {
            this.mpmPart.setRotation(rot);
        }
    }

    public NopVector3f getScale() {
        if (this.mcPart != null) {
            return new NopVector3f(this.mcPart.xScale, this.mcPart.yScale, this.mcPart.zScale);
        }
        return this.mpmPart.scale;
    }

    public void setScale(NopVector3f scale) {
        if (this.mcPart != null) {
            this.mcPart.xScale = scale.x;
            this.mcPart.yScale = scale.y;
            this.mcPart.zScale = scale.z;
        } else {
            this.mpmPart.scale = scale;
        }
    }

    public void setVisible(boolean b) {
        if (this.mcPart != null) {
            this.mcPart.visible = b;
        } else {
            this.mpmPart.visible = b;
        }
    }
}

