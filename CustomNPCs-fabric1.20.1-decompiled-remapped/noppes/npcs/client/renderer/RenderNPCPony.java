/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.Identifier
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.resource.Resource
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 */
package noppes.npcs.client.renderer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import noppes.npcs.client.model.ModelPony;
import noppes.npcs.client.model.ModelPonyArmor;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityNpcPony;

public class RenderNPCPony<T extends EntityNpcPony, M extends ModelPony<T>>
extends RenderNPCInterface<T, M> {
    private ModelPony modelBipedMain;
    private ModelPonyArmor modelArmorChestplate;
    private ModelPonyArmor modelArmor;

    public RenderNPCPony(EntityRendererFactory.Context manager, M model) {
        super(manager, model, 0.5f);
        this.modelBipedMain = model;
        this.modelArmorChestplate = new ModelPonyArmor(1.0f);
        this.modelArmor = new ModelPonyArmor(0.5f);
    }

    @Override
    public Identifier getTextureLocation(T pony) {
        Resource resource;
        boolean check = ((EntityNpcPony)((Object)pony)).textureLocation == null || ((EntityNpcPony)((Object)pony)).textureLocation != ((EntityNpcPony)((Object)pony)).checked;
        Identifier loc = super.getTextureLocation(pony);
        if (check && (resource = (Resource)MinecraftClient.getInstance().getResourceManager().getResource(loc).orElse(null)) != null) {
            try {
                BufferedImage bufferedimage = ImageIO.read(resource.getInputStream());
                ((EntityNpcPony)((Object)pony)).isPegasus = false;
                ((EntityNpcPony)((Object)pony)).isUnicorn = false;
                Color color = new Color(bufferedimage.getRGB(0, 0), true);
                Color color1 = new Color(249, 177, 49, 255);
                Color color2 = new Color(136, 202, 240, 255);
                Color color3 = new Color(209, 159, 228, 255);
                Color color4 = new Color(254, 249, 252, 255);
                if (color.equals(color1)) {
                    // empty if block
                }
                if (color.equals(color2)) {
                    ((EntityNpcPony)((Object)pony)).isPegasus = true;
                }
                if (color.equals(color3)) {
                    ((EntityNpcPony)((Object)pony)).isUnicorn = true;
                }
                if (color.equals(color4)) {
                    ((EntityNpcPony)((Object)pony)).isPegasus = true;
                    ((EntityNpcPony)((Object)pony)).isUnicorn = true;
                }
                ((EntityNpcPony)((Object)pony)).checked = loc;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return loc;
    }

    @Override
    public void render(T pony, float entityYaw, float partialTicks, MatrixStack matrixStack, VertexConsumerProvider buffer, int packedLight) {
        ItemStack itemstack = ((EntityNPCInterface)((Object)pony)).getMainHandStack();
        this.modelBipedMain.heldItemRight = itemstack == null ? 0 : 1;
        this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight;
        this.modelArmorChestplate.heldItemRight = this.modelBipedMain.heldItemRight;
        this.modelArmor.isSneak = this.modelBipedMain.isSneak = ((EntityNPCInterface)((Object)pony)).isInSneakingPose();
        this.modelArmorChestplate.isSneak = this.modelBipedMain.isSneak;
        this.modelBipedMain.riding = false;
        this.modelArmor.riding = false;
        this.modelArmorChestplate.riding = false;
        this.modelArmor.isSleeping = this.modelBipedMain.isSleeping = ((EntityNPCInterface)((Object)pony)).isSleeping();
        this.modelArmorChestplate.isSleeping = this.modelBipedMain.isSleeping;
        this.modelArmor.isUnicorn = this.modelBipedMain.isUnicorn = ((EntityNpcPony)((Object)pony)).isUnicorn;
        this.modelArmorChestplate.isUnicorn = this.modelBipedMain.isUnicorn;
        this.modelArmor.isPegasus = this.modelBipedMain.isPegasus = ((EntityNpcPony)((Object)pony)).isPegasus;
        this.modelArmorChestplate.isPegasus = this.modelBipedMain.isPegasus;
        super.render(pony, entityYaw, partialTicks, matrixStack, buffer, packedLight);
        this.modelBipedMain.aimedBow = false;
        this.modelArmor.aimedBow = false;
        this.modelArmorChestplate.aimedBow = false;
        this.modelBipedMain.riding = false;
        this.modelArmor.riding = false;
        this.modelArmorChestplate.riding = false;
        this.modelBipedMain.isSneak = false;
        this.modelArmor.isSneak = false;
        this.modelArmorChestplate.isSneak = false;
        this.modelBipedMain.heldItemRight = 0;
        this.modelArmor.heldItemRight = 0;
        this.modelArmorChestplate.heldItemRight = 0;
    }
}

