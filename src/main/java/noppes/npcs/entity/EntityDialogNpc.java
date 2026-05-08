/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ActionResult
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.world.World
 */
package noppes.npcs.entity;

import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import noppes.npcs.CustomEntities;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityDialogNpc
extends EntityNPCInterface {
    public EntityDialogNpc(World world) {
        super(CustomEntities.entityCustomNpc, world);
    }

    @Override
    public boolean isInvisibleTo(PlayerEntity player) {
        return true;
    }

    @Override
    public boolean isInvisible() {
        return true;
    }

    @Override
    public void tick() {
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        return ActionResult.FAIL;
    }
}

