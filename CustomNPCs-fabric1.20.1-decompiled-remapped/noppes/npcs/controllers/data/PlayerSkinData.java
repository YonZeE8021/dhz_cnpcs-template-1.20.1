/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.util.Identifier
 *  net.minecraft.resource.ResourceManager
 */
package noppes.npcs.controllers.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.resource.ResourceManager;
import noppes.npcs.api.IPlayerSkin;

public class PlayerSkinData
implements IPlayerSkin {
    private boolean isMale = true;
    private int body;
    private int bodyColor;
    private int hair;
    private int hairColor;
    private int face;
    private int eyesColor;
    private int leg;
    private int jacket;
    private int shoes;
    private List<Integer> peculiarities;
    private boolean isActive;
    private Identifier cacheResLoc = null;
    private boolean hasChanged;
    private static boolean skinsNeedResync;

    @Override
    public boolean isMale() {
        return this.isMale;
    }

    @Override
    public PlayerSkinData setMale(boolean male) {
        this.isMale = male;
        this.markChanged();
        return this;
    }

    public String getGender() {
        return this.isMale ? "male" : "female";
    }

    @Override
    public int getBodyType() {
        return this.body;
    }

    @Override
    public PlayerSkinData setBodyType(int body) {
        this.body = body;
        this.markChanged();
        return this;
    }

    @Override
    public int getBodyColor() {
        return this.bodyColor;
    }

    @Override
    public PlayerSkinData setBodyColor(int bodyColor) {
        this.bodyColor = bodyColor;
        this.markChanged();
        return this;
    }

    @Override
    public int getHairType() {
        return this.hair;
    }

    @Override
    public PlayerSkinData setHairType(int hair) {
        this.hair = hair;
        this.markChanged();
        return this;
    }

    @Override
    public int getHairColor() {
        return this.hairColor;
    }

    @Override
    public PlayerSkinData setHairColor(int hairColor) {
        this.hairColor = hairColor;
        this.markChanged();
        return this;
    }

    @Override
    public int getFaceType() {
        return this.face;
    }

    @Override
    public PlayerSkinData setFaceType(int face) {
        this.face = face;
        this.markChanged();
        return this;
    }

    @Override
    public int getEyesColor() {
        return this.eyesColor;
    }

    @Override
    public PlayerSkinData setEyesColor(int eyesColor) {
        this.eyesColor = eyesColor;
        this.markChanged();
        return this;
    }

    @Override
    public int getPantsType() {
        return this.leg;
    }

    @Override
    public PlayerSkinData setPantsType(int leg) {
        this.leg = leg;
        this.markChanged();
        return this;
    }

    @Override
    public int getJacketType() {
        return this.jacket;
    }

    @Override
    public PlayerSkinData setJacketType(int jacket) {
        this.jacket = jacket;
        this.markChanged();
        return this;
    }

    @Override
    public int getShoesType() {
        return this.shoes;
    }

    @Override
    public PlayerSkinData setShoesType(int shoes) {
        this.shoes = shoes;
        this.markChanged();
        return this;
    }

    @Override
    public List<Integer> getPeculiarities() {
        return this.peculiarities;
    }

    @Override
    public PlayerSkinData setPeculiarities(List<Integer> peculiarities) {
        this.peculiarities = peculiarities;
        this.markChanged();
        return this;
    }

    public void markChanged() {
        this.calculateResLoc();
        skinsNeedResync = true;
        this.hasChanged = true;
        this.isActive = true;
    }

    public boolean hasChanged() {
        return this.hasChanged;
    }

    public void markSynced() {
        this.hasChanged = false;
    }

    public boolean isActive() {
        return this.isActive;
    }

    private void calculateResLoc() {
        StringBuilder path = new StringBuilder("textures/entity/custom/");
        path.append(this.getGender()).append("_");
        path.append(this.getBodyType()).append("_");
        path.append(this.getBodyColor()).append("_");
        path.append(this.getHairType()).append("_");
        path.append(this.getHairColor()).append("_");
        path.append(this.getFaceType()).append("_");
        path.append(this.getEyesColor()).append("_");
        path.append(this.getPantsType()).append("_");
        path.append(this.getJacketType()).append("_");
        path.append(this.getShoesType());
        for (int id : this.peculiarities) {
            path.append("_").append(id);
        }
        path.append(".png");
        this.cacheResLoc = new Identifier("customnpcs", path.toString());
    }

    public Identifier getResLoc() {
        if (this.cacheResLoc == null) {
            this.calculateResLoc();
        }
        return this.cacheResLoc;
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getPartResLocByNumber(ResourceManager textureManager, String name, int partNum) {
        Identifier loc = new Identifier("customnpcs", "textures/entity/custom/" + this.getGender() + "/" + name + "/" + partNum + ".png");
        if (textureManager.getResource(loc).isEmpty()) {
            loc = new Identifier("customnpcs", "textures/entity/custom/" + this.getGender() + "/" + name + "/0.png");
        }
        if (!textureManager.getResource(loc).isEmpty()) {
            return loc;
        }
        return null;
    }

    public NbtCompound saveNBTData(NbtCompound tag) {
        tag.putBoolean("isMale", this.isMale);
        tag.putInt("body", this.body);
        tag.putInt("bodyColor", this.bodyColor);
        tag.putInt("hair", this.hair);
        tag.putInt("hairColor", this.hairColor);
        tag.putInt("face", this.face);
        tag.putInt("eyesColor", this.eyesColor);
        tag.putInt("leg", this.leg);
        tag.putInt("jacket", this.jacket);
        tag.putInt("shoes", this.shoes);
        tag.putIntArray("peculiarities", this.peculiarities);
        tag.putBoolean("isActive", this.isActive);
        return tag;
    }

    public void loadNBTData(NbtCompound tag) {
        this.isMale = tag.getBoolean("isMale");
        this.body = tag.getInt("body");
        this.bodyColor = tag.getInt("bodyColor");
        this.hair = tag.getInt("hair");
        this.hairColor = tag.getInt("hairColor");
        this.face = tag.getInt("face");
        this.eyesColor = tag.getInt("eyesColor");
        this.leg = tag.getInt("leg");
        this.jacket = tag.getInt("jacket");
        this.shoes = tag.getInt("shoes");
        this.peculiarities = Arrays.stream(tag.getIntArray("peculiarities")).boxed().collect(Collectors.toList());
        this.isActive = tag.getBoolean("isActive");
    }

    public static boolean needsAnyResync() {
        return skinsNeedResync;
    }

    public static void resyncPerformed() {
        skinsNeedResync = false;
    }
}

