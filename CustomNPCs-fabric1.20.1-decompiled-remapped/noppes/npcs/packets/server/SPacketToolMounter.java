/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.util.Identifier
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.packets.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketToolMounter
extends PacketServerBasic {
    private int type;
    private String name = "";
    private int tab = -1;
    private NbtCompound compound = new NbtCompound();

    private SPacketToolMounter(int type, String name, int tab, NbtCompound compound) {
        this.type = type;
        this.name = name;
        this.tab = tab;
        this.compound = compound;
    }

    public SPacketToolMounter(int type, String name, int tab) {
        this.type = type;
        this.name = name;
        this.tab = tab;
    }

    public SPacketToolMounter(int type, NbtCompound compound) {
        this.type = type;
        this.compound = compound;
    }

    public SPacketToolMounter() {
        this.type = 3;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return item.getItem() == CustomItems.mount;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.TOOL_MOUNTER;
    }

    public static void encode(SPacketToolMounter msg, PacketByteBuf buf) {
        buf.writeInt(msg.type);
        buf.writeString(msg.name);
        buf.writeInt(msg.tab);
        buf.writeNbt(msg.compound);
    }

    public static SPacketToolMounter decode(PacketByteBuf buf) {
        return new SPacketToolMounter(buf.readInt(), buf.readString(Short.MAX_VALUE), buf.readInt(), buf.readNbt());
    }

    @Override
    protected void handle() {
        PlayerData data = PlayerData.get((PlayerEntity)this.player);
        if (data.mounted == null) {
            return;
        }
        if (this.type == 0) {
            Entity entity = (Entity)EntityType.getEntityFromNbt((NbtCompound)this.compound, (World)this.player.getWorld()).get();
            entity.setPosition(data.mounted.getX(), data.mounted.getY(), data.mounted.getZ());
            this.player.getWorld().spawnEntity(entity);
            entity.startRiding(data.mounted, true);
        } else if (this.type == 1) {
            Entity entity = (Entity)EntityType.getEntityFromNbt((NbtCompound)ServerCloneController.Instance.getCloneData(this.player.getCommandSource(), this.name, this.tab), (World)this.player.getWorld()).get();
            entity.setPosition(data.mounted.getX(), data.mounted.getY(), data.mounted.getZ());
            this.player.getWorld().spawnEntity(entity);
            entity.startRiding(data.mounted, true);
        } else if (this.type == 2) {
            Identifier loc = EntityUtil.getAllEntities(this.player.getWorld(), false).get(this.name);
            EntityType type = (EntityType)Registries.ENTITY_TYPE.get(loc);
            Entity entity = type.create(this.player.getWorld());
            if (entity == null) {
                return;
            }
            entity.setPosition(data.mounted.getX(), data.mounted.getY(), data.mounted.getZ());
            this.player.getWorld().spawnEntity(entity);
            entity.startRiding(data.mounted, true);
        } else {
            this.player.startRiding(data.mounted, true);
        }
    }
}

