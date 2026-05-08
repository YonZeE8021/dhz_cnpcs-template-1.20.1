/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.ActionResult
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$Settings
 *  net.minecraft.item.ItemStack
 *  net.minecraft.client.item.TooltipContext
 *  net.minecraft.item.ItemUsageContext
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.text.Text
 *  net.minecraft.text.MutableText
 */
package noppes.npcs.items;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Formatting;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.MutableText;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleFollower;

public class ItemSoulstoneFilled
extends Item {
    public ItemSoulstoneFilled() {
        super(new Item.Settings().maxCount(1));
    }

    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World level, List<Text> list, TooltipContext flag) {
        NbtCompound compound = stack.getNbt();
        if (compound == null || !compound.contains("Entity", 10)) {
            list.add((Text)Text.literal((String)(String.valueOf(Formatting.field_1061) + "Error")));
            return;
        }
        MutableText name = Text.translatable((String)compound.getString("Name"));
        if (compound.contains("DisplayName")) {
            name = Text.translatable((String)compound.getString("DisplayName")).append(" (").append((Text)name).append(")");
        }
        list.add((Text)Text.literal((String)String.valueOf(Formatting.field_1078)).append((Text)name));
        if (stack.getNbt().contains("ExtraText")) {
            String[] split;
            MutableText text = Text.literal((String)"");
            for (String s : split = compound.getString("ExtraText").split(",")) {
                text.append((Text)Text.translatable((String)s));
            }
            list.add((Text)text);
        }
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().isClient) {
            return ActionResult.SUCCESS;
        }
        ItemStack stack = context.getStack();
        if (ItemSoulstoneFilled.Spawn(context.getPlayer(), stack, context.getWorld(), context.getBlockPos()) == null) {
            return ActionResult.FAIL;
        }
        if (!context.getPlayer().getAbilities().creativeMode) {
            stack.split(1);
        }
        return ActionResult.SUCCESS;
    }

    public static Entity Spawn(PlayerEntity player, ItemStack stack, World level, BlockPos pos) {
        if (level.isClient) {
            return null;
        }
        if (stack.getNbt() == null || !stack.getNbt().contains("Entity", 10)) {
            return null;
        }
        NbtCompound compound = stack.getNbt().getCompound("Entity");
        Entity entity = EntityType.getEntityFromNbt((NbtCompound)compound, (World)level).orElse(null);
        if (entity == null) {
            return null;
        }
        entity.setPosition((double)pos.getX() + 0.5, (double)((float)(pos.getY() + 1) + 0.2f), (double)pos.getZ() + 0.5);
        if (entity instanceof EntityNPCInterface) {
            EntityNPCInterface npc = (EntityNPCInterface)entity;
            npc.ais.setStartPos(pos);
            npc.setHealth(npc.getMaxHealth());
            npc.setPosition((float)pos.getX() + 0.5f, npc.getStartYPos(), (float)pos.getZ() + 0.5f);
            if (npc.role.getType() == 6 && player != null) {
                PlayerData data = PlayerData.get(player);
                if (data.hasCompanion()) {
                    return null;
                }
                ((RoleCompanion)npc.role).setOwner(player);
                data.setCompanion(npc);
            }
            if (npc.role.getType() == 2 && player != null) {
                ((RoleFollower)npc.role).setOwner(player);
            }
        }
        if (!level.spawnEntity(entity)) {
            player.sendMessage((Text)Text.translatable((String)"error.failedToSpawn"));
            return null;
        }
        return entity;
    }
}

