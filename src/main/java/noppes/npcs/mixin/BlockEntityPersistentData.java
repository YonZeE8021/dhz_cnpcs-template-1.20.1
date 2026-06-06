/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.block.entity.BlockEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package noppes.npcs.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.block.entity.BlockEntity;
import noppes.npcs.entity.data.IEntityPersistentData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={BlockEntity.class})
public class BlockEntityPersistentData
implements IEntityPersistentData {
    @Unique
    private NbtCompound CNPC_tag;

    @Override
    @Unique
    public NbtCompound getPersistentData() {
        if (this.CNPC_tag == null) {
            this.CNPC_tag = new NbtCompound();
        }
        return this.CNPC_tag;
    }

    @Inject(method={"writeNbt"}, at={@At(value="TAIL")})
    public void save(NbtCompound tag, CallbackInfo ci) {
        if (this.CNPC_tag != null) {
            tag.put("CNPC_persistantData", (NbtElement)this.CNPC_tag);
        }
    }

    @Inject(method={"readNbt"}, at={@At(value="TAIL")})
    public void read(NbtCompound compound, CallbackInfo ci) {
        if (compound.contains("CNPC_persistantData")) {
            this.CNPC_tag = compound.getCompound("CNPC_persistantData");
        }
    }
}

