/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.BlockState
 */
package noppes.npcs.blocks.tiles;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.BlockState;
import noppes.npcs.entity.data.IEntityPersistentData;

public class TileNpcEntity
extends BlockEntity {
    public Map<String, Object> tempData = new HashMap<String, Object>();

    public TileNpcEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        NbtCompound extraData = compound.getCompound("ExtraData");
        if (!extraData.isEmpty()) {
            ((IEntityPersistentData)((Object)this)).getPersistentData().put("CustomNPCsData", (NbtElement)extraData);
        }
    }

    public void writeNbt(NbtCompound compound) {
        super.writeNbt(compound);
    }
}

