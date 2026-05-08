/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtList
 *  net.minecraft.nbt.NbtElement
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package noppes.npcs.mixin;

import java.util.List;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={NbtList.class})
public interface ListNBTMixin {
    @Accessor("value")
    public List<NbtElement> getList();
}

