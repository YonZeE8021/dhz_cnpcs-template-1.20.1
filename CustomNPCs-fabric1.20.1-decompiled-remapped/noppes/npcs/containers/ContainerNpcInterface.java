/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.screen.ScreenHandlerType
 */
package noppes.npcs.containers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.screen.ScreenHandlerType;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.wrapper.ContainerWrapper;

public class ContainerNpcInterface
extends ScreenHandler {
    private int posX;
    private int posZ;
    public PlayerEntity player;
    public IContainer scriptContainer;

    public ContainerNpcInterface(ScreenHandlerType type, int containerId, PlayerInventory playerInventory) {
        super(type, containerId);
        this.player = playerInventory.player;
        this.posX = MathHelper.floor((double)this.player.getX());
        this.posZ = MathHelper.floor((double)this.player.getZ());
        this.player.setVelocity(Vec3d.ZERO);
    }

    public ItemStack quickMove(PlayerEntity p_38941_, int p_38942_) {
        return ItemStack.EMPTY;
    }

    public boolean canUse(PlayerEntity player) {
        return !player.isRemoved() && this.posX == MathHelper.floor((double)player.getX()) && this.posZ == MathHelper.floor((double)player.getZ());
    }

    public static IContainer getOrCreateIContainer(ContainerNpcInterface container) {
        if (container.scriptContainer != null) {
            return container.scriptContainer;
        }
        container.scriptContainer = new ContainerWrapper(container);
        return container.scriptContainer;
    }
}

