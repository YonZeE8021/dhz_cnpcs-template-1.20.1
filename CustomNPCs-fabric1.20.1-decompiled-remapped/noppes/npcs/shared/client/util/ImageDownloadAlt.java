/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.TextureUtil
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.texture.NativeImage
 *  net.minecraft.client.texture.ResourceTexture
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.resource.ResourceManager
 *  org.apache.commons.io.FileUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package noppes.npcs.shared.client.util;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import noppes.npcs.shared.SharedReferences;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ImageDownloadAlt
extends ResourceTexture {
    private static final Logger logger = LogManager.getLogger();
    public final File cacheFile;
    private final String imageUrl;
    private boolean fix64;
    private Runnable r;
    public final Identifier location;
    public boolean uploaded = false;

    public ImageDownloadAlt(File file, String url, Identifier location, Identifier defaultLocation, boolean fix64, Runnable r) {
        super(defaultLocation);
        this.location = location;
        this.cacheFile = file;
        this.imageUrl = url;
        this.fix64 = fix64;
        this.r = r;
    }

    public void setImage(NativeImage image) {
        MinecraftClient.getInstance().execute(() -> {
            this.uploaded = true;
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(() -> this.upload(image));
            } else {
                this.upload(image);
            }
            this.r.run();
        });
    }

    private void upload(NativeImage imageIn) {
        TextureUtil.prepareImage((int)this.getGlId(), (int)imageIn.getWidth(), (int)imageIn.getHeight());
        imageIn.upload(0, 0, 0, true);
    }

    public void load(ResourceManager resourceManager) throws IOException {
        if (this.cacheFile != null && this.cacheFile.isFile()) {
            logger.debug("Loading http texture from local cache ({})", new Object[]{this.cacheFile});
            NativeImage image = null;
            try {
                image = NativeImage.read((InputStream)new FileInputStream(this.cacheFile));
                this.setImage(this.parseUserSkin(image));
                return;
            }
            catch (IOException ioexception) {
                super.load(resourceManager);
                logger.error("Couldn't load skin " + String.valueOf(this.cacheFile), (Throwable)ioexception);
            }
        }
        if (!this.uploaded) {
            try {
                this.uploaded = true;
                super.load(resourceManager);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadTextureFromServer() {
        HttpURLConnection connection = null;
        logger.debug("Downloading http texture from {} to {}", new Object[]{this.imageUrl, this.cacheFile});
        try {
            connection = (HttpURLConnection)new URL(this.imageUrl).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
            connection.connect();
            String type = connection.getContentType();
            long size = connection.getContentLengthLong();
            if (connection.getResponseCode() / 100 != 2 || !type.equals("image/png") || size > 2000000L && !MinecraftClient.getInstance().isIntegratedServerRunning()) {
                return;
            }
            FileUtils.copyInputStreamToFile((InputStream)connection.getInputStream(), (File)this.cacheFile);
        }
        catch (Exception exception) {
            logger.error("Couldn't download http texture", (Throwable)exception);
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public NativeImage parseUserSkin(NativeImage image) {
        boolean lvt_2_1_;
        if (image.getHeight() != image.getWidth() && image.getWidth() / 2 != image.getHeight()) {
            throw new IllegalArgumentException("Invalid texture size: " + image.getWidth() + "x" + image.getHeight());
        }
        int scale = image.getWidth() / 64;
        boolean bl = lvt_2_1_ = image.getHeight() != image.getWidth();
        if (lvt_2_1_ && this.fix64) {
            NativeImage nativeImage = new NativeImage(64 * scale, 64 * scale, true);
            nativeImage.copyFrom(image);
            image.close();
            image = nativeImage;
            nativeImage.fillRect(0, 32 * scale, 64 * scale, 32 * scale, 0);
            nativeImage.copyRect(4 * scale, 16 * scale, 16 * scale, 32 * scale, 4 * scale, 4 * scale, true, false);
            nativeImage.copyRect(8 * scale, 16 * scale, 16 * scale, 32 * scale, 4 * scale, 4 * scale, true, false);
            nativeImage.copyRect(0, 20 * scale, 24 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(4 * scale, 20 * scale, 16 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(8 * scale, 20 * scale, 8 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(12 * scale, 20 * scale, 16 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(44 * scale, 16 * scale, -8 * scale, 32 * scale, 4 * scale, 4 * scale, true, false);
            nativeImage.copyRect(48 * scale, 16 * scale, -8 * scale, 32 * scale, 4 * scale, 4 * scale, true, false);
            nativeImage.copyRect(40 * scale, 20 * scale, 0, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(44 * scale, 20 * scale, -8 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(48 * scale, 20 * scale, -16 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
            nativeImage.copyRect(52 * scale, 20 * scale, -8 * scale, 32 * scale, 4 * scale, 12 * scale, true, false);
        }
        if (!SharedReferences.AllowFullyInvisibleSkins()) {
            ImageDownloadAlt.setAreaOpaque(image, 0, 0, 32 * scale, 16 * scale);
        }
        if (lvt_2_1_ && this.fix64) {
            ImageDownloadAlt.setAreaTransparent(image, 32 * scale, 0, 64 * scale, 32 * scale);
        }
        return image;
    }

    private static void setAreaTransparent(NativeImage image, int x, int y, int width, int height) {
        for (int i = x; i < width; ++i) {
            for (int j = y; j < height; ++j) {
                int k = image.getColor(i, j);
                if ((k >> 24 & 0xFF) >= 128) continue;
                return;
            }
        }
        for (int l = x; l < width; ++l) {
            for (int i1 = y; i1 < height; ++i1) {
                image.setColor(l, i1, image.getColor(l, i1) & 0xFFFFFF);
            }
        }
    }

    private static void setAreaOpaque(NativeImage image, int x, int y, int width, int height) {
        for (int i = x; i < width; ++i) {
            for (int j = y; j < height; ++j) {
                image.setColor(i, j, image.getColor(i, j) | 0xFF000000);
            }
        }
    }
}

