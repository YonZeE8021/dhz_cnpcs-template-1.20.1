/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.util.math.MatrixStack
 */
package noppes.npcs.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.util.math.MatrixStack;
import noppes.npcs.ModelPartConfig;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.shared.client.model.NopModelPart;

public class ModelScaleRenderer
extends NopModelPart {
    public boolean isCompiled;
    public int displayList;
    public ModelPartConfig config;
    public EnumParts part;

    public ModelScaleRenderer(Model base, EnumParts part) {
        this(base, 0, 0, part);
    }

    public ModelScaleRenderer(Model par1Model, int par2, int par3, EnumParts part) {
        super(64, 64, par2, par3);
        this.part = part;
    }

    @Override
    public void translateAndRotate(MatrixStack mStack) {
        if (this.config != null) {
            mStack.translate(this.config.transX, this.config.transY, this.config.transZ);
        }
        super.translateAndRotate(mStack);
        if (this.config != null) {
            mStack.scale(this.config.scaleX, this.config.scaleY, this.config.scaleZ);
        }
    }
}

