/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.model.ModelTransform
 *  net.minecraft.client.model.Dilation
 *  net.minecraft.client.model.ModelPartBuilder
 *  net.minecraft.client.model.TexturedModelData
 *  net.minecraft.client.model.ModelData
 *  net.minecraft.client.model.ModelPartData
 *  net.minecraft.client.model.ModelPart
 */
package noppes.npcs.client.parts;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelPart;
import noppes.npcs.client.parts.ModelPartWrapper;
import noppes.npcs.client.parts.MpmPartAbstractClient;
import noppes.npcs.client.parts.MpmPartData;
import noppes.npcs.client.parts.MpmPartReader;
import noppes.npcs.mixin.ModelPartMixin;
import noppes.npcs.shared.client.model.Model2DRenderer;
import noppes.npcs.shared.common.util.NopVector2i;
import noppes.npcs.shared.common.util.NopVector3f;

public class MpmPartBedrock
extends MpmPartAbstractClient {
    public final Map<Identifier, Model2DRenderer> playerModels = new HashMap<Identifier, Model2DRenderer>();
    private ModelPart model;
    private ModelPartMixin modelMixin;
    public NopVector2i textureSize = NopVector2i.ZERO;

    @Override
    public void render(MpmPartData data, MatrixStack mStack, VertexConsumer c, int lightmapUV, LivingEntity player) {
        mStack.push();
        if (this.model != null) {
            Map<String, ModelPart> children = this.modelMixin.getChildren();
            this.model.rotate(mStack);
            float f = 0.0625f;
            mStack.translate(-this.rotatePoint.x * f, -this.rotatePoint.y * f, -this.rotatePoint.z * f);
            mStack.scale(this.scale.x, this.scale.y, this.scale.z);
            for (ModelPart modelpart : children.values()) {
                modelpart.render(mStack, c, lightmapUV, OverlayTexture.DEFAULT_UV, data.color.x, data.color.y, data.color.z, 1.0f);
            }
        }
        mStack.pop();
    }

    @Override
    public void load(JsonObject renderData) {
        if (renderData != null && renderData.size() > 0) {
            ModelData meshdefinition = new ModelData();
            ModelPartData root = meshdefinition.getRoot();
            JsonObject ob = renderData.get("minecraft:geometry").getAsJsonArray().get(0).getAsJsonObject();
            JsonObject description = ob.get("description").getAsJsonObject();
            this.textureSize = new NopVector2i(description.get("texture_width").getAsInt(), description.get("texture_height").getAsInt());
            JsonArray bones = ob.get("bones").getAsJsonArray();
            HashMap<String, ModelPartData> namedParts = new HashMap<String, ModelPartData>();
            HashMap<String, NopVector3f> parentPivots = new HashMap<String, NopVector3f>();
            HashMap<String, ModelPartWrapper> defaultPose = new HashMap<String, ModelPartWrapper>();
            for (int i = 0; i < bones.size(); ++i) {
                JsonObject bone = bones.get(i).getAsJsonObject();
                String name = bone.get("name").getAsString();
                String pName = bone.has("parent") ? bone.get("parent").getAsString() : null;
                ModelPartData parent = pName != null && namedParts.containsKey(pName) ? (ModelPartData)namedParts.get(pName) : root;
                NopVector3f ppivot = parentPivots.containsKey(pName) ? (NopVector3f)parentPivots.get(pName) : NopVector3f.ZERO;
                NopVector3f pivot = MpmPartReader.jsonVector3f(bone.get("pivot"));
                parentPivots.put(name, pivot);
                NopVector3f rotation = MpmPartReader.jsonVector3f(bone.get("rotation")).mul((float)Math.PI / 180);
                ModelTransform pose = ModelTransform.of((float)(pivot.x - ppivot.x), (float)(ppivot.y - pivot.y), (float)(pivot.z - ppivot.z), (float)rotation.x, (float)rotation.y, (float)rotation.z);
                ModelPartData partDef = parent.addChild(name, ModelPartBuilder.create(), pose);
                defaultPose.put(name, new ModelPartWrapper((ModelPart)null, new NopVector3f(pose.pivotX, pose.pivotY, pose.pivotZ), rotation));
                if (bone.has("cubes")) {
                    JsonArray cubes = bone.get("cubes").getAsJsonArray();
                    for (int j = 0; j < cubes.size(); ++j) {
                        ModelPartBuilder builder = ModelPartBuilder.create();
                        JsonObject cube = cubes.get(j).getAsJsonObject();
                        NopVector2i uv = MpmPartReader.jsonVector2i(cube.get("uv"));
                        boolean mirror = cube.has("mirror") && cube.get("mirror").getAsBoolean();
                        NopVector3f cpivot = MpmPartReader.jsonVector3f(cube.get("pivot"));
                        rotation = MpmPartReader.jsonVector3f(cube.get("rotation")).mul((float)Math.PI / 180);
                        NopVector3f origin = MpmPartReader.jsonVector3f(cube.get("origin"));
                        NopVector3f size = MpmPartReader.jsonVector3f(cube.get("size"));
                        Dilation deform = cube.has("inflate") ? new Dilation(cube.get("inflate").getAsFloat()) : Dilation.NONE;
                        builder.uv(uv.x, uv.y).mirrored(mirror).cuboid(origin.x - cpivot.x, cpivot.y - size.y - origin.y, origin.z - cpivot.z, size.x, size.y, size.z, deform);
                        partDef.addChild("cube_" + name + j, builder, ModelTransform.of((float)(cpivot.x - pivot.x), (float)(pivot.y - cpivot.y), (float)(cpivot.z - pivot.z), (float)rotation.x, (float)rotation.y, (float)rotation.z));
                    }
                }
                namedParts.put(name, partDef);
            }
            this.model = TexturedModelData.of((ModelData)meshdefinition, (int)description.get("texture_width").getAsInt(), (int)description.get("texture_height").getAsInt()).createModel();
            this.model.setPivot(this.translate.x, this.translate.y, this.translate.z);
            for (Map.Entry entry : defaultPose.entrySet()) {
                ((ModelPartWrapper)entry.getValue()).mcPart = this.getChild(this.model, (String)entry.getKey());
            }
            defaultPose.put(null, new ModelPartWrapper(this.model, this.translate, this.rotate));
            this.defaultPose = defaultPose;
            NopVector3f rotation = this.rotate.mul((float)Math.PI / 180);
            this.model.setAngles(rotation.x, rotation.y, rotation.z);
            this.modelMixin = (ModelPartMixin)this.model;
        }
    }

    private ModelPart getChild(ModelPart root, String name) {
        Map<String, ModelPart> children = ((ModelPartMixin)root).getChildren();
        if (children.containsKey(name)) {
            return children.get(name);
        }
        for (ModelPart child : children.values()) {
            ModelPart p = this.getChild(child, name);
            if (p == null) continue;
            return p;
        }
        return null;
    }
}

