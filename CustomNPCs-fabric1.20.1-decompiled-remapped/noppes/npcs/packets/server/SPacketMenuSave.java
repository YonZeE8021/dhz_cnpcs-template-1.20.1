/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.network.PacketByteBuf
 */
package noppes.npcs.packets.server;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.server.permission.nodes.PermissionNodeCompat;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NBTTags;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.packets.PacketServerBasic;

public class SPacketMenuSave
extends PacketServerBasic {
    private EnumMenuType type;
    private NbtCompound data;

    public SPacketMenuSave(EnumMenuType type, NbtCompound data) {
        this.type = type;
        this.data = data;
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
        if (this.type == EnumMenuType.DISPLAY || this.type == EnumMenuType.MODEL) {
            return CustomNpcsPermissions.NPC_DISPLAY;
        }
        if (this.type == EnumMenuType.STATS) {
            return CustomNpcsPermissions.NPC_STATS;
        }
        if (this.type == EnumMenuType.INVENTORY) {
            return CustomNpcsPermissions.NPC_INVENTORY;
        }
        if (this.type == EnumMenuType.AI) {
            return CustomNpcsPermissions.NPC_AI;
        }
        if (this.type == EnumMenuType.ADVANCED || this.type == EnumMenuType.TRANSFORM || this.type == EnumMenuType.MARK) {
            return CustomNpcsPermissions.NPC_ADVANCED;
        }
        if (this.type == EnumMenuType.MOVING_PATH) {
            return CustomNpcsPermissions.TOOL_PATHER;
        }
        return CustomNpcsPermissions.NPC_GUI;
    }

    public static void encode(SPacketMenuSave msg, PacketByteBuf buf) {
        buf.writeEnumConstant((Enum)msg.type);
        buf.writeNbt(msg.data);
    }

    public static SPacketMenuSave decode(PacketByteBuf buf) {
        return new SPacketMenuSave((EnumMenuType)buf.readEnumConstant(EnumMenuType.class), buf.readNbt());
    }

    @Override
    protected void handle() {
        if (this.type == EnumMenuType.DISPLAY) {
            this.npc.display.readToNBT(this.data);
        }
        if (this.type == EnumMenuType.STATS) {
            this.npc.stats.readToNBT(this.data);
        }
        if (this.type == EnumMenuType.INVENTORY) {
            this.npc.inventory.load(this.data);
            this.npc.updateAI = true;
        }
        if (this.type == EnumMenuType.AI) {
            this.npc.ais.readToNBT(this.data);
            this.npc.setHealth(this.npc.getMaxHealth());
            this.npc.updateAI = true;
        }
        if (this.type == EnumMenuType.ADVANCED) {
            this.npc.advanced.readToNBT(this.data);
            this.npc.updateAI = true;
        }
        if (this.type == EnumMenuType.MODEL) {
            ((EntityCustomNpc)this.npc).modelData.load(this.data);
        }
        if (this.type == EnumMenuType.TRANSFORM) {
            boolean isValid = this.npc.transform.isValid();
            this.npc.transform.readOptions(this.data);
            if (isValid != this.npc.transform.isValid()) {
                this.npc.updateAI = true;
            }
        }
        if (this.type == EnumMenuType.MOVING_PATH) {
            this.npc.ais.setMovingPath(NBTTags.getIntegerArraySet(this.data.getList("MovingPathNew", 10)));
        }
        if (this.type == EnumMenuType.MARK) {
            MarkData mark = MarkData.get((LivingEntity)this.npc);
            mark.setNBT(this.data);
            mark.syncClients();
        }
        this.npc.updateClient = true;
    }
}

