/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.world.World
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.text.Text
 *  net.minecraft.client.render.DiffuseLighting
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.client.render.entity.EntityRenderDispatcher
 */
package noppes.npcs.client.gui.custom.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiEntityDisplayWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import noppes.npcs.entity.EntityNPCInterface;

public class CustomGuiEntityDisplay
extends ClickableWidget
implements IGuiComponent {
    private GuiCustom parent;
    public CustomGuiEntityDisplayWrapper component;
    private Entity entity;
    public int id;

    public CustomGuiEntityDisplay(GuiCustom parent, CustomGuiEntityDisplayWrapper component) {
        super(component.getPosX(), component.getPosY(), component.getWidth(), component.getHeight(), (Text)Text.empty());
        this.component = component;
        this.parent = parent;
        this.init();
    }

    @Override
    public void init() {
        this.id = this.component.getID();
        this.setX(this.component.getPosX());
        this.setY(this.component.getPosY());
        this.setWidth(this.component.getWidth());
        this.height = this.component.getHeight();
        if (this.component.entityId != -1) {
            this.entity = MinecraftClient.getInstance().player.getEntityWorld().getEntityById(this.component.entityId);
        } else if (!this.component.getEntityData().isEmpty()) {
            this.entity = EntityType.getEntityFromNbt((NbtCompound)this.component.getEntityData().getMCNBT(), (World)MinecraftClient.getInstance().world).orElse(null);
        }
        this.active = this.component.getEnabled() && this.component.getVisible();
        this.visible = this.component.getVisible();
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public void onRender(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        boolean hovered;
        if (!this.visible) {
            return;
        }
        if (this.component.getBackground()) {
            graphics.fillGradient(this.getX(), this.getY(), this.width + this.getX(), this.height + this.getY(), -1072689136, -804253680);
        }
        if (this.entity != null) {
            CustomGuiEntityDisplay.drawEntity(graphics, this.entity, this.getX(), this.getY(), this.component.getScale(), this.component.getRotation() / 2 + 180, mouseX, mouseY, (float)this.width / 2.0f, (float)this.height * 0.9f, this.component.isFollowingCursor);
        }
        boolean bl = hovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        if (hovered && this.component.hasHoverText()) {
            this.parent.hoverText = this.component.getHoverTextList();
        }
    }

    protected void renderButton(DrawContext p_282139_, int p_268034_, int p_268009_, float p_268085_) {
    }

    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
        return true;
    }

    public static CustomGuiEntityDisplay fromComponent(GuiCustom parent, CustomGuiEntityDisplayWrapper component) {
        CustomGuiEntityDisplay btn = new CustomGuiEntityDisplay(parent, component);
        return btn;
    }

    public static void drawEntity(DrawContext graphics, Entity entity, int x, int y, float zoomed, int rotation, int xMouse, int yMouse, float guiLeft, float guiTop) {
        CustomGuiEntityDisplay.drawEntity(graphics, entity, x, y, zoomed, rotation, xMouse, yMouse, guiLeft, guiTop, true);
    }

    public static void drawEntity(DrawContext graphics, Entity entity, int x, int y, float zoomed, int rotation, int xMouse, int yMouse, float guiLeft, float guiTop, boolean followCursor) {
        EntityNPCInterface npc = null;
        if (entity instanceof EntityNPCInterface) {
            npc = (EntityNPCInterface)entity;
        }
        LivingEntity livingEntity = null;
        if (entity instanceof LivingEntity) {
            livingEntity = (LivingEntity)entity;
        }
        float f3 = entity.getYaw();
        float f4 = entity.getPitch();
        float f2 = 0.0f;
        float f5 = 0.0f;
        float f6 = 0.0f;
        if (livingEntity != null) {
            f2 = livingEntity.bodyYaw;
            f5 = livingEntity.prevHeadYaw;
            f6 = livingEntity.headYaw;
        }
        float scale = 1.0f;
        if ((double)entity.getHeight() > 2.4) {
            scale = 2.0f / entity.getHeight();
        }
        float f7 = guiLeft + (float)x - (float)xMouse;
        float f8 = (guiTop + (float)y - 50.0f * scale * zoomed) * (entity.getHeight() / entity.getStandingEyeHeight()) - (float)yMouse;
        if (followCursor) {
            entity.setYaw((float)Math.atan(f7 / 80.0f) * 40.0f + (float)rotation);
            entity.setPitch(-((float)Math.atan(f8 / 40.0f)) * 20.0f);
        } else {
            entity.setYaw((float)rotation);
            entity.setPitch(0.0f);
        }
        if (livingEntity != null) {
            livingEntity.headYaw = livingEntity.bodyYaw = entity.getYaw();
            livingEntity.prevHeadYaw = livingEntity.bodyYaw;
        }
        int orientation = 0;
        int showname = 0;
        if (npc != null) {
            orientation = npc.ais.orientation;
            npc.ais.orientation = (int)entity.getYaw();
            showname = npc.display.getShowName();
            npc.display.setShowName(1);
        }
        float fs = 30.0f * scale * zoomed;
        MatrixStack posestack = RenderSystem.getModelViewStack();
        posestack.translate(0.0f, 0.0f, 1050.0f);
        posestack.scale(1.0f, 1.0f, -1.0f);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(guiLeft + (float)x, guiTop + (float)y, 0.0f);
        matrixStack.scale(fs, fs, fs);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        matrixStack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)rotation));
        DiffuseLighting.method_34742();
        EntityRenderDispatcher entityrenderdispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        entityrenderdispatcher.setRenderShadows(false);
        RenderSystem.runAsFancy(() -> entityrenderdispatcher.render(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, matrixStack, (VertexConsumerProvider)graphics.getVertexConsumers(), 0xF000F0));
        graphics.draw();
        entityrenderdispatcher.setRenderShadows(true);
        posestack.scale(1.0f, 1.0f, -1.0f);
        posestack.translate(0.0f, 0.0f, -1050.0f);
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
        matrixStack.pop();
        entity.setYaw(f3);
        entity.setPitch(f4);
        if (livingEntity != null) {
            livingEntity.bodyYaw = f2;
            livingEntity.prevHeadYaw = f5;
            livingEntity.headYaw = f6;
        }
        if (npc != null) {
            npc.ais.orientation = orientation;
            npc.display.setShowName(showname);
        }
    }

    public void appendClickableNarrations(NarrationMessageBuilder p_169152_) {
    }

    @Override
    public ICustomGuiComponent component() {
        return this.component;
    }
}

