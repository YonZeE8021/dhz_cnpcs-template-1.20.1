/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.util.Identifier
 *  net.minecraft.registry.RegistryKey
 *  net.minecraft.registry.RegistryKeys
 */
package noppes.npcs.controllers.data;

import net.minecraft.world.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import noppes.npcs.api.entity.data.role.IRoleTransporter;
import noppes.npcs.controllers.data.TransportCategory;

public class TransportLocation
implements IRoleTransporter.ITransportLocation {
    public int id = -1;
    public String name = "default name";
    public BlockPos pos;
    public int type = 0;
    public RegistryKey<World> dimension = RegistryKey.of((RegistryKey)RegistryKeys.WORLD, (Identifier)World.OVERWORLD.getValue());
    public TransportCategory category;

    public void readNBT(NbtCompound compound) {
        if (compound == null) {
            return;
        }
        this.id = compound.getInt("Id");
        this.pos = new BlockPos((int)compound.getDouble("PosX"), (int)compound.getDouble("PosY"), (int)compound.getDouble("PosZ"));
        this.type = compound.getInt("Type");
        this.dimension = RegistryKey.of((RegistryKey)RegistryKeys.WORLD, (Identifier)new Identifier(compound.getString("DimensionType")));
        this.name = compound.getString("Name");
    }

    public NbtCompound writeNBT() {
        NbtCompound compound = new NbtCompound();
        compound.putInt("Id", this.id);
        compound.putDouble("PosX", (double)this.pos.getX());
        compound.putDouble("PosY", (double)this.pos.getY());
        compound.putDouble("PosZ", (double)this.pos.getZ());
        compound.putInt("Type", this.type);
        compound.putString("DimensionType", this.dimension.getValue().toString());
        compound.putString("Name", this.name);
        return compound;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getDimension() {
        return this.dimension.getValue().toString();
    }

    @Override
    public int getX() {
        return this.pos.getX();
    }

    @Override
    public int getY() {
        return this.pos.getY();
    }

    @Override
    public int getZ() {
        return this.pos.getZ();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getType() {
        return this.type;
    }

    public boolean isDefault() {
        return this.type == 1;
    }
}

