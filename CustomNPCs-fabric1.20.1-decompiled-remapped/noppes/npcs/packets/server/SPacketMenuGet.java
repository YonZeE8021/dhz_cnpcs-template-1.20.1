/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.packets.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketMenuGet
extends PacketServerBasic {
    private EnumMenuType type;

    public SPacketMenuGet(EnumMenuType type) {
        this.type = type;
    }

    @Override
    public boolean toolAllowed(ItemStack item) {
        if (this.type == EnumMenuType.MOVING_PATH) {
            return item.getItem() == CustomItems.moving;
        }
        return item.getItem() == CustomItems.wand;
    }

    @Override
    public boolean requiresNpc() {
        return true;
    }

    @Override
    public PermissionNodeCompat<Boolean> getPermission() {
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketMenuGet msg, PacketByteBuf buf) {
        buf.writeEnumConstant((Enum)msg.type);
    }

    public static SPacketMenuGet decode(PacketByteBuf buf) {
        return new SPacketMenuGet((EnumMenuType)buf.readEnumConstant(EnumMenuType.class));
    }

    @Override
    protected void handle() {
        NbtCompound data = new NbtCompound();
        if (this.type == EnumMenuType.DISPLAY) {
            this.npc.display.save(data);
        }
        if (this.type == EnumMenuType.STATS) {
            this.npc.stats.save(data);
        }
        if (this.type == EnumMenuType.INVENTORY) {
            this.npc.inventory.save(data);
        }
        if (this.type == EnumMenuType.AI || this.type == EnumMenuType.MOVING_PATH) {
            this.npc.ais.save(data);
        }
        if (this.type == EnumMenuType.ADVANCED) {
            this.npc.advanced.save(data);
        }
        if (this.type == EnumMenuType.TRANSFORM) {
            this.npc.transform.writeOptions(data);
        }
        Packets.send(this.player, new PacketGuiData(data));
    }
}

