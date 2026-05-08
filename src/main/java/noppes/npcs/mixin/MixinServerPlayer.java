/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.world.TeleportTarget
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package noppes.npcs.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.TeleportTarget;
import noppes.npcs.CustomItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ServerPlayerEntity.class})
public abstract class MixinServerPlayer
extends PlayerEntity {
    public MixinServerPlayer(World level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Inject(method={"findDimensionEntryPoint"}, at={@At(value="HEAD")}, cancellable=true)
    protected void findDimensionEntryPoint(ServerWorld destination, CallbackInfoReturnable<TeleportTarget> cir) {
        if (this.getMainHandStack().isOf(CustomItems.teleporter)) {
            cir.setReturnValue(new TeleportTarget(this.getPos(), Vec3d.ZERO, this.getYaw(), this.getPitch()));
        }
    }
}

