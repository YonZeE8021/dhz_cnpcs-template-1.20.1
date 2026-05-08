/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.network.PacketByteBuf
 *  net.minecraft.text.Text
 *  net.minecraft.world.Heightmap$Type
 *  net.minecraft.util.Identifier
 *  net.minecraft.server.world.ServerWorld
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 *  net.minecraft.server.MinecraftServer
 */
package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.world.Heightmap;
import net.minecraft.util.Identifier;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import noppes.npcs.CustomItems;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketDimensionTeleport
extends PacketServerBasic {
    private Identifier id;

    public SPacketDimensionTeleport(Identifier id) {
        this.id = id;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        return item.getItem() == CustomItems.teleporter;
    }

    public static void encode(SPacketDimensionTeleport msg, PacketByteBuf buf) {
        buf.writeIdentifier(msg.id);
    }

    public static SPacketDimensionTeleport decode(PacketByteBuf buf) {
        return new SPacketDimensionTeleport(buf.readIdentifier());
    }

    @Override
    protected void handle() {
        RegistryKey dimension = RegistryKey.of((RegistryKey)RegistryKeys.WORLD, (Identifier)this.id);
        ServerWorld level = this.player.getServer().getWorld(dimension);
        BlockPos coords = level.getSpawnPos();
        if (coords == null) {
            coords = level.getSpawnPos();
            if (!level.isAir(coords)) {
                coords = level.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, coords);
            } else {
                while (level.isAir(coords) && coords.getY() > 0) {
                    coords = coords.down();
                }
                if (coords.getY() == 0) {
                    coords = level.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, coords);
                }
            }
        }
        SPacketDimensionTeleport.teleportPlayer(this.player, coords.getX(), coords.getY(), coords.getZ(), (RegistryKey<World>)dimension);
    }

    public static void teleportPlayer(ServerPlayerEntity player, double x, double y, double z, RegistryKey<World> dimension) {
        if (player.getWorld().getRegistryKey() != dimension) {
            MinecraftServer server = player.getServer();
            ServerWorld wor = server.getWorld(dimension);
            if (wor == null) {
                player.sendMessage((Text)Text.literal((String)"Broken transporter. Dimension does not exist"));
                return;
            }
            player.refreshPositionAndAngles(x, y, z, player.getYaw(), player.getPitch());
            player.moveToWorld(wor);
        } else {
            player.networkHandler.requestTeleport(x, y, z, player.getYaw(), player.getPitch());
        }
    }
}

