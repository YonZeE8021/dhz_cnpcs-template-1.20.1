/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.world.Difficulty
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.GameRules
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.item.FoodComponent
 */
package noppes.npcs.roles.companion;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.Difficulty;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameRules;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.item.FoodComponent;
import noppes.npcs.entity.EntityNPCInterface;

public class CompanionFoodStats {
    private int foodLevel = 20;
    private float foodSaturationLevel = 5.0f;
    private float foodExhaustionLevel;
    private int foodTimer;
    private int prevFoodLevel = 20;

    private void addStats(int p_75122_1_, float p_75122_2_) {
        this.foodLevel = Math.min(p_75122_1_ + this.foodLevel, 20);
        this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float)p_75122_1_ * p_75122_2_ * 2.0f, (float)this.foodLevel);
    }

    public void onFoodEaten(FoodComponent food, ItemStack itemstack) {
        this.addStats(food.getHunger(), food.getSaturationModifier());
    }

    public void onUpdate(EntityNPCInterface npc) {
        Difficulty enumdifficulty = npc.getWorld().getDifficulty();
        this.prevFoodLevel = this.foodLevel;
        if (this.foodExhaustionLevel > 4.0f) {
            this.foodExhaustionLevel -= 4.0f;
            if (this.foodSaturationLevel > 0.0f) {
                this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0f, 0.0f);
            } else if (enumdifficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }
        if (npc.getWorld().getGameRules().getBoolean(GameRules.NATURAL_REGENERATION) && this.foodLevel >= 18 && npc.getHealth() > 0.0f && npc.getHealth() < npc.getMaxHealth()) {
            ++this.foodTimer;
            if (this.foodTimer >= 80) {
                npc.heal(1.0f);
                this.addExhaustion(3.0f);
                this.foodTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.foodTimer;
            if (this.foodTimer >= 80) {
                if (npc.getHealth() > 10.0f || enumdifficulty == Difficulty.HARD || npc.getHealth() > 1.0f && enumdifficulty == Difficulty.NORMAL) {
                    npc.damage(npc.getDamageSources().starve(), 1.0f);
                }
                this.foodTimer = 0;
            }
        } else {
            this.foodTimer = 0;
        }
    }

    public void readNBT(NbtCompound compound) {
        if (compound.contains("foodLevel", 99)) {
            this.foodLevel = compound.getInt("foodLevel");
            this.foodTimer = compound.getInt("foodTickTimer");
            this.foodSaturationLevel = compound.getFloat("foodSaturationLevel");
            this.foodExhaustionLevel = compound.getFloat("foodExhaustionLevel");
        }
    }

    public void writeNBT(NbtCompound compound) {
        compound.putInt("foodLevel", this.foodLevel);
        compound.putInt("foodTickTimer", this.foodTimer);
        compound.putFloat("foodSaturationLevel", this.foodSaturationLevel);
        compound.putFloat("foodExhaustionLevel", this.foodExhaustionLevel);
    }

    public int getFoodLevel() {
        return this.foodLevel;
    }

    @Environment(value=EnvType.CLIENT)
    public int getPrevFoodLevel() {
        return this.prevFoodLevel;
    }

    public boolean needFood() {
        return this.foodLevel < 20;
    }

    public void addExhaustion(float p_75113_1_) {
        this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + p_75113_1_, 40.0f);
    }

    public float getSaturationLevel() {
        return this.foodSaturationLevel;
    }

    @Environment(value=EnvType.CLIENT)
    public void setFoodLevel(int p_75114_1_) {
        this.foodLevel = p_75114_1_;
    }

    @Environment(value=EnvType.CLIENT)
    public void setFoodSaturationLevel(float p_75119_1_) {
        this.foodSaturationLevel = p_75119_1_;
    }
}

