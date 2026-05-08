/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package noppes.npcs.mixin;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Identifier;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import noppes.npcs.client.controllers.ClientSkinController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={AbstractClientPlayerEntity.class}, priority=1001)
public abstract class AbstractClientPlayerEntityMixin
extends PlayerEntity {
    public AbstractClientPlayerEntityMixin(World p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @Inject(at={@At(value="RETURN")}, method={"getSkinTextureLocation"}, cancellable=true)
    public void getSkinLocation(CallbackInfoReturnable<Identifier> cir) {
        cir.setReturnValue(((Identifier)MoreObjects.firstNonNull(ClientSkinController.getSkinForPlayer(this.getDisplayName().getString()), ((Identifier)cir.getReturnValue()))));
    }
}

