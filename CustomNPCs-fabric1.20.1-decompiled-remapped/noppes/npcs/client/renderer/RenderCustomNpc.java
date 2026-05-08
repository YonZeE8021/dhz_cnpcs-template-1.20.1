/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.UseAction
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.BipedEntityModel$ArmPose
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
 *  net.minecraft.client.render.entity.feature.HeadFeatureRenderer
 *  net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer
 */
package noppes.npcs.client.renderer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import noppes.npcs.ModelData;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.layer.LayerGlow;
import noppes.npcs.client.layer.LayerHeadwear;
import noppes.npcs.client.layer.LayerNpcCloak;
import noppes.npcs.client.layer.LayerParts;
import noppes.npcs.client.layer.LayerPreRender;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.constants.BodyPart;
import noppes.npcs.controllers.CobblemonHelper;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.ArmorLayerMixin;
import noppes.npcs.mixin.LivingRenderer2Mixin;
import noppes.npcs.mixin.LivingRenderer3Mixin;

public class RenderCustomNpc<T extends EntityCustomNpc, M extends BipedEntityModel<T>>
extends RenderNPCInterface<T, M> {
    private float partialTicks;
    private LivingEntity entity;
    private EntityNPCInterface npc;
    private LivingEntityRenderer renderEntity;
    public M npcmodel;
    public Model otherModel;
    public ArmorLayerMixin armorLayer;
    public List<FeatureRenderer<T, M>> npclayers = Lists.newArrayList();
    private FeatureRenderer renderLayer = new FeatureRenderer(null){

        public void render(MatrixStack mStack, VertexConsumerProvider typeBuffer, int lightmapUV, Entity p_225628_4_, float limbSwing, float limbSwingAmount, float partialTicks, float age, float netHeadYaw, float headPitch) {
            for (FeatureRenderer layer : ((LivingRenderer2Mixin)RenderCustomNpc.this.renderEntity).layers()) {
                layer.render(mStack, typeBuffer, lightmapUV, (Entity)RenderCustomNpc.this.entity, limbSwing, limbSwingAmount, partialTicks, age, netHeadYaw, headPitch);
            }
        }
    };
    private final BipedEntityModel renderModel;

    public RenderCustomNpc(EntityRendererFactory.Context manager, M model) {
        super(manager, model, 0.5f);
        this.npcmodel = model;
        this.addFeature((FeatureRenderer)new HeadFeatureRenderer((FeatureRendererContext)this, manager.getModelLoader(), manager.getHeldItemRenderer()));
        this.addFeature(new LayerHeadwear(this));
        this.addFeature(new LayerNpcCloak(this));
        this.addFeature(new LayerParts(this));
        this.addFeature((FeatureRenderer)new HeldItemFeatureRenderer((FeatureRendererContext)this, manager.getHeldItemRenderer()));
        this.addFeature(new LayerGlow(this));
        ArmorFeatureRenderer armorLayer = new ArmorFeatureRenderer((FeatureRendererContext)this, new BipedEntityModel(manager.getPart(EntityModelLayers.PLAYER_INNER_ARMOR)), new BipedEntityModel(manager.getPart(EntityModelLayers.PLAYER_OUTER_ARMOR)), manager.getModelManager());
        this.addFeature((FeatureRenderer)armorLayer);
        this.armorLayer = (ArmorLayerMixin)armorLayer;
        this.renderModel = new BipedEntityModel(manager.getPart(EntityModelLayers.field_27577)){

            public void render(MatrixStack mStack, VertexConsumer iVertex, int lightmapUV, int packedOverlayIn, float red, float green, float blue, float alpha) {
                int color = RenderCustomNpc.this.npc.display.getTint();
                if (color < 0xFFFFFF) {
                    red = (float)(color >> 16 & 0xFF) / 255.0f;
                    green = (float)(color >> 8 & 0xFF) / 255.0f;
                    blue = (float)(color & 0xFF) / 255.0f;
                }
                RenderCustomNpc.this.otherModel.render(mStack, iVertex, lightmapUV, packedOverlayIn, red, green, blue, alpha);
            }

            public void setAngles(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
                if (RenderCustomNpc.this.otherModel instanceof EntityModel) {
                    EntityModel em = (EntityModel)RenderCustomNpc.this.otherModel;
                    em.setAngles((Entity)RenderCustomNpc.this.entity, limbSwing, limbSwingAmount, ((LivingRenderer3Mixin)RenderCustomNpc.this.renderEntity).callGetBob(RenderCustomNpc.this.entity, MinecraftClient.getInstance().getTickDelta()), netHeadYaw, headPitch);
                }
            }

            public void animateModel(Entity npc, float animationPos, float animationSpeed, float partialTicks) {
                Model pixModel;
                if (PixelmonHelper.isPixelmon((Entity)RenderCustomNpc.this.entity) && (pixModel = (Model)PixelmonHelper.getModel(RenderCustomNpc.this.entity)) != null) {
                    RenderCustomNpc.this.otherModel = pixModel;
                    PixelmonHelper.setupModel(RenderCustomNpc.this.entity, pixModel);
                }
                if (RenderCustomNpc.this.otherModel instanceof BipedEntityModel) {
                    BipedEntityModel bm = (BipedEntityModel)RenderCustomNpc.this.otherModel;
                    bm.leaningPitch = ((EntityCustomNpc)npc).getLeaningPitch(partialTicks);
                    bm.sneaking = ((BipedEntityModel)RenderCustomNpc.this.npcmodel).sneaking;
                }
                if (RenderCustomNpc.this.otherModel instanceof EntityModel) {
                    EntityModel em = (EntityModel)RenderCustomNpc.this.otherModel;
                    em.riding = RenderCustomNpc.this.entity.hasVehicle() && RenderCustomNpc.this.entity.getVehicle() != null;
                    em.child = RenderCustomNpc.this.entity.isBaby();
                    em.handSwingProgress = RenderCustomNpc.this.getHandSwingProgress((LivingEntity)((EntityCustomNpc)npc), partialTicks);
                    em.animateModel((Entity)RenderCustomNpc.this.entity, animationPos, animationSpeed, partialTicks);
                }
            }
        };
    }

    public Vec3d getRenderOffset(T npc, float partialTicks) {
        float xOffset = 0.0f;
        float yOffset = ((EntityCustomNpc)((Object)npc)).currentAnimation == 0 ? ((EntityCustomNpc)((Object)npc)).ais.bodyOffsetY / 10.0f - 0.5f : 0.0f;
        float zOffset = 0.0f;
        if (((EntityNPCInterface)((Object)npc)).isAlive()) {
            if (((EntityNPCInterface)((Object)npc)).isSleeping()) {
                xOffset = (float)(-Math.cos(Math.toRadians(180 - ((EntityCustomNpc)((Object)npc)).ais.orientation)));
                zOffset = (float)(-Math.sin(Math.toRadians(((EntityCustomNpc)((Object)npc)).ais.orientation)));
                yOffset += 0.14f;
            } else if (((EntityCustomNpc)((Object)npc)).currentAnimation == 1 || npc.hasVehicle()) {
                yOffset -= 0.5f - ((EntityCustomNpc)((Object)npc)).modelData.getLegsY() * 0.8f;
            } else if (((EntityNPCInterface)((Object)npc)).isInSneakingPose()) {
                yOffset = (float)((double)yOffset - 0.125);
            }
        }
        return new Vec3d((double)xOffset, (double)(yOffset * ((float)((EntityCustomNpc)((Object)npc)).display.getSize() / 5.0f)), (double)zOffset);
    }

    void hideParts() {
        if (this.npc instanceof EntityCustomNpc) {
            ModelData data = ModelData.get((EntityCustomNpc)this.npc);
            ((BipedEntityModel)this.npcmodel).leftLeg.visible = !data.hiddenParts.contains((Object)BodyPart.LEFT_LEG);
            ((BipedEntityModel)this.npcmodel).rightLeg.visible = !data.hiddenParts.contains((Object)BodyPart.RIGHT_LEG);
            ((BipedEntityModel)this.npcmodel).leftArm.visible = !data.hiddenParts.contains((Object)BodyPart.LEFT_ARM);
            ((BipedEntityModel)this.npcmodel).rightArm.visible = !data.hiddenParts.contains((Object)BodyPart.RIGHT_ARM);
            ((BipedEntityModel)this.npcmodel).body.visible = !data.hiddenParts.contains((Object)BodyPart.BODY);
            ((BipedEntityModel)this.npcmodel).head.visible = !data.hiddenParts.contains((Object)BodyPart.HEAD);
            boolean bl = ((BipedEntityModel)this.npcmodel).hat.visible = !data.hiddenParts.contains((Object)BodyPart.HEAD);
            if (this.npcmodel instanceof PlayerEntityModel) {
                PlayerEntityModel playerModel = (PlayerEntityModel)this.npcmodel;
                playerModel.jacket.visible = !data.hiddenParts.contains((Object)BodyPart.BODY);
                playerModel.leftSleeve.visible = !data.hiddenParts.contains((Object)BodyPart.LEFT_ARM);
                playerModel.rightSleeve.visible = !data.hiddenParts.contains((Object)BodyPart.RIGHT_ARM);
                playerModel.leftPants.visible = !data.hiddenParts.contains((Object)BodyPart.LEFT_LEG);
                playerModel.rightPants.visible = !data.hiddenParts.contains((Object)BodyPart.RIGHT_LEG);
            }
        }
    }

    @Override
    public void render(T npc, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider buffer, int packedLight) {
        this.npc = npc;
        this.partialTicks = partialTicks;
        LivingEntity prevEntity = this.entity;
        this.entity = ((EntityCustomNpc)((Object)npc)).modelData.getEntity((EntityNPCInterface)((Object)npc));
        if (prevEntity != null && this.entity == null) {
            this.model = this.npcmodel;
            this.renderEntity = null;
            this.features.clear();
            this.features.addAll(this.npclayers);
        }
        if (this.entity != null) {
            EntityRenderer render = this.dispatcher.getRenderer((Entity)this.entity);
            if (((EntityCustomNpc)((Object)npc)).modelData.simpleRender) {
                this.renderEntity = null;
                matrixStack.push();
                render.render((Entity)this.entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
                this.renderNameTag(npc, (Text)Text.empty(), matrixStack, buffer, packedLight);
                matrixStack.pop();
                return;
            }
            if (render instanceof LivingEntityRenderer) {
                this.renderEntity = (LivingEntityRenderer)render;
                this.otherModel = this.renderEntity.getModel();
                if (CobblemonHelper.Enabled && CobblemonHelper.isPokemon((Entity)this.entity)) {
                    this.otherModel = CobblemonHelper.getPokemonModel((Entity)this.entity);
                }
                this.model = this.renderModel;
                this.features.clear();
                this.features.add(this.renderLayer);
                this.features.add(new LayerGlow(this));
                if (render instanceof RenderCustomNpc) {
                    for (FeatureRenderer layer : ((LivingRenderer2Mixin)this.renderEntity).layers()) {
                        if (!(layer instanceof LayerPreRender)) continue;
                        ((LayerPreRender)layer).preRender((EntityCustomNpc)this.entity);
                    }
                }
            } else {
                this.renderEntity = null;
                this.entity = null;
                this.model = this.npcmodel;
                this.features.clear();
                this.features.addAll(this.npclayers);
            }
        } else {
            this.hideParts();
            List list = this.features;
            for (FeatureRenderer layer : list) {
                if (!(layer instanceof LayerPreRender)) continue;
                ((LayerPreRender)layer).preRender((EntityCustomNpc)((Object)npc));
            }
        }
        ((BipedEntityModel)this.npcmodel).rightArmPose = this.getPose(npc, ((EntityNPCInterface)((Object)npc)).getMainHandStack());
        ((BipedEntityModel)this.npcmodel).leftArmPose = this.getPose(npc, ((EntityNPCInterface)((Object)npc)).getOffHandStack());
        super.render(npc, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    protected RenderLayer getRenderType(T p_230496_1_, boolean p_230496_2_, boolean p_230496_3_, boolean p_230496_4_) {
        Identifier resourcelocation = this.getTextureLocation(p_230496_1_);
        if (p_230496_2_ && this.model == this.renderModel) {
            return this.otherModel.getLayer(resourcelocation);
        }
        if (this.entity == null) {
            return ((BipedEntityModel)this.model).getLayer(resourcelocation);
        }
        return super.getRenderLayer(p_230496_1_, p_230496_2_, p_230496_3_, p_230496_4_);
    }

    public BipedEntityModel.ArmPose getPose(T npc, ItemStack item) {
        if (NoppesUtilServer.IsItemStackNull(item)) {
            return BipedEntityModel.ArmPose.field_3409;
        }
        if (npc.getItemUseTimeLeft() > 0) {
            UseAction enumaction = item.getUseAction();
            if (enumaction == UseAction.field_8949) {
                return BipedEntityModel.ArmPose.field_3406;
            }
            if (enumaction == UseAction.field_8953) {
                return BipedEntityModel.ArmPose.field_3403;
            }
        }
        return BipedEntityModel.ArmPose.field_3410;
    }

    @Override
    protected void scale(T npc, MatrixStack matrixScale, float f) {
        if (this.renderEntity != null) {
            this.renderColor((EntityNPCInterface)((Object)npc));
            int size = ((EntityCustomNpc)((Object)npc)).display.getSize();
            if (this.entity instanceof EntityNPCInterface) {
                ((EntityNPCInterface)this.entity).display.setSize(5);
            }
            EntityRenderer render = this.dispatcher.getRenderer((Entity)this.entity);
            if (!((EntityCustomNpc)((Object)npc)).modelData.simpleRender && render instanceof LivingEntityRenderer) {
                ((LivingRenderer3Mixin)render).callScale(this.entity, matrixScale, this.partialTicks);
            }
            ((EntityCustomNpc)((Object)npc)).display.setSize(size);
            matrixScale.scale(0.2f * (float)((EntityCustomNpc)((Object)npc)).display.getSize(), 0.2f * (float)((EntityCustomNpc)((Object)npc)).display.getSize(), 0.2f * (float)((EntityCustomNpc)((Object)npc)).display.getSize());
        } else {
            super.scale(npc, matrixScale, f);
        }
    }
}

