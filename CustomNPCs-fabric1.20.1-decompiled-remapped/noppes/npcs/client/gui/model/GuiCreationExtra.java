/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.nbt.NbtByte
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.render.entity.EntityRenderer
 */
package noppes.npcs.client.gui.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.entity.EntityRenderer;
import noppes.npcs.client.gui.model.GuiCreationScreenInterface;
import noppes.npcs.controllers.CobblemonHelper;
import noppes.npcs.controllers.PixelmonHelper;
import noppes.npcs.entity.EntityFakeLiving;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonBiDirectional;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiTextFieldNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiInterface;
import noppes.npcs.shared.client.gui.listeners.ITextfieldListener;

public class GuiCreationExtra
extends GuiCreationScreenInterface
implements ICustomScrollListener,
ITextfieldListener {
    private final String[] ignoredTags = new String[]{"CanBreakDoors", "Bred", "PlayerCreated", "HasReproduced"};
    private final String[] grimmsTags = new String[]{"DataSkin", "DataHair", "DataFace", "DataUniform", "DataGemstone", "DataVisor", "DataGloves", "DataCape"};
    private final String[] booleanTags = new String[0];
    private GuiCustomScrollNop scroll;
    private Map<String, GuiType> data = new HashMap<String, GuiType>();
    private GuiType selected;
    public int nextAvailableFieldId = 0;

    public GuiCreationExtra(EntityNPCInterface npc) {
        super(npc);
        this.active = 2;
    }

    @Override
    public void init() {
        super.init();
        if (this.entity == null) {
            return;
        }
        this.data = this.getData(this.entity);
        if (this.scroll == null) {
            this.scroll = new GuiCustomScrollNop(this, 0);
            ArrayList<String> list = new ArrayList<String>(this.data.keySet());
            this.scroll.setList(list);
            if (list.isEmpty()) {
                return;
            }
            this.scroll.setSelected((String)list.get(0));
        }
        this.selected = this.data.get(this.scroll.getSelected());
        if (this.selected == null) {
            return;
        }
        this.scroll.guiLeft = this.guiLeft;
        this.scroll.guiTop = this.guiTop + 46;
        this.scroll.setSize(100, this.imageHeight - 74);
        this.addScroll(this.scroll);
        this.selected.init();
    }

    public Map<String, GuiType> getData(LivingEntity entity) {
        HashMap<String, GuiType> data = new HashMap<String, GuiType>();
        NbtCompound compound = this.getExtras(entity);
        Set keys = compound.getKeys();
        for (String name : keys) {
            byte b;
            if (this.isIgnored(name)) continue;
            NbtElement base = compound.get(name);
            if (name.equals("Age")) {
                data.put("Child", new GuiTypeBoolean("Child", entity.isBaby()));
                continue;
            }
            if (name.equals("Color") && base.getType() == 1) {
                data.put("Color", new GuiTypeByte("Color", compound.getByte("Color")));
                continue;
            }
            if (base.getType() == 3) {
                data.put(name, new GuiTypeInt(name, compound.getInt(name)));
                continue;
            }
            if (base.getType() != 1 || (b = ((NbtByte)base).byteValue()) != 0 && b != 1) continue;
            if (this.playerdata.extra.contains(name)) {
                b = this.playerdata.extra.getByte(name);
            }
            data.put(name, new GuiTypeBoolean(name, b == 1));
        }
        if (PixelmonHelper.isPixelmon((Entity)entity)) {
            data.put("Model", new GuiTypePixelmon("Model"));
        }
        if (CobblemonHelper.isPokemon((Entity)entity)) {
            data.put("CobblemonModel", new GuiTypeCobblemon("CobblemonModel"));
        }
        if (entity.getSavedEntityId().equals("tgvstyle.Dog")) {
            data.put("Breed", new GuiTypeDoggyStyle("Breed"));
        }
        return data;
    }

    private boolean isIgnored(String tag) {
        for (String s : this.ignoredTags) {
            if (!s.equals(tag)) continue;
            return true;
        }
        return false;
    }

    private boolean isGrimms(String tag) {
        for (String s : this.grimmsTags) {
            if (!s.equals(tag)) continue;
            return true;
        }
        return false;
    }

    private void updateTexture() {
        LivingEntity entity = this.playerdata.getEntity(this.npc);
        EntityRenderer render = this.client.getEntityRenderDispatcher().getRenderer((Entity)entity);
        this.npc.display.setSkinTexture(render.getTexture((Entity)entity).toString());
    }

    private NbtCompound getExtras(LivingEntity entity) {
        NbtCompound fake = new NbtCompound();
        new EntityFakeLiving(entity.getWorld()).writeCustomDataToNbt(fake);
        NbtCompound compound = new NbtCompound();
        try {
            entity.writeCustomDataToNbt(compound);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        Set keys = fake.getKeys();
        for (String name : keys) {
            compound.remove(name);
        }
        return compound;
    }

    @Override
    public void scrollClicked(double i, double j, int k, GuiCustomScrollNop scroll) {
        if (scroll.id == 0) {
            this.init();
        } else if (this.selected != null) {
            this.selected.scrollClicked(i, j, k, scroll);
        }
    }

    @Override
    public void buttonEvent(GuiButtonNop btn) {
        if (this.selected != null) {
            this.selected.buttonEvent(btn);
        }
    }

    @Override
    public void unFocused(GuiTextFieldNop textfield) {
        if (this.selected != null) {
            this.selected.unFocused(textfield);
        }
    }

    @Override
    public void scrollDoubleClicked(String selection, GuiCustomScrollNop scroll) {
    }

    abstract class GuiType {
        public String name;

        public GuiType(String name) {
            this.name = name;
        }

        public void init() {
        }

        public void buttonEvent(GuiButtonNop button) {
        }

        public void scrollClicked(double i, double j, int k, GuiCustomScrollNop scroll) {
        }

        public void unFocused(GuiTextFieldNop textfield) {
        }
    }

    class GuiTypeBoolean
    extends GuiType {
        private boolean bo;

        public GuiTypeBoolean(String name, boolean bo) {
            super(name);
            this.bo = bo;
        }

        @Override
        public void init() {
            GuiCreationExtra.this.addButton(new GuiButtonYesNo((IGuiInterface)GuiCreationExtra.this, 11, GuiCreationExtra.this.guiLeft + 120, GuiCreationExtra.this.guiTop + 50, 60, 20, this.bo));
        }

        @Override
        public void buttonEvent(GuiButtonNop button) {
            if (button.id != 11) {
                return;
            }
            this.bo = ((GuiButtonYesNo)button).getBoolean();
            if (this.name.equals("Child")) {
                GuiCreationExtra.this.playerdata.extra.putInt("Age", this.bo ? -24000 : 0);
                GuiCreationExtra.this.playerdata.clearEntity();
            } else {
                GuiCreationExtra.this.playerdata.extra.putBoolean(this.name, this.bo);
                GuiCreationExtra.this.playerdata.clearEntity();
                GuiCreationExtra.this.updateTexture();
            }
        }
    }

    class GuiTypeByte
    extends GuiType {
        private byte b;

        public GuiTypeByte(String name, byte b) {
            super(name);
            this.b = b;
        }

        @Override
        public void init() {
            GuiCreationExtra.this.addButton(new GuiButtonBiDirectional((IGuiInterface)GuiCreationExtra.this, 11, GuiCreationExtra.this.guiLeft + 120, GuiCreationExtra.this.guiTop + 45, 50, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"}, this.b));
        }

        @Override
        public void buttonEvent(GuiButtonNop button) {
            if (button.id != 11) {
                return;
            }
            GuiCreationExtra.this.playerdata.extra.putByte(this.name, (byte)button.getValue());
            GuiCreationExtra.this.playerdata.clearEntity();
            GuiCreationExtra.this.updateTexture();
        }
    }

    class GuiTypeInt
    extends GuiType {
        private int initVal;
        private int fieldId;

        public GuiTypeInt(String name, int b) {
            super(name);
            this.initVal = b;
            this.fieldId = GuiCreationExtra.this.nextAvailableFieldId++;
        }

        @Override
        public void init() {
            GuiTextFieldNop field = new GuiTextFieldNop(11, (Screen)GuiCreationExtra.this, GuiCreationExtra.this.guiLeft + 120, GuiCreationExtra.this.guiTop + 45, 50, 20, "" + this.initVal);
            field.setNumbersOnly();
            GuiCreationExtra.this.addTextField(field);
        }

        @Override
        public void unFocused(GuiTextFieldNop textfield) {
            if (textfield.id != 11) {
                return;
            }
            GuiCreationExtra.this.playerdata.extra.putInt(this.name, textfield.getInteger());
            GuiCreationExtra.this.playerdata.clearEntity();
            GuiCreationExtra.this.updateTexture();
        }
    }

    class GuiTypePixelmon
    extends GuiType {
        public GuiTypePixelmon(String name) {
            super(name);
        }

        @Override
        public void init() {
            GuiCustomScrollNop scroll = new GuiCustomScrollNop(GuiCreationExtra.this, 1);
            scroll.setSize(120, 200);
            scroll.guiLeft = GuiCreationExtra.this.guiLeft + 120;
            scroll.guiTop = GuiCreationExtra.this.guiTop + 20;
            GuiCreationExtra.this.addScroll(scroll);
            scroll.setList(PixelmonHelper.getPixelmonList());
            scroll.setSelected(PixelmonHelper.getName(GuiCreationExtra.this.entity));
        }

        @Override
        public void scrollClicked(double i, double j, int k, GuiCustomScrollNop scroll) {
            String name = scroll.getSelected();
            GuiCreationExtra.this.playerdata.setExtra(GuiCreationExtra.this.entity, "name", name);
            GuiCreationExtra.this.updateTexture();
        }
    }

    class GuiTypeCobblemon
    extends GuiType {
        public GuiTypeCobblemon(String name) {
            super(name);
        }

        @Override
        public void init() {
            GuiCustomScrollNop scroll = new GuiCustomScrollNop(GuiCreationExtra.this, 1);
            scroll.setSize(120, 200);
            scroll.guiLeft = GuiCreationExtra.this.guiLeft + 120;
            scroll.guiTop = GuiCreationExtra.this.guiTop + 20;
            GuiCreationExtra.this.addScroll(scroll);
            scroll.setList(CobblemonHelper.getTypes());
            scroll.setSelected(CobblemonHelper.getType((Entity)GuiCreationExtra.this.entity).toString());
        }

        @Override
        public void scrollClicked(double i, double j, int k, GuiCustomScrollNop scroll) {
            String name = scroll.getSelected();
            GuiCreationExtra.this.playerdata.setExtra(GuiCreationExtra.this.entity, "CobblemonModel", name);
            GuiCreationExtra.this.updateTexture();
        }
    }

    class GuiTypeDoggyStyle
    extends GuiType {
        public GuiTypeDoggyStyle(String name) {
            super(name);
        }

        @Override
        public void init() {
            Enum breed = null;
            try {
                Method method = GuiCreationExtra.this.entity.getClass().getMethod("getBreedID", new Class[0]);
                breed = (Enum)method.invoke((Object)GuiCreationExtra.this.entity, new Object[0]);
            }
            catch (Exception exception) {
                // empty catch block
            }
            GuiCreationExtra.this.addButton(new GuiButtonBiDirectional((IGuiInterface)GuiCreationExtra.this, 11, GuiCreationExtra.this.guiLeft + 120, GuiCreationExtra.this.guiTop + 45, 50, 20, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26"}, breed.ordinal()));
        }

        @Override
        public void buttonEvent(GuiButtonNop button) {
            if (button.id != 11) {
                return;
            }
            int breed = button.getValue();
            LivingEntity entity = GuiCreationExtra.this.playerdata.getEntity(GuiCreationExtra.this.npc);
            GuiCreationExtra.this.playerdata.setExtra(entity, "breed", "" + button.getValue());
            GuiCreationExtra.this.updateTexture();
        }
    }
}

