/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.resource.Resource
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui.model;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiComponentWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTexturedRectWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.client.gui.custom.components.CustomGuiButton;
import noppes.npcs.client.gui.custom.components.CustomGuiTextField;
import noppes.npcs.client.gui.custom.components.CustomGuiTexturedRect;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiModelColor
extends GuiCustom
implements ITextfieldListener {
    private GuiCustom parent;
    private static final Identifier colorPicker = new Identifier("moreplayermodels:textures/gui/color.png");
    private static final Identifier colorgui = new Identifier("moreplayermodels:textures/gui/color_gui.png");
    private int colorX;
    private int colorY;
    public int color;
    private CustomGuiTextField textfield;
    private CustomGuiButton button;
    private ColorCallback callback;

    public GuiModelColor(GuiCustom parent, int c, ColorCallback callback) {
        super((ContainerCustomGui)parent.getScreenHandler(), parent.inv, (Text)Text.empty());
        this.parent = parent;
        this.callback = callback;
        this.backgroundHeight = 170;
        this.backgroundWidth = 130;
        this.color = c;
        CustomGuiTexturedRectWrapper bg = new CustomGuiTexturedRectWrapper();
        bg.setTexture("customnpcs:textures/gui/components.png").setSize(this.backgroundWidth, this.backgroundHeight);
        bg.setTextureOffset(0, 0).setRepeatingTexture(64, 64, 4);
        this.background = new CustomGuiTexturedRect(this, bg);
        this.textfield = new CustomGuiTextField(this, (CustomGuiTextFieldWrapper)new CustomGuiTextFieldWrapper(24, 35, 25, 60, 20).setCharacterType(2).setColor(this.color).setText(this.getColor()).setOnChange((gui, text) -> {
            this.color = Integer.parseInt(text.getText(), 16);
            callback.color(this.color);
            this.textfield.setEditableColor(this.color);
        }));
        this.button = new CustomGuiButton(this, (CustomGuiButtonWrapper)((CustomGuiComponentWrapper)((Object)new CustomGuiButtonWrapper(66, "x", 107, 8, 20, 20).setOnPress((gui, button) -> {
            parent.subgui = null;
        }))).setDisablePackets());
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public void init() {
        super.init();
        this.add(this.textfield);
        this.add(this.button);
        this.background.setTexture(colorgui);
        this.colorX = this.x + 4;
        this.colorY = this.y + 50;
    }

    @Override
    public void render(DrawContext graphics, int par1, int limbSwingAmount, float par3) {
        super.render(graphics, par1, limbSwingAmount, par3);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)colorPicker);
        graphics.drawTexture(colorPicker, this.colorX, this.colorY, 0, 0, 120, 120);
    }

    @Override
    public boolean mouseClicked(double i, double j, int k) {
        super.mouseClicked(i, j, k);
        if (i < (double)this.colorX || i > (double)(this.colorX + 120) || j < (double)this.colorY || j > (double)(this.colorY + 120)) {
            return false;
        }
        Resource resource = this.client.getResourceManager().getResource(colorPicker).orElse(null);
        if (resource != null) {
            try (InputStream stream = resource.getInputStream();){
                BufferedImage bufferedimage = ImageIO.read(stream);
                int color = bufferedimage.getRGB((int)(i - (double)this.x - 4.0) * 4, (int)(j - (double)this.y - 50.0) * 4) & 0xFFFFFF;
                if (color != 0) {
                    this.color = color;
                    this.callback.color(color);
                    this.textfield.setEditableColor(color);
                    this.textfield.setText(this.getColor());
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return true;
    }

    @Override
    public void unFocused(GuiTextFieldNop textfield) {
        try {
            this.color = Integer.parseInt(textfield.getText(), 16);
        }
        catch (NumberFormatException e) {
            this.color = 0;
        }
        this.callback.color(this.color);
        textfield.setEditableColor(this.color);
    }

    public String getColor() {
        Object str = Integer.toHexString(this.color);
        while (((String)str).length() < 6) {
            str = "0" + (String)str;
        }
        return str;
    }

    public static interface ColorCallback {
        public void color(int var1);
    }
}

