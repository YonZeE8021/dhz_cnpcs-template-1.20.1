/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.sound.PositionedSoundInstance
 *  net.minecraft.client.sound.SoundInstance
 *  net.minecraft.client.sound.SoundInstance$AttenuationType
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.sound.SoundCategory
 */
package noppes.npcs.client.controllers;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;

public class MusicController {
    public static MusicController Instance;
    public SoundInstance playing;
    public Identifier playingResource;
    public Entity playingEntity;

    public MusicController() {
        Instance = this;
    }

    public void stopMusic() {
        SoundManager handler = MinecraftClient.getInstance().getSoundManager();
        if (this.playing != null) {
            handler.stop(this.playing);
        }
        handler.stopSounds(null, SoundCategory.MUSIC);
        handler.stopSounds(null, SoundCategory.AMBIENT);
        handler.stopSounds(null, SoundCategory.RECORDS);
        this.playingResource = null;
        this.playingEntity = null;
        this.playing = null;
    }

    public void playStreaming(String music, Entity entity, boolean isLooping) {
        if (this.isPlaying(music)) {
            return;
        }
        this.stopMusic();
        this.playingEntity = entity;
        this.playingResource = new Identifier(music);
        SoundManager handler = MinecraftClient.getInstance().getSoundManager();
        this.playing = new PositionedSoundInstance(this.playingResource, SoundCategory.RECORDS, 4.0f, 1.0f, SoundInstance.createRandom(), isLooping, 0, SoundInstance.AttenuationType.LINEAR, entity.getX(), entity.getY(), entity.getZ(), false);
        handler.play(this.playing);
    }

    public void playMusic(String music, Entity entity, boolean isLooping) {
        if (this.isPlaying(music)) {
            return;
        }
        this.stopMusic();
        this.playingResource = new Identifier(music);
        this.playingEntity = entity;
        SoundManager handler = MinecraftClient.getInstance().getSoundManager();
        this.playing = new PositionedSoundInstance(this.playingResource, SoundCategory.MUSIC, 1.0f, 1.0f, SoundInstance.createRandom(), isLooping, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, false);
        handler.play(this.playing);
    }

    public boolean isPlaying(String music) {
        Identifier resource = new Identifier(music);
        if (this.playingResource == null || !this.playingResource.equals(resource)) {
            return false;
        }
        return MinecraftClient.getInstance().getSoundManager().isPlaying(this.playing);
    }

    public void playSound(SoundCategory cat, String music, BlockPos pos, float volume, float pitch) {
        PositionedSoundInstance rec = new PositionedSoundInstance(new Identifier(music), cat, volume, pitch, SoundInstance.createRandom(), false, 0, SoundInstance.AttenuationType.LINEAR, (double)((float)pos.getX() + 0.5f), (double)pos.getY(), (double)((float)pos.getZ() + 0.5f), false);
        MinecraftClient.getInstance().getSoundManager().play((SoundInstance)rec);
    }
}

