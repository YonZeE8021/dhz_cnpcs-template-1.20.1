/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.util.math.MathHelper
 */
package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.controllers.ChunkController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.JobInterface;

public class JobChunkLoader
extends JobInterface {
    private List<ChunkPos> chunks = new ArrayList<ChunkPos>();
    private int ticks = 20;
    private long playerLastSeen = -1L;

    public JobChunkLoader(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public NbtCompound save(NbtCompound compound) {
        compound.putLong("ChunkPlayerLastSeen", this.playerLastSeen);
        return compound;
    }

    @Override
    public void load(NbtCompound compound) {
        this.playerLastSeen = compound.getLong("ChunkPlayerLastSeen");
    }

    @Override
    public boolean aiShouldExecute() {
        --this.ticks;
        if (this.ticks > 0) {
            return false;
        }
        this.ticks = 20;
        List players = this.npc.getWorld().getNonSpectatingEntities(PlayerEntity.class, this.npc.getBoundingBox().expand(48.0, 48.0, 48.0));
        if (!players.isEmpty()) {
            this.playerLastSeen = System.currentTimeMillis();
        }
        if (this.playerLastSeen < 0L) {
            return false;
        }
        if (System.currentTimeMillis() > this.playerLastSeen + 600000L) {
            ChunkController.instance.unload((ServerWorld)this.npc.getWorld(), this.npc.getUuid(), this.npc.getChunkPos().x, this.npc.getChunkPos().z);
            this.chunks.clear();
            this.playerLastSeen = -1L;
            return false;
        }
        double x = this.npc.getX() / 16.0;
        double z = this.npc.getZ() / 16.0;
        ArrayList<ChunkPos> list = new ArrayList<ChunkPos>();
        list.add(new ChunkPos(MathHelper.floor((double)x), MathHelper.floor((double)z)));
        list.add(new ChunkPos(MathHelper.ceil((double)x), MathHelper.ceil((double)z)));
        list.add(new ChunkPos(MathHelper.floor((double)x), MathHelper.ceil((double)z)));
        list.add(new ChunkPos(MathHelper.ceil((double)x), MathHelper.floor((double)z)));
        for (ChunkPos chunk : list) {
            if (!this.chunks.contains(chunk)) {
                ChunkController.instance.load((ServerWorld)this.npc.getWorld(), this.npc.getUuid(), chunk.x, chunk.z);
            }
            this.chunks.remove(chunk);
        }
        for (ChunkPos chunk : this.chunks) {
            ChunkController.instance.unload((ServerWorld)this.npc.getWorld(), this.npc.getUuid(), chunk.x, chunk.z);
        }
        this.chunks = list;
        return false;
    }

    @Override
    public boolean aiContinueExecute() {
        return false;
    }

    @Override
    public void reset() {
        if (this.npc.getWorld() instanceof ServerWorld) {
            ChunkController.instance.unload((ServerWorld)this.npc.getWorld(), this.npc.getUuid(), this.npc.getChunkPos().x, this.npc.getChunkPos().z);
            this.chunks.clear();
            this.playerLastSeen = 0L;
        }
    }

    @Override
    public void delete() {
    }

    @Override
    public int getType() {
        return 8;
    }
}

