/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.util.Identifier
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package noppes.npcs.mixin;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.Identifier;
import noppes.npcs.client.controllers.ClientSkinController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets={"org.tlauncher.TLSkinCape"})
public class TLSkinCapeMixin {
    @Inject(at={@At(value="RETURN")}, method={"getLocationSkin"}, cancellable=true, remap=false)
    private static void registerSkinTexture(GameProfile profile, CallbackInfoReturnable<Identifier> cir) {
        cir.setReturnValue(((Identifier)MoreObjects.firstNonNull(ClientSkinController.getSkinForPlayer(profile.getName()), ((Identifier)cir.getReturnValue()))));
    }
}

