/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.World
 *  net.minecraft.world.dimension.DimensionType
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.util.ProgressListener
 *  net.minecraft.util.profiler.Profiler
 *  net.minecraft.world.MutableWorldProperties
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.DynamicRegistryManager
 *  net.minecraft.registry.entry.RegistryEntry
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package noppes.npcs.mixin;

import java.util.function.Supplier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntry;
import noppes.npcs.controllers.ScriptController;
import noppes.npcs.controllers.data.PlayerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ServerWorld.class})
public abstract class MixinServerLevel
extends World {
    protected MixinServerLevel(MutableWorldProperties levelData, RegistryKey<World> dimension, DynamicRegistryManager registryAccess, RegistryEntry<DimensionType> dimensionTypeRegistration, Supplier<Profiler> profiler, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, profiler, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Inject(method={"addPlayer"}, at={@At(value="HEAD")})
    private void addPlayer(ServerPlayerEntity player, CallbackInfo ci) {
        PlayerData data = PlayerData.get((PlayerEntity)player);
        data.updateCompanion(this);
    }

    @Inject(method={"save"}, at={@At(value="TAIL")})
    public void save(ProgressListener progress, boolean flush, boolean skipSave, CallbackInfo ci) {
        if (!skipSave) {
            ScriptController.Instance.saveLevel(this);
        }
    }
}

