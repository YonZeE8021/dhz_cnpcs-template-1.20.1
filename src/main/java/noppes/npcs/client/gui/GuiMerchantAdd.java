/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.text.Text
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 */
package noppes.npcs.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import noppes.npcs.containers.ContainerMerchantAdd;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiWrapper;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;

@Environment(value=EnvType.CLIENT)
public class GuiMerchantAdd
extends HandledScreen<ContainerMerchantAdd>
implements IGuiInterface {
    public GuiMerchantAdd(ContainerMerchantAdd container, PlayerInventory inv, Text titleIn) {
        super(container, inv, titleIn);
    }

    protected void drawBackground(DrawContext p_283065_, float p_97788_, int p_97789_, int p_97790_) {
    }

    @Override
    public void buttonEvent(GuiButtonNop button) {
    }

    @Override
    public void save() {
    }

    @Override
    public boolean hasSubGui() {
        return false;
    }

    @Override
    public Screen getSubGui() {
        return null;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public Screen getParent() {
        return null;
    }

    @Override
    public void elementClicked() {
    }

    @Override
    public void subGuiClosed(Screen subgui) {
    }

    @Override
    public GuiWrapper getWrapper() {
        return null;
    }

    @Override
    public void initGui() {
        this.init();
    }
}

