/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.texture.MissingSprite
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.util.Identifier
 */
package noppes.npcs;

import java.util.Random;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import noppes.npcs.client.parts.MpmPartData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketEyeBlink;
import noppes.npcs.shared.common.util.ColorUtil;
import noppes.npcs.shared.common.util.NopVector2i;
import noppes.npcs.shared.common.util.NopVector3f;

public class ModelEyeData
extends MpmPartData {
    public static final Identifier RESOURCE = new Identifier("moreplayermodels", "eyes");
    public static final Identifier RESOURCE_LEFT = new Identifier("moreplayermodels", "eyes_left");
    public static final Identifier RESOURCE_RIGHT = new Identifier("moreplayermodels", "eyes_right");
    private final Random r = new Random();
    public boolean glint = true;
    public NopVector3f browThickness = new NopVector3f(1.0f, 0.4f, 1.0f);
    public NopVector2i eyePos = NopVector2i.ZERO;
    public boolean mirror = false;
    public int eyeSize = 0;
    public int skinType = 0;
    public boolean useLidTexture = false;
    public NopVector3f lidColor = ColorUtil.colorToRgb(11830381);
    public NopVector3f browColor = ColorUtil.colorToRgb(5982516);
    public long blinkStart = 0L;
    public boolean disableBlink = false;

    public ModelEyeData() {
        this.color = (new NopVector3f[]{ColorUtil.colorToRgb(8368696), ColorUtil.colorToRgb(16247203), ColorUtil.colorToRgb(0xA0A0FF), ColorUtil.colorToRgb(0xA7A7A7), ColorUtil.colorToRgb(10791096), ColorUtil.colorToRgb(0x4040FF), ColorUtil.colorToRgb(14188339), ColorUtil.colorToRgb(11685080), ColorUtil.colorToRgb(6724056), ColorUtil.colorToRgb(0xE5E533), ColorUtil.colorToRgb(55610), ColorUtil.colorToRgb(8375321), ColorUtil.colorToRgb(15892389), ColorUtil.colorToRgb(0x999999), ColorUtil.colorToRgb(5013401), ColorUtil.colorToRgb(8339378), ColorUtil.colorToRgb(3361970), ColorUtil.colorToRgb(6704179), ColorUtil.colorToRgb(6717235), ColorUtil.colorToRgb(0x993333), ColorUtil.colorToRgb(16445005), ColorUtil.colorToRgb(6085589), ColorUtil.colorToRgb(4882687)})[this.r.nextInt(23)];
    }

    @Override
    public NbtCompound getNbt() {
        NbtCompound compound = super.getNbt();
        compound.putBoolean("Glint", this.glint);
        compound.putBoolean("UseLidTexture", this.useLidTexture);
        compound.putBoolean("Mirror", this.mirror);
        compound.putBoolean("DisableBlink", this.disableBlink);
        compound.putInt("SkinType", this.skinType);
        compound.putInt("EyeSize", this.eyeSize);
        compound.putInt("SkinColor", ColorUtil.rgbToColor(this.lidColor));
        compound.putInt("BrowColor", ColorUtil.rgbToColor(this.browColor));
        compound.putInt("PositionX", this.eyePos.x);
        compound.putInt("PositionY", this.eyePos.y);
        compound.putInt("BrowThickness", (int)(this.browThickness.y * 10.0f));
        return compound;
    }

    @Override
    public void setNbt(NbtCompound compound) {
        super.setNbt(compound);
        this.glint = compound.getBoolean("Glint");
        this.useLidTexture = compound.getBoolean("UseLidTexture");
        this.mirror = compound.getBoolean("Mirror");
        this.disableBlink = compound.getBoolean("DisableBlink");
        this.skinType = compound.getInt("SkinType");
        this.eyeSize = compound.getInt("EyeSize");
        this.lidColor = ColorUtil.colorToRgb(compound.getInt("SkinColor"));
        this.browColor = ColorUtil.colorToRgb(compound.getInt("BrowColor"));
        this.eyePos = new NopVector2i(compound.getInt("PositionX"), compound.getInt("PositionY"));
        this.browThickness = new NopVector3f(1.0f, (float)compound.getInt("BrowThickness") / 10.0f, 1.0f);
    }

    public void update(LivingEntity player) {
        if (!player.isAlive() || this.disableBlink) {
            return;
        }
        if (this.blinkStart < 0L) {
            ++this.blinkStart;
        } else if (this.blinkStart == 0L) {
            if (this.r.nextInt(140) == 1) {
                this.blinkStart = System.currentTimeMillis();
                if (!player.getWorld().isClient) {
                    Packets.sendNearby((Entity)player, new PacketEyeBlink(player.getId()));
                }
            }
        } else if (System.currentTimeMillis() - this.blinkStart > 300L) {
            this.blinkStart = -20L;
        }
    }

    @Override
    public Identifier getUrlTexture() {
        Identifier url = super.getUrlTexture();
        if (url == null) {
            return MissingSprite.getMissingSpriteId();
        }
        return url;
    }
}

