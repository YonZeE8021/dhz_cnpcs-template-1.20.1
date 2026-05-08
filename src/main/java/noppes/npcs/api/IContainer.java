/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.screen.ScreenHandler
 */
package noppes.npcs.api;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.ScreenHandler;
import noppes.npcs.api.item.IItemStack;

public interface IContainer {
    public int getSize();

    public IItemStack getSlot(int var1);

    public void setSlot(int var1, IItemStack var2);

    public Inventory getMCInventory();

    public ScreenHandler getMCContainer();

    public int count(IItemStack var1, boolean var2, boolean var3);

    public IItemStack[] getItems();
}

