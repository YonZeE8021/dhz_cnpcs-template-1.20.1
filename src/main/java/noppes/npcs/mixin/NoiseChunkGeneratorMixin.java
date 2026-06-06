/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.world.ChunkRegion
 *  net.minecraft.world.gen.chunk.NoiseChunkGenerator
 *  net.minecraft.world.ServerWorldAccess
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package noppes.npcs.mixin;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.ServerWorldAccess;
import noppes.npcs.NPCSpawning;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={NoiseChunkGenerator.class})
public class NoiseChunkGeneratorMixin {
    @Inject(at={@At(value="HEAD")}, method={"populateEntities"}, cancellable=false)
    private void spawnOriginalMobs(ChunkRegion region, CallbackInfo ci) {
        ChunkPos chunkpos = region.getCenterPos();
        int x = chunkpos.getStartX();
        int z = chunkpos.getStartZ();
        Biome biome = (Biome)region.getBiome(new ChunkPos(x, z).getStartPos()).value();
        NPCSpawning.performLevelGenSpawning((ServerWorldAccess)region, biome, x, z, region.getRandom());
    }
}

