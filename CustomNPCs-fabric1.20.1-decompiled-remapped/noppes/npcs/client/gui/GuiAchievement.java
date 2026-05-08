/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.toast.Toast
 *  net.minecraft.client.toast.Toast$Visibility
 *  net.minecraft.client.toast.ToastManager
 *  net.minecraft.client.render.GameRenderer
 */
package noppes.npcs.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.render.GameRenderer;

@Environment(value=EnvType.CLIENT)
public class GuiAchievement
implements Toast {
    private String title;
    private String subtitle;
    private int type;
    private long firstDrawTime;
    private boolean newDisplay;

    public GuiAchievement(Text titleComponent, Text subtitleComponent, int type) {
        this.title = titleComponent.getString();
        this.subtitle = subtitleComponent == null ? null : subtitleComponent.getString();
        this.type = type;
    }

    public Toast.Visibility draw(DrawContext graphics, ToastManager toastGui, long delta) {
        if (this.newDisplay) {
            this.firstDrawTime = delta;
            this.newDisplay = false;
        }
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (Identifier)TEXTURE);
        graphics.drawTexture(TEXTURE, 0, 0, 0, 32 * this.type, 160, 32);
        int color1 = -256;
        int color2 = -1;
        if (this.type == 1 || this.type == 3) {
            color1 = -11534256;
            color2 = -16777216;
        }
        graphics.drawTextWithShadow(toastGui.getClient().textRenderer, this.title, 18, 7, color1);
        graphics.drawTextWithShadow(toastGui.getClient().textRenderer, this.subtitle, 18, 18, color2);
        return delta - this.firstDrawTime < 5000L ? Toast.Visibility.field_2210 : Toast.Visibility.field_2209;
    }
}

