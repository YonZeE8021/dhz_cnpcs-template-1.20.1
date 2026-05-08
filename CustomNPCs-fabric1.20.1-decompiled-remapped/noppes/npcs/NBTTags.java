/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.recipe.Ingredient
 *  net.minecraft.util.collection.DefaultedList
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtDouble
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.util.Identifier
 */
package noppes.npcs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.controllers.IScriptHandler;
import noppes.npcs.controllers.ScriptContainer;

public class NBTTags {
    public static void getItemStackList(NbtList tagList, DefaultedList<ItemStack> items) {
        items.clear();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            try {
                items.set(nbttagcompound.getByte("Slot") & 0xFF, (Object)ItemStack.fromNbt((NbtCompound)nbttagcompound));
                continue;
            }
            catch (ClassCastException e) {
                items.set(nbttagcompound.getInt("Slot"), (Object)ItemStack.fromNbt((NbtCompound)nbttagcompound));
            }
        }
    }

    public static Map<Integer, IItemStack> getIItemStackMap(NbtList tagList) {
        HashMap<Integer, IItemStack> list = new HashMap<Integer, IItemStack>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            ItemStack item = ItemStack.fromNbt((NbtCompound)nbttagcompound);
            if (item.isEmpty()) continue;
            try {
                list.put(nbttagcompound.getByte("Slot") & 0xFF, NpcAPI.Instance().getIItemStack(item));
                continue;
            }
            catch (ClassCastException e) {
                list.put(nbttagcompound.getInt("Slot"), NpcAPI.Instance().getIItemStack(item));
            }
        }
        return list;
    }

    public static ItemStack[] getItemStackArray(NbtList tagList) {
        ItemStack[] list = new ItemStack[tagList.size()];
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            list[nbttagcompound.getByte((String)"Slot") & 0xFF] = ItemStack.fromNbt((NbtCompound)nbttagcompound);
        }
        return list;
    }

    public static DefaultedList<Ingredient> getIngredientList(NbtList tagList) {
        DefaultedList list = DefaultedList.of();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            list.add(nbttagcompound.getByte("Slot") & 0xFF, (Object)Ingredient.ofStacks((ItemStack[])new ItemStack[]{ItemStack.fromNbt((NbtCompound)nbttagcompound)}));
        }
        return list;
    }

    public static ArrayList<int[]> getIntegerArraySet(NbtList tagList) {
        ArrayList<int[]> set = new ArrayList<int[]>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound compound = tagList.getCompound(i);
            set.add(compound.getIntArray("Array"));
        }
        return set;
    }

    public static HashMap<Integer, Boolean> getBooleanList(NbtList tagList) {
        HashMap<Integer, Boolean> list = new HashMap<Integer, Boolean>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            list.put(nbttagcompound.getInt("Slot"), nbttagcompound.getBoolean("Boolean"));
        }
        return list;
    }

    public static HashMap<Integer, Integer> getIntegerIntegerMap(NbtList tagList) {
        HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            list.put(nbttagcompound.getInt("Slot"), nbttagcompound.getInt("Integer"));
        }
        return list;
    }

    public static HashMap<Integer, Float> getFloatIntegerMap(NbtList tagList) {
        HashMap<Integer, Float> list = new HashMap<Integer, Float>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            list.put(nbttagcompound.getInt("Slot"), Float.valueOf(nbttagcompound.getFloat("Integer")));
        }
        return list;
    }

    public static HashMap<Integer, Long> getIntegerLongMap(NbtList tagList) {
        HashMap<Integer, Long> list = new HashMap<Integer, Long>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            list.put(nbttagcompound.getInt("Slot"), nbttagcompound.getLong("Long"));
        }
        return list;
    }

    public static HashSet<Integer> getIntegerSet(NbtList tagList) {
        HashSet<Integer> list = new HashSet<Integer>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            list.add(nbttagcompound.getInt("Integer"));
        }
        return list;
    }

    public static List<Integer> getIntegerList(NbtList tagList) {
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            list.add(nbttagcompound.getInt("Integer"));
        }
        return list;
    }

    public static HashMap<String, String> getStringStringMap(NbtList tagList) {
        HashMap<String, String> list = new HashMap<String, String>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            list.put(nbttagcompound.getString("Slot"), nbttagcompound.getString("Value"));
        }
        return list;
    }

    public static HashMap<Integer, String> getIntegerStringMap(NbtList tagList) {
        HashMap<Integer, String> list = new HashMap<Integer, String>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            list.put(nbttagcompound.getInt("Slot"), nbttagcompound.getString("Value"));
        }
        return list;
    }

    public static HashMap<String, Integer> getStringIntegerMap(NbtList tagList) {
        HashMap<String, Integer> list = new HashMap<String, Integer>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            list.put(nbttagcompound.getString("Slot"), nbttagcompound.getInt("Value"));
        }
        return list;
    }

    public static HashMap<String, Vector<String>> getVectorMap(NbtList tagList) {
        HashMap<String, Vector<String>> map = new HashMap<String, Vector<String>>();
        for (int i = 0; i < tagList.size(); ++i) {
            Vector<String> values = new Vector<String>();
            NbtCompound nbttagcompound = tagList.getCompound(i);
            NbtList list = nbttagcompound.getList("Values", 10);
            for (int j = 0; j < list.size(); ++j) {
                NbtCompound value = list.getCompound(j);
                values.add(value.getString("Value"));
            }
            map.put(nbttagcompound.getString("Key"), values);
        }
        return map;
    }

    public static List<String> getStringList(NbtList tagList) {
        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            String line = nbttagcompound.getString("Line");
            list.add(line);
        }
        return list;
    }

    public static List<Identifier> getResourceLocationList(NbtList tagList) {
        ArrayList<Identifier> list = new ArrayList<Identifier>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            Identifier line = new Identifier(nbttagcompound.getString("Line"));
            list.add(line);
        }
        return list;
    }

    public static String[] getStringArray(NbtList tagList, int size) {
        String[] arr = new String[size];
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            String line = nbttagcompound.getString("Value");
            int slot = nbttagcompound.getInt("Slot");
            arr[slot] = line;
        }
        return arr;
    }

    public static NbtList nbtIntegerArraySet(List<int[]> set) {
        NbtList nbttaglist = new NbtList();
        if (set == null) {
            return nbttaglist;
        }
        for (int[] arr : set) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putIntArray("Array", arr);
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }

    public static NbtList nbtItemStackList(DefaultedList<ItemStack> inventory) {
        NbtList nbttaglist = new NbtList();
        for (int slot = 0; slot < inventory.size(); ++slot) {
            ItemStack item = (ItemStack)inventory.get(slot);
            if (item.isEmpty()) continue;
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putByte("Slot", (byte)slot);
            item.writeNbt(nbttagcompound);
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }

    public static NbtList nbtIItemStackMap(Map<Integer, IItemStack> inventory) {
        NbtList nbttaglist = new NbtList();
        if (inventory == null) {
            return nbttaglist;
        }
        for (int slot : inventory.keySet()) {
            IItemStack item = inventory.get(slot);
            if (item == null) continue;
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putByte("Slot", (byte)slot);
            item.getMCItemStack().writeNbt(nbttagcompound);
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }

    public static NbtList nbtItemStackArray(ItemStack[] inventory) {
        NbtList nbttaglist = new NbtList();
        if (inventory == null) {
            return nbttaglist;
        }
        for (int slot = 0; slot < inventory.length; ++slot) {
            ItemStack item = inventory[slot];
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putByte("Slot", (byte)slot);
            if (item != null) {
                item.writeNbt(nbttagcompound);
            }
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }

    public static NbtList nbtIngredientList(DefaultedList<Ingredient> inventory) {
        NbtList nbttaglist = new NbtList();
        if (inventory == null) {
            return nbttaglist;
        }
        for (int slot = 0; slot < inventory.size(); ++slot) {
            Ingredient ingredient = (Ingredient)inventory.get(slot);
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putByte("Slot", (byte)slot);
            if (ingredient != null && ingredient.getMatchingStacks().length > 0) {
                ingredient.getMatchingStacks()[0].writeNbt(nbttagcompound);
            }
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }

    public static NbtList nbtBooleanList(HashMap<Integer, Boolean> updatedSlots) {
        NbtList nbttaglist = new NbtList();
        if (updatedSlots == null) {
            return nbttaglist;
        }
        HashMap<Integer, Boolean> inventory2 = updatedSlots;
        for (Integer slot : inventory2.keySet()) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putInt("Slot", slot.intValue());
            nbttagcompound.putBoolean("Boolean", inventory2.get(slot).booleanValue());
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }

    public static NbtList nbtIntegerIntegerMap(Map<Integer, Integer> lines) {
        NbtList nbttaglist = new NbtList();
        if (lines == null) {
            return nbttaglist;
        }
        for (int slot : lines.keySet()) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putInt("Slot", slot);
            nbttagcompound.putInt("Integer", lines.get(slot).intValue());
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }

    public static NbtList nbtFloatMap(Map<Integer, Float> lines) {
        NbtList nbttaglist = new NbtList();
        if (lines == null) {
            return nbttaglist;
        }
        for (int slot : lines.keySet()) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putInt("Slot", slot);
            nbttagcompound.putFloat("Integer", lines.get(slot).floatValue());
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }

    public static NbtList nbtIntegerLongMap(HashMap<Integer, Long> lines) {
        NbtList nbttaglist = new NbtList();
        if (lines == null) {
            return nbttaglist;
        }
        for (int slot : lines.keySet()) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putInt("Slot", slot);
            nbttagcompound.putLong("Long", lines.get(slot).longValue());
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }

    public static NbtList nbtIntegerCollection(Collection<Integer> set) {
        NbtList nbttaglist = new NbtList();
        if (set == null) {
            return nbttaglist;
        }
        for (int slot : set) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putInt("Integer", slot);
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }

    public static NbtList nbtVectorMap(HashMap<String, Vector<String>> map) {
        NbtList list = new NbtList();
        if (map == null) {
            return list;
        }
        for (String key : map.keySet()) {
            NbtCompound compound = new NbtCompound();
            compound.putString("Key", key);
            NbtList values = new NbtList();
            for (String value : map.get(key)) {
                NbtCompound comp = new NbtCompound();
                comp.putString("Value", value);
                values.add((Object)comp);
            }
            compound.put("Values", (NbtElement)values);
            list.add((Object)compound);
        }
        return list;
    }

    public static NbtList nbtStringStringMap(HashMap<String, String> map) {
        NbtList nbttaglist = new NbtList();
        if (map == null) {
            return nbttaglist;
        }
        for (String slot : map.keySet()) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putString("Slot", slot);
            nbttagcompound.putString("Value", map.get(slot));
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }

    public static NbtList nbtStringIntegerMap(Map<String, Integer> map) {
        NbtList nbttaglist = new NbtList();
        if (map == null) {
            return nbttaglist;
        }
        for (String slot : map.keySet()) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putString("Slot", slot);
            nbttagcompound.putInt("Value", map.get(slot).intValue());
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }

    public static NbtElement nbtIntegerStringMap(Map<Integer, String> map) {
        NbtList nbttaglist = new NbtList();
        if (map == null) {
            return nbttaglist;
        }
        for (int slot : map.keySet()) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putInt("Slot", slot);
            nbttagcompound.putString("Value", map.get(slot));
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }

    public static NbtList nbtStringArray(String[] list) {
        NbtList nbttaglist = new NbtList();
        if (list == null) {
            return nbttaglist;
        }
        for (int i = 0; i < list.length; ++i) {
            if (list[i] == null) continue;
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putString("Value", list[i]);
            nbttagcompound.putInt("Slot", i);
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }

    public static NbtList nbtStringList(List<String> list) {
        NbtList nbttaglist = new NbtList();
        for (String s : list) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putString("Line", s);
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }

    public static NbtList nbtResourceLocationList(List<Identifier> list) {
        NbtList nbttaglist = new NbtList();
        for (Identifier s : list) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putString("Line", s.toString());
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }

    public static NbtList nbtDoubleList(double ... par1ArrayOfDouble) {
        NbtList nbttaglist = new NbtList();
        double[] adouble = par1ArrayOfDouble;
        int i = par1ArrayOfDouble.length;
        for (int j = 0; j < i; ++j) {
            double d1 = adouble[j];
            nbttaglist.add((Object)NbtDouble.of((double)d1));
        }
        return nbttaglist;
    }

    public static NbtCompound NBTMerge(NbtCompound data, NbtCompound merge) {
        NbtCompound compound = data.copy();
        Set names = merge.getKeys();
        for (String name : names) {
            NbtElement base = merge.get(name);
            if (base.getType() == 10) {
                base = NBTTags.NBTMerge(compound.getCompound(name), (NbtCompound)base);
            }
            compound.put(name, base);
        }
        return compound;
    }

    public static List<ScriptContainer> GetScript(NbtList list, IScriptHandler handler) {
        ArrayList<ScriptContainer> scripts = new ArrayList<ScriptContainer>();
        for (int i = 0; i < list.size(); ++i) {
            NbtCompound compoundd = list.getCompound(i);
            ScriptContainer script = new ScriptContainer(handler);
            script.load(compoundd);
            scripts.add(script);
        }
        return scripts;
    }

    public static NbtList NBTScript(List<ScriptContainer> scripts) {
        NbtList list = new NbtList();
        for (ScriptContainer script : scripts) {
            NbtCompound compound = new NbtCompound();
            script.save(compound);
            list.add((Object)compound);
        }
        return list;
    }

    public static TreeMap<Long, String> GetLongStringMap(NbtList tagList) {
        TreeMap<Long, String> list = new TreeMap<Long, String>();
        for (int i = 0; i < tagList.size(); ++i) {
            NbtCompound nbttagcompound = tagList.getCompound(i);
            list.put(nbttagcompound.getLong("Long"), nbttagcompound.getString("String"));
        }
        return list;
    }

    public static NbtList NBTLongStringMap(Map<Long, String> map) {
        NbtList nbttaglist = new NbtList();
        if (map == null) {
            return nbttaglist;
        }
        for (long slot : map.keySet()) {
            NbtCompound nbttagcompound = new NbtCompound();
            nbttagcompound.putLong("Long", slot);
            nbttagcompound.putString("String", map.get(slot));
            nbttaglist.add((Object)nbttagcompound);
        }
        return nbttaglist;
    }
}

