/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtString
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.api.function.gui.GuiComponentClicked;
import noppes.npcs.api.gui.IButton;
import noppes.npcs.api.gui.IButtonList;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiComponentWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTexturedRectWrapper;

public class CustomGuiButtonListWrapper
extends CustomGuiButtonWrapper
implements IButtonList {
    CustomGuiTexturedRectWrapper left = new CustomGuiTexturedRectWrapper();
    CustomGuiTexturedRectWrapper right = new CustomGuiTexturedRectWrapper();
    private int selected = 0;
    private String[] values = new String[0];

    public CustomGuiButtonListWrapper() {
    }

    public CustomGuiButtonListWrapper(int id, int x, int y, int width, int height) {
        super(id, "", x, y, width, height);
        CustomGuiTexturedRectWrapper rect = this.getTextureRect();
        rect.setTexture("customnpcs:textures/gui/components.png");
        rect.setRepeatingTexture(64, 22, 3).setTextureOffset(0, 64).setPos(7, 0);
        this.setTextureHoverOffset(22);
        this.left.setTexture("customnpcs:textures/gui/components.png").setTextureOffset(0, 130);
        this.left.setSize(10, 20).setPos(0, 0);
        this.right.setTexture("customnpcs:textures/gui/components.png").setTextureOffset(12, 130);
        this.right.setSize(10, 20).setPos(width - 10, 0);
    }

    @Override
    public CustomGuiButtonListWrapper setSize(int width, int height) {
        super.setSize(width, height);
        this.getTextureRect().setSize(width - 14, height);
        return this;
    }

    @Override
    public CustomGuiButtonListWrapper setValues(String ... values) {
        if (values == null || values.length == 0) {
            this.values = new String[0];
            this.setLabel("");
        } else {
            this.values = values;
            this.selected %= values.length;
            this.setLabel(this.values[this.selected]);
        }
        return this;
    }

    @Override
    public String[] getValues() {
        return this.values;
    }

    @Override
    public CustomGuiButtonListWrapper setSelected(int selected) {
        if (selected < 0) {
            selected = this.values.length + selected;
        }
        if (selected >= this.values.length) {
            selected %= this.values.length;
        }
        this.selected = selected;
        this.setLabel(this.values[this.selected]);
        return this;
    }

    @Override
    public int getSelected() {
        return this.selected;
    }

    @Override
    public CustomGuiTexturedRectWrapper getLeftTexture() {
        return this.left;
    }

    @Override
    public CustomGuiTexturedRectWrapper getRightTexture() {
        return this.right;
    }

    @Override
    public int getType() {
        return 7;
    }

    @Override
    public CustomGuiButtonListWrapper setOnPress(GuiComponentClicked<IButton> onPress) {
        super.setOnPress((GuiComponentClicked)onPress);
        return this;
    }

    @Override
    public NbtCompound toNBT(NbtCompound nbt) {
        super.toNBT(nbt);
        nbt.putInt("selected", this.selected);
        NbtList list = new NbtList();
        for (String s : this.values) {
            list.add(NbtString.of((String)s));
        }
        nbt.put("values", (NbtElement)list);
        nbt.put("left", (NbtElement)this.left.toNBT(new NbtCompound()));
        nbt.put("right", (NbtElement)this.right.toNBT(new NbtCompound()));
        return nbt;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(NbtCompound nbt) {
        super.fromNBT(nbt);
        this.selected = nbt.getInt("selected");
        this.values = (String[])nbt.getList("values", 8).stream().map(NbtElement::asString).toArray(String[]::new);
        this.left.fromNBT(nbt.getCompound("left"));
        this.right.fromNBT(nbt.getCompound("right"));
        return this;
    }
}

