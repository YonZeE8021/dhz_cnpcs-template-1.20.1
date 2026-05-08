/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.mob.PathAwareEntity
 *  net.minecraft.world.World
 */
package noppes.npcs.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import noppes.npcs.entity.EntityCustomNpc;

public class EntityNPC64x32
extends EntityCustomNpc {
    public EntityNPC64x32(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.display.setSkinTexture("customnpcs:textures/entity/humanmale/steve64x32.png");
    }
}

