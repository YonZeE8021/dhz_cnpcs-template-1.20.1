/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package noppes.npcs.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import noppes.npcs.client.gui.custom.GuiCustom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Screen.class})
public class ScreenMixin {
    @Inject(at={@At(value="TAIL")}, method={"init*"})
    private void renderToBuffer(MinecraftClient mc, int width, int height, CallbackInfo callbackInfo) {
        if ((Object)this instanceof GuiCustom) {
            GuiCustom gui = (GuiCustom)(Object)this;
            if (gui.subgui != null) {
                gui.subgui.init(mc, width, height);
            }
        }
    }
}

