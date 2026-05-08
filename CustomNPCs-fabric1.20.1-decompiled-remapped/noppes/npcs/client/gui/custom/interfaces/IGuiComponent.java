/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package noppes.npcs.client.gui.custom.interfaces;

import net.minecraft.client.gui.DrawContext;
import noppes.npcs.api.gui.ICustomGuiComponent;

public interface IGuiComponent {
    public int getID();

    public void onRender(DrawContext var1, int var2, int var3, float var4);

    default public void onRenderPost(DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
    }

    public void init();

    public ICustomGuiComponent component();
}

