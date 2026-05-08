/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.TypedActionResult
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$Settings
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package noppes.npcs.items;

import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumGuiType;

public class ItemNpcScripter
extends Item {
    public ItemNpcScripter() {
        super(new Item.Settings().maxCount(1));
    }

    public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);
        if (!level.isClient || hand != Hand.MAIN_HAND) {
            return new TypedActionResult(ActionResult.SUCCESS, itemstack);
        }
        CustomNpcs.proxy.openGui(player, EnumGuiType.ScriptPlayers);
        return new TypedActionResult(ActionResult.SUCCESS, itemstack);
    }
}

