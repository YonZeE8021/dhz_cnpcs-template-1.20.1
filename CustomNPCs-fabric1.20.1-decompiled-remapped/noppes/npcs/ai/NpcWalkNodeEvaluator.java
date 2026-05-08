/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.mob.MobEntity
 *  net.minecraft.entity.ai.pathing.LandPathNodeMaker
 *  net.minecraft.world.chunk.ChunkCache
 *  net.minecraft.entity.ai.pathing.PathNodeType
 */
package noppes.npcs.ai;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.world.chunk.ChunkCache;
import net.minecraft.entity.ai.pathing.PathNodeType;

public class NpcWalkNodeEvaluator
extends LandPathNodeMaker {
    public PathNodeType getNodeType(MobEntity p_77568_, int p_77569_, int p_77570_, int p_77571_) {
        return super.getNodeType(p_77568_, p_77569_, p_77570_, p_77571_);
    }

    public void clear() {
        ChunkCache level = this.cachedWorld;
        MobEntity mob = this.entity;
        super.clear();
        this.cachedWorld = level;
        this.entity = mob;
    }
}

