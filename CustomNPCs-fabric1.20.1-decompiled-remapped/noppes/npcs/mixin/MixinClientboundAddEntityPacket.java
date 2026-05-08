/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package noppes.npcs.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixinintf.IMixinClientboundAddEntityPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={EntitySpawnS2CPacket.class})
public class MixinClientboundAddEntityPacket
implements IMixinClientboundAddEntityPacket {
    @Unique
    private PacketByteBuf buf;
    @Unique
    private Entity entity;

    @Inject(method={"<init>(Lnet/minecraft/world/entity/Entity;I)V"}, at={@At(value="TAIL")})
    public void initFromEnt1(Entity entity, int data, CallbackInfo ci) {
        this.entity = entity;
    }

    @Inject(method={"Lnet/minecraft/network/protocol/game/ClientboundAddEntityPacket;<init>(Lnet/minecraft/world/entity/Entity;ILnet/minecraft/core/BlockPos;)V"}, at={@At(value="TAIL")})
    public void initFromEnt2(Entity entity, int data, BlockPos pos, CallbackInfo ci) {
        this.entity = entity;
    }

    @Inject(method={"<init>(Lnet/minecraft/network/FriendlyByteBuf;)V"}, at={@At(value="TAIL")})
    public void initFromBuf(PacketByteBuf buffer, CallbackInfo ci) {
        int count = buffer.readVarInt();
        if (count > 0) {
            PacketByteBuf spawnDataBuffer = new PacketByteBuf(Unpooled.buffer());
            spawnDataBuffer.writeBytes((ByteBuf)buffer, count);
            this.buf = spawnDataBuffer;
            return;
        }
        this.buf = new PacketByteBuf(Unpooled.buffer());
    }

    @Inject(method={"write"}, at={@At(value="TAIL")})
    public void write(PacketByteBuf buffer, CallbackInfo ci) {
        if (this.entity instanceof EntityNPCInterface) {
            PacketByteBuf spawnDataBuffer = new PacketByteBuf(Unpooled.buffer());
            ((EntityNPCInterface)this.entity).writeSpawnData(spawnDataBuffer);
            buffer.writeVarInt(spawnDataBuffer.readableBytes());
            buffer.writeBytes((ByteBuf)spawnDataBuffer);
            spawnDataBuffer.release();
        } else {
            buffer.writeVarInt(0);
        }
    }

    @Override
    public Entity getEntity() {
        return this.entity;
    }

    @Override
    public PacketByteBuf getBuf() {
        return this.buf;
    }
}

