/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
 *  net.minecraft.client.gui.DrawContext
 */
package noppes.npcs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import noppes.npcs.client.controllers.OverlayController;

public class OverlayEventHandler
implements HudRenderCallback {
    public void onHudRender(DrawContext graphics, float tickDelta) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        OverlayController.getInstance().renderOverlays(graphics);
        RenderSystem.disableBlend();
    }
}

