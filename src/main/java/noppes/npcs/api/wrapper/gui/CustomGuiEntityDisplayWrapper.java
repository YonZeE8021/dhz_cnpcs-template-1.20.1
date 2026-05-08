/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.nbt.NbtElement
 */
package noppes.npcs.api.wrapper.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.gui.IEntityDisplay;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiComponentWrapper;

public class CustomGuiEntityDisplayWrapper
extends CustomGuiComponentWrapper
implements IEntityDisplay {
    private IEntity entity;
    private INbt entityData = new NBTWrapper(new NbtCompound());
    public int entityId = -1;
    private int rotation;
    public boolean isFollowingCursor = true;
    private float scale = 1.0f;
    private boolean showBackground = true;
    private float offsetX = 0.0f;
    private float offsetY = 0.0f;

    public CustomGuiEntityDisplayWrapper() {
    }

    public CustomGuiEntityDisplayWrapper(int id, IEntity entity, int x, int y) {
        this.setID(id);
        this.setEntity(entity);
        this.setPos(x, y);
    }

    @Override
    public IEntity getEntity() {
        return this.entity;
    }

    public INbt getEntityData() {
        return this.entityData;
    }

    @Override
    public IEntityDisplay setEntity(IEntity entity) {
        this.entity = entity;
        this.entityData = entity == null ? new NBTWrapper(new NbtCompound()) : entity.getEntityNbt();
        if (entity != null && entity.getMCEntity() instanceof PlayerEntity) {
            this.entityId = entity.getMCEntity().getId();
        }
        return this;
    }

    @Override
    public int getRotation() {
        return this.rotation;
    }

    @Override
    public IEntityDisplay setRotation(int rotation) {
        this.rotation = rotation;
        return this;
    }

    @Override
    public boolean isFollowingCursor() {
        return this.isFollowingCursor;
    }

    @Override
    public IEntityDisplay setFollowingCursor(boolean state) {
        this.isFollowingCursor = state;
        return this;
    }

    @Override
    public float getScale() {
        return this.scale;
    }

    @Override
    public IEntityDisplay setScale(float scale) {
        this.scale = scale;
        return this;
    }

    @Override
    public boolean getBackground() {
        return this.showBackground;
    }

    @Override
    public IEntityDisplay setBackground(boolean bo) {
        this.showBackground = bo;
        return this;
    }

    @Override
    public int getType() {
        return 9;
    }

    @Override
    public NbtCompound toNBT(NbtCompound compound) {
        super.toNBT(compound);
        compound.put("entity", (NbtElement)this.entityData.getMCNBT());
        compound.putInt("entityId", this.entityId);
        compound.putInt("rotation", this.rotation);
        compound.putFloat("scale", this.scale);
        compound.putBoolean("followCursor", this.isFollowingCursor);
        compound.putBoolean("background", this.showBackground);
        return compound;
    }

    @Override
    public CustomGuiComponentWrapper fromNBT(NbtCompound compound) {
        super.fromNBT(compound);
        this.entityData = NpcAPI.Instance().getINbt(compound.getCompound("entity"));
        this.entityId = compound.getInt("entityId");
        this.setRotation(compound.getInt("rotation"));
        this.setScale(compound.getFloat("scale"));
        this.setFollowingCursor(compound.getBoolean("followCursor"));
        this.setBackground(compound.getBoolean("background"));
        return this;
    }
}

