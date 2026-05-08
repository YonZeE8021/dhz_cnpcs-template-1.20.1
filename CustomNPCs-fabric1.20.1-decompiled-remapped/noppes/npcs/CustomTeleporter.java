/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.PortalForcer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.border.WorldBorder
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.world.BlockLocating$Rectangle
 */
package noppes.npcs;

import java.util.Optional;
import net.minecraft.world.PortalForcer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.BlockLocating;

public class CustomTeleporter
extends PortalForcer {
    private float yRot;
    private float xRot;
    private Vec3d pos;

    public CustomTeleporter(ServerWorld par1ServerLevel, Vec3d pos, float yRot, float xRot) {
        super(par1ServerLevel);
        this.pos = pos;
        this.yRot = yRot;
        this.xRot = xRot;
    }

    public Optional<BlockLocating.Rectangle> getPortalRect(BlockPos pos, boolean isNether, WorldBorder border) {
        return Optional.empty();
    }
}

