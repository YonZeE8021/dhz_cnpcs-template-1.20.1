/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.MobSpawnerLogic
 *  net.minecraft.world.World
 *  net.minecraft.world.MobSpawnerEntry
 *  net.minecraft.util.math.BlockPos
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 */
package noppes.npcs.mixin;

import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value={MobSpawnerLogic.class})
public interface BaseSpawnerMixin {
    @Invoker("setSpawnEntry")
    public void callSetNextSpawnData(World var1, BlockPos var2, MobSpawnerEntry var3);
}

