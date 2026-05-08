/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 */
package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.NbtCompound;
import noppes.npcs.api.gui.ITextArea;
import noppes.npcs.api.wrapper.gui.CustomGuiComponentWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;

public class CustomGuiTextAreaWrapper
extends CustomGuiTextFieldWrapper
implements ITextArea {
    private boolean codeTheme = false;

    public CustomGuiTextAreaWrapper() {
    }

    public CustomGuiTextAreaWrapper(int id, int x, int y, int width, int height) {
        super(id, x, y, width, height);
    }

    @Override
    public int getType() {
        return 6;
    }

    @Override
    public NbtCompound toNBT(NbtCompound nbt) {
        super.toNBT(nbt);
        nbt.putBoolean("codetheme", this.codeTheme);
        return nbt;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(NbtCompound nbt) {
        super.fromNBT(nbt);
        this.setCodeTheme(nbt.getBoolean("codetheme"));
        return this;
    }

    @Override
    public void setCodeTheme(boolean bo) {
        this.codeTheme = bo;
    }

    @Override
    public boolean getCodeTheme() {
        return this.codeTheme;
    }
}

