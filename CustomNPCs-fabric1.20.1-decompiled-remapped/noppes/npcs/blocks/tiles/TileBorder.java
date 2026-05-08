/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.thrown.EnderPearlEntity
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.text.Text
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
 *  net.minecraft.block.BlockState
 *  net.minecraft.state.property.Property
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.blocks.tiles;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3i;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.CustomBlocks;
import noppes.npcs.blocks.BlockBorder;
import noppes.npcs.blocks.tiles.TileNpcEntity;
import noppes.npcs.controllers.data.Availability;

public class TileBorder
extends TileNpcEntity
implements com.google.common.base.Predicate {
    public Availability availability = new Availability();
    public Box boundingbox;
    public int rotation = 0;
    public int height = 10;
    public String message = "availability.areaNotAvailble";

    public TileBorder(BlockPos pos, BlockState state) {
        super(CustomBlocks.tile_border, pos, state);
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        this.readExtraNBT(compound);
        if (this.getWorld() != null) {
            this.getWorld().setBlockState(this.getPos(), (BlockState)CustomBlocks.border.getDefaultState().with((Property)BlockBorder.ROTATION, (Comparable)Integer.valueOf(this.rotation)));
        }
    }

    public void readExtraNBT(NbtCompound compound) {
        this.availability.load(compound.getCompound("BorderAvailability"));
        this.rotation = compound.getInt("BorderRotation");
        this.height = compound.getInt("BorderHeight");
        this.message = compound.getString("BorderMessage");
    }

    @Override
    public void writeNbt(NbtCompound compound) {
        this.writeExtraNBT(compound);
        super.writeNbt(compound);
    }

    public void writeExtraNBT(NbtCompound compound) {
        compound.put("BorderAvailability", (NbtElement)this.availability.save(new NbtCompound()));
        compound.putInt("BorderRotation", this.rotation);
        compound.putInt("BorderHeight", this.height);
        compound.putString("BorderMessage", this.message);
    }

    public static void tick(World level, BlockPos pos, BlockState state, TileBorder tile) {
        if (level.isClient) {
            return;
        }
        Box box = new Box((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + tile.height + 1), (double)(pos.getZ() + 1));
        List list = level.getEntitiesByClass(Entity.class, box, (Predicate)((Object)tile));
        for (Entity entity : list) {
            if (entity instanceof EnderPearlEntity) {
                EnderPearlEntity pearl = (EnderPearlEntity)entity;
                if (!(pearl.getOwner() instanceof PlayerEntity) || tile.availability.isAvailable((PlayerEntity)pearl.getOwner())) continue;
                entity.setRemoved(Entity.RemovalReason.DISCARDED);
                continue;
            }
            PlayerEntity player = (PlayerEntity)entity;
            if (tile.availability.isAvailable(player)) continue;
            BlockPos pos2 = new BlockPos((Vec3i)tile.pos);
            if (tile.rotation == 2) {
                pos2 = pos2.south();
            } else if (tile.rotation == 0) {
                pos2 = pos2.north();
            } else if (tile.rotation == 1) {
                pos2 = pos2.east();
            } else if (tile.rotation == 3) {
                pos2 = pos2.west();
            }
            while (!level.isAir(pos2)) {
                pos2 = pos2.up();
            }
            player.requestTeleport((double)pos2.getX() + 0.5, (double)pos2.getY(), (double)pos2.getZ() + 0.5);
            if (tile.message.isEmpty()) continue;
            player.sendMessage((Text)Text.translatable((String)tile.message), true);
        }
    }

    public BlockEntityUpdateS2CPacket getUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create((BlockEntity)this);
    }

    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound compound = new NbtCompound();
        compound.putInt("x", this.pos.getX());
        compound.putInt("y", this.pos.getY());
        compound.putInt("z", this.pos.getZ());
        compound.putInt("Rotation", this.rotation);
        return compound;
    }

    public boolean isEntityApplicable(Entity var1) {
        return var1 instanceof ServerPlayerEntity || var1 instanceof EnderPearlEntity;
    }

    public boolean apply(Object ob) {
        return this.isEntityApplicable((Entity)ob);
    }
}

