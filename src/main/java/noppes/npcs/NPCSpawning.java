/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.minecraft.entity.ai.pathing.NavigationType
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.world.BlockView
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.world.WorldAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.LightType
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$Mutable
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.Box
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.block.BlockState
 *  net.minecraft.world.chunk.WorldChunk
 *  net.minecraft.world.Heightmap$Type
 *  net.minecraft.util.Identifier
 *  net.minecraft.server.world.ChunkHolder
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.entity.SpawnReason
 *  net.minecraft.server.world.ThreadedAnvilChunkStorage
 *  net.minecraft.world.WorldView
 *  net.minecraft.world.ServerWorldAccess
 *  net.minecraft.world.entity.SectionedEntityCache
 *  net.minecraft.util.TypeFilter
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.util.function.LazyIterationConsumer$NextIteration
 */
package noppes.npcs;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Box;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.Heightmap;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.WorldView;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.entity.SectionedEntityCache;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.random.Random;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.function.LazyIterationConsumer;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomNpcs;
import noppes.npcs.controllers.SpawnController;
import noppes.npcs.controllers.data.SpawnData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.ChunkMapMixin;
import noppes.npcs.mixin.PersistentEntitySectionManagerMixin;
import noppes.npcs.mixin.ServerLevelMixin;

public class NPCSpawning {
    public static void findChunksForSpawning(ServerWorld level) {
        ChunkHolder chunkHolder;
        WorldChunk levelchunk;
        if (SpawnController.instance.data.isEmpty() || level.getTime() % 400L != 0L) {
            return;
        }
        SectionedEntityCache sectionManager = ((PersistentEntitySectionManagerMixin)((ServerLevelMixin)level).entityManager()).sectionStorage();
        ThreadedAnvilChunkStorage chunkManager = level.getChunkManager().threadedAnvilChunkStorage;
        ArrayList list = new ArrayList(((ChunkMapMixin)chunkManager).visibleChunkMap().values());
        Collections.shuffle(list);
        Iterator iterator = list.iterator();
        while (iterator.hasNext() && (levelchunk = (chunkHolder = (ChunkHolder)iterator.next()).getWorldChunk()) != null) {
            ChunkPos pos = levelchunk.getPos();
            Biome biome = (Biome)level.getBiome(pos.getStartPos()).value();
            if (!SpawnController.instance.hasSpawnList(level.getRegistryManager().get(RegistryKeys.BIOME).getId(biome))) continue;
            Box bb = new Box((double)pos.getStartX(), 0.0, (double)pos.getStartZ(), (double)pos.getEndX(), (double)level.getTopY(), (double)pos.getEndZ());
            ArrayList entities = Lists.newArrayList();
            sectionManager.forEachIntersects((TypeFilter)EntityType.PLAYER, bb.expand(4.0), e -> {
                entities.add(e);
                return LazyIterationConsumer.NextIteration.CONTINUE;
            });
            if (!entities.isEmpty()) continue;
            sectionManager.forEachIntersects(CustomEntities.entityCustomNpc, bb, e -> {
                entities.add(e);
                return LazyIterationConsumer.NextIteration.CONTINUE;
            });
            if (entities.size() >= CustomNpcs.NpcNaturalSpawningChunkLimit) continue;
            NPCSpawning.spawnChunk(level, levelchunk);
        }
    }

    private static void spawnChunk(ServerWorld level, WorldChunk chunk) {
        BlockPos chunkposition = NPCSpawning.getChunk((World)level, chunk);
        int j1 = chunkposition.getX();
        int k1 = chunkposition.getY();
        int l1 = chunkposition.getZ();
        for (int i = 0; i < 3; ++i) {
            int x = j1;
            int y = k1;
            int z = l1;
            int b1 = 6;
            BlockPos pos = new BlockPos(x += level.random.nextInt(b1) - level.random.nextInt(b1), y, z += level.random.nextInt(b1) - level.random.nextInt(b1));
            Identifier name = level.getRegistryManager().get(RegistryKeys.BIOME).getId(level.getBiome(pos).value());
            SpawnData data = SpawnController.instance.getRandomSpawnData(name);
            if (data == null || data.data.isEmpty() || !NPCSpawning.canCreatureTypeSpawnAtLocation(data, (WorldView)level, pos)) continue;
            NPCSpawning.spawnData(data, (ServerWorldAccess)level, pos);
        }
    }

    public static int countNPCs(ServerWorld level) {
        int count = 0;
        Iterable<Entity> list = level.iterateEntities();
        for (Entity entity : list) {
            if (!(entity instanceof EntityNPCInterface)) continue;
            ++count;
        }
        return count;
    }

