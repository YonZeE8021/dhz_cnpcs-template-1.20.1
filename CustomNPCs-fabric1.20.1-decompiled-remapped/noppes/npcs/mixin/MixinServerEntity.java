/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.server.network.EntityTrackerEntry
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package noppes.npcs.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.EntityTrackerEntry;
import noppes.npcs.controllers.VisibilityController;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketMarkData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntityTrackerEntry.class})
public class MixinServerEntity {
    @Shadow
    @Final
    private Entity entity;

    @Inject(method={"addPairing"}, at={@At(value="TAIL")})
    public void addPairing(ServerPlayerEntity player, CallbackInfo ci) {
        if (this.entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface)this.entity;
            npc.tracking.add(player.getId());
            VisibilityController.checkIsVisible(npc, player);
        }
        if (!(this.entity instanceof LivingEntity) || this.entity.getWorld().isClient) {
            return;
        }
        MarkData data = MarkData.get((LivingEntity)this.entity);
        if (data.marks.isEmpty()) {
            return;
        }
        Packets.send(player, new PacketMarkData(this.entity.getId(), data.getNBT()));
    }

    @Inject(method={"removePairing"}, at={@At(value="TAIL")})
    public void removePairing(ServerPlayerEntity player, CallbackInfo ci) {
        if (this.entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface)this.entity;
            npc.tracking.remove(player.getId());
        }
    }
}

