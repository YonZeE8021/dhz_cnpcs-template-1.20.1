/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.TypedActionResult
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$Settings
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemUsageContext
 *  net.minecraft.world.World
 *  net.minecraft.text.Text
 *  net.minecraft.server.network.ServerPlayerEntity
 */
package noppes.npcs.items;

import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.util.CustomNPCsScheduler;

public class ItemNpcWand
extends Item {
    public ItemNpcWand() {
        super(new Item.Settings().maxCount(1));
    }

    public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);
        if (!level.isClient) {
            return new TypedActionResult(ActionResult.SUCCESS, (Object)itemstack);
        }
        CustomNpcs.proxy.openGui(player, EnumGuiType.NpcRemote);
        return new TypedActionResult(ActionResult.SUCCESS, (Object)itemstack);
    }

    public int getMaxUseTime(ItemStack p_77626_1_) {
        return 72000;
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().isClient) {
            return ActionResult.SUCCESS;
        }
        if (CustomNpcs.OpsOnly && !context.getPlayer().getServer().getPlayerManager().isOperator(context.getPlayer().getGameProfile())) {
            context.getPlayer().sendMessage((Text)Text.translatable((String)"availability.permission"));
        } else if (CustomNpcsPermissions.hasPermission((ServerPlayerEntity)context.getPlayer(), CustomNpcsPermissions.NPC_CREATE)) {
            EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, context.getWorld());
            npc.ais.setStartPos(context.getBlockPos().up());
            npc.refreshPositionAndAngles((float)context.getBlockPos().getX() + 0.5f, npc.getStartYPos(), (float)context.getBlockPos().getZ() + 0.5f, context.getPlayer().getYaw(), context.getPlayer().getPitch());
            context.getWorld().spawnEntity((Entity)npc);
            npc.setHealth(npc.getMaxHealth());
            CustomNPCsScheduler.runTack(() -> NoppesUtilServer.sendOpenGui(context.getPlayer(), EnumGuiType.MainMenuDisplay, npc), 100);
        } else {
            ((ServerPlayerEntity)context.getPlayer()).sendMessage((Text)Text.translatable((String)"availability.permission"));
        }
        return ActionResult.SUCCESS;
    }

    public ItemStack finishUsing(ItemStack stack, World worldIn, LivingEntity playerIn) {
        return stack;
    }
}

