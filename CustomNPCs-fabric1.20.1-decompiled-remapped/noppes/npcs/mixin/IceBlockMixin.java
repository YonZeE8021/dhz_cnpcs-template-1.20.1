/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.block.IceBlock
 *  net.minecraft.block.BlockState
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.random.Random
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package noppes.npcs.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.block.IceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import noppes.npcs.CustomNpcs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={IceBlock.class})
public class IceBlockMixin {
    @Inject(at={@At(value="HEAD")}, method={"randomTick"}, cancellable=true)
    private void setupAnimPre(BlockState p_221355_, ServerWorld p_221356_, BlockPos p_221357_, Random p_221358_, CallbackInfo ci) {
        if (!CustomNpcs.IceMeltsEnabled) {
            ci.cancel();
        }
    }
}

