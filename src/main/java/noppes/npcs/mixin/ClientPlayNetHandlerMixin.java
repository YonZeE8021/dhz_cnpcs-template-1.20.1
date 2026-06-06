/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.world.World
 *  net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.world.ClientWorld
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package noppes.npcs.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import noppes.npcs.CustomEntities;
import noppes.npcs.entity.EntityProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientPlayNetworkHandler.class})
public class ClientPlayNetHandlerMixin {
    @Inject(at={@At(value="TAIL")}, method={"onEntitySpawn"})
    private void handleAddEntity(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        EntityProjectile entity = null;
        ClientWorld level = MinecraftClient.getInstance().world;
        if (packet.getEntityType() == CustomEntities.entityProjectile) {
            entity = new EntityProjectile(CustomEntities.entityProjectile, (World)level);
            Entity entity2 = level.getEntityById(packet.getEntityData());
            if (entity2 != null) {
                entity.setOwner(entity2);
            }
        }
        if (entity != null) {
            int i = packet.getId();
            entity.setPosition(packet.getX(), packet.getY(), packet.getZ());
            entity.refreshPositionAfterTeleport(packet.getX(), packet.getY(), packet.getZ());
            entity.setPitch(packet.getPitch() * 360.0f / 256.0f);
            entity.setYaw(packet.getYaw() * 360.0f / 256.0f);
            entity.setId(i);
            entity.setUuid(packet.getUuid());
            MinecraftClient.getInstance().world.addEntity(i, (Entity)entity);
        }
    }
}

