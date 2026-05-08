/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtString
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.text.Text
 *  net.minecraft.text.TranslatableTextContent
 */
package noppes.npcs.api.wrapper.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiAssetsSelectorWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonListWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiButtonWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiColoredLineWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiEntityDisplayWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiItemRendererWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiItemSlotWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiLabelWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiScrollWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiSliderWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTextAreaWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTextFieldWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiTexturedRectWrapper;

public abstract class CustomGuiComponentWrapper
implements ICustomGuiComponent {
    public UUID uniqueId = UUID.randomUUID();
    private int id;
    private int posX;
    private int posY;
    private int width;
    private int height;
    private List<Text> hoverText = new ArrayList<Text>();
    private boolean enabled = true;
    private boolean visible = true;
    public boolean disablePackets = false;

    public CustomGuiComponentWrapper setDisablePackets() {
        this.disablePackets = true;
        return this;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public CustomGuiComponentWrapper setID(int id) {
        this.id = id;
        return this;
    }

    @Override
    public boolean getEnabled() {
        return this.enabled;
    }

    @Override
    public CustomGuiComponentWrapper setEnabled(boolean bo) {
        this.enabled = bo;
        return this;
    }

    @Override
    public boolean getVisible() {
        return this.visible;
    }

    @Override
    public CustomGuiComponentWrapper setVisible(boolean bo) {
        this.visible = bo;
        return this;
    }

    @Override
    public UUID getUniqueID() {
        return this.uniqueId;
    }

    @Override
    public int getPosX() {
        return this.posX;
    }

    @Override
    public int getPosY() {
        return this.posY;
    }

    @Override
    public CustomGuiComponentWrapper setPos(int x, int y) {
        this.posX = x;
        this.posY = y;
        return this;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public CustomGuiComponentWrapper setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public boolean hasHoverText() {
        return this.hoverText.size() > 0;
    }

    @Override
    public String[] getHoverText() {
        String[] ht = new String[this.hoverText.size()];
        for (int i = 0; i < this.hoverText.size(); ++i) {
            ht[i] = ((TranslatableTextContent)this.hoverText.get(i).getContent()).getKey();
        }
        return ht;
    }

    public List<Text> getHoverTextList() {
        return this.hoverText;
    }

    @Override
    public CustomGuiComponentWrapper setHoverText(String text) {
        this.hoverText = new ArrayList<Text>();
        this.hoverText.add((Text)Text.translatable((String)text));
        return this;
    }

    @Override
    public CustomGuiComponentWrapper setHoverText(String[] text) {
        this.hoverText = new ArrayList<Text>();
        for (String obj : text) {
            if (obj instanceof Text) {
                this.hoverText.add((Text)obj);
                continue;
            }
            this.hoverText.add((Text)Text.translatable((String)String.valueOf(obj)));
        }
        return this;
    }

    public CustomGuiComponentWrapper setHoverText(List<Text> list) {
        this.hoverText = list;
        return this;
    }

    public NbtCompound toNBT(NbtCompound nbt) {
        nbt.putInt("id", this.id);
        nbt.putBoolean("enabled", this.enabled);
        nbt.putBoolean("visible", this.visible);
        nbt.putUuid("uniqueId", this.uniqueId);
        nbt.putIntArray("pos", new int[]{this.posX, this.posY});
        nbt.putIntArray("size", new int[]{this.width, this.height});
        if (this.hoverText != null) {
            NbtList list = new NbtList();
            for (Text s : this.hoverText) {
                list.add((Object)NbtString.of((String)((TranslatableTextContent)s.getContent()).getKey()));
            }
            if (list.size() > 0) {
                nbt.put("hover", (NbtElement)list);
            }
        }
        nbt.putInt("type", this.getType());
        return nbt;
    }

    public CustomGuiComponentWrapper fromNBT(NbtCompound nbt) {
        this.setID(nbt.getInt("id"));
        this.setEnabled(nbt.getBoolean("enabled"));
        this.setVisible(nbt.getBoolean("visible"));
        this.uniqueId = nbt.getUuid("uniqueId");
        this.setPos(nbt.getIntArray("pos")[0], nbt.getIntArray("pos")[1]);
        this.setSize(nbt.getIntArray("size")[0], nbt.getIntArray("size")[1]);
        if (nbt.contains("hover")) {
            NbtList list = nbt.getList("hover", 8);
            String[] hoverText = new String[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                hoverText[i] = list.get(i).asString();
            }
            this.setHoverText(hoverText);
        }
        return this;
    }

    public static CustomGuiComponentWrapper createFromNBT(NbtCompound nbt) {
        switch (nbt.getInt("type")) {
            case 0: {
                return new CustomGuiButtonWrapper().fromNBT(nbt);
            }
            case 7: {
                return new CustomGuiButtonListWrapper().fromNBT(nbt);
            }
            case 1: {
                return new CustomGuiLabelWrapper().fromNBT(nbt);
            }
            case 2: {
                return new CustomGuiTexturedRectWrapper().fromNBT(nbt);
            }
            case 3: {
                return new CustomGuiTextFieldWrapper().fromNBT(nbt);
            }
            case 4: {
                return new CustomGuiScrollWrapper().fromNBT(nbt);
            }
            case 5: {
                return new CustomGuiItemSlotWrapper().fromNBT(nbt);
            }
            case 6: {
                return new CustomGuiTextAreaWrapper().fromNBT(nbt);
            }
            case 8: {
                return new CustomGuiSliderWrapper().fromNBT(nbt);
            }
            case 9: {
                return new CustomGuiEntityDisplayWrapper().fromNBT(nbt);
            }
            case 10: {
                return new CustomGuiAssetsSelectorWrapper().fromNBT(nbt);
            }
            case 11: {
                return new CustomGuiColoredLineWrapper().fromNBT(nbt);
            }
            case 12: {
                return new CustomGuiItemRendererWrapper().fromNBT(nbt);
            }
        }
        return null;
    }
}

