/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.text.OrderedText
 *  net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package noppes.npcs.mixin;

import net.minecraft.text.OrderedText;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={OrderedTextTooltipComponent.class})
public interface ClientTextTooltipMixin {
    @Accessor
    public OrderedText getText();
}

