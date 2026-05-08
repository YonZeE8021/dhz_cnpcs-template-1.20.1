/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL$TypeReference
 *  net.minecraft.datafixer.TypeReferences
 *  net.minecraft.util.Util
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$Settings
 *  net.minecraft.block.Block
 *  net.minecraft.registry.Registry
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.BlockEntityType$Builder
 *  net.minecraft.block.entity.BlockEntityType$BlockEntityFactory
 *  net.minecraft.registry.Registries
 */
package noppes.npcs;

import com.mojang.datafixers.DSL;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Util;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.registry.Registry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import noppes.npcs.blocks.BlockBorder;
import noppes.npcs.blocks.BlockBuilder;
import noppes.npcs.blocks.BlockCarpentryBench;
import noppes.npcs.blocks.BlockCopy;
import noppes.npcs.blocks.BlockMailbox;
import noppes.npcs.blocks.BlockNpcRedstone;
import noppes.npcs.blocks.BlockScripted;
import noppes.npcs.blocks.BlockScriptedDoor;
import noppes.npcs.blocks.BlockWaypoint;
import noppes.npcs.blocks.tiles.TileBlockAnvil;
import noppes.npcs.blocks.tiles.TileBorder;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.blocks.tiles.TileCopy;
import noppes.npcs.blocks.tiles.TileMailbox;
import noppes.npcs.blocks.tiles.TileRedstoneBlock;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.blocks.tiles.TileWaypoint;
import noppes.npcs.items.ItemNpcBlock;
import noppes.npcs.items.ItemScriptedDoor;

public class CustomBlocks {
    public static Block redstone = new BlockNpcRedstone();
    public static Item redstone_item = CustomBlocks.createItem(redstone);
    public static Block mailbox = new BlockMailbox(0);
    public static Item mailbox_item = CustomBlocks.createItem(mailbox);
    public static Block mailbox2 = new BlockMailbox(1);
    public static Item mailbox2_item = CustomBlocks.createItem(mailbox2);
    public static Block mailbox3 = new BlockMailbox(2);
    public static Item mailbox3_item = CustomBlocks.createItem(mailbox3);
    public static Block waypoint = new BlockWaypoint();
    public static Item waypoint_item = CustomBlocks.createItem(waypoint);
    public static Block border = new BlockBorder();
    public static Item border_item = CustomBlocks.createItem(border);
    public static Block scripted = new BlockScripted();
    public static Item scripted_item = CustomBlocks.createItem(scripted);
    public static Block scripted_door = new BlockScriptedDoor();
    public static Item scripted_door_item = new ItemScriptedDoor(scripted_door);
    public static Block builder = new BlockBuilder();
    public static Item builder_item = CustomBlocks.createItem(builder);
    public static Block copy = new BlockCopy();
    public static Item copy_item = CustomBlocks.createItem(copy);
    public static Block carpenty = new BlockCarpentryBench();
    public static Item carpentry_item = CustomBlocks.createItem(carpenty);
    public static BlockEntityType<TileBlockAnvil> tile_anvil = CustomBlocks.createTile("tileblockanvil", TileBlockAnvil::new, carpenty);
    public static BlockEntityType<TileBorder> tile_border = CustomBlocks.createTile("tilenpcborder", TileBorder::new, border);
    public static BlockEntityType<TileBuilder> tile_builder = CustomBlocks.createTile("tilenpcbuilder", TileBuilder::new, builder);
    public static BlockEntityType<TileCopy> tile_copy = CustomBlocks.createTile("tilenpccopy", TileCopy::new, copy);
    public static BlockEntityType<TileMailbox> tile_mailbox = CustomBlocks.createTile("tilemailbox", TileMailbox::new, mailbox, mailbox2, mailbox3);
    public static BlockEntityType<TileRedstoneBlock> tile_redstoneblock = CustomBlocks.createTile("tileredstoneblock", TileRedstoneBlock::new, redstone);
    public static BlockEntityType<TileScripted> tile_scripted = CustomBlocks.createTile("tilenpcscripted", TileScripted::new, scripted);
    public static BlockEntityType<TileScriptedDoor> tile_scripteddoor = CustomBlocks.createTile("tilenpcscripteddoor", TileScriptedDoor::new, scripted_door);
    public static BlockEntityType<TileWaypoint> tile_waypoint = CustomBlocks.createTile("tilewaypoint", TileWaypoint::new, waypoint);

