/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.text.OrderedText
 *  net.minecraft.client.item.TooltipData
 *  net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent
 *  net.minecraft.client.gui.tooltip.TooltipComponent
 *  net.minecraft.client.gui.tooltip.TooltipPositioner
 *  net.minecraft.client.gui.tooltip.HoveredTooltipPositioner
 *  net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer
 *  org.joml.Vector2ic
 */
package noppes.npcs.client.gui.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.OrderedText;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.gui.tooltip.OrderedTextTooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import noppes.npcs.mixin.ClientTextTooltipMixin;
import org.joml.Vector2ic;

public class GuiTooltipUtils {
    private static ItemStack tooltipStack = ItemStack.EMPTY;

    public static void renderTooltip(DrawContext graphics, TextRenderer p_282308_, ItemStack p_282781_, int p_282687_, int p_282292_) {
        tooltipStack = p_282781_;
        GuiTooltipUtils.renderTooltip(graphics, p_282308_, Screen.getTooltipFromItem((MinecraftClient)MinecraftClient.getInstance(), (ItemStack)p_282781_), p_282781_.getTooltipData(), p_282687_, p_282292_);
        tooltipStack = ItemStack.EMPTY;
    }

    public static void renderTooltip(DrawContext graphics, TextRenderer font, List<Text> textComponents, Optional<TooltipData> tooltipComponent, ItemStack stack, int mouseX, int mouseY) {
        tooltipStack = stack;
        GuiTooltipUtils.renderTooltip(graphics, font, textComponents, tooltipComponent, mouseX, mouseY);
        tooltipStack = ItemStack.EMPTY;
    }

    public static void renderTooltip(DrawContext graphics, TextRenderer p_283128_, List<Text> tooltipLines, Optional<TooltipData> visualTooltipComponent, int p_283678_, int p_281696_) {
        List<TooltipComponent> list = tooltipLines.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Collectors.toList());
        visualTooltipComponent.ifPresent(tooltipComponent -> list.add(1, TooltipComponent.of((TooltipData)tooltipComponent)));
        GuiTooltipUtils.renderTooltipInternal(graphics, p_283128_, list, p_283678_, p_281696_, HoveredTooltipPositioner.INSTANCE);
    }

    public static void renderTooltip(DrawContext graphics, TextRenderer p_282269_, Text p_282572_, int p_282044_, int p_282545_) {
        GuiTooltipUtils.renderTooltip(graphics, p_282269_, List.of(p_282572_.asOrderedText()), p_282044_, p_282545_);
    }

    public static void renderTooltip(DrawContext graphics, TextRenderer p_282192_, List<? extends OrderedText> p_282297_, int p_281680_, int p_283325_) {
        GuiTooltipUtils.renderTooltipInternal(graphics, p_282192_, p_282297_.stream().map(TooltipComponent::of).collect(Collectors.toList()), p_281680_, p_283325_, HoveredTooltipPositioner.INSTANCE);
    }

    public static void renderTooltip(DrawContext graphics, TextRenderer p_281627_, List<OrderedText> p_283313_, TooltipPositioner p_283571_, int p_282367_, int p_282806_) {
        GuiTooltipUtils.renderTooltipInternal(graphics, p_281627_, p_283313_.stream().map(TooltipComponent::of).collect(Collectors.toList()), p_282367_, p_282806_, p_283571_);
    }

    private static void renderTooltipInternal(DrawContext graphics, TextRenderer p_282675_, List<TooltipComponent> p_282615_, int p_283230_, int p_283417_, TooltipPositioner p_282442_) {
        if (!p_282615_.isEmpty()) {
            int i = 0;
            int j = p_282615_.size() == 1 ? -2 : 0;
            for (TooltipComponent clienttooltipcomponent : p_282615_) {
                int k = clienttooltipcomponent.getWidth(p_282675_);
                if (k > i) {
                    i = k;
                }
                j += clienttooltipcomponent.getHeight();
            }
            int i2 = i;
            int j2 = j;
            Vector2ic vector2ic = p_282442_.getPosition(graphics.getScaledWindowWidth(), graphics.getScaledWindowHeight(), p_283230_, p_283417_, i2, j2);
            int l = vector2ic.x();
            int i1 = vector2ic.y();
            graphics.getMatrices().push();
            int j1 = 400;
            graphics.draw(() -> TooltipBackgroundRenderer.render((DrawContext)graphics, (int)l, (int)i1, (int)i2, (int)j2, (int)400));
            graphics.getMatrices().translate(0.0f, 0.0f, 400.0f);
            int k1 = i1;
            for (int l1 = 0; l1 < p_282615_.size(); ++l1) {
                TooltipComponent clienttooltipcomponent1 = p_282615_.get(l1);
                if (clienttooltipcomponent1 instanceof OrderedTextTooltipComponent) {
                    graphics.drawText(p_282675_, ((ClientTextTooltipMixin)clienttooltipcomponent1).getText(), l, k1, 0xFFFFFF, false);
                } else {
                    clienttooltipcomponent1.drawText(p_282675_, l, k1, graphics.getMatrices().peek().getPositionMatrix(), graphics.getVertexConsumers());
                }
                k1 += clienttooltipcomponent1.getHeight() + (l1 == 0 ? 2 : 0);
            }
            k1 = i1;
            for (int k2 = 0; k2 < p_282615_.size(); ++k2) {
                TooltipComponent clienttooltipcomponent2 = p_282615_.get(k2);
                clienttooltipcomponent2.drawItems(p_282675_, l, k1, graphics);
                k1 += clienttooltipcomponent2.getHeight() + (k2 == 0 ? 2 : 0);
            }
            graphics.getMatrices().pop();
        }
    }
}

