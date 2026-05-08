/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.resource.DirectoryResourcePack
 *  net.minecraft.resource.ResourcePack
 *  net.minecraft.resource.ResourcePackManager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package noppes.npcs.mixin;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackManager;
import noppes.npcs.CustomNpcs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ResourcePackManager.class})
public class PackRepositoryMixin {
    @Inject(at={@At(value="TAIL")}, method={"openAllSelected"}, cancellable=true)
    private void reload(CallbackInfoReturnable<List<ResourcePack>> ci) {
        ArrayList<DirectoryResourcePack> l = new ArrayList<DirectoryResourcePack>((Collection)ci.getReturnValue());
        l.add(new DirectoryResourcePack("cnpcs", CustomNpcs.Dir.toPath(), false));
        ci.setReturnValue(ImmutableList.copyOf(l));
    }
}

