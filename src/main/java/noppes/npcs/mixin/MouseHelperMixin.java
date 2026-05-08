/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Mouse
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package noppes.npcs.mixin;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={Mouse.class})
public interface MouseHelperMixin {
    @Accessor(value="activeButton")
    public int getActiveButton();

    @Accessor("cursorLocked")
    public void setGrabbed(boolean var1);

    @Accessor("x")
    public void setX(double var1);

    @Accessor("y")
    public void setY(double var1);
}

