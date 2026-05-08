/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
 *  net.minecraft.registry.Registry
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.registry.Registries
 */
package noppes.npcs;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;

public class CustomTabs {
    public static void registerCreativeTab() {
        Registry.register((Registry)Registries.ITEM_GROUP, (Identifier)new Identifier("customnpcs", "cnpcs"), (Object)FabricItemGroup.builder().displayName((Text)Text.literal((String)"cnpcs")).icon(() -> CustomItems.wand.getDefaultStack()).entries((params, output) -> {
            output.add(CustomItems.wand.getDefaultStack());
            output.add(CustomItems.cloner.getDefaultStack());
            output.add(CustomItems.scripter.getDefaultStack());
            output.add(CustomItems.moving.getDefaultStack());
            output.add(CustomItems.mount.getDefaultStack());
            output.add(CustomItems.teleporter.getDefaultStack());
            output.add(CustomItems.scripted_item.getDefaultStack());
            output.add(CustomItems.nbt_book.getDefaultStack());
            output.add(CustomItems.soulstoneEmpty.getDefaultStack());
            output.add(CustomBlocks.redstone_item.getDefaultStack());
            output.add(CustomBlocks.waypoint_item.getDefaultStack());
            output.add(CustomBlocks.border_item.getDefaultStack());
            output.add(CustomBlocks.scripted_item.getDefaultStack());
            output.add(CustomBlocks.scripted_door_item.getDefaultStack());
            output.add(CustomBlocks.builder_item.getDefaultStack());
            output.add(CustomBlocks.copy_item.getDefaultStack());
            output.add(CustomBlocks.carpentry_item.getDefaultStack());
            output.add(CustomBlocks.mailbox_item.getDefaultStack());
            output.add(CustomBlocks.mailbox2_item.getDefaultStack());
            output.add(CustomBlocks.mailbox3_item.getDefaultStack());
        }).build());
    }
}

