/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.network.PlayerListEntry
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package noppes.npcs.mixin;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.Identifier;
import net.minecraft.client.network.PlayerListEntry;
import noppes.npcs.client.controllers.ClientSkinController;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={PlayerListEntry.class})
public abstract class NetworkPlayerInfoMixin {
    @Shadow
    @Final
    private GameProfile profile;

    @Inject(at={@At(value="RETURN")}, method={"getSkinLocation"}, cancellable=true)
    public void getSkinLocation(CallbackInfoReturnable<Identifier> cir) {
        cir.setReturnValue((Object)((Identifier)MoreObjects.firstNonNull((Object)ClientSkinController.getSkinForPlayer(this.profile.getName()), (Object)((Identifier)cir.getReturnValue()))));
    }
}

