/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.gui.widget.TextFieldWidget
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package noppes.npcs.mixin;

import net.minecraft.SharedConstants;
import net.minecraft.client.gui.widget.TextFieldWidget;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={TextFieldWidget.class})
public class MixinEditBox {
    @Redirect(method={"write"}, at=@At(value="INVOKE", target="Lnet/minecraft/SharedConstants;stripInvalidChars(Ljava/lang/String;)Ljava/lang/String;"))
    public String filterTextProxy(String p_136191_) {
        if ((Object)this instanceof GuiTextFieldNop) {
            return p_136191_;
        }
        return SharedConstants.stripInvalidChars((String)p_136191_);
    }
}

