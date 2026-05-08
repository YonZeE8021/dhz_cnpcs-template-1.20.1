/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.block.VineBlock
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
import net.minecraft.block.VineBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import noppes.npcs.CustomNpcs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={VineBlock.class})
public class VineBlockMixin {
    @Inject(at={@At(value="HEAD")}, method={"randomTick"}, cancellable=true)
    private void setupAnimPre(BlockState p_222655_, ServerWorld p_222656_, BlockPos p_222657_, Random p_222658_, CallbackInfo ci) {
        if (!CustomNpcs.VineGrowthEnabled) {
            ci.cancel();
        }
    }
}

