/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.particle.ParticleEffect
 */
package noppes.npcs;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;

public class CommonProxy {
    public boolean newVersionAvailable = false;
    public int revision = 4;

    public void load() {
    }

    public void postload() {
    }

    public void openGui(EntityNPCInterface npc, EnumGuiType gui) {
    }

    public void openGui(PlayerEntity player, EnumGuiType gui) {
    }

    public void openGui(PlayerEntity player, Object guiscreen) {
    }

    public void spawnParticle(LivingEntity player, String string, Object ... ob) {
    }

    public boolean hasClient() {
        return false;
    }

    public PlayerEntity getPlayer() {
        return null;
    }

    public void spawnParticle(ParticleEffect type, double x, double y, double z, double motionX, double motionY, double motionZ, float scale) {
    }

    public PlayerData getPlayerData(PlayerEntity player) {
        return null;
    }
}