    public static void registerBlocks() {
        Registry.register((Registry)Registries.BLOCK, (String)"customnpcs:npcredstoneblock", redstone);
        Registry.register((Registry)Registries.BLOCK, (String)"customnpcs:npcmailbox", mailbox);
        Registry.register((Registry)Registries.BLOCK, (String)"customnpcs:npcmailbox2", mailbox2);
        Registry.register((Registry)Registries.BLOCK, (String)"customnpcs:npcmailbox3", mailbox3);
        Registry.register((Registry)Registries.BLOCK, (String)"customnpcs:npcwaypoint", waypoint);
        Registry.register((Registry)Registries.BLOCK, (String)"customnpcs:npcborder", border);
        Registry.register((Registry)Registries.BLOCK, (String)"customnpcs:npcscripted", scripted);
        Registry.register((Registry)Registries.BLOCK, (String)"customnpcs:npcscripteddoor", scripted_door);
        Registry.register((Registry)Registries.BLOCK, (String)"customnpcs:npcbuilderblock", builder);
        Registry.register((Registry)Registries.BLOCK, (String)"customnpcs:npccopyblock", copy);
        Registry.register((Registry)Registries.BLOCK, (String)"customnpcs:npccarpentybench", carpenty);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcredstoneblock", redstone_item);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcmailbox", mailbox_item);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcmailbox2", mailbox2_item);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcmailbox3", mailbox3_item);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcwaypoint", waypoint_item);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcborder", border_item);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcscripted", scripted_item);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcscripteddoortool", scripted_door_item);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npcbuilderblock", builder_item);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npccopyblock", copy_item);
        Registry.register((Registry)Registries.ITEM, (String)"customnpcs:npccarpentybench", carpentry_item);
        Registry.register((Registry)Registries.BLOCK_ENTITY_TYPE, (String)"customnpcs:tileblockanvil", tile_anvil);
        Registry.register((Registry)Registries.BLOCK_ENTITY_TYPE, (String)"customnpcs:tilenpcborder", tile_border);
        Registry.register((Registry)Registries.BLOCK_ENTITY_TYPE, (String)"customnpcs:tilenpcbuilder", tile_builder);
        Registry.register((Registry)Registries.BLOCK_ENTITY_TYPE, (String)"customnpcs:tilenpccopy", tile_copy);
        Registry.register((Registry)Registries.BLOCK_ENTITY_TYPE, (String)"customnpcs:tilemailbox", tile_mailbox);
        Registry.register((Registry)Registries.BLOCK_ENTITY_TYPE, (String)"customnpcs:tileredstoneblock", tile_redstoneblock);
        Registry.register((Registry)Registries.BLOCK_ENTITY_TYPE, (String)"customnpcs:tilenpcscripted", tile_scripted);
        Registry.register((Registry)Registries.BLOCK_ENTITY_TYPE, (String)"customnpcs:tilenpcscripteddoor", tile_scripteddoor);
        Registry.register((Registry)Registries.BLOCK_ENTITY_TYPE, (String)"customnpcs:tilewaypoint", tile_waypoint);
    }

    private static <T extends BlockEntity> BlockEntityType<T> createTile(String key, BlockEntityType.BlockEntityFactory<T> factoryIn, Block ... blocks) {
        BlockEntityType.Builder builder = BlockEntityType.Builder.create(factoryIn, (Block[])blocks);
        return builder.build(Util.getChoiceType((DSL.TypeReference)TypeReferences.BLOCK_ENTITY, (String)key));
    }

    public static Item createItem(Block block) {
        ItemNpcBlock item = new ItemNpcBlock(block, new Item.Settings());
        return item;
    }
}