    private static BlockPos getChunk(World level, WorldChunk chunk) {
        ChunkPos chunkpos = chunk.getPos();
        int i = chunkpos.getStartX() + level.random.nextInt(16);
        int j = chunkpos.getStartZ() + level.random.nextInt(16);
        int k = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, i, j) + 1;
        int l = level.random.nextInt(Math.max(k + 1, 1));
        return new BlockPos(i, l, j);
    }

    public static void performLevelGenSpawning(ServerWorldAccess level, Biome biome, int x, int z, Random rand) {
        if (biome.getSpawnSettings().getCreatureSpawnProbability() >= 1.0f || biome.getSpawnSettings().getCreatureSpawnProbability() < 0.0f || !SpawnController.instance.hasSpawnList(level.getRegistryManager().get(RegistryKeys.BIOME).getId(biome))) {
            return;
        }
        int tries = 0;
        block0: while (rand.nextFloat() < biome.getSpawnSettings().getCreatureSpawnProbability() && ++tries <= 20) {
            SpawnData data = SpawnController.instance.getRandomSpawnData(level.getRegistryManager().get(RegistryKeys.BIOME).getId(biome));
            int size = 16;
            int j1 = x + rand.nextInt(size);
            int k1 = z + rand.nextInt(size);
            int l1 = j1;
            int i2 = k1;
            for (int k2 = 0; k2 < 4; ++k2) {
                BlockPos pos = NPCSpawning.getTopNonCollidingPos((WorldView)level, CustomEntities.entityCustomNpc, j1, k1);
                if (!NPCSpawning.canCreatureTypeSpawnAtLocation(data, (WorldView)level, pos)) {
                    j1 += rand.nextInt(5) - rand.nextInt(5);
                    k1 += rand.nextInt(5) - rand.nextInt(5);
                    while (j1 < x || j1 >= x + size || k1 < z || k1 >= z + size) {
                        j1 = l1 + rand.nextInt(5) - rand.nextInt(5);
                        k1 = i2 + rand.nextInt(5) - rand.nextInt(5);
                    }
                    continue;
                }
                if (NPCSpawning.spawnData(data, level, pos)) continue block0;
            }
        }
    }

    private static boolean spawnData(SpawnData data, ServerWorldAccess level, BlockPos pos) {
        MobEntity entityliving;
        try {
            NbtCompound nbt = data.getCompound(1);
            if (nbt == null) {
                return false;
            }
            Entity entity = EntityType.getEntityFromNbt((NbtCompound)nbt, (World)level.toServerWorld()).orElse(null);
            if (entity == null || !(entity instanceof MobEntity)) {
                return false;
            }
            entityliving = (MobEntity)entity;
            if (entity instanceof EntityCustomNpc) {
                EntityCustomNpc npc = (EntityCustomNpc)entity;
                npc.stats.spawnCycle = 4;
                npc.stats.respawnTime = 0;
                npc.ais.returnToStart = false;
                npc.ais.setStartPos(pos);
            }
            entity.refreshPositionAndAngles((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, level.getRandom().nextFloat() * 360.0f, 0.0f);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        if (!entityliving.canSpawn((WorldAccess)level, SpawnReason.NATURAL) || !entityliving.canSpawn((WorldView)level)) {
            return false;
        }
        level.getServer().submit(() -> level.spawnEntity((Entity)entityliving));
        return true;
    }

    public static float getLightLevel(WorldView level, BlockPos pos) {
        int blockLight = level.getLightLevel(LightType.BLOCK, pos);
        int skyLight = level.getLightLevel(LightType.SKY, pos);
        int skyDarken = level.getAmbientDarkness();
        float skyLightValue = (11.0f - (float)skyDarken) * 15.0f / 11.0f;
        return Math.max((float)blockLight, (float)skyLight / 15.0f * skyLightValue);
    }

    public static boolean canCreatureTypeSpawnAtLocation(SpawnData data, WorldView level, BlockPos pos) {
        if (!level.getWorldBorder().contains(pos) || !level.isSpaceEmpty(CustomEntities.entityCustomNpc.createSimpleBoundingBox((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()))) {
            return false;
        }
        if (data.type == 1 && NPCSpawning.getLightLevel(level, pos) > 8.0f || data.type == 2 && NPCSpawning.getLightLevel(level, pos) <= 8.0f) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (data.liquid) {
            return state.isLiquid() && level.getBlockState(pos.down()).isLiquid() && !level.getBlockState(pos.up()).isSolidBlock((BlockView)level, pos.up());
        }
        BlockPos blockpos1 = pos.down();
        BlockState state1 = level.getBlockState(blockpos1);
        Block block1 = state1.getBlock();
        boolean flag = block1 != Blocks.BEDROCK && block1 != Blocks.BARRIER;
        BlockPos down = blockpos1.down();
        return (flag |= level.getBlockState(down).allowsSpawning((BlockView)level, down, CustomEntities.entityCustomNpc)) && !state.emitsRedstonePower() && !state.isLiquid() && !level.getBlockState(pos.up()).emitsRedstonePower();
    }

    private static BlockPos getTopNonCollidingPos(WorldView p_208498_0_, EntityType<?> p_208498_1_, int p_208498_2_, int p_208498_3_) {
        BlockPos blockpos;
        int i = p_208498_0_.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, p_208498_2_, p_208498_3_);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_208498_2_, i, p_208498_3_);
        if (p_208498_0_.getDimension().hasCeiling()) {
            do {
                blockpos$mutable.move(Direction.DOWN);
            } while (!p_208498_0_.getBlockState((BlockPos)blockpos$mutable).isAir());
            do {
                blockpos$mutable.move(Direction.DOWN);
            } while (p_208498_0_.getBlockState((BlockPos)blockpos$mutable).isAir() && blockpos$mutable.getY() > 0);
        }
        if (p_208498_0_.getBlockState(blockpos = blockpos$mutable.down()).canPathfindThrough((BlockView)p_208498_0_, blockpos, NavigationType.LAND)) {
            return blockpos;
        }
        return blockpos$mutable.toImmutable();
    }
}

