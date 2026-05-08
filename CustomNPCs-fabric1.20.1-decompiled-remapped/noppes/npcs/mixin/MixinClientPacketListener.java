/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
 *  net.minecraft.client.network.ClientPlayNetworkHandler
 *  net.minecraft.client.world.ClientWorld
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package noppes.npcs.mixin;

import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixinintf.IMixinClientboundAddEntityPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ClientPlayNetworkHandler.class})
public class MixinClientPacketListener {
    @Shadow
    private ClientWorld world;

    @Inject(method={"handleAddEntity"}, at={@At(value="TAIL")})
    public void handleAddEntity(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        IMixinClientboundAddEntityPacket accessor = (IMixinClientboundAddEntityPacket)packet;
        if (this.world.getEntityById(packet.getId()) instanceof EntityNPCInterface) {
            if (accessor.getEntity() instanceof EntityNPCInterface) {
                ((EntityNPCInterface)this.world.getEntityById(packet.getId())).readSpawnData(((EntityNPCInterface)accessor.getEntity()).writeSpawnData());
            } else {
                ((EntityNPCInterface)this.world.getEntityById(packet.getId())).readSpawnData(accessor.getBuf());
            }
        }
    }
}

