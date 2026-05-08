/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.texture.AbstractTexture
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 */
package noppes.npcs.shared.client.util;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import noppes.npcs.mixin.SkinManagerMixin;
import noppes.npcs.shared.SharedReferences;
import noppes.npcs.shared.client.util.ImageDownloadAlt;

public class ResourceDownloader {
    private static final Set<Identifier> active = Collections.synchronizedSet(new HashSet());
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public static void load(ImageDownloadAlt resource) {
        if (active.contains(resource.location)) {
            return;
        }
        active.add(resource.location);
        executor.execute(() -> {
            resource.loadTextureFromServer();
            MinecraftClient.getInstance().submit(() -> {
                MinecraftClient.getInstance().getTextureManager().registerTexture(resource.location, (AbstractTexture)resource);
                active.remove(resource.location);
            });
            try {
                Thread.sleep(400L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public static Identifier getUrlResourceLocation(String url, boolean fixSkin) {
        return new Identifier(SharedReferences.modid(), "skins/" + (url + fixSkin).hashCode() + (fixSkin ? "" : "32"));
    }

    public static File getUrlFile(String url, boolean fixSkin) {
        return new File(((SkinManagerMixin)MinecraftClient.getInstance().getSkinProvider()).getDir(), "" + (url + fixSkin).hashCode());
    }

    public static boolean contains(Identifier location) {
        return active.contains(location);
    }
}

