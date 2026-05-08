/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.TypedActionResult
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$Settings
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 *  net.minecraft.block.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 *  net.minecraft.util.hit.BlockHitResult
 */
package noppes.npcs.items;

import java.util.List;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;
import net.minecraft.util.hit.BlockHitResult;
import noppes.npcs.CustomNpcs;
import noppes.npcs.constants.EnumGuiType;

public class ItemTeleporter
extends Item {
    public ItemTeleporter() {
        super(new Item.Settings().maxCount(1));
    }

    public TypedActionResult<ItemStack> use(World level, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getStackInHand(hand);
        if (!level.isClient) {
            return new TypedActionResult(ActionResult.PASS, (Object)itemstack);
        }
        CustomNpcs.proxy.openGui(player, EnumGuiType.NpcDimensions);
        return new TypedActionResult(ActionResult.PASS, (Object)itemstack);
    }

    public static boolean onEntitySwing(ItemStack stack, LivingEntity livingEntity) {
        if (livingEntity.getWorld().isClient) {
            return true;
        }
        float f = livingEntity.getPitch();
        float f1 = livingEntity.getYaw();
        Vec3d vector3d = livingEntity.getCameraPosVec(1.0f);
        float f2 = MathHelper.cos((float)(-f1 * ((float)Math.PI / 180) - (float)Math.PI));
        float f3 = MathHelper.sin((float)(-f1 * ((float)Math.PI / 180) - (float)Math.PI));
        float f4 = -MathHelper.cos((float)(-f * ((float)Math.PI / 180)));
        float f5 = MathHelper.sin((float)(-f * ((float)Math.PI / 180)));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = 80.0;
        Vec3d vector3d1 = vector3d.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        BlockHitResult movingobjectposition = livingEntity.getWorld().raycast(new RaycastContext(vector3d, vector3d1, RaycastContext.ShapeType.field_17558, RaycastContext.FluidHandling.field_1347, (Entity)livingEntity));
        if (movingobjectposition == null) {
            return true;
        }
        Vec3d vec32 = livingEntity.getRotationVec(f);
        boolean flag = false;
        float f9 = 1.0f;
        List list = livingEntity.getWorld().getOtherEntities((Entity)livingEntity, livingEntity.getBoundingBox().expand(vec32.x * d0, vec32.y * d0, vec32.z * d0).expand((double)f9, (double)f9, (double)f9));
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = (Entity)list.get(i);
            if (!entity.isCollidable()) continue;
            float f10 = entity.getTargetingMargin();
            Box axisalignedbb = entity.getBoundingBox().expand((double)f10, (double)f10, (double)f10);
            if (!axisalignedbb.contains(vector3d)) continue;
            flag = true;
        }
        if (flag) {
            return true;
        }
        if (movingobjectposition.getType() == HitResult.Type.field_1332) {
            BlockPos pos = movingobjectposition.getBlockPos();
            while (livingEntity.getWorld().getBlockState(pos).getBlock() != Blocks.field_10124) {
                pos = pos.up();
            }
            livingEntity.requestTeleport((double)((float)pos.getX() + 0.5f), (double)((float)pos.getY() + 1.0f), (double)((float)pos.getZ() + 0.5f));
        }
        return true;
    }
}

