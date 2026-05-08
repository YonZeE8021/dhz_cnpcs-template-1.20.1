/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry
 *  net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry$DynamicItemRenderer
 *  net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactories
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.util.Identifier
 */
package noppes.npcs.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.item.ItemConvertible;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomEntities;
import noppes.npcs.entity.EntityChairMount;
import noppes.npcs.client.CustomTileEntityItemStackRenderer;
import noppes.npcs.client.model.ModelClassicPlayer;
import noppes.npcs.client.model.ModelNPCGolem;
import noppes.npcs.client.model.ModelNpcCrystal;
import noppes.npcs.client.model.ModelNpcDragon;
import noppes.npcs.client.model.ModelNpcSlime;
import noppes.npcs.client.model.ModelPlayer64x32;
import noppes.npcs.client.model.ModelPony;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.client.renderer.RenderNPCPony;
import noppes.npcs.client.renderer.RenderNpcCrystal;
import noppes.npcs.client.renderer.RenderNpcDragon;
import noppes.npcs.client.renderer.RenderNpcSlime;
import noppes.npcs.client.renderer.RenderProjectile;
import noppes.npcs.client.renderer.blocks.BlockBuilderRenderer;
import noppes.npcs.client.renderer.blocks.BlockCarpentryBenchRenderer;
import noppes.npcs.client.renderer.blocks.BlockCopyRenderer;
import noppes.npcs.client.renderer.blocks.BlockDoorRenderer;
import noppes.npcs.client.renderer.blocks.BlockMailboxRenderer;
import noppes.npcs.client.renderer.blocks.BlockScriptedRenderer;

@Environment(value=EnvType.CLIENT)
public class CustomRenderers {
    @Environment(value=EnvType.CLIENT)
    public static void registerEntityRenderer() {
        EntityRendererRegistry.register(CustomEntities.entityNpcPony, manager -> new RenderNPCPony(manager, new ModelPony()));
        EntityRendererRegistry.register(CustomEntities.entityNpcCrystal, manager -> new RenderNpcCrystal(manager, new ModelNpcCrystal()));
        EntityRendererRegistry.register(CustomEntities.entityNpcDragon, manager -> new RenderNpcDragon(manager, new ModelNpcDragon(), 0.5f));
        EntityRendererRegistry.register(CustomEntities.entityNpcSlime, manager -> new RenderNpcSlime(manager, new ModelNpcSlime(16), (Model)new ModelNpcSlime(0), 0.25f));
        EntityRendererRegistry.register(CustomEntities.entityProjectile, manager -> new RenderProjectile(manager));
        EntityRendererRegistry.register(CustomEntities.entityCustomNpc, manager -> new RenderCustomNpc(manager, new PlayerEntityModel(manager.getModelLoader().getModelPart(EntityModelLayers.PLAYER), false)));
        EntityRendererRegistry.register(CustomEntities.entityNPC64x32, manager -> new RenderCustomNpc(manager, new ModelPlayer64x32(manager.getModelLoader().getModelPart(EntityModelLayers.PLAYER))));
        EntityRendererRegistry.register(CustomEntities.entityNPCGolem, manager -> new RenderNPCInterface(manager, new ModelNPCGolem(0.0f), 0.0f));
        EntityRendererRegistry.register(CustomEntities.entityNpcAlex, manager -> new RenderCustomNpc(manager, new PlayerEntityModel(manager.getModelLoader().getModelPart(EntityModelLayers.PLAYER_SLIM), true)));
        EntityRendererRegistry.register(CustomEntities.entityNpcClassicPlayer, manager -> new RenderCustomNpc(manager, new ModelClassicPlayer(manager.getModelLoader().getModelPart(EntityModelLayers.PLAYER), 0.0f)));
        EntityRendererRegistry.register(
            (EntityType<EntityChairMount>) CustomEntities.entityChairMount,
            ctx -> new EntityRenderer<EntityChairMount>(ctx) {
                private static final Identifier TEX = new Identifier("minecraft", "textures/misc/white.png");

                @Override
                public Identifier getTexture(EntityChairMount entity) {
                    return TEX;
                }
            });
        BlockEntityRendererFactories.register(CustomBlocks.tile_anvil, BlockCarpentryBenchRenderer::new);
        BlockEntityRendererFactories.register(CustomBlocks.tile_mailbox, BlockMailboxRenderer::new);
        BlockEntityRendererFactories.register(CustomBlocks.tile_scripted, BlockScriptedRenderer::new);
        BlockEntityRendererFactories.register(CustomBlocks.tile_scripteddoor, BlockDoorRenderer::new);
        BlockEntityRendererFactories.register(CustomBlocks.tile_copy, BlockCopyRenderer::new);
        BlockEntityRendererFactories.register(CustomBlocks.tile_builder, BlockBuilderRenderer::new);
        BuiltinItemRendererRegistry.INSTANCE.register((ItemConvertible)CustomBlocks.redstone_item, (BuiltinItemRendererRegistry.DynamicItemRenderer)CustomTileEntityItemStackRenderer.instance());
        BuiltinItemRendererRegistry.INSTANCE.register((ItemConvertible)CustomBlocks.mailbox_item, (BuiltinItemRendererRegistry.DynamicItemRenderer)CustomTileEntityItemStackRenderer.instance());
        BuiltinItemRendererRegistry.INSTANCE.register((ItemConvertible)CustomBlocks.mailbox2_item, (BuiltinItemRendererRegistry.DynamicItemRenderer)CustomTileEntityItemStackRenderer.instance());
        BuiltinItemRendererRegistry.INSTANCE.register((ItemConvertible)CustomBlocks.mailbox3_item, (BuiltinItemRendererRegistry.DynamicItemRenderer)CustomTileEntityItemStackRenderer.instance());
        BuiltinItemRendererRegistry.INSTANCE.register((ItemConvertible)CustomBlocks.waypoint_item, (BuiltinItemRendererRegistry.DynamicItemRenderer)CustomTileEntityItemStackRenderer.instance());
        BuiltinItemRendererRegistry.INSTANCE.register((ItemConvertible)CustomBlocks.scripted_item, (BuiltinItemRendererRegistry.DynamicItemRenderer)CustomTileEntityItemStackRenderer.instance());
        BuiltinItemRendererRegistry.INSTANCE.register((ItemConvertible)CustomBlocks.builder_item, (BuiltinItemRendererRegistry.DynamicItemRenderer)CustomTileEntityItemStackRenderer.instance());
        BuiltinItemRendererRegistry.INSTANCE.register((ItemConvertible)CustomBlocks.copy_item, (BuiltinItemRendererRegistry.DynamicItemRenderer)CustomTileEntityItemStackRenderer.instance());
        BuiltinItemRendererRegistry.INSTANCE.register((ItemConvertible)CustomBlocks.border_item, (BuiltinItemRendererRegistry.DynamicItemRenderer)CustomTileEntityItemStackRenderer.instance());
        BuiltinItemRendererRegistry.INSTANCE.register((ItemConvertible)CustomBlocks.carpentry_item, (BuiltinItemRendererRegistry.DynamicItemRenderer)CustomTileEntityItemStackRenderer.instance());
    }
}

