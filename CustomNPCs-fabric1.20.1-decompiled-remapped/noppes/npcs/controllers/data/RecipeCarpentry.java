/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.inventory.CraftingInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.recipe.Ingredient
 *  net.minecraft.recipe.ShapedRecipe
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.world.World
 *  net.minecraft.block.Block
 *  net.minecraft.util.collection.DefaultedList
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.util.Identifier
 *  net.minecraft.registry.DynamicRegistryManager
 *  net.minecraft.recipe.book.CraftingRecipeCategory
 *  net.minecraft.inventory.RecipeInputInventory
 */
package noppes.npcs.controllers.data;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.item.ItemConvertible;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.inventory.RecipeInputInventory;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilPlayer;
import noppes.npcs.api.handler.data.IRecipe;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.Availability;

public class RecipeCarpentry
extends ShapedRecipe
implements IRecipe {
    public Availability availability = new Availability();
    public boolean isGlobal = false;
    public boolean ignoreDamage = false;
    public boolean ignoreNBT = false;
    public boolean savesRecipe = true;
    public String name;

    public RecipeCarpentry(Identifier location, int width, int height, DefaultedList<Ingredient> recipe, ItemStack result) {
        super(location, "customnpcs", CraftingRecipeCategory.field_40251, width, height, recipe, result);
        this.name = location.getPath();
    }

    public RecipeCarpentry(Identifier location, String name) {
        super(location, "customnpcs", CraftingRecipeCategory.field_40251, 4, 4, DefaultedList.of(), ItemStack.EMPTY);
        this.name = name;
    }

    public static RecipeCarpentry load(NbtCompound compound) {
        Identifier location = null;
        location = compound.contains("ID") ? new Identifier("customnpcs", compound.getString("ID")) : new Identifier(compound.getString("Id"));
        RecipeCarpentry recipe = new RecipeCarpentry(location, compound.getInt("Width"), compound.getInt("Height"), NBTTags.getIngredientList(compound.getList("Materials", 10)), ItemStack.fromNbt((NbtCompound)compound.getCompound("Item")));
        recipe.availability.load(compound.getCompound("Availability"));
        recipe.ignoreDamage = compound.getBoolean("IgnoreDamage");
        recipe.ignoreNBT = compound.getBoolean("IgnoreNBT");
        recipe.isGlobal = compound.getBoolean("Global");
        recipe.name = compound.getString("Name");
        return recipe;
    }

    public NbtCompound writeNBT() {
        NbtCompound compound = new NbtCompound();
        compound.putInt("Width", this.getWidth());
        compound.putInt("Height", this.getHeight());
        if (this.getResult() != null) {
            compound.put("Item", (NbtElement)this.getResult().writeNbt(new NbtCompound()));
        }
        compound.put("Materials", (NbtElement)NBTTags.nbtIngredientList((DefaultedList<Ingredient>)this.getIngredients()));
        compound.put("Availability", (NbtElement)this.availability.save(new NbtCompound()));
        compound.putString("Name", this.name);
        compound.putString("Id", this.getId().toString());
        compound.putBoolean("Global", this.isGlobal);
        compound.putBoolean("IgnoreDamage", this.ignoreDamage);
        compound.putBoolean("IgnoreNBT", this.ignoreNBT);
        return compound;
    }

    public static RecipeCarpentry createRecipe(Identifier location, RecipeCarpentry recipe, ItemStack par1ItemStack, Object ... limbSwingAmountArrayOfObj) {
        int var9;
        Object var3 = "";
        int var4 = 0;
        int var5 = 0;
        int var6 = 0;
        if (limbSwingAmountArrayOfObj[var4] instanceof String[]) {
            String[] var7;
            String[] var8 = var7 = (String[])limbSwingAmountArrayOfObj[var4++];
            var9 = var7.length;
            for (int var10 = 0; var10 < var9; ++var10) {
                String var11 = var8[var10];
                ++var6;
                var5 = var11.length();
                var3 = (String)var3 + var11;
            }
        } else {
            while (limbSwingAmountArrayOfObj[var4] instanceof String) {
                String var13 = (String)limbSwingAmountArrayOfObj[var4++];
                ++var6;
                var5 = var13.length();
                var3 = (String)var3 + var13;
            }
        }
        HashMap<Character, ItemStack> var14 = new HashMap<Character, ItemStack>();
        while (var4 < limbSwingAmountArrayOfObj.length) {
            Character var16 = (Character)limbSwingAmountArrayOfObj[var4];
            ItemStack var17 = ItemStack.EMPTY;
            if (limbSwingAmountArrayOfObj[var4 + 1] instanceof Item) {
                var17 = new ItemStack((ItemConvertible)((Item)limbSwingAmountArrayOfObj[var4 + 1]));
            } else if (limbSwingAmountArrayOfObj[var4 + 1] instanceof Block) {
                var17 = new ItemStack((ItemConvertible)((Block)limbSwingAmountArrayOfObj[var4 + 1]), 1);
            } else if (limbSwingAmountArrayOfObj[var4 + 1] instanceof ItemStack) {
                var17 = (ItemStack)limbSwingAmountArrayOfObj[var4 + 1];
            }
            var14.put(var16, var17);
            var4 += 2;
        }
        DefaultedList ingredients = DefaultedList.of();
        for (var9 = 0; var9 < var5 * var6; ++var9) {
            char var18 = ((String)var3).charAt(var9);
            if (var14.containsKey(Character.valueOf(var18))) {
                ingredients.add(var9, (Object)Ingredient.ofStacks((ItemStack[])new ItemStack[]{((ItemStack)var14.get(Character.valueOf(var18))).copy()}));
                continue;
            }
            ingredients.add(var9, (Object)Ingredient.EMPTY);
        }
        RecipeCarpentry newrecipe = new RecipeCarpentry(location, var5, var6, (DefaultedList<Ingredient>)ingredients, par1ItemStack);
        newrecipe.copy(recipe);
        if (var5 == 4 || var6 == 4) {
            newrecipe.isGlobal = false;
        }
        return newrecipe;
    }

    public boolean matches(RecipeInputInventory inventoryCrafting, World world) {
        for (int i = 0; i <= 4 - this.getWidth(); ++i) {
            for (int j = 0; j <= 4 - this.getHeight(); ++j) {
                if (this.checkMatch((Inventory)inventoryCrafting, i, j, true)) {
                    return true;
                }
                if (!this.checkMatch((Inventory)inventoryCrafting, i, j, false)) continue;
                return true;
            }
        }
        return false;
    }

    public ItemStack getOutput(DynamicRegistryManager p_266881_) {
        if (super.getOutput(p_266881_).isEmpty()) {
            return ItemStack.EMPTY;
        }
        return super.getOutput(p_266881_).copy();
    }

    private boolean checkMatch(Inventory inventoryCrafting, int par2, int par3, boolean par4) {
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                int var7 = i - par2;
                int var8 = j - par3;
                Ingredient ingredient = Ingredient.EMPTY;
                if (var7 >= 0 && var8 >= 0 && var7 < this.getWidth() && var8 < this.getHeight()) {
                    ingredient = par4 ? (Ingredient)this.getIngredients().get(this.getWidth() - var7 - 1 + var8 * this.getWidth()) : (Ingredient)this.getIngredients().get(var7 + var8 * this.getWidth());
                }
                ItemStack var10 = ItemStack.EMPTY;
                if (inventoryCrafting instanceof CraftingInventory) {
                    CraftingInventory tcc = (CraftingInventory)inventoryCrafting;
                    var10 = tcc.getStack(i + j * tcc.getWidth());
                }
                if (!var10.isEmpty() && ingredient.getMatchingStacks().length == 0) {
                    return false;
                }
                if (var10.isEmpty() && ingredient.getMatchingStacks().length == 0) continue;
                ItemStack var9 = ingredient.getMatchingStacks()[0];
                if (var10.isEmpty() && var9.isEmpty() || NoppesUtilPlayer.compareItems(var9, var10, this.ignoreDamage, this.ignoreNBT)) continue;
                return false;
            }
        }
        return true;
    }

    public DefaultedList<ItemStack> getRemainingItems(RecipeInputInventory inventoryCrafting) {
        DefaultedList list = DefaultedList.ofSize((int)inventoryCrafting.size(), (Object)ItemStack.EMPTY);
        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemstack = inventoryCrafting.getStack(i);
            if (itemstack.getItem().hasRecipeRemainder()) {
                list.set(i, (Object)new ItemStack((ItemConvertible)itemstack.getItem().getRecipeRemainder()));
                continue;
            }
            list.set(i, (Object)ItemStack.EMPTY);
        }
        return list;
    }

    public boolean isIgnoredInRecipeBook() {
        return false;
    }

    public void copy(RecipeCarpentry recipe) {
        this.availability = recipe.availability;
        this.isGlobal = recipe.isGlobal;
        this.ignoreDamage = recipe.ignoreDamage;
        this.ignoreNBT = recipe.ignoreNBT;
    }

    public ItemStack getCraftingItem(int i) {
        if (i >= this.getIngredients().size()) {
            return ItemStack.EMPTY;
        }
        Ingredient ingredients = (Ingredient)this.getIngredients().get(i);
        if (ingredients.getMatchingStacks().length == 0) {
            return ItemStack.EMPTY;
        }
        return ingredients.getMatchingStacks()[0];
    }

    public boolean isValid() {
        if (this.getIngredients().size() == 0 || this.getResult().isEmpty()) {
            return false;
        }
        for (Ingredient ingredient : this.getIngredients()) {
            if (ingredient.getMatchingStacks().length <= 0) continue;
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public ItemStack getResult() {
        return this.getOutput(null);
    }

    @Override
    public boolean isGlobal() {
        return this.isGlobal;
    }

    @Override
    public void setIsGlobal(boolean bo) {
        this.isGlobal = bo;
    }

    @Override
    public boolean getIgnoreNBT() {
        return this.ignoreNBT;
    }

    @Override
    public void setIgnoreNBT(boolean bo) {
        this.ignoreNBT = bo;
    }

    @Override
    public boolean getIgnoreDamage() {
        return this.ignoreDamage;
    }

    @Override
    public void setIgnoreDamage(boolean bo) {
        this.ignoreDamage = bo;
    }

    @Override
    public void save() {
        RecipeController.instance.saveRecipe(this);
    }

    @Override
    public void delete() {
    }

    @Override
    public ItemStack[] getRecipe() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        for (Ingredient ingredient : this.getIngredients()) {
            if (ingredient.getMatchingStacks().length <= 0) continue;
            list.add(ingredient.getMatchingStacks()[0]);
        }
        return list.toArray(new ItemStack[list.size()]);
    }

    @Override
    public void saves(boolean bo) {
        this.savesRecipe = bo;
    }

    @Override
    public boolean saves() {
        return this.savesRecipe;
    }
}

