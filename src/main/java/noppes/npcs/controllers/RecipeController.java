/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.recipe.Ingredient
 *  net.minecraft.util.collection.DefaultedList
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.util.Identifier
 *  net.minecraft.inventory.RecipeInputInventory
 */
package noppes.npcs.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.inventory.RecipeInputInventory;
import noppes.npcs.CustomNpcs;
import noppes.npcs.EventHooks;
import noppes.npcs.api.handler.IRecipeHandler;
import noppes.npcs.api.handler.data.IRecipe;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.controllers.data.RecipesDefault;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketSyncRecipeRemove;
import noppes.npcs.packets.client.PacketSyncRecipeUpdate;

public class RecipeController
implements IRecipeHandler {
    public HashMap<Identifier, RecipeCarpentry> globalRecipes = new HashMap();
    public HashMap<Identifier, RecipeCarpentry> anvilRecipes = new HashMap();
    public static RecipeController instance;
    public static final int version = 1;
    public int nextId = 1;
    public static HashMap<Identifier, RecipeCarpentry> syncRecipes;

    public RecipeController() {
        instance = this;
    }

    public void load() {
        this.loadCategories();
        this.reloadGlobalRecipes();
        EventHooks.onGlobalRecipesLoaded(this);
    }

    public void reloadGlobalRecipes() {
    }

    private void loadCategories() {
        File saveDir = CustomNpcs.getLevelSaveDirectory();
        try {
            File file = new File(saveDir, "recipes.dat");
            if (file.exists()) {
                this.loadCategories(file);
            } else {
                this.globalRecipes.clear();
                this.anvilRecipes.clear();
                this.loadDefaultRecipes(-1);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                File file = new File(saveDir, "recipes.dat_old");
                if (file.exists()) {
                    this.loadCategories(file);
                }
            }
            catch (Exception ee) {
                e.printStackTrace();
            }
        }
    }

    private void loadDefaultRecipes(int i) {
        if (i == 1) {
            return;
        }
        RecipesDefault.loadDefaultRecipes(i);
        this.saveCategories();
    }

    private void loadCategories(File file) throws Exception {
        NbtCompound nbttagcompound1 = NbtIo.readCompressed((InputStream)new FileInputStream(file));
        this.nextId = nbttagcompound1.getInt("LastId");
        NbtList list = nbttagcompound1.getList("Data", 10);
        HashMap<Identifier, RecipeCarpentry> globalRecipes = new HashMap<Identifier, RecipeCarpentry>();
        HashMap<Identifier, RecipeCarpentry> anvilRecipes = new HashMap<Identifier, RecipeCarpentry>();
        if (list != null) {
            for (int i = 0; i < list.size(); ++i) {
                RecipeCarpentry recipe = RecipeCarpentry.load(list.getCompound(i));
                if (recipe.isGlobal) {
                    globalRecipes.put(recipe.getId(), recipe);
                    continue;
                }
                anvilRecipes.put(recipe.getId(), recipe);
            }
        }
        this.anvilRecipes = anvilRecipes;
        this.globalRecipes = globalRecipes;
        this.loadDefaultRecipes(nbttagcompound1.getInt("Version"));
    }

    private void saveCategories() {
        try {
            File saveDir = CustomNpcs.getLevelSaveDirectory();
            NbtList list = new NbtList();
            for (RecipeCarpentry recipe : this.globalRecipes.values()) {
                if (!recipe.savesRecipe) continue;
                list.add(recipe.writeNBT());
            }
            for (RecipeCarpentry recipe : this.anvilRecipes.values()) {
                if (!recipe.savesRecipe) continue;
                list.add(recipe.writeNBT());
            }
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.put("Data", (NbtElement)list);
            nbttagcompound.putInt("LastId", this.nextId);
            nbttagcompound.putInt("Version", 1);
            File file = new File(saveDir, "recipes.dat_new");
            File file1 = new File(saveDir, "recipes.dat_old");
            File file2 = new File(saveDir, "recipes.dat");
            NbtIo.writeCompressed((NbtCompound)nbttagcompound, (OutputStream)new FileOutputStream(file));
            if (file1.exists()) {
                file1.delete();
            }
            file2.renameTo(file1);
            if (file2.exists()) {
                file2.delete();
            }
            file.renameTo(file2);
            if (file.exists()) {
                file.delete();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RecipeCarpentry findMatchingRecipe(RecipeInputInventory inventoryCrafting) {
        for (RecipeCarpentry recipe : this.anvilRecipes.values()) {
            if (!recipe.isValid() || !recipe.matches(inventoryCrafting, null)) continue;
            return recipe;
        }
        return null;
    }

    public RecipeCarpentry getRecipe(Identifier id) {
        if (this.globalRecipes.containsKey(id)) {
            return this.globalRecipes.get(id);
        }
        if (this.anvilRecipes.containsKey(id)) {
            return this.anvilRecipes.get(id);
        }
        return null;
    }

    public RecipeCarpentry saveRecipe(RecipeCarpentry recipe) {
        RecipeCarpentry current = this.getRecipe(recipe.getId());
        if (current != null && !current.name.equals(recipe.name)) {
            while (this.containsRecipeName(recipe.name)) {
                recipe.name = recipe.name + "_";
            }
        }
        if (recipe.isGlobal) {
            this.globalRecipes.remove(recipe.getId());
            this.globalRecipes.put(recipe.getId(), recipe);
            Packets.sendAll(new PacketSyncRecipeUpdate(recipe.getId(), 6, recipe.writeNBT()));
        } else {
            this.anvilRecipes.remove(recipe.getId());
            this.anvilRecipes.put(recipe.getId(), recipe);
            Packets.sendAll(new PacketSyncRecipeUpdate(recipe.getId(), 7, recipe.writeNBT()));
        }
        this.saveCategories();
        this.reloadGlobalRecipes();
        return recipe;
    }

    private int getUniqueId() {
        ++this.nextId;
        return this.nextId;
    }

    private boolean containsRecipeName(String name) {
        name = name.toLowerCase();
        for (RecipeCarpentry recipe : this.globalRecipes.values()) {
            if (!recipe.name.toLowerCase().equals(name)) continue;
            return true;
        }
        for (RecipeCarpentry recipe : this.anvilRecipes.values()) {
            if (!recipe.name.toLowerCase().equals(name)) continue;
            return true;
        }
        return false;
    }

    @Override
    public RecipeCarpentry delete(Identifier id) {
        RecipeCarpentry recipe = this.getRecipe(id);
        if (recipe == null) {
            return null;
        }
        this.globalRecipes.remove(recipe.getId());
        this.anvilRecipes.remove(recipe.getId());
        if (recipe.isGlobal) {
            Packets.sendAll(new PacketSyncRecipeRemove(id, 6));
        } else {
            Packets.sendAll(new PacketSyncRecipeRemove(id, 7));
        }
        this.saveCategories();
        this.reloadGlobalRecipes();
        return recipe;
    }

    @Override
    public List<IRecipe> getGlobalList() {
        return new ArrayList<IRecipe>(this.globalRecipes.values());
    }

    @Override
    public List<IRecipe> getCarpentryList() {
        return new ArrayList<IRecipe>(this.anvilRecipes.values());
    }

    @Override
    public IRecipe addRecipe(String name, boolean global, ItemStack result, Object ... objects) {
        RecipeCarpentry recipe = new RecipeCarpentry(new Identifier("customnpcs", name), name);
        recipe.isGlobal = global;
        recipe = RecipeCarpentry.createRecipe(new Identifier("customnpcs", name), recipe, result, objects);
        return this.saveRecipe(recipe);
    }

    @Override
    public IRecipe addRecipe(String name, boolean global, ItemStack result, int width, int height, ItemStack ... objects) {
        DefaultedList list = DefaultedList.of();
        for (ItemStack item : objects) {
            if (item.isEmpty()) continue;
            list.add(Ingredient.ofStacks((ItemStack[])new ItemStack[]{item}));
        }
        RecipeCarpentry recipe = new RecipeCarpentry(new Identifier("customnpcs", name), width, height, (DefaultedList<Ingredient>)list, result);
        recipe.isGlobal = global;
        recipe.name = name;
        return this.saveRecipe(recipe);
    }

    static {
        syncRecipes = new HashMap();
    }
}

