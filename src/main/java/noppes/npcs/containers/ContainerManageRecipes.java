/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.inventory.SimpleInventory
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Identifier
 *  net.minecraft.registry.DynamicRegistryManager
 */
package noppes.npcs.containers;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.registry.DynamicRegistryManager;
import noppes.npcs.CustomContainer;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.controllers.data.RecipeCarpentry;

public class ContainerManageRecipes
extends ScreenHandler {
    private SimpleInventory craftingMatrix;
    public RecipeCarpentry recipe;
    public int size;
    public int width;
    private boolean init = false;

    public ContainerManageRecipes(int containerId, PlayerInventory playerInventory, int size) {
        super(CustomContainer.container_managerecipes, containerId);
        this.size = size * size;
        this.width = size;
        this.craftingMatrix = new SimpleInventory(this.size + 1);
        this.recipe = new RecipeCarpentry(new Identifier(""), "");
        this.addSlot(new Slot((Inventory)this.craftingMatrix, 0, 87, 61));
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                this.addSlot(new Slot((Inventory)this.craftingMatrix, i * this.width + j + 1, j * 18 + 8, i * 18 + 35){

                    public int getMaxItemCount() {
                        return 1;
                    }
                });
            }
        }
        for (int i1 = 0; i1 < 3; ++i1) {
            for (int l1 = 0; l1 < 9; ++l1) {
                this.addSlot(new Slot((Inventory)playerInventory, l1 + i1 * 9 + 9, 8 + l1 * 18, 113 + i1 * 18));
            }
        }
        for (int j1 = 0; j1 < 9; ++j1) {
            this.addSlot(new Slot((Inventory)playerInventory, j1, 8 + j1 * 18, 171));
        }
    }

    public ItemStack quickMove(PlayerEntity par1Player, int i) {
        return ItemStack.EMPTY;
    }

    public boolean canUse(PlayerEntity entityplayer) {
        return true;
    }

    public void setRecipe(RecipeCarpentry recipe, DynamicRegistryManager access) {
        this.craftingMatrix.setStack(0, recipe.getOutput(access));
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.width; ++j) {
                if (j >= recipe.getWidth()) {
                    this.craftingMatrix.setStack(i * this.width + j + 1, ItemStack.EMPTY);
                    continue;
                }
                this.craftingMatrix.setStack(i * this.width + j + 1, recipe.getCraftingItem(i * recipe.getWidth() + j));
            }
        }
        this.recipe = recipe;
    }

    public void saveRecipe() {
        int nextChar = 0;
        char[] chars = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P'};
        HashMap<ItemStack, Character> nameMapping = new HashMap<ItemStack, Character>();
        int firstRow = this.width;
        int lastRow = 0;
        int firstColumn = this.width;
        int lastColumn = 0;
        boolean seenRow = false;
        for (int i = 0; i < this.width; ++i) {
            boolean seenColumn = false;
            for (int j = 0; j < this.width; ++j) {
                ItemStack item = this.craftingMatrix.getStack(i * this.width + j + 1);
                if (NoppesUtilServer.IsItemStackNull(item)) continue;
                if (!seenColumn && j < firstColumn) {
                    firstColumn = j;
                }
                if (j > lastColumn) {
                    lastColumn = j;
                }
                seenColumn = true;
                Character letter = null;
                for (ItemStack mapped : nameMapping.keySet()) {
                    if (!NoppesUtilPlayer.compareItems(mapped, item, this.recipe.ignoreDamage, this.recipe.ignoreNBT)) continue;
                    letter = (Character)nameMapping.get(mapped);
                }
                if (letter != null) continue;
                letter = Character.valueOf(chars[nextChar]);
                ++nextChar;
                nameMapping.put(item, letter);
            }
            if (!seenColumn) continue;
            if (!seenRow) {
                firstRow = i;
                lastRow = i;
                seenRow = true;
                continue;
            }
            lastRow = i;
        }
        ArrayList<Object> recipe = new ArrayList<Object>();
        for (int i = 0; i < this.width; ++i) {
            if (i < firstRow || i > lastRow) continue;
            Object row = "";
            for (int j = 0; j < this.width; ++j) {
                if (j < firstColumn || j > lastColumn) continue;
                ItemStack item = this.craftingMatrix.getStack(i * this.width + j + 1);
                if (NoppesUtilServer.IsItemStackNull(item)) {
                    row = (String)row + " ";
                    continue;
                }
                for (ItemStack mapped : nameMapping.keySet()) {
                    if (!NoppesUtilPlayer.compareItems(mapped, item, false, false)) continue;
                    row = (String)row + String.valueOf(nameMapping.get(mapped));
                }
            }
            recipe.add(row);
        }
        if (nameMapping.isEmpty()) {
            RecipeCarpentry r = new RecipeCarpentry(new Identifier("customnpcs", this.recipe.name), this.recipe.name);
            r.copy(this.recipe);
            this.recipe = r;
            return;
        }
        for (ItemStack mapped : nameMapping.keySet()) {
            Character letter = (Character)nameMapping.get(mapped);
            recipe.add(letter);
            recipe.add(mapped);
        }
        this.recipe = RecipeCarpentry.createRecipe(new Identifier("customnpcs", this.recipe.name), this.recipe, this.craftingMatrix.getStack(0), recipe.toArray());
    }
}

