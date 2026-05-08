/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.text.Text
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package noppes.npcs.mixin;

import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import noppes.npcs.client.gui.player.GuiMailmanWrite;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={HandledScreen.class})
public abstract class MixinAbstractContainerScreen
extends Screen {
    @Shadow
    @Nullable
    protected Slot focusedSlot;

    @Shadow
    protected abstract boolean handleHotbarKeyPressed(int var1, int var2);

    @Shadow
    protected abstract void onMouseClick(Slot var1, int var2, int var3, SlotActionType var4);

    protected MixinAbstractContainerScreen(Text title) {
        super(title);
    }

    @Inject(method={"keyPressed"}, at={@At(value="HEAD")}, cancellable=true)
    public void keyPressed(int p_97765_, int p_97766_, int p_97767_, CallbackInfoReturnable<Boolean> cir) {
        if ((Object)this instanceof GuiMailmanWrite) {
            if (super.keyPressed(p_97765_, p_97766_, p_97767_)) {
                cir.setReturnValue(true);
            } else if (this.client.options.inventoryKey.matchesKey(p_97765_, p_97766_)) {
                this.close();
                cir.setReturnValue(true);
            } else {
                boolean handled = this.handleHotbarKeyPressed(p_97765_, p_97766_);
                if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
                    if (this.client.options.pickItemKey.matchesKey(p_97765_, p_97766_)) {
                        this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, SlotActionType.CLONE);
                        handled = true;
                    } else if (this.client.options.dropKey.matchesKey(p_97765_, p_97766_)) {
                        this.onMouseClick(this.focusedSlot, this.focusedSlot.id, MixinAbstractContainerScreen.hasControlDown() ? 1 : 0, SlotActionType.THROW);
                        handled = true;
                    }
                } else if (this.client.options.dropKey.matchesKey(p_97765_, p_97766_)) {
                    handled = true;
                }
                cir.setReturnValue(handled);
            }
        }
    }
}

