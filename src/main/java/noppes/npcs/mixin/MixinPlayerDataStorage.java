/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.WorldSaveHandler
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package noppes.npcs.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.WorldSaveHandler;
import noppes.npcs.controllers.data.PlayerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={WorldSaveHandler.class})
public class MixinPlayerDataStorage {
    @Inject(method={"save"}, at={@At(value="TAIL")})
    public void save(PlayerEntity player, CallbackInfo ci) {
        PlayerData.get(player).save(false);
    }
}

