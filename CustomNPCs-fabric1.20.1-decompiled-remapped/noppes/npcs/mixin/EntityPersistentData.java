/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.world.World
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package noppes.npcs.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.entity.data.IEntityPersistentData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Entity.class})
public class EntityPersistentData
implements IEntityPersistentData {
    @Shadow
    private World world;
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

    @Inject(method={"saveWithoutId"}, at={@At(value="TAIL")})
    public void save(NbtCompound compound, CallbackInfoReturnable<NbtCompound> cir) {
        if (this.CNPC_tag != null && this instanceof LivingEntity) {
            MarkData.get((LivingEntity)this).save();
            compound.put("CNPC_persistantData", (NbtElement)this.CNPC_tag);
        }
    }

    @Inject(method={"load"}, at={@At(value="TAIL")})
    public void read(NbtCompound compound, CallbackInfo ci) {
        if (compound.contains("CNPC_persistantData") && this instanceof LivingEntity) {
            this.CNPC_tag = compound.getCompound("CNPC_persistantData");
            MarkData.get((LivingEntity)this);
        }
    }
}

