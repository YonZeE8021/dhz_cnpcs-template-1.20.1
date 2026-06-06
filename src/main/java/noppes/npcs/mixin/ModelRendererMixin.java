/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.model.ModelPart
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package noppes.npcs.mixin;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.model.ModelPart;
import noppes.npcs.ModelPartConfig;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.constants.EnumParts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ModelPart.class})
public class ModelRendererMixin {
    public ModelPartConfig cnpcconfig;

    @Inject(at={@At(value="HEAD")}, method={"rotate"})
    private void translateAndRotatePre(MatrixStack mStack, CallbackInfo callbackInfo) {
        this.cnpcconfig = this.getCnpcconfig();
        if (this.cnpcconfig != null) {
            mStack.translate(this.cnpcconfig.transX, this.cnpcconfig.transY, this.cnpcconfig.transZ);
        }
    }

    @Inject(at={@At(value="TAIL")}, method={"rotate"})
    private void translateAndRotatePost(MatrixStack mStack, CallbackInfo callbackInfo) {
        this.cnpcconfig = this.getCnpcconfig();
        if (this.cnpcconfig != null) {
            mStack.scale(this.cnpcconfig.scaleX, this.cnpcconfig.scaleY, this.cnpcconfig.scaleZ);
        }
    }

    private ModelPartConfig getCnpcconfig() {
        if (ClientProxy.data == null) {
            return null;
        }
        ModelPart model = (ModelPart)(Object)this;
        if (model == ClientProxy.playerModel.body || model == ClientProxy.playerModel.jacket || model == ((BipedEntityModel)ClientProxy.armorLayer.getOuter()).body || model == ((BipedEntityModel)ClientProxy.armorLayer.getInner()).body) {
            return ClientProxy.data.getPartConfig(EnumParts.BODY);
        }
        if (model == ClientProxy.playerModel.head || model == ClientProxy.playerModel.hat || model == ((BipedEntityModel)ClientProxy.armorLayer.getOuter()).head) {
            return ClientProxy.data.getPartConfig(EnumParts.HEAD);
        }
        if (model == ClientProxy.playerModel.leftLeg || model == ClientProxy.playerModel.leftPants || model == ((BipedEntityModel)ClientProxy.armorLayer.getOuter()).leftLeg || model == ((BipedEntityModel)ClientProxy.armorLayer.getInner()).leftLeg) {
            return ClientProxy.data.getPartConfig(EnumParts.LEG_LEFT);
        }
        if (model == ClientProxy.playerModel.rightLeg || model == ClientProxy.playerModel.rightPants || model == ((BipedEntityModel)ClientProxy.armorLayer.getOuter()).rightLeg || model == ((BipedEntityModel)ClientProxy.armorLayer.getInner()).rightLeg) {
            return ClientProxy.data.getPartConfig(EnumParts.LEG_RIGHT);
        }
        if (model == ClientProxy.playerModel.leftArm || model == ClientProxy.playerModel.leftSleeve || model == ((BipedEntityModel)ClientProxy.armorLayer.getOuter()).leftArm) {
            return ClientProxy.data.getPartConfig(EnumParts.ARM_LEFT);
        }
        if (model == ClientProxy.playerModel.rightArm || model == ClientProxy.playerModel.rightSleeve || model == ((BipedEntityModel)ClientProxy.armorLayer.getOuter()).rightArm) {
            return ClientProxy.data.getPartConfig(EnumParts.ARM_RIGHT);
        }
        return null;
    }
}

