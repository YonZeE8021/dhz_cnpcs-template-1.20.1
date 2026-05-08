/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap
 *  net.minecraft.server.world.ChunkHolder
 *  net.minecraft.server.world.PlayerChunkWatchingManager
 *  net.minecraft.server.world.ThreadedAnvilChunkStorage
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package noppes.npcs.mixin;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.PlayerChunkWatchingManager;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={ThreadedAnvilChunkStorage.class})
public interface ChunkMapMixin {
    @Accessor(value="visibleChunkMap")
    public Long2ObjectLinkedOpenHashMap<ChunkHolder> visibleChunkMap();

    @Accessor(value="playerMap")
    public PlayerChunkWatchingManager playerMap();
}

