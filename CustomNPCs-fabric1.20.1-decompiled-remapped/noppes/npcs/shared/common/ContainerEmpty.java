/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.item.ItemStack
 */
package noppes.npcs.shared.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;

public class ContainerEmpty
extends ScreenHandler {
    public ContainerEmpty() {
        super(null, 0);
    }

    public ItemStack quickMove(PlayerEntity p_38941_, int p_38942_) {
        return ItemStack.EMPTY;
    }

    public boolean canUse(PlayerEntity var1) {
        return false;
    }
}

