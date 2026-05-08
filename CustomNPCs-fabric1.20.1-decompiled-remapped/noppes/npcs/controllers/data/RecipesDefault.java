/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.block.Block
 *  net.minecraft.util.Identifier
 */
package noppes.npcs.controllers.data;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemConvertible;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import noppes.npcs.CustomItems;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;

public class RecipesDefault {
    public static void addRecipe(String name, Object ob, boolean isGlobal, Object ... recipe) {
        ItemStack item = ob instanceof Item ? new ItemStack((ItemConvertible)((Item)ob)) : (ob instanceof Block ? new ItemStack((ItemConvertible)((Block)ob)) : (ItemStack)ob);
        RecipeCarpentry recipeAnvil = new RecipeCarpentry(new Identifier("customnpcs", name), name);
        recipeAnvil.isGlobal = isGlobal;
        recipeAnvil = RecipeCarpentry.createRecipe(new Identifier("customnpcs", name), recipeAnvil, item, recipe);
        RecipeController.instance.saveRecipe(recipeAnvil);
    }

    public static void loadDefaultRecipes(int i) {
        if (i < 0) {
            RecipesDefault.addRecipe("npc_wand", CustomItems.wand, true, "XX", " Y", " Y", Character.valueOf('X'), Items.field_8229, Character.valueOf('Y'), Items.field_8600);
            RecipesDefault.addRecipe("mob_cloner", CustomItems.cloner, true, "XX", "XY", " Y", Character.valueOf('X'), Items.field_8229, Character.valueOf('Y'), Items.field_8600);
        }
    }
}

