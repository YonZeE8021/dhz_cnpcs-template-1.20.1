/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.texture.ResourceTexture
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.resource.Resource
 *  net.minecraft.resource.ResourceManager
 */
package noppes.npcs.shared.client.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import noppes.npcs.shared.client.util.CTextureUtil;
import noppes.npcs.shared.common.util.LogWriter;

public class TextureCache
extends ResourceTexture {
    private final Identifier original;

    public TextureCache(Identifier location, Identifier original) {
        super(location);
        this.original = original;
    }

    public void load(ResourceManager p_195413_1_) throws IOException {
        ResourceManager manager = MinecraftClient.getInstance().getResourceManager();
        Resource r = manager.getResource(this.original).orElse(null);
        if (r != null) {
            try {
                BufferedImage bufferedimage = ImageIO.read(r.getInputStream());
                int i = bufferedimage.getWidth();
                int j = bufferedimage.getHeight();
                BufferedImage bufferedImage = new BufferedImage(i * 4, j * 2, 1);
                Graphics g = bufferedImage.getGraphics();
                g.drawImage(bufferedimage, 0, 0, null);
                g.drawImage(bufferedimage, i, 0, null);
                g.drawImage(bufferedimage, i * 2, 0, null);
                g.drawImage(bufferedimage, i * 3, 0, null);
                g.drawImage(bufferedimage, 0, i, null);
                g.drawImage(bufferedimage, i, j, null);
                g.drawImage(bufferedimage, i * 2, j, null);
                g.drawImage(bufferedimage, i * 3, j, null);
                MinecraftClient.getInstance().submit(() -> CTextureUtil.uploadTextureImage(super.getGlId(), bufferedImage));
            }
            catch (Exception e) {
                LogWriter.error("Failed caching texture: " + String.valueOf(this.location), e);
            }
        }
    }
}

