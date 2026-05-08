/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.resource.ResourceReloader
 *  net.minecraft.resource.ReloadableResourceManagerImpl
 *  net.minecraft.client.RunArgs
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package noppes.npcs.mixin;

import net.minecraft.item.ItemConvertible;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.client.RunArgs;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemScripted;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.CustomNpcResourceListener;
import noppes.npcs.client.CustomRenderers;
import noppes.npcs.mixin.MinecraftAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={MinecraftClient.class})
public class MixinMinecraft {
    @Inject(method={"<init>"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/Minecraft;createSearchTrees()V")})
    private void lateInit(CallbackInfo ci) {
        CustomRenderers.registerEntityRenderer();
        CustomNpcResourceListener listener = new CustomNpcResourceListener();
        ((ReloadableResourceManagerImpl)MinecraftClient.getInstance().getResourceManager()).registerReloader((ResourceReloader)listener);
        listener.reload(MinecraftClient.getInstance().getResourceManager());
        ((MinecraftAccessor)MinecraftClient.getInstance()).getItemColors().register((stack, tintIndex) -> 9127187, new ItemConvertible[]{CustomItems.mount, CustomItems.cloner, CustomItems.moving, CustomItems.scripter, CustomItems.wand, CustomItems.teleporter});
        ((MinecraftAccessor)MinecraftClient.getInstance()).getItemColors().register((stack, tintIndex) -> {
            IItemStack item;
            if (stack.getItem() == CustomItems.scripted_item && !(item = NpcAPI.Instance().getIItemStack(stack)).isEmpty()) {
                return ((IItemScripted)item).getColor();
            }
            return -1;
        }, new ItemConvertible[]{CustomItems.scripted_item});
    }

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    private void veryLateInit(RunArgs gameConfig, CallbackInfo ci) {
        ClientProxy.Font = new ClientProxy.FontContainer(CustomNpcs.FontType, CustomNpcs.FontSize);
    }
}

