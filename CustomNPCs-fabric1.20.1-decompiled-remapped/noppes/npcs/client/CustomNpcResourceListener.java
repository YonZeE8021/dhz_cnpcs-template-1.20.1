/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.texture.TextureManager
 *  net.minecraft.client.resource.language.I18n
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.resource.ResourceManager
 *  net.minecraft.resource.SynchronousResourceReloader
 *  net.minecraft.resource.ResourceFactory
 *  net.minecraft.client.gl.ShaderProgram
 */
package noppes.npcs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.client.gl.ShaderProgram;
import noppes.npcs.client.gui.select.GuiTextureSelection;
import noppes.npcs.client.parts.MpmPartReader;
import noppes.npcs.shared.client.model.util.CustomRenderStates;
import noppes.npcs.shared.client.util.TextureCache;

public class CustomNpcResourceListener
implements SynchronousResourceReloader {
    public static int DefaultTextColor = 0x404040;

    public void reload(ResourceManager manager) {
        try {
            DefaultTextColor = Integer.parseInt(I18n.translate((String)"customnpcs.defaultTextColor", (Object[])new Object[0]), 16);
        }
        catch (NumberFormatException e) {
            DefaultTextColor = 0x404040;
        }
        GuiTextureSelection.clear();
        MpmPartReader.reload();
        RenderSystem.recordRenderCall(() -> {
            try {
                CustomRenderStates.posTexNormalShader = new ShaderProgram((ResourceFactory)manager, "position_tex_normal", CustomRenderStates.POS_TEX_NORMAL);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void createTextureCache() {
        this.enlargeTexture("acacia_planks");
        this.enlargeTexture("birch_planks");
        this.enlargeTexture("crimson_planks");
        this.enlargeTexture("dark_oak_planks");
        this.enlargeTexture("jungle_planks");
        this.enlargeTexture("oak_planks");
        this.enlargeTexture("spruce_planks");
        this.enlargeTexture("warped_planks");
        this.enlargeTexture("iron_block");
        this.enlargeTexture("diamond_block");
        this.enlargeTexture("stone");
        this.enlargeTexture("gold_block");
        this.enlargeTexture("white_wool");
    }

    private void enlargeTexture(String texture) {
        Identifier location;
        TextureManager manager = MinecraftClient.getInstance().getTextureManager();
        Object ob = manager.getTexture(location = new Identifier("customnpcs:textures/cache/" + texture + ".png"));
        if (!(ob instanceof TextureCache)) {
            ob = new TextureCache(location, new Identifier("textures/block/" + texture + ".png"));
            manager.registerTexture(location, ob);
        }
    }
}

