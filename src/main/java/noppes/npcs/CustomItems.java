/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$Settings
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.world.World
 *  net.minecraft.block.DispenserBlock
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPointer
 *  net.minecraft.block.dispenser.ItemDispenserBehavior
 *  net.minecraft.util.math.Direction
 *  net.minecraft.block.dispenser.DispenserBehavior
 *  net.minecraft.registry.Registry
 *  net.minecraft.state.property.Property
 *  net.minecraft.registry.Registries
 */
package noppes.npcs;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.world.World;
import net.minecraft.block.DispenserBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.util.math.Direction;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.Property;
import net.minecraft.registry.Registries;
import noppes.npcs.items.ItemMounter;
import noppes.npcs.items.ItemNbtBook;
import noppes.npcs.items.ItemNpcCloner;
import noppes.npcs.items.ItemNpcMovingPath;
import noppes.npcs.items.ItemNpcScripter;
import noppes.npcs.items.ItemNpcWand;
import noppes.npcs.items.ItemScripted;
import noppes.npcs.items.ItemSoulstoneEmpty;
import noppes.npcs.items.ItemSoulstoneFilled;
import noppes.npcs.items.ItemTeleporter;

public class CustomItems {
    public static Item wand = new ItemNpcWand();
    public static Item cloner = new ItemNpcCloner();
    public static Item scripter = new ItemNpcScripter();
    public static Item moving = new ItemNpcMovingPath();
    public static Item mount = new ItemMounter();
    public static Item teleporter = new ItemTeleporter();
    public static ItemScripted scripted_item = new ItemScripted(new Item.Settings().maxCount(1));
    public static ItemNbtBook nbt_book = new ItemNbtBook();
    public static final Item soulstoneEmpty = new ItemSoulstoneEmpty();
    public static final Item soulstoneFull = new ItemSoulstoneFilled();

    public static void registerItems() {
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcwand", wand);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcmobcloner", cloner);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcscripter", scripter);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcmovingpath", moving);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcmounter", mount);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcteleporter", teleporter);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcsoulstoneempty", soulstoneEmpty);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcsoulstonefilled", soulstoneFull);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:scripted_item", (scripted_item));
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:nbt_book", (nbt_book));
    }

    public static void registerDispenser() {
        DispenserBlock.registerBehavior((ItemConvertible)soulstoneFull, (DispenserBehavior)new ItemDispenserBehavior(){

            public ItemStack dispenseSilently(BlockPointer source, ItemStack item) {
                Direction enumfacing = (Direction)source.getBlockState().get((Property)DispenserBlock.FACING);
                double x = source.getX() + (double)enumfacing.getOffsetX();
                double z = source.getZ() + (double)enumfacing.getOffsetZ();
                ItemSoulstoneFilled.Spawn(null, item, (World)source.getWorld(), new BlockPos((int)x, (int)source.getY(), (int)z));
                item.split(1);
                return item;
            }
        });
    }
}

