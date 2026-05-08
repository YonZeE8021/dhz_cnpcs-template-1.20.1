/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.MobSpawnerLogic
 *  net.minecraft.world.World
 *  net.minecraft.world.MobSpawnerEntry
 *  net.minecraft.block.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.text.Text
 *  net.minecraft.block.entity.MobSpawnerBlockEntity
 */
package noppes.npcs.packets.server;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.World;
import net.minecraft.world.MobSpawnerEntry;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NBTTags;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.BaseSpawnerMixin;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketToolMobSpawner
extends PacketServerBasic {
    private boolean createSpawner;
    private boolean server;
    private BlockPos pos;
    private String name = "";
    private int tab = -1;
    private NbtCompound clone = new NbtCompound();

    public SPacketToolMobSpawner(boolean createSpawner, BlockPos pos, String name, int tab) {
        this.server = true;
        this.createSpawner = createSpawner;
        this.pos = pos;
        this.name = name;
        this.tab = tab;
    }

    public SPacketToolMobSpawner(boolean createSpawner, BlockPos pos, NbtCompound clone) {
        this.server = false;
        this.createSpawner = createSpawner;
        this.pos = pos;
        this.clone = clone;
    }

    public SPacketToolMobSpawner(boolean createSpawner, boolean server, BlockPos pos, String name, int tab, NbtCompound clone) {
        this.createSpawner = createSpawner;
        this.server = server;
        this.pos = pos;
        this.name = name;
        this.tab = tab;
        this.clone = clone;
    }

    public SPacketToolMobSpawner(PacketByteBuf buf) {
        this.createSpawner = buf.readBoolean();
        this.server = buf.readBoolean();
        this.pos = buf.readBlockPos();
        this.name = buf.readString(Short.MAX_VALUE);
        this.tab = buf.readInt();
        this.clone = buf.readNbt();
    }

    public static SPacketToolMobSpawner decode(PacketByteBuf buf) {
        return new SPacketToolMobSpawner(buf);
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return item.getItem() == CustomItems.cloner;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        if (this.createSpawner) {
            return CustomNpcsPermissions.SPAWNER_CREATE;
        }
        return CustomNpcsPermissions.SPAWNER_MOB;
    }

    @Override
    protected void handle() {
        if (this.server) {
            this.clone = ServerCloneController.Instance.getCloneData(this.player.getCommandSource(), this.name, this.tab);
        }
        if (this.clone == null || this.clone.isEmpty()) {
            return;
        }
        if (this.createSpawner) {
            SPacketToolMobSpawner.createMobSpawner(this.pos, this.clone, (PlayerEntity)this.player);
        } else {
            Entity entity = SPacketToolMobSpawner.spawnClone(this.clone, (double)this.pos.getX() + 0.5, this.pos.getY() + 1, (double)this.pos.getZ() + 0.5, this.player.getWorld());
            if (entity == null) {
                this.player.sendMessage((Text)Text.literal((String)"Failed to create an entity out of your clone"));
            }
        }
    }

    public static Entity spawnClone(NbtCompound compound, double x, double y, double z, World world) {
        ServerCloneController.Instance.cleanTags(compound);
        compound.put("Pos", (NbtElement)NBTTags.nbtDoubleList(x, y, z));
        Entity entity = (Entity)EntityType.getEntityFromNbt((NbtCompound)compound, (World)world).get();
        if (entity == null) {
            return null;
        }
        if (entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface)entity;
            npc.ais.setStartPos(npc.getBlockPos());
        }
        world.spawnEntity(entity);
        return entity;
    }

    public static void createMobSpawner(BlockPos pos, NbtCompound comp, PlayerEntity player) {
        ServerCloneController.Instance.cleanTags(comp);
        if (comp.getString("id").equalsIgnoreCase("entityhorse")) {
            player.sendMessage((Text)Text.literal((String)"Currently you cant create horse spawner, its a minecraft bug"));
            return;
        }
        player.getWorld().setBlockState(pos, Blocks.field_10260.getDefaultState());
        MobSpawnerBlockEntity tile = (MobSpawnerBlockEntity)player.getWorld().getBlockEntity(pos);
        MobSpawnerLogic logic = tile.getLogic();
        if (!comp.contains("id", 8)) {
            comp.putString("id", "Pig");
        }
        comp.putIntArray("StartPosNew", new int[]{pos.getX(), pos.getY(), pos.getZ()});
        ((BaseSpawnerMixin)logic).callSetNextSpawnData(player.getWorld(), pos, new MobSpawnerEntry(comp, Optional.empty()));
    }

    public static void encode(SPacketToolMobSpawner msg, PacketByteBuf buf) {
        buf.writeBoolean(msg.createSpawner);
        buf.writeBoolean(msg.server);
        buf.writeBlockPos(msg.pos);
        buf.writeString(msg.name);
        buf.writeInt(msg.tab);
        buf.writeNbt(msg.clone);
    }
}

