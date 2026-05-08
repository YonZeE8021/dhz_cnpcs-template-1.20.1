/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ActionResult
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.TallBlockItem
 *  net.minecraft.item.Item$Settings
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemUsageContext
 *  net.minecraft.world.World
 *  net.minecraft.block.Block
 */
package noppes.npcs.items;

import net.minecraft.util.ActionResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.TallBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.packets.server.SPacketGuiOpen;

public class ItemScriptedDoor
extends TallBlockItem {
    public ItemScriptedDoor(Block block) {
        super(block, new Item.Settings().maxCount(1));
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult res = super.useOnBlock(context);
        if (res == ActionResult.SUCCESS && !context.getWorld().isClient) {
            PlayerData data = PlayerData.get(context.getPlayer());
            data.scriptBlockPos = context.getBlockPos();
            SPacketGuiOpen.sendOpenGui(context.getPlayer(), EnumGuiType.ScriptDoor, null, context.getBlockPos().up());
            return ActionResult.SUCCESS;
        }
        return res;
    }

    public ItemStack finishUsing(ItemStack stack, World worldIn, LivingEntity playerIn) {
        return stack;
    }
}

