/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.Inventory
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.inventory.CraftingInventory
 *  net.minecraft.inventory.CraftingResultInventory
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
 *  net.minecraft.server.network.ServerPlayerEntity
 *  net.minecraft.inventory.RecipeInputInventory
 */
package noppes.npcs.containers;

import net.minecraft.inventory.Inventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomContainer;
import noppes.npcs.containers.SlotNpcCrafting;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.controllers.data.RecipeCarpentry;

public class ContainerCarpentryBench
extends ScreenHandler {
    public RecipeInputInventory craftMatrix = new CraftingInventory((ScreenHandler)this, 4, 4);
    public Inventory craftResult = new CraftingResultInventory();
    private PlayerEntity player;
    private BlockPos pos;

    public ContainerCarpentryBench(int id, PlayerInventory par1PlayerInventory, BlockPos pos) {
        super(CustomContainer.container_carpentrybench, id);
        int var7;
        int var6;
        this.pos = pos;
        this.player = par1PlayerInventory.player;
        this.addSlot((Slot)new SlotNpcCrafting(par1PlayerInventory.player, this.craftMatrix, this.craftResult, 0, 133, 41));
        for (var6 = 0; var6 < 4; ++var6) {
            for (var7 = 0; var7 < 4; ++var7) {
                this.addSlot(new Slot((Inventory)this.craftMatrix, var7 + var6 * 4, 17 + var7 * 18, 14 + var6 * 18));
            }
        }
        for (var6 = 0; var6 < 3; ++var6) {
            for (var7 = 0; var7 < 9; ++var7) {
                this.addSlot(new Slot((Inventory)par1PlayerInventory, var7 + var6 * 9 + 9, 8 + var7 * 18, 98 + var6 * 18));
            }
        }
        for (var6 = 0; var6 < 9; ++var6) {
            this.addSlot(new Slot((Inventory)par1PlayerInventory, var6, 8 + var6 * 18, 156));
        }
        this.onContentChanged((Inventory)this.craftMatrix);
    }

    public void onContentChanged(Inventory par1Container) {
        if (!this.player.getWorld().isClient) {
            RecipeCarpentry recipe = RecipeController.instance.findMatchingRecipe(this.craftMatrix);
            ItemStack item = ItemStack.EMPTY;
            if (recipe != null && recipe.availability.isAvailable(this.player)) {
                item = recipe.craft(this.craftMatrix, this.player.getWorld().getRegistryManager());
            }
            this.craftResult.setStack(0, item);
            ServerPlayerEntity plmp = (ServerPlayerEntity)this.player;
            plmp.networkHandler.sendPacket((Packet)new ScreenHandlerSlotUpdateS2CPacket(this.syncId, this.nextRevision(), 0, item));
        }
    }

    public void onClosed(PlayerEntity par1Player) {
        super.onClosed(par1Player);
        if (!par1Player.getWorld().isClient) {
            for (int var2 = 0; var2 < 16; ++var2) {
                ItemStack var3 = this.craftMatrix.removeStack(var2);
                if (var3 == null) continue;
                par1Player.dropItem(var3, false);
            }
        }
    }

    public boolean canUse(PlayerEntity par1Player) {
        return par1Player.getWorld().getBlockState(this.pos).getBlock() == CustomBlocks.carpenty && par1Player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

    public ItemStack quickMove(PlayerEntity par1Player, int par1) {
        ItemStack var2 = ItemStack.EMPTY;
        Slot var3 = (Slot)this.slots.get(par1);
        if (var3 != null && var3.hasStack()) {
            ItemStack var4 = var3.getStack();
            var2 = var4.copy();
            if (par1 == 0) {
                if (!this.insertItem(var4, 17, 53, true)) {
                    return ItemStack.EMPTY;
                }
                var3.onQuickTransfer(var4, var2);
            } else if (par1 >= 17 && par1 < 44 ? !this.insertItem(var4, 44, 53, false) : (par1 >= 44 && par1 < 53 ? !this.insertItem(var4, 17, 44, false) : !this.insertItem(var4, 17, 53, false))) {
                return ItemStack.EMPTY;
            }
            if (var4.getCount() == 0) {
                var3.setStackNoCallbacks(ItemStack.EMPTY);
            } else {
                var3.markDirty();
            }
            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY;
            }
            var3.onTakeItem(par1Player, var4);
        }
        return var2;
    }

    public boolean canInsertIntoSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != this.craftResult && super.canInsertIntoSlot(stack, slotIn);
    }
}

