/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtString
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.api.wrapper;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.api.item.IItemBook;
import noppes.npcs.api.wrapper.ItemStackWrapper;

public class ItemBookWrapper
extends ItemStackWrapper
implements IItemBook {
    protected ItemBookWrapper(ItemStack item) {
        super(item);
    }

    @Override
    public String getTitle() {
        return this.getTag().getString("title");
    }

    @Override
    public void setTitle(String title) {
        this.getTag().putString("title", title);
    }

    @Override
    public String getAuthor() {
        return this.getTag().getString("author");
    }

    @Override
    public void setAuthor(String author) {
        this.getTag().putString("author", author);
    }

    @Override
    public String[] getText() {
        ArrayList<String> list = new ArrayList<String>();
        NbtList pages = this.getTag().getList("pages", 8);
        for (int i = 0; i < pages.size(); ++i) {
            list.add(pages.getString(i));
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public void setText(String[] pages) {
        NbtList list = new NbtList();
        if (pages != null && pages.length > 0) {
            for (String page : pages) {
                list.add((Object)NbtString.of((String)page));
            }
        }
        this.getTag().put("pages", (NbtElement)list);
    }

    private NbtCompound getTag() {
        NbtCompound comp = this.item.getNbt();
        if (comp == null) {
            comp = new NbtCompound();
            this.item.setNbt(comp);
        }
        return comp;
    }

    @Override
    public boolean isBook() {
        return true;
    }

    @Override
    public int getType() {
        return 1;
    }
}

