/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.api.wrapper.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.gui.IItemRenderer;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiComponentWrapper;

public class CustomGuiItemRendererWrapper
extends CustomGuiComponentWrapper
implements IItemRenderer {
    IItemStack stack;
    public int width;
    public int height;
    public float scale;

    public CustomGuiItemRendererWrapper() {
        this.stack = ItemStackWrapper.AIR;
    }

    public CustomGuiItemRendererWrapper(int id, int x, int y, int width, int height, IItemStack stack) {
        this.setID(id);
        this.stack = ItemStackWrapper.AIR;
        this.scale = 1.0f;
        this.setPos(x, y);
        this.setStack(stack);
        this.setHoverBox(width, height);
    }

    @Override
    public boolean hasStack() {
        return !this.stack.isEmpty();
    }

    @Override
    public IItemStack getStack() {
        return this.stack;
    }

    @Override
    public IItemRenderer setStack(IItemStack itemStack) {
        this.stack = itemStack == null ? ItemStackWrapper.AIR : itemStack;
        return this;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public IItemRenderer setHoverBox(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public float getScale() {
        return this.scale;
    }

    @Override
    public IItemRenderer setScale(float scaleFactor) {
        this.scale = scaleFactor;
        return this;
    }

    @Override
    public int getType() {
        return 12;
    }

    @Override
    public NbtCompound toNBT(NbtCompound nbt) {
        super.toNBT(nbt);
        nbt.put("stack", (NbtElement)this.stack.getMCItemStack().writeNbt(new NbtCompound()));
        nbt.putFloat("scale", this.scale);
        nbt.putInt("width", this.width);
        nbt.putInt("height", this.height);
        return nbt;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(NbtCompound nbt) {
        super.fromNBT(nbt);
        this.setStack(NpcAPI.Instance().getIItemStack(ItemStack.fromNbt((NbtCompound)nbt.getCompound("stack"))));
        this.setScale(nbt.getFloat("scale"));
        this.setHoverBox(nbt.getInt("width"), nbt.getInt("height"));
        return this;
    }
}

