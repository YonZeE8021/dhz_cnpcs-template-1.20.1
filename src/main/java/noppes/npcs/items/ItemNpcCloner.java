/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ActionResult
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$Settings
 *  net.minecraft.item.ItemUsageContext
 */
package noppes.npcs.items;

import net.minecraft.util.ActionResult;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.packets.server.SPacketGuiOpen;

public class ItemNpcCloner
extends Item {
    public ItemNpcCloner() {
        super(new Item.Settings().maxCount(1));
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient) {
            SPacketGuiOpen.sendOpenGui(context.getPlayer(), EnumGuiType.MobSpawner, null, context.getBlockPos());
        }
        return ActionResult.SUCCESS;
    }
}

