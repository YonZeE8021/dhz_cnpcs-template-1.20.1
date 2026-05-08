/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.util.Identifier
 */
package noppes.npcs.client.parts;

import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.util.Identifier;
import noppes.npcs.client.parts.ModelPartWrapper;
import noppes.npcs.client.parts.MpmPartAnimation;
import noppes.npcs.client.parts.PartBehaviorType;
import noppes.npcs.client.parts.PartRenderType;
import noppes.npcs.constants.BodyPart;
import noppes.npcs.shared.common.util.NopVector3f;

public class MpmPart {
    public boolean isEnabled;
    public Identifier id;
    public Identifier parentId;
    public String name;
    public Identifier texture;
    public String menu;
    public PartRenderType renderType;
    public PartBehaviorType animationType;
    public BodyPart bodyPart;
    public List<BodyPart> hiddenParts;
    public NopVector3f translate = NopVector3f.ZERO;
    public NopVector3f scale = NopVector3f.ZERO;
    public NopVector3f rotatePoint = NopVector3f.ZERO;
    public NopVector3f rotate = NopVector3f.ZERO;
    public int previewRotation = 45;
    public boolean disableCustomTextures;
    public boolean defaultUsePlayerSkins;
    public String author;
    public MpmPartAnimation animationData = new MpmPartAnimation();

    public void load(JsonObject renderData) {
    }

    public ModelPartWrapper getPart(String name) {
        return null;
    }
}

