/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.world.World
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.particle.ParticleTypes
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.entity.EntityDimensions
 *  net.minecraft.entity.EntityPose
 *  net.minecraft.registry.Registries
 */
package noppes.npcs.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.registry.Registries;
import noppes.npcs.CustomEntities;
import noppes.npcs.ModelData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;

public class EntityNpcDragon
extends EntityNPCInterface {
    private EntityDimensions size = new EntityDimensions(1.8f, 1.4f, false);
    public double[][] field_40162_d = new double[64][3];
    public int field_40164_e = -1;
    public float prevAnimTime = 0.0f;
    public float animTime = 0.0f;
    public int field_40178_aA = 0;
    public boolean isFlying = false;
    private boolean exploded = false;

    public EntityNpcDragon(EntityType<? extends EntityNPCInterface> type, World world) {
        super(type, world);
        this.scaleX = 0.4f;
        this.scaleY = 0.4f;
        this.scaleZ = 0.4f;
        this.display.setSkinTexture("customnpcs:textures/entity/dragon/blackdragon.png");
    }

    public double getMountedHeightOffset() {
        return 1.1;
    }

    public double[] getMovementOffsets(int i, float f) {
        double d1;
        f = 1.0f - f;
        int j = this.field_40164_e - i * 1 & 0x3F;
        int k = this.field_40164_e - i * 1 - 1 & 0x3F;
        double[] ad = new double[3];
        double d = this.field_40162_d[j][0];
        for (d1 = this.field_40162_d[k][0] - d; d1 < -180.0; d1 += 360.0) {
        }
        while (d1 >= 180.0) {
            d1 -= 360.0;
        }
        ad[0] = d + d1 * (double)f;
        d = this.field_40162_d[j][1];
        d1 = this.field_40162_d[k][1] - d;
        ad[1] = d + d1 * (double)f;
        ad[2] = this.field_40162_d[j][2] + (this.field_40162_d[k][2] - this.field_40162_d[j][2]) * (double)f;
        return ad;
    }

    @Override
    public void tick() {
        this.discard();
        this.setAiDisabled(true);
        if (!this.getWorld().isClient) {
            NbtCompound compound = new NbtCompound();
            this.writeCustomDataToNbt(compound);
            EntityCustomNpc npc = new EntityCustomNpc(CustomEntities.entityCustomNpc, this.getWorld());
            npc.readCustomDataFromNbt(compound);
            ModelData data = npc.modelData;
            data.setEntity(Registries.ENTITY_TYPE.getId(CustomEntities.entityNpcDragon));
            this.getWorld().spawnEntity((Entity)npc);
        }
        super.tick();
    }

    @Override
    public void tickMovement() {
        this.prevAnimTime = this.animTime;
        if (this.getWorld().isClient && this.getHealth() <= 0.0f) {
            if (!this.exploded) {
                this.exploded = true;
                float f = (this.random.nextFloat() - 0.5f) * 8.0f;
                float f2 = (this.random.nextFloat() - 0.5f) * 4.0f;
                float f4 = (this.random.nextFloat() - 0.5f) * 8.0f;
                this.getWorld().addParticle((ParticleEffect)ParticleTypes.field_11236, this.getX() + (double)f, this.getY() + 2.0 + (double)f2, this.getZ() + (double)f4, 0.0, 0.0, 0.0);
            }
        } else {
            this.exploded = false;
            float f1 = 0.045f;
            this.animTime += (f1 *= (float)Math.pow(2.0, this.getVelocity().y)) * 0.5f;
        }
        super.tickMovement();
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pos) {
        return this.size;
    }
}

