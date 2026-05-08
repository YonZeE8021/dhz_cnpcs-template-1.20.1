/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.client.MinecraftClient
 */
package noppes.npcs.roles;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.client.MinecraftClient;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.entity.data.role.IJobBard;
import noppes.npcs.client.controllers.MusicController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.MusicManagerMixin;
import noppes.npcs.roles.JobInterface;
import noppes.npcs.shared.client.util.NoppesStringUtils;

public class JobBard
extends JobInterface
implements IJobBard {
    public int minRange = 2;
    public int maxRange = 64;
    public boolean isStreamer = true;
    public boolean isLooping = false;
    public boolean hasOffRange = true;
    public String song = "";

    public JobBard(EntityNPCInterface npc) {
        super(npc);
    }

    @Override
    public NbtCompound save(NbtCompound nbttagcompound) {
        nbttagcompound.putString("BardSong", this.song);
        nbttagcompound.putInt("BardMinRange", this.minRange);
        nbttagcompound.putInt("BardMaxRange", this.maxRange);
        nbttagcompound.putBoolean("BardStreamer", this.isStreamer);
        nbttagcompound.putBoolean("BardLoops", this.isLooping);
        nbttagcompound.putBoolean("BardHasOff", this.hasOffRange);
        return nbttagcompound;
    }

    @Override
    public void load(NbtCompound nbttagcompound) {
        this.song = nbttagcompound.getString("BardSong");
        this.minRange = nbttagcompound.getInt("BardMinRange");
        this.maxRange = nbttagcompound.getInt("BardMaxRange");
        this.isStreamer = nbttagcompound.getBoolean("BardStreamer");
        this.isLooping = nbttagcompound.getBoolean("BardLoops");
        this.hasOffRange = nbttagcompound.getBoolean("BardHasOff");
    }

    public void aiStep() {
        List list;
        if (!this.npc.isClientSide() || this.song.isEmpty()) {
            return;
        }
        if (!MusicController.Instance.isPlaying(this.song)) {
            List list2 = this.npc.getWorld().getNonSpectatingEntities(PlayerEntity.class, this.npc.getBoundingBox().expand((double)this.minRange, (double)(this.minRange / 2), (double)this.minRange));
            if (!list2.contains(CustomNpcs.proxy.getPlayer())) {
                return;
            }
            if (this.isStreamer) {
                MusicController.Instance.playStreaming(this.song, (Entity)this.npc, this.isLooping);
            } else {
                MusicController.Instance.playMusic(this.song, (Entity)this.npc, this.isLooping);
            }
        } else if (MusicController.Instance.playingEntity != this.npc) {
            PlayerEntity player = CustomNpcs.proxy.getPlayer();
            if (this.npc.squaredDistanceTo((Entity)player) < MusicController.Instance.playingEntity.squaredDistanceTo((Entity)player)) {
                MusicController.Instance.playingEntity = this.npc;
            }
        } else if (this.hasOffRange && !(list = this.npc.getWorld().getNonSpectatingEntities(PlayerEntity.class, this.npc.getBoundingBox().expand((double)this.maxRange, (double)(this.maxRange / 2), (double)this.maxRange))).contains(CustomNpcs.proxy.getPlayer())) {
            MusicController.Instance.stopMusic();
        }
        if (MusicController.Instance.isPlaying(this.song)) {
            ((MusicManagerMixin)MinecraftClient.getInstance().getMusicTracker()).nextSongDelay(12000);
        }
    }

    @Override
    public void killed() {
        this.delete();
    }

    @Override
    public void delete() {
        if (this.npc.getWorld().isClient && this.hasOffRange && MusicController.Instance.isPlaying(this.song)) {
            MusicController.Instance.stopMusic();
        }
    }

    @Override
    public String getSong() {
        return NoppesStringUtils.cleanResource(this.song);
    }

    @Override
    public void setSong(String song) {
        this.song = NoppesStringUtils.cleanResource(song);
        this.npc.updateClient = true;
    }

    @Override
    public int getType() {
        return 1;
    }
}

