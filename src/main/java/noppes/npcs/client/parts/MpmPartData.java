/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.client.texture.MissingSprite
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 */
package noppes.npcs.client.parts;

import java.io.File;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import noppes.npcs.client.parts.MpmPart;
import noppes.npcs.client.parts.MpmPartReader;
import noppes.npcs.shared.client.util.ImageDownloadAlt;
import noppes.npcs.shared.client.util.NoppesStringUtils;
import noppes.npcs.shared.client.util.ResourceDownloader;
import noppes.npcs.shared.common.util.NopVector3f;

public class MpmPartData {
    public static final NopVector3f WHITE = new NopVector3f(1.0f, 1.0f, 1.0f);
    public Identifier partId;
    public boolean usePlayerSkin = false;
    public NopVector3f color = WHITE;
    public Identifier texture = null;
    private Identifier textureUrl = null;
    public String url = "";

    public MpmPart getPart() {
        return MpmPartReader.PARTS.get(this.partId);
    }

    public Identifier getTexture() {
        if (this.getUrlTexture() != null) {
            return this.getUrlTexture();
        }
        if (this.texture != null) {
            return this.texture;
        }
        MpmPart part = this.getPart();
        if (part != null && part.texture != null) {
            return this.getPart().texture;
        }
        return MissingSprite.getMissingSpriteId();
    }

    public Identifier getUrlTexture() {
        if (this.textureUrl != null) {
            return this.textureUrl;
        }
        if (!this.url.isEmpty()) {
            Identifier resource = ResourceDownloader.getUrlResourceLocation(this.url, false);
            File file = ResourceDownloader.getUrlFile(this.url, false);
            TextureManager texturemanager = MinecraftClient.getInstance().getTextureManager();
            AbstractTexture object = texturemanager.getOrDefault(resource, null);
            if (object == null) {
                this.textureUrl = this.getDefaultTexture();
                ResourceDownloader.load(new ImageDownloadAlt(file, this.url, resource, this.getDefaultTexture(), false, () -> {
                    this.textureUrl = resource;
                }));
            } else {
                this.textureUrl = resource;
            }
        }
        return this.textureUrl;
    }

    public void setTexture(String s) {
        this.texture = s == null || s.isEmpty() ? null : new Identifier(s);
    }

    public void setUrl(String url) {
        if (NoppesStringUtils.areEqual(this.url, url)) {
            return;
        }
        this.url = url;
        this.textureUrl = null;
    }

    public Identifier getDefaultTexture() {
        if (this.texture != null) {
            return this.texture;
        }
        return this.getPart().texture;
    }

    public int getColor() {
        int r = (int)(this.color.x * 255.0f) << 16;
        int g = (int)(this.color.y * 255.0f) << 8;
        int b = (int)(this.color.z * 255.0f);
        return r + g + b;
    }

    public void setColor(int color) {
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        this.color = new NopVector3f(r, g, b);
    }

    public NbtCompound getNbt() {
        NbtCompound item = new NbtCompound();
        item.putString("Id", this.partId.toString());
        item.putBoolean("UsePlayerSkin", this.usePlayerSkin);
        item.putString("Url", this.url);
        item.putString("Texture", this.texture == null ? "" : this.texture.toString());
        item.putFloat("ColorR", this.color.x);
        item.putFloat("ColorG", this.color.y);
        item.putFloat("ColorB", this.color.z);
        return item;
    }

    public void setNbt(NbtCompound compound) {
        this.partId = new Identifier(compound.getString("Id"));
        this.usePlayerSkin = compound.getBoolean("UsePlayerSkin");
        this.setUrl(compound.getString("Url"));
        this.setTexture(compound.getString("Texture"));
        this.color = new NopVector3f(compound.getFloat("ColorR"), compound.getFloat("ColorG"), compound.getFloat("ColorB"));
    }
}

