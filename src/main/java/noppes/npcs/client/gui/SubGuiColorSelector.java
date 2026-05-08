/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.util.Identifier
 *  net.minecraft.resource.Resource
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import net.minecraft.util.Identifier;
import net.minecraft.resource.Resource;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import noppes.npcs.shared.client.gui.components.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class SubGuiColorSelector
extends GuiBasic
implements ITextfieldListener {
    private static final Identifier resource = new Identifier("customnpcs:textures/gui/color.png");
    private int colorX;
    private int colorY;
    private GuiTextFieldNop textfield;
    public int color;

    public SubGuiColorSelector(int color) {
        this.imageWidth = 176;
        this.imageHeight = 222;
        this.color = color;
        this.setBackground("smallbg.png");
    }

    @Override
    public void init() {
        super.init();
        this.colorX = this.guiLeft + 30;
        this.colorY = this.guiTop + 50;
        this.textfield = new GuiTextFieldNop(0, (Screen)this, this.guiLeft + 53, this.guiTop + 20, 70, 20, this.getColor());
        this.addTextField(this.textfield);
        this.textfield.setEditableColor(this.color);
        this.addButton(new GuiButtonNop(this, 66, this.guiLeft + 112, this.guiTop + 198, 60, 20, "gui.done"));
    }

    public String getColor() {
        String str = Integer.toHexString(this.color);
        while (str.length() < 6) {
            str = "0" + str;
        }
        return str;
    }

    @Override
    public boolean charTyped(char c, int i) {
        String prev = this.textfield.getText();
        super.charTyped(c, i);
        String newText = this.textfield.getText();
        if (newText.equals(prev)) {
            return false;
        }
        try {
            this.color = Integer.parseInt(this.textfield.getText(), 16);
            this.textfield.setEditableColor(this.color);
        }
        catch (NumberFormatException e) {
            this.textfield.setText(prev);
        }
        return true;
    }

    @Override
    public void buttonEvent(GuiButtonNop btn) {
        if (btn.id == 66) {
            this.close();
        }
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)resource);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        graphics.drawTexture(resource, this.colorX, this.colorY, 0, 0, 120, 120);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean mouseClicked(double i, double j, int k) {
        super.mouseClicked(i, j, k);
        if (i < (double)this.colorX || i > (double)(this.colorX + 117) || j < (double)this.colorY || j > (double)(this.colorY + 117)) {
            return false;
        }
        InputStream stream = null;
        Resource iresource = this.client.getResourceManager().getResource(resource).orElse(null);
        if (iresource != null) {
            try {
                stream = iresource.getInputStream();
                BufferedImage bufferedimage = ImageIO.read(stream);
                this.color = bufferedimage.getRGB((int)(i - (double)this.guiLeft - 30.0) * 4, (int)(j - (double)this.guiTop - 50.0) * 4) & 0xFFFFFF;
                this.textfield.setEditableColor(this.color);
                this.textfield.setText(this.getColor());
            }
            catch (IOException iOException) {
            }
            finally {
                if (stream != null) {
                    try {
                        stream.close();
                    }
                    catch (IOException iOException) {}
                }
            }
        }
        return true;
    }

    @Override
    public void unFocused(GuiTextFieldNop textfield) {
        int color = 0;
        try {
            color = Integer.parseInt(textfield.getText(), 16);
        }
        catch (NumberFormatException e) {
            color = 0;
        }
        this.color = color;
        textfield.setEditableColor(color);
    }
}

