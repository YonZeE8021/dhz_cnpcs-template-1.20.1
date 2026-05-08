/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.AnimalEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$Settings
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtString
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.roles.RoleInterface;
import noppes.npcs.shared.common.CommonUtil;

public class ItemSoulstoneEmpty
extends Item {
    public ItemSoulstoneEmpty() {
        super(new Item.Settings().maxCount(64));
    }

    public boolean store(LivingEntity entity, ItemStack stack, PlayerEntity player) {
        if (!this.hasPermission(entity, player) || entity instanceof PlayerEntity) {
            return false;
        }
        ItemStack stone = new ItemStack((ItemConvertible)CustomItems.soulstoneFull);
        NbtCompound compound = new NbtCompound();
        if (!entity.saveSelfNbt(compound)) {
            return false;
        }
        ServerCloneController.Instance.cleanTags(compound);
        stone.setSubNbt("Entity", (NbtElement)compound);
        String name = entity.getSavedEntityId();
        if (name == null) {
            name = "generic";
        }
        stone.setSubNbt("Name", (NbtElement)NbtString.of((String)name));
        if (entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface)entity;
            stone.setSubNbt("DisplayName", (NbtElement)NbtString.of((String)entity.getName().getString()));
            if (npc.role.getType() == 6) {
                RoleCompanion role = (RoleCompanion)npc.role;
                stone.setSubNbt("ExtraText", (NbtElement)NbtString.of((String)("companion.stage,: ," + role.stage.name)));
            }
        } else if (entity.hasCustomName()) {
            stone.setSubNbt("DisplayName", (NbtElement)NbtString.of((String)entity.getCustomName().getString()));
        }
        NoppesUtilServer.GivePlayerItem((Entity)player, player, stone);
        if (!player.getAbilities().creativeMode) {
            stack.split(1);
            if (stack.getCount() <= 0) {
                player.getInventory().removeOne(stack);
            }
        }
        entity.discard();
        return true;
    }

    public boolean hasPermission(LivingEntity entity, PlayerEntity player) {
        if (CommonUtil.isOp(player)) {
            return true;
        }
        if (CustomNpcsPermissions.hasPermission((ServerPlayerEntity)player, CustomNpcsPermissions.SOULSTONE_ALL)) {
            return true;
        }
        if (entity instanceof EntityNPCInterface) {
            RoleInterface role;
            EntityNPCInterface npc = (EntityNPCInterface)entity;
            if (npc.role.getType() == 6 && ((RoleCompanion)(role = (RoleCompanion)npc.role)).getOwner() == player) {
                return true;
            }
            if (npc.role.getType() == 2 && ((RoleFollower)(role = (RoleFollower)npc.role)).getOwner() == player) {
                return !((RoleFollower)role).refuseSoulStone;
            }
            return CustomNpcs.SoulStoneNPCs;
        }
        if (entity instanceof AnimalEntity) {
            return CustomNpcs.SoulStoneAnimals;
        }
        return false;
    }
}

