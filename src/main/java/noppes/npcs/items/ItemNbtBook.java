/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$Settings
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.BlockState
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.EntityHitResult
 *  org.jetbrains.annotations.Nullable
 */
package noppes.npcs.items;

import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;
import noppes.npcs.packets.client.PacketGuiOpen;
import org.jetbrains.annotations.Nullable;

public class ItemNbtBook
extends Item {
    public ItemNbtBook() {
        super(new Item.Settings().maxCount(1));
    }

    public void blockEvent(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        Packets.send((ServerPlayerEntity)player, new PacketGuiOpen(EnumGuiType.NbtBook, hitResult.getBlockPos()));
        BlockState state = world.getBlockState(hitResult.getBlockPos());
        NbtCompound data = new NbtCompound();
        BlockEntity tile = world.getBlockEntity(hitResult.getBlockPos());
        if (tile != null) {
            tile.createNbtWithIdentifyingData();
        }
        NbtCompound compound = new NbtCompound();
        compound.put("Data", (NbtElement)data);
        Packets.send((ServerPlayerEntity)player, new PacketGuiData(compound));
    }

    public void entityEvent(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        Packets.send((ServerPlayerEntity)player, new PacketGuiOpen(EnumGuiType.NbtBook, BlockPos.ORIGIN));
        NbtCompound data = new NbtCompound();
        entity.saveSelfNbt(data);
        NbtCompound compound = new NbtCompound();
        compound.putInt("EntityId", entity.getId());
        compound.put("Data", (NbtElement)data);
        Packets.send((ServerPlayerEntity)player, new PacketGuiData(compound));
    }
}

