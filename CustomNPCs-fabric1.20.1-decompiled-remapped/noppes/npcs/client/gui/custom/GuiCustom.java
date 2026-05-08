/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 */
package noppes.npcs.client.gui.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.wrapper.gui.CustomGuiTexturedRectWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.client.gui.custom.GuiCustomComponents;
import noppes.npcs.client.gui.custom.GuiCustomScrollingPanel;
import noppes.npcs.client.gui.custom.components.CustomGuiTexturedRect;
import noppes.npcs.client.gui.custom.interfaces.IGuiComponent;
import noppes.npcs.client.gui.util.GuiTooltipUtils;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCustomGuiSubGuiClosed;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class GuiCustom
extends HandledScreen<ContainerCustomGui>
implements IGuiData {
    protected CustomGuiTexturedRect background;
    public CustomGuiWrapper guiWrapper;
    public List<Text> hoverText;
    protected GuiCustomComponents components = new GuiCustomComponents();
    protected GuiCustomScrollingPanel scrollingPanel = new GuiCustomScrollingPanel();
    public GuiCustom subgui = null;
    public GuiCustom parent = null;
    public PlayerInventory inv;
    public InitCallback initCallback;

    public GuiCustom(ContainerCustomGui container, PlayerInventory inv, Text titleIn) {
        super((ScreenHandler)container, inv, titleIn);
        this.inv = inv;
    }

    public void init() {
        super.init();
        if (this.guiWrapper != null) {
            this.scrollingPanel.setComponents(this, this.guiWrapper.getScrollingPanel());
            this.components.setComponents(this, this.guiWrapper);
        }
        if (this.initCallback != null) {
            this.initCallback.init();
        }
        if (this.subgui != null) {
            this.subgui.init();
        }
    }

    public void handledScreenTick() {
        if (this.subgui != null) {
            this.subgui.handledScreenTick();
        } else {
            this.components.containerTick();
            this.scrollingPanel.containerTick();
        }
    }

    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        this.hoverText = null;
        MatrixStack matrixStack = graphics.getMatrices();
        this.renderBackground(graphics);
        MatrixStack posestack = RenderSystem.getModelViewStack();
        posestack.push();
        posestack.translate((float)this.x, (float)this.y, 0.0f);
        RenderSystem.applyModelViewMatrix();
        matrixStack.push();
        if (this.background != null) {
            this.background.onRender(graphics, mouseX, mouseY, partialTicks);
        }
        this.components.render(graphics, mouseX - this.x, mouseY - this.y, partialTicks);
        this.scrollingPanel.render(graphics, mouseX - this.x, mouseY - this.y, partialTicks);
        if (this.hoverText != null && !this.hoverText.isEmpty() && this.subgui == null) {
            GuiTooltipUtils.renderTooltip(graphics, this.textRenderer, this.hoverText, Optional.empty(), mouseX - this.x, mouseY - this.y);
        }
        posestack.pop();
        RenderSystem.applyModelViewMatrix();
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (this.subgui == null) {
            this.drawMouseoverTooltip(graphics, mouseX, mouseY);
        }
        matrixStack.pop();
        if (this.subgui != null) {
            matrixStack.push();
            posestack.push();
            posestack.translate(0.0f, 0.0f, 40.0f);
            RenderSystem.applyModelViewMatrix();
            matrixStack.translate(0.0f, 0.0f, 40.0f);
            this.subgui.render(graphics, mouseX, mouseY, partialTicks);
            matrixStack.pop();
            posestack.pop();
            RenderSystem.applyModelViewMatrix();
        }
    }

    protected void drawBackground(DrawContext p_283065_, float p_97788_, int p_97789_, int p_97790_) {
    }

    protected void drawForeground(DrawContext p_281635_, int p_282681_, int p_283686_) {
    }

    public boolean charTyped(char typedChar, int keyCode) {
        if (this.subgui != null) {
            return this.subgui.charTyped(typedChar, keyCode);
        }
        if (this.components.charTyped(typedChar, keyCode)) {
            return true;
        }
        if (this.scrollingPanel.charTyped(typedChar, keyCode)) {
            return true;
        }
        return super.charTyped(typedChar, keyCode);
    }

    public boolean keyPressed(int key, int p_keyPressed_2_, int p_keyPressed_3_) {
        if (this.subgui != null) {
            return this.subgui.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_);
        }
        if (this.components.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_)) {
            return true;
        }
        if (this.scrollingPanel.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_)) {
            return true;
        }
        return super.keyPressed(key, p_keyPressed_2_, p_keyPressed_3_);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (this.subgui != null) {
            return this.subgui.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (this.components.mouseClicked(mouseX - (double)this.x, mouseY - (double)this.y, mouseButton)) {
            return true;
        }
        if (this.scrollingPanel.mouseClicked(mouseX - (double)this.x, mouseY - (double)this.y, mouseButton)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double mouseScrolled) {
        if (this.subgui != null) {
            return this.subgui.mouseScrolled(mouseX, mouseY, mouseScrolled);
        }
        if (super.mouseScrolled(mouseX, mouseY, mouseScrolled)) {
            return true;
        }
        return this.scrollingPanel.mouseScrolled(mouseX - (double)this.x, mouseY - (double)this.y, mouseScrolled);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dx, double dy) {
        if (this.subgui != null) {
            return this.subgui.mouseDragged(mouseX, mouseY, mouseButton, dx, dy);
        }
        if (this.components.mouseDragged(mouseX - (double)this.x, mouseY - (double)this.y, mouseButton, dx, dy)) {
            return true;
        }
        if (this.scrollingPanel.mouseDragged(mouseX - (double)this.x, mouseY - (double)this.y, mouseButton, dx, dy)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, mouseButton, dx, dy);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (this.subgui != null) {
            return this.subgui.mouseReleased(mouseX, mouseY, mouseButton);
        }
        if (this.components.mouseReleased(mouseX - (double)this.x, mouseY - (double)this.y, mouseButton)) {
            return true;
        }
        if (this.scrollingPanel.mouseReleased(mouseX - (double)this.x, mouseY - (double)this.y, mouseButton)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public boolean shouldPause() {
        if (this.guiWrapper != null) {
            return this.guiWrapper.getDoesPauseGame();
        }
        return true;
    }

    public void close() {
        if (this.subgui == null) {
            if (this.parent == null) {
                super.close();
            } else {
                Packets.sendServer(new SPacketCustomGuiSubGuiClosed());
                this.parent.subgui = null;
            }
        } else {
            this.subgui.close();
        }
    }

    @Override
    public void setGuiData(NbtCompound compound) {
        this.setGuiWrapper((CustomGuiWrapper)new CustomGuiWrapper((IPlayer)NpcAPI.Instance().getIEntity((Entity)MinecraftClient.getInstance().player)).fromNBT(compound));
        this.init();
    }

    public void resize(MinecraftClient minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        if (this.subgui != null) {
            this.subgui.resize(minecraft, width, height);
        }
    }

    public void setGuiWrapper(CustomGuiWrapper guiWrapper) {
        this.guiWrapper = guiWrapper;
        this.backgroundWidth = guiWrapper.getWidth();
        this.backgroundHeight = guiWrapper.getHeight();
        this.background = new CustomGuiTexturedRect(this, (CustomGuiTexturedRectWrapper)guiWrapper.getBackgroundRect());
        if (guiWrapper.hasSubGui()) {
            if (this.subgui == null) {
                this.subgui = new GuiCustom((ContainerCustomGui)this.handler, MinecraftClient.getInstance().player.getInventory(), (Text)Text.empty());
                this.subgui.init(this.client, this.width, this.height);
            }
            this.subgui.parent = this;
            this.subgui.setGuiWrapper(guiWrapper.getSubGui());
        } else {
            ((ContainerCustomGui)this.handler).setGui(guiWrapper, (PlayerEntity)MinecraftClient.getInstance().player);
            this.subgui = null;
            if (this.parent == null) {
                this.init();
            }
        }
    }

    public IGuiComponent getComponent(UUID id) {
        Optional<IGuiComponent> c = this.components.components.values().stream().filter(t -> t.component() != null && t.component().getUniqueID().equals(id)).findFirst();
        if (c.isPresent()) {
            return c.get();
        }
        c = this.scrollingPanel.components.values().stream().filter(t -> t.component() != null && t.component().getUniqueID().equals(id)).findFirst();
        if (c.isPresent()) {
            return c.get();
        }
        if (this.subgui != null) {
            return this.subgui.getComponent(id);
        }
        return null;
    }

    public int getTotalGuiLeft() {
        if (this.parent != null) {
            return this.parent.getTotalGuiLeft() + this.x;
        }
        return this.x;
    }

    public int getTotalGuiTop() {
        if (this.parent != null) {
            return this.parent.getTotalGuiTop() + this.y;
        }
        return this.y;
    }

    public void add(IGuiComponent component) {
        this.components.components.put(component.getID(), component);
    }

    public void addPanel(IGuiComponent component) {
        this.scrollingPanel.components.put(component.getID(), component);
    }

    public static interface InitCallback {
        public void init();
    }
}

