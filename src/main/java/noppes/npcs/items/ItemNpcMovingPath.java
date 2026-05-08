/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.TypedActionResult
 *  net.minecraft.entity.Entity
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

import java.util.List;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;

public class ItemNpcMovingPath
extends Item {
    public ItemNpcMovingPath() {
        super(new Item.Settings().maxCount(1));
    }

    public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);
        if (level.isClient || !CustomNpcsPermissions.hasPermission((ServerPlayerEntity)player, CustomNpcsPermissions.TOOL_PATHER)) {
            return new TypedActionResult(ActionResult.PASS, itemstack);
        }
        EntityNPCInterface npc = this.getNpc(itemstack, level);
        if (npc != null) {
            NoppesUtilServer.sendOpenGui(player, EnumGuiType.MovingPath, npc);
        }
        return new TypedActionResult(ActionResult.SUCCESS, itemstack);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().isClient || !CustomNpcsPermissions.hasPermission((ServerPlayerEntity)context.getPlayer(), CustomNpcsPermissions.TOOL_PATHER)) {
            return ActionResult.FAIL;
        }
        ItemStack stack = context.getStack();
        EntityNPCInterface npc = this.getNpc(stack, context.getWorld());
        if (npc == null) {
            return ActionResult.PASS;
        }
        List<int[]> list = npc.ais.getMovingPath();
        int[] pos = list.get(list.size() - 1);
        int x = context.getBlockPos().getX();
        int y = context.getBlockPos().getY();
        int z = context.getBlockPos().getZ();
        list.add(new int[]{x, y, z});
        double d3 = x - pos[0];
        double d4 = y - pos[1];
        double d5 = z - pos[2];
        double distance = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
        context.getPlayer().sendMessage((Text)Text.translatable((String)"message.pather.added", (Object[])new Object[]{x, y, z, npc.getName()}));
        if (distance > (double)CustomNpcs.NpcNavRange) {
            ((ServerPlayerEntity)context.getPlayer()).sendMessage((Text)Text.translatable((String)"message.pather.farwarning", (Object[])new Object[]{CustomNpcs.NpcNavRange}));
        }
        return ActionResult.SUCCESS;
    }

    private EntityNPCInterface getNpc(ItemStack item, World level) {
        if (level.isClient || item.getNbt() == null) {
            return null;
        }
        Entity entity = level.getEntityById(item.getNbt().getInt("NPCID"));
        if (entity == null || !(entity instanceof EntityNPCInterface)) {
            return null;
        }
        return (EntityNPCInterface)entity;
    }
}

