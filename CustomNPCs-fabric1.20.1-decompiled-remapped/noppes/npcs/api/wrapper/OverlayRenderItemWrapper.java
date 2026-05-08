/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.api.wrapper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.overlay.IRenderItemOverlay;
import noppes.npcs.api.wrapper.OverlayComponentWrapper;

public class OverlayRenderItemWrapper
extends OverlayComponentWrapper
implements IRenderItemOverlay {
    private ItemStack item;

    public OverlayRenderItemWrapper(int id, int x, int y, IItemStack item) {
        super(id, x, y);
        this.item = item == null ? ItemStack.EMPTY : item.getMCItemStack();
    }

    @Override
    public IItemStack getItem() {
        return NpcAPI.Instance().getIItemStack(this.item);
    }

    @Override
    public IRenderItemOverlay setItem(IItemStack item) {
        this.item = item.getMCItemStack();
        return this;
    }

    @Override
    public int getType() {
        return 2;
    }

    @Override
    public void toNbt(NbtCompound compound) {
        super.toNbt(compound);
        compound.put("item", (NbtElement)this.item.writeNbt(new NbtCompound()));
    }

    @Override
    public void fromNbt(NbtCompound compound) {
        super.fromNbt(compound);
        this.item = ItemStack.fromNbt((NbtCompound)compound.getCompound("item"));
    }
}

